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

import com.angrydwarfs.framework.controllers.TagController;
import com.angrydwarfs.framework.controllers.UserController;
import com.angrydwarfs.framework.models.Enums.ELevel;
import com.angrydwarfs.framework.models.Enums.ETag;
import com.angrydwarfs.framework.models.Tag;
import com.angrydwarfs.framework.models.User;
import com.angrydwarfs.framework.payload.response.JwtResponse;
import com.angrydwarfs.framework.repository.LevelRepository;
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

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = { "spring.config.location=classpath:application-test.yml" })
@Sql(value = {"/create-user-before.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/create-user-after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class TagTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    TokenRepository tokenRepository;

    @Autowired
    LevelRepository levelRepository;

    @Autowired
    private UserController userController;

    @Autowired
    private TagController tagController;

    @Autowired
    TagRepository tagRepository;

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
        assertThat(tagController).isNotNull();
        assertThat(tagRepository).isNotNull();
    }

    @Test
    @DisplayName("Проверяет добавление тэгов пользователю ADMIN.")
    public void testAdminAddTags() throws Exception{
        JwtResponse jwtResponse = tokenUtils.makeAuth(username, password);
        tokenUtils.makeToken(username, jwtResponse.getToken());

        this.mockMvc.perform(put("/api/auth/tags/addUserTags")
                .header("Authorization", "Bearer " + jwtResponse.getToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"tags\": [\"JOGGING\", \"FITNESS\"] }"))
                .andExpect(status().is(200))
                .andExpect(jsonPath("message").value("Tags was added successfully!"));

        User user = userRepository.findByUserName(username).get();
        Assert.assertTrue(user.getTags().toString().contains("JOGGING"));
        Assert.assertTrue(user.getTags().toString().contains("FITNESS"));
        Assert.assertFalse(user.getTags().toString().contains("CROSSFIT"));

    }

    @Test
    @DisplayName("Проверяет добавление тэгов пользователю USER.")
    public void testUserAddTags() throws Exception{
        JwtResponse jwtResponse = tokenUtils.makeAuth("user", password);
        tokenUtils.makeToken(username, jwtResponse.getToken());

        this.mockMvc.perform(put("/api/auth/tags/addUserTags")
                .header("Authorization", "Bearer " + jwtResponse.getToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"tags\": [\"JOGGING\", \"FITNESS\"] }"))
                .andExpect(status().is(200))
                .andExpect(jsonPath("message").value("Tags was added successfully!"));

        User user = userRepository.findByUserName("user").get();
        Assert.assertTrue(user.getTags().toString().contains("JOGGING"));
        Assert.assertTrue(user.getTags().toString().contains("FITNESS"));
        Assert.assertFalse(user.getTags().toString().contains("CROSSFIT"));
    }

    @Test
    @DisplayName("Проверяет изменение тэгов activity ADMIN.")
    public void testChangeUserTagsLevel() throws Exception{
        JwtResponse jwtResponse = tokenUtils.makeAuth(username, password);
        tokenUtils.makeToken(username, jwtResponse.getToken());
        String tagName = "JOGGING";
        String tagLevel = "2";

        User user = userRepository.findByUserName(username).get();
        Set<Tag> tags = new HashSet<>();
        Tag tempTag = tagRepository.findByTagName(ETag.valueOf(tagName)).get();
        tempTag.setTagLevel(levelRepository.findByLevelName(ELevel.FIRST_LEVEL).get());
        tags.add(tempTag);
        user.setTags(tags);

        Assert.assertTrue(user.getTags().toString().contains("JOGGING"));
        Assert.assertTrue(user.getTags().toString().contains("FIRST_LEVEL"));

        this.mockMvc.perform(put("/api/auth/tags/changeUserTagsLevel/" + username + "/" + tagName + "/" + tagLevel)
                .header("Authorization", "Bearer " + jwtResponse.getToken())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("message").value("Tag was changed successfully!"));

    }

    @Test
    @DisplayName("Проверяет удаление тэга пользователя ADMIN.")
    public void testUserDeleteTags() throws Exception{
        JwtResponse jwtResponse = tokenUtils.makeAuth(username, password);
        tokenUtils.makeToken(username, jwtResponse.getToken());
        String tagName = "JOGGING";

        User user = userRepository.findByUserName(username).get();
        Set<Tag> tags = new HashSet<>();
        Tag tempTag = tagRepository.findByTagName(ETag.valueOf(tagName)).get();
        tempTag.setTagLevel(levelRepository.findByLevelName(ELevel.FIRST_LEVEL).get());
        tags.add(tempTag);
        user.setTags(tags);

        Assert.assertTrue(user.getTags().toString().contains("JOGGING"));

        this.mockMvc.perform(delete("/api/auth/tags/deleteTag/" + username + "/" + tagName)
                .header("Authorization", "Bearer " + jwtResponse.getToken())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("message").value("Tag was deleted successfully!"));

    }

}
