/*
 * Copyright 2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.angrydwarfs.framework;

import com.angrydwarfs.framework.controllers.ActivityController;
import com.angrydwarfs.framework.controllers.UserController;
import com.angrydwarfs.framework.models.Activity;
import com.angrydwarfs.framework.models.Enums.EStatus;
import com.angrydwarfs.framework.models.Enums.ETag;
import com.angrydwarfs.framework.models.Status;
import com.angrydwarfs.framework.models.Tag;
import com.angrydwarfs.framework.models.User;
import com.angrydwarfs.framework.payload.response.JwtResponse;
import com.angrydwarfs.framework.repository.ActivityRepository;
import com.angrydwarfs.framework.repository.TagRepository;
import com.angrydwarfs.framework.repository.TokenRepository;
import com.angrydwarfs.framework.repository.UserRepository;
import com.angrydwarfs.framework.security.jwt.TokenUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = { "spring.config.location=classpath:application-test.yml" })
@Sql(value = {"/create-user-before.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/create-user-after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class ActivityTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    TokenRepository tokenRepository;

    @Autowired
    TagRepository tagRepository;

    @Autowired
    private UserController userController;

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private ActivityController activityController;

    @Autowired
    TokenUtils tokenUtils;

    @Value("${dwarfsframework.app.jwtSecret}")
    private String jwtSecret;

    @Value("${dwarfsframework.app.jwtExpirationMs}")
    private int jwtExpirationMs;

    String username = "admin";
    String password = "12345";

    @Test
    @DisplayName("Проверяет успешную подгрузку контроллеров из контекста.")
    public void loadControllers() {
        assertThat(activityRepository).isNotNull();
        assertThat(activityController).isNotNull();
    }

    @Test
    @DisplayName("Проверяет отображение всех сообщений пользователем ADMIN.")
    public void showAllActivitiesFromDb() {
        User user = commonUser();
        List<Activity> activityList = activityRepository.findByUserActivities(user);
        System.out.println("ActivityTest.createNewActivity " + activityRepository.findByUserActivities(user));

        assertEquals("First activity", activityList.get(0).getActivityTitle());
        assertEquals("First user body activity FIRST", activityList.get(0).getActivityBody());
        assertEquals(LocalDateTime.now().getYear(), activityList.get(0).getCreationDate().getYear());
        assertEquals(LocalDateTime.now().getMonth(), activityList.get(0).getCreationDate().getMonth());
        assertEquals(LocalDateTime.now().getDayOfMonth(), activityList.get(0).getCreationDate().getDayOfMonth());

        assertEquals("Second activity", activityList.get(1).getActivityTitle());
        assertEquals("First user body activity SECOND", activityList.get(1).getActivityBody());
        assertEquals(LocalDateTime.now().getYear(), activityList.get(1).getCreationDate().getYear());
        assertEquals(LocalDateTime.now().getMonth(), activityList.get(1).getCreationDate().getMonth());
        assertEquals(LocalDateTime.now().getDayOfMonth(), activityList.get(1).getCreationDate().getDayOfMonth());
    }

    @Test
    @DisplayName("Проверяет отображение всех сообщений пользователя ADMIN.")
    public void showAllActivities() throws Exception{
        JwtResponse jwtResponse = tokenUtils.makeAuth(username, password);
        tokenUtils.makeToken(username, jwtResponse.getToken());
        LocalDate localDate = LocalDate.now();
        String date = localDate + " 00:00:00";

        this.mockMvc.perform(get("/api/auth/users/activities")
                .header("Authorization", "Bearer " + jwtResponse.getToken())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().string("[{\"activityTitle\":\"First activity\",\"activityBody\":\"First user body activity FIRST\",\"creationDate\":\"" + date + "\",\"userActivities\":{\"userName\":\"admin\",\"creationDate\":\"" + date + "\",\"lastVisitedDate\":null,\"userStatus\":[]},\"tags\":[]},{\"activityTitle\":\"Second activity\",\"activityBody\":\"First user body activity SECOND\",\"creationDate\":\"" + date + "\",\"userActivities\":{\"userName\":\"admin\",\"creationDate\":\"" + date + "\",\"lastVisitedDate\":null,\"userStatus\":[]},\"tags\":[]}]"));
    }

    @Test
    @DisplayName("Проверяет создание нового activity пользователем ADMIN.")
    public void createNewActivity() {
        User user = commonUser();
        Set<Tag> tags = createTags();
        Activity activity = new Activity("Test title", "Body message of test activity", user);
        activity.setTags(tags);
        activity.setId(new Long(4));
        activityRepository.save(activity);

        assertEquals("Test title", activity.getActivityTitle());
        assertEquals("Body message of test activity", activity.getActivityBody());
        assertEquals(LocalDateTime.now().getYear(), activity.getCreationDate().getYear());
        assertEquals(LocalDateTime.now().getMonth(), activity.getCreationDate().getMonth());
        assertEquals(LocalDateTime.now().getDayOfMonth(), activity.getCreationDate().getDayOfMonth());

        Assert.assertTrue(activity.getTags().toString().contains("JOGGING"));
        Assert.assertTrue(activity.getTags().toString().contains("FITNESS"));
        Assert.assertTrue(activity.getTags().toString().contains("CROSSFIT"));
    }

    //    @Test
//    @DisplayName("Проверяет изменение не своих данных пользователем с правами ADMIN.")
//    public void testChangeUserData() throws Exception{
//        String id = "2";
//        JwtResponse jwtResponse = tokenUtils.makeAuth(username, password);
//        tokenUtils.makeToken(username, jwtResponse.getAccessToken());
//
//        this.mockMvc.perform(put("/api/auth/users/" + id)
//                .header("Authorization", "Bearer " + jwtResponse.getAccessToken())
//                .contentType(MediaType.APPLICATION_JSON)
//                .content("{ \"userName\": \"user2\", \"userEmail\": \"user2@user2.com\", \"password\": \"12345\"] }"))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("message").value("User data was update successfully!"));
//    }

    private User commonUser() {
        User user = userRepository.findByUserName(username).get();
        Set<Status> userStatus = new HashSet<>();
        userStatus.add(new Status(EStatus.COMMON));
        user.setUserStatus(userStatus);
        user.setStatusStartDate(LocalDateTime.now());
        user.setStatusEndDate(null);

        return user;
    }

    private Set<Tag> createTags() {
        Set<Tag> tags = new HashSet<>();
        tags.add(tagRepository.findByTagName(ETag.CROSSFIT).get());
        tags.add(tagRepository.findByTagName(ETag.JOGGING).get());
        tags.add(tagRepository.findByTagName(ETag.FITNESS).get());

        return tags;
    }

}
