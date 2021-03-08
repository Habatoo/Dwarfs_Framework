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
import com.angrydwarfs.framework.controllers.SubscriptionController;
import com.angrydwarfs.framework.controllers.UserController;
import com.angrydwarfs.framework.payload.response.JwtResponse;
import com.angrydwarfs.framework.repository.ActivityRepository;
import com.angrydwarfs.framework.repository.TagRepository;
import com.angrydwarfs.framework.repository.TokenRepository;
import com.angrydwarfs.framework.repository.UserRepository;
import com.angrydwarfs.framework.security.jwt.TokenUtils;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.BeforeClass;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = { "spring.config.location=classpath:application-test.yml" })
@Sql(value = {"/create-user-before.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/create-user-after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class SubscriptionTest {
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
    private SubscriptionController subscriptionController;

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private ActivityController activityController;

    @Autowired
    private TokenUtils tokenUtils;

    @Value("${dwarfsframework.app.jwtSecret}")
    private String jwtSecret;

    @Value("${dwarfsframework.app.jwtExpirationMs}")
    private int jwtExpirationMs;

    static String username = "admin";
    static String  password = "12345";
    static JwtResponse jwtResponse;

    @Before
    public void createToken() {
        jwtResponse = tokenUtils.makeAuth(username, password);
        tokenUtils.makeToken(username, jwtResponse.getToken());
    }

    @Test
    @DisplayName("Проверяет успешную подгрузку контроллеров из контекста.")
    public void loadControllers() {
        assertThat(subscriptionController).isNotNull();
    }

    @Test
    @DisplayName("Проверяет текущие подписки у пользователя ADMIN.")
    public void testSubscriptionInUserInfo() throws Exception{
        String id = "1";

        this.mockMvc.perform(get("/api/auth/subscription/" + id)
                .header("Authorization", "Bearer " + jwtResponse.getToken())
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect((jsonPath("subscriptions", Matchers.empty())))
                .andExpect((jsonPath("subscribers", Matchers.empty())));
    }

    @Test
    @DisplayName("Проверяет текущие подписки у пользователя USER.")
    public void testMyAdminSubscription() throws Exception{
        String id = "2";

        this.mockMvc.perform(get("/api/auth/subscription/" + id)
                .header("Authorization", "Bearer " + jwtResponse.getToken())
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect((jsonPath("subscriptions", Matchers.empty())))
                .andExpect((jsonPath("subscribers", Matchers.empty())));
    }

    @Test
    @DisplayName("Проверяет добавление подписчиков у пользователя ADMIN.")
    public void testMyAdminSubscribersAdd() throws Exception{
        String channelId = "2";

        this.mockMvc.perform(post("/api/auth/subscription/change-subscription/" + channelId)
                .header("Authorization", "Bearer " + jwtResponse.getToken())
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect((jsonPath("subscriptions", Matchers.empty())))
                .andExpect((jsonPath("subscribers", Matchers.contains(1))));

    }

    @Test
    @DisplayName("Проверяет исключение подписчиков у пользователя ADMIN.")
    public void testMyAdminSubscribersDelete() throws Exception{
        String channelId = "2";

        this.mockMvc.perform(post("/api/auth/subscription/change-subscription/" + channelId)
                .header("Authorization", "Bearer " + jwtResponse.getToken())
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect((jsonPath("subscriptions", Matchers.empty())))
                .andExpect((jsonPath("subscribers", Matchers.contains(1))));

        this.mockMvc.perform(post("/api/auth/subscription/change-subscription/" + channelId)
                .header("Authorization", "Bearer " + jwtResponse.getToken())
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect((jsonPath("subscriptions", Matchers.empty())))
                .andExpect((jsonPath("subscribers", Matchers.empty())));
    }
}
