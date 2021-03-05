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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
        User user = commonUser(username);
        List<Activity> activityList = activityRepository.findByUserActivities(user);
        System.out.println("ActivityTest.createNewActivity " + activityRepository.findByUserActivities(user));

        assertEquals("First activity", activityList.get(0).getActivityTitle());
        assertEquals("First user body activity FIRST", activityList.get(0).getActivityDescription());
        assertEquals(LocalDateTime.now().getYear(), activityList.get(0).getCreationDate().getYear());
        assertEquals(LocalDateTime.now().getMonth(), activityList.get(0).getCreationDate().getMonth());
        assertEquals(LocalDateTime.now().getDayOfMonth(), activityList.get(0).getCreationDate().getDayOfMonth());

        assertEquals("Second activity", activityList.get(1).getActivityTitle());
        assertEquals("First user body activity SECOND", activityList.get(1).getActivityDescription());
        assertEquals(LocalDateTime.now().getYear(), activityList.get(1).getCreationDate().getYear());
        assertEquals(LocalDateTime.now().getMonth(), activityList.get(1).getCreationDate().getMonth());
        assertEquals(LocalDateTime.now().getDayOfMonth(), activityList.get(1).getCreationDate().getDayOfMonth());
    }

    @Test
    @DisplayName("Проверяет отображение всех сообщений пользователя ADMIN.")
    public void showAllAdminActivities() throws Exception{
        JwtResponse jwtResponse = tokenUtils.makeAuth(username, password);
        tokenUtils.makeToken(username, jwtResponse.getToken());
        LocalDate localDate = LocalDate.now();
        String date = localDate + " 00:00:00";

        this.mockMvc.perform(get("/api/auth/users/activities")
                .header("Authorization", "Bearer " + jwtResponse.getToken())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().string("[{\"activityTitle\":\"First activity\",\"activityDescription\":\"First user body activity FIRST\",\"latitude\":null,\"longitude\":null,\"activityAddress\":null,\"creationDate\":\"" + date + "\",\"dateOfActivity\":null,\"userActivities\":{\"userName\":\"admin\",\"creationDate\":\"" + date + "\",\"lastVisitedDate\":null,\"userStatus\":[]},\"tags\":[]},{\"activityTitle\":\"Second activity\",\"activityDescription\":\"First user body activity SECOND\",\"latitude\":null,\"longitude\":null,\"activityAddress\":null,\"creationDate\":\"" + date + "\",\"dateOfActivity\":null,\"userActivities\":{\"userName\":\"admin\",\"creationDate\":\"" + date + "\",\"lastVisitedDate\":null,\"userStatus\":[]},\"tags\":[]}]"));
    }

    @Test
    @DisplayName("Проверяет отображение всех сообщений пользователя USER.")
    public void showAllUserActivities() throws Exception{
        JwtResponse jwtResponse = tokenUtils.makeAuth("user", password);
        tokenUtils.makeToken(username, jwtResponse.getToken());
        LocalDate localDate = LocalDate.now();
        String date = localDate + " 00:00:00";

        this.mockMvc.perform(get("/api/auth/users/activities")
                .header("Authorization", "Bearer " + jwtResponse.getToken())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().string("[{\"activityTitle\":\"Fourth activity\",\"activityDescription\":\"Third user body activity FOURTH\",\"latitude\":null,\"longitude\":null,\"activityAddress\":null,\"creationDate\":\"" + date + "\",\"dateOfActivity\":null,\"userActivities\":{\"userName\":\"user\",\"creationDate\":\"" + date + "\",\"lastVisitedDate\":null,\"userStatus\":[]},\"tags\":[]}]"));
    }

    @Test
    @DisplayName("Проверяет создание нового activity пользователем ADMIN.")
    public void createAdminNewActivity() {
        User user = commonUser(username);
        Set<Tag> tags = createTags();
        Activity activity = new Activity("Test title", "Body message of test activity", user);
        activity.setTags(tags);
        activity.setId(new Long(4));
        activityRepository.save(activity);

        assertEquals("Test title", activity.getActivityTitle());
        assertEquals("Body message of test activity", activity.getActivityDescription());
        assertEquals(LocalDateTime.now().getYear(), activity.getCreationDate().getYear());
        assertEquals(LocalDateTime.now().getMonth(), activity.getCreationDate().getMonth());
        assertEquals(LocalDateTime.now().getDayOfMonth(), activity.getCreationDate().getDayOfMonth());

        Assert.assertTrue(activity.getTags().toString().contains("JOGGING"));
        Assert.assertTrue(activity.getTags().toString().contains("FITNESS"));
        Assert.assertTrue(activity.getTags().toString().contains("CROSSFIT"));
    }

    @Test
    @DisplayName("Проверяет создание нового activity пользователем USER.")
    public void createUserNewActivity() {
        User user = commonUser("user");
        Set<Tag> tags = createTags();
        Activity activity = new Activity("Test user title", "Body user message of test activity", user);
        activity.setTags(tags);
        activity.setId(new Long(4));
        activityRepository.save(activity);

        assertEquals("Test user title", activity.getActivityTitle());
        assertEquals("Body user message of test activity", activity.getActivityDescription());
        assertEquals(LocalDateTime.now().getYear(), activity.getCreationDate().getYear());
        assertEquals(LocalDateTime.now().getMonth(), activity.getCreationDate().getMonth());
        assertEquals(LocalDateTime.now().getDayOfMonth(), activity.getCreationDate().getDayOfMonth());

        Assert.assertTrue(activity.getTags().toString().contains("JOGGING"));
        Assert.assertTrue(activity.getTags().toString().contains("FITNESS"));
        Assert.assertTrue(activity.getTags().toString().contains("CROSSFIT"));
    }

    @Test
    @DisplayName("Проверяет создание нового activity пользователем ADMIN.")
    public void createAdminNewActivityAPI() throws Exception{
        JwtResponse jwtResponse = tokenUtils.makeAuth(username, password);
        tokenUtils.makeToken(username, jwtResponse.getToken());
        LocalDate localDate = LocalDate.now();
        String date = localDate + " 00:00:00";

        this.mockMvc.perform(post("/api/auth/users/activities/newActivity")
                .header("Authorization", "Bearer " + jwtResponse.getToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"activityTitle\": \"Test activity\", \"activityBody\": \"Test activity body\", \"creationDate\": \"2021-02-22 00:00:00\", \"tags\": [\"JOGGING\", \"FITNESS\"] }"))
                .andDo(print())
                .andExpect(status().isOk());

        Activity activity = activityRepository.findById(new Long(10)).get();
        assertEquals("Test activity", activity.getActivityTitle());
        assertEquals("Test activity body", activity.getActivityDescription());
        assertEquals(2021, activity.getCreationDate().getYear());
        assertEquals(java.time.Month.FEBRUARY, activity.getCreationDate().getMonth());
        assertEquals(22, activity.getCreationDate().getDayOfMonth());

//        System.out.println("tags " + activity.getTags());
//        System.out.println("user " + activity.getUserActivities());
//        System.out.println("uuid " + activity.getActivityIndex());
//        System.out.println("id " + activity.getId());
//        System.out.println("Activity " + activityRepository.findById(new Long(10)).get().getTags().toString().contains("FITNESS"));
//        System.out.println("Activity " + activityRepository.findById(new Long(10)).get().getTags().toString().contains("JOGGING"));
//        System.out.println("Activity " + activityRepository.findById(new Long(10)).get().getTags().toString().contains("CROSSFIT"));

        Assert.assertTrue(activityRepository.findById(new Long(10)).get().getTags().toString().contains("JOGGING"));
        Assert.assertTrue(activity.getTags().toString().contains("FITNESS"));
        Assert.assertFalse(activity.getTags().toString().contains("CROSSFIT"));
    }

    @Test
    @DisplayName("Проверяет выбор activity по его id.")
    public void testShowActivity() throws Exception{
        String id = "3"; // Сообщение пользователя USER
        JwtResponse jwtResponse = tokenUtils.makeAuth(username, password);
        tokenUtils.makeToken(username, jwtResponse.getToken());
        LocalDate localDate = LocalDate.now();
        String date = localDate + " 00:00:00";

        this.mockMvc.perform(get("/api/auth/users/activities/" + id)
                .header("Authorization", "Bearer " + jwtResponse.getToken())
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.activityTitle").value("Third activity"))
                .andExpect(jsonPath("$.activityDescription").value("Second user body activity THIRD"))
                .andExpect(jsonPath("$.creationDate").value(date))
                .andExpect(jsonPath("$.userActivities").isNotEmpty());
    }

    @Test
    @DisplayName("Проверяет изменение своего activity пользователем с правами ADMIN.")
    public void testChangeMyAdminActivity() throws Exception{
        String id = "1"; // Сообщение пользователя ADMIN
        JwtResponse jwtResponse = tokenUtils.makeAuth(username, password);
        tokenUtils.makeToken(username, jwtResponse.getToken());

        this.mockMvc.perform(put("/api/auth/users/activities/" + id)
                .header("Authorization", "Bearer " + jwtResponse.getToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"activityTitle\": \"Third edit activity\", \"activityDescription\": \"Second edit user body activity THIRD\" }"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("message").value("Activity was update successfully!"));

        assertEquals("Third edit activity", activityRepository.findById(new Long(1)).get().getActivityTitle());
        assertEquals("Second edit user body activity THIRD", activityRepository.findById(new Long(1)).get().getActivityDescription());
    }

    @Test
    @DisplayName("Проверяет изменение не своего activity пользователем с правами ADMIN.")
    public void testChangeNotMyAdminActivity() throws Exception{
        String id = "4"; // Сообщение пользователя USER
        JwtResponse jwtResponse = tokenUtils.makeAuth(username, password);
        tokenUtils.makeToken(username, jwtResponse.getToken());

        this.mockMvc.perform(put("/api/auth/users/activities/" + id)
                .header("Authorization", "Bearer " + jwtResponse.getToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"activityTitle\": \"Third edit activity\", \"activityDescription\": \"Second edit user body activity THIRD\" }"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("message").value("Activity was update successfully!"));

        assertEquals("Third edit activity", activityRepository.findById(new Long(4)).get().getActivityTitle());
        assertEquals("Second edit user body activity THIRD", activityRepository.findById(new Long(4)).get().getActivityDescription());
    }

    @Test
    @DisplayName("Проверяет изменение своего activity пользователем с правами USER.")
    public void testChangeMyUserActivity() throws Exception{
        String id = "4"; // Сообщение пользователя USER
        JwtResponse jwtResponse = tokenUtils.makeAuth("user", password);
        tokenUtils.makeToken(username, jwtResponse.getToken());

        this.mockMvc.perform(put("/api/auth/users/activities/" + id)
                .header("Authorization", "Bearer " + jwtResponse.getToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"activityTitle\": \"Third edit activity\", \"activityDescription\": \"Second edit user body activity THIRD\" }"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("message").value("Activity was update successfully!"));

        assertEquals("Third edit activity", activityRepository.findById(new Long(4)).get().getActivityTitle());
        assertEquals("Second edit user body activity THIRD", activityRepository.findById(new Long(4)).get().getActivityDescription());
    }

    @Test
    @DisplayName("Проверяет изменение не своего activity пользователем с правами USER.")
    public void testChangeNotMyUserActivity() throws Exception{
        String id = "1"; // Сообщение пользователя ADMIN
        JwtResponse jwtResponse = tokenUtils.makeAuth("user", password);
        tokenUtils.makeToken(username, jwtResponse.getToken());

        this.mockMvc.perform(put("/api/auth/users/activities/" + id)
                .header("Authorization", "Bearer " + jwtResponse.getToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"activityTitle\": \"Third edit activity\", \"activityDescription\": \"Second edit user body activity THIRD\" }"))
                .andDo(print())
                .andExpect(status().is(400))
                .andExpect(jsonPath("message").value("You can edit only yourself data!"));

    }

    @Test
    @DisplayName("Проверяет удаление своего activity автором ADMIN.")
    public void testDeleteMyActivityByAdmin() throws Exception{
        String id = "1";
        JwtResponse jwtResponse = tokenUtils.makeAuth(username, password);
        tokenUtils.makeToken(username, jwtResponse.getToken());

        this.mockMvc.perform(delete("/api/auth/users/activities/" + id)
                .header("Authorization", "Bearer " + jwtResponse.getToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("message").value("Activity was deleted successfully!"));
    }

    @Test
    @DisplayName("Проверяет удаление не своего activity автором ADMIN.")
    public void testDeleteNotMyActivityByAdmin() throws Exception{
        String id = "3";
        JwtResponse jwtResponse = tokenUtils.makeAuth(username, password);
        tokenUtils.makeToken(username, jwtResponse.getToken());

        this.mockMvc.perform(delete("/api/auth/users/activities/" + id)
                .header("Authorization", "Bearer " + jwtResponse.getToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("message").value("Activity was deleted successfully!"));
    }

    @Test
    @DisplayName("Проверяет удаление своего activity автором USER.")
    public void testDeleteMyActivityByUser() throws Exception{
        String id = "4";
        JwtResponse jwtResponse = tokenUtils.makeAuth(username, password);
        tokenUtils.makeToken(username, jwtResponse.getToken());

        this.mockMvc.perform(delete("/api/auth/users/activities/" + id)
                .header("Authorization", "Bearer " + jwtResponse.getToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("message").value("Activity was deleted successfully!"));
    }

    @Test
    @DisplayName("Проверяет удаление не своего activity автором USER.")
    public void testDeleteNotMyActivityByUser() throws Exception{
        String id = "1";
        JwtResponse jwtResponse = tokenUtils.makeAuth("user", password);
        tokenUtils.makeToken(username, jwtResponse.getToken());

        this.mockMvc.perform(delete("/api/auth/users/activities/" + id)
                .header("Authorization", "Bearer " + jwtResponse.getToken()))
                .andExpect(status().is(400))
                .andExpect(jsonPath("message").value("You can delete only yourself data!"));
    }

    private User commonUser(String username) {
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
