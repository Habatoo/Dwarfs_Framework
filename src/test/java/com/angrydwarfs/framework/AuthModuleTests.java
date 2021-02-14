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

import com.angrydwarfs.framework.controllers.AuthController;
import com.angrydwarfs.framework.models.User;
import com.angrydwarfs.framework.payload.response.JwtResponse;
import com.angrydwarfs.framework.repository.UserRepository;
import com.angrydwarfs.framework.security.jwt.TokenUtils;
import org.hamcrest.Matchers;
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

import static org.assertj.core.api.Assertions.assertThat;
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
public class AuthModuleTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthController authController;

    @Autowired
    TokenUtils tokenUtils;

    @Autowired
    private UserRepository userRepository;

    @Value("${dwarfsframework.app.jwtSecret}")
    private String jwtSecret;

    @Value("${dwarfsframework.app.jwtExpirationMs}")
    private int jwtExpirationMs;

    String username = "admin";
    String password = "12345";

    @Test
    @DisplayName("Проверяет успешную подгрузку контроллера из контекста.")
    public void loadControllers() {
        assertThat(authController).isNotNull();
    }

    @Test
    @DisplayName("Проверяет создание пользователя с ролями ADMIN, MOD и USER.")
    public void testCreateAdmin() throws Exception{
        this.mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"userName\": \"testmod\", \"email\": \"testmod@mod.com\", \"password\": \"12345\", \"role\": [\"admin\", \"mod\", \"user\"] }"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("message").value("User registered successfully!"));

        User user = userRepository.findByUserName("testmod").get();
        JwtResponse jwtResponse = tokenUtils.makeAuth(user.getUserName(), password);
        tokenUtils.makeToken("testmod", jwtResponse.getToken());
        Assert.assertTrue(user.getMainRoles().toString().contains("ROLE_ADMINISTRATOR"));
        Assert.assertTrue(user.getMainRoles().toString().contains("ROLE_USER"));
        Assert.assertTrue(user.getMainRoles().toString().contains("ROLE_MODERATOR"));
        Assert.assertTrue(user.getSubRoles().toString().contains("COMMON_USER"));
        Assert.assertTrue(user.getUserEmail().contains("testmod@mod.com"));
    }


    @Test
    @DisplayName("Проверяет создание пользователя с ролью USER.")
    public void testCreateUser() throws Exception{
        this.mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"userName\": \"guest\", \"email\": \"guest@guest.com\", \"password\": \"12345\", \"role\": [\"user\"] }"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("message").value("User registered successfully!"));

        User user = userRepository.findByUserName("guest").get();
        JwtResponse jwtResponse = tokenUtils.makeAuth(user.getUserName(), password);
        tokenUtils.makeToken("guest", jwtResponse.getToken());
        Assert.assertFalse(user.getMainRoles().toString().contains("ROLE_ADMINISTRATOR"));
        Assert.assertTrue(user.getMainRoles().toString().contains("ROLE_USER"));
        Assert.assertFalse(user.getMainRoles().toString().contains("ROLE_MODERATOR"));
        Assert.assertTrue(user.getSubRoles().toString().contains("COMMON_USER"));
        Assert.assertTrue(user.getUserEmail().contains("guest@guest.com"));
    }

    @Test
    @DisplayName("Проверяет создание пользователя с ролью ADMIN")
    public void testCreateAdminAndUser() throws Exception{
        this.mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"userName\": \"admin2\", \"email\": \"admin2@admin2.com\", \"password\": \"12345\", \"role\": [\"admin\"] }"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("message").value("User registered successfully!"));
    }

    @Test
    @DisplayName("Проверяет создание пользователя с существующим userName.")
    public void testCreateUsernameInDb() throws Exception{
        this.mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"userName\": \"admin\", \"email\": \"admin2@admin2.com\", \"password\": \"12345\", \"role\": [\"admin\"] }"))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("message").value("Error: Username is already taken!"));
    }

    @Test
    @DisplayName("Проверяет создание пользователя с существующим email.")
    public void testCreateEmailInDb() throws Exception{
        this.mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"userName\": \"admin2\", \"email\": \"admin@admin.com\", \"password\": \"12345\", \"role\": [\"admin\"] }"))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("message").value("Error: Email is already in use!"));
    }

    @Test
    @DisplayName("Проверяет создание пользователя с не существующей ролью.")
    public void testCreateRoleNotInDb() throws Exception{
        this.mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"userName\": \"cat\", \"email\": \"cat@cat.com\", \"password\": \"12345\", \"role\": [\"cat\"] }"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("message").value("User registered successfully!"));
    }

    @Test
    @DisplayName("Проверяет создание пользователя автором без роли ADMIN")
    public void testFailCreateUserWithoutAdminRole() throws Exception{
        this.mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"userName\": \"admin2\", \"email\": \"admin2@admin2.com\", \"password\": \"12345\", \"role\": [\"admin\"] }"))
                .andExpect(status().is(200))
                .andExpect(jsonPath("message").value("User registered successfully!"));
    }

    @Test
    @DisplayName("Проверяет логин с некорректным паролем.")
    public void loginForbiddenTest() throws Exception{
        this.mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"userName\": \"mod\", \"password\": \"123456\" }"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("path").value(""))
                .andExpect(jsonPath("error").value("Unauthorized"))
                .andExpect(jsonPath("message").value("Bad credentials"))
                .andExpect(jsonPath("status").value(401));
    }

    @Test
    @DisplayName("Проверяет аутентификацию пользователя ADMIN.")
    public void testAdminLogin() throws Exception{
        this.mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"userName\": \"admin\", \"password\": \"12345\" }"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName").value("admin"))
                .andExpect(jsonPath("$.userEmail").value("admin@admin.com"))
                .andExpect((jsonPath("$.roles", Matchers.containsInAnyOrder("ROLE_ADMINISTRATOR","ROLE_MODERATOR", "ROLE_USER"))));
    }

    @Test
    @DisplayName("Проверяет аутентификацию пользователя USER.")
    public void testUserLogin() throws Exception{
        this.mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"userName\": \"user\", \"password\": \"12345\" }"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName").value("user"))
                .andExpect(jsonPath("$.userEmail").value("user@user.com"))
                .andExpect((jsonPath("$.roles", Matchers.containsInAnyOrder("ROLE_USER"))));
    }

    @Test
    @DisplayName("Проверяет выход без токена.")
    public void logoutFailTest() throws Exception {
        this.mockMvc.perform(get("/api/auth/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"token\": \"\" }"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("path").value(""))
                .andExpect(jsonPath("error").value("Unauthorized"))
                .andExpect(jsonPath("message").value("Full authentication is required to access this resource"))
                .andExpect(jsonPath("status").value(401));

    }

    /**
     * Проверка метода logout, для корректной проверки требует токена с активным статусом и не истекшим сроком
     * @throws Exception
     */
    @Test
    @DisplayName("Проверяет выход с токеном.")
    public void logoutTest() throws Exception {
        JwtResponse jwtResponse = tokenUtils.makeAuth(username, password);
        tokenUtils.makeToken(username, jwtResponse.getToken());

        this.mockMvc.perform(get("/api/auth/logout")
                .header("Authorization", "Bearer " + jwtResponse.getToken()))
                .andExpect(status().is(400))
                .andExpect(jsonPath("message").value("You are logout."));
    }

//    /**
//     * Проверка метода reset, для корректной проверки требует seсretKey
//     * @throws Exception
//     */
//    @Test
//    @DisplayName("Проверяет сброс пароля с использованием seсretKey.")
//    public void resetTest() throws Exception {
//        this.mockMvc.perform(post("/api/auth/reset")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content("{ \"userName\": \"admin\", \"secretKey\": \"1234567890\", \"password\": \"12346\" }"))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("userName").value("admin"))
//                .andExpect(jsonPath("password").value("12346"));
//    }

//    /**
//     * Проверка метода reset, для корректной проверки требует seсretKey
//     * @throws Exception
//     */
//    @Test
//    @DisplayName("Проверяет сброс пароля с использованием seсretKey, для не существующего пользователя.")
//    public void resetNoUserTest() throws Exception {
//        this.mockMvc.perform(post("/api/auth/reset")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content("{ \"userName\": \"bob\", \"secretKey\": \"1234567890\", \"password\": \"12346\" }"))
//                .andDo(print())
//                .andExpect(status().is4xxClientError())
//                .andExpect(jsonPath("message").value("Error: Username does not exist!"));
//    }

//    /**
//     * Проверка метода reset, для корректной проверки требует seсretKey
//     * @throws Exception
//     */
//    @Test
//    @DisplayName("Проверяет сброс пароля с использованием некорректного seсretKey, для существующего пользователя.")
//    public void resetWrongSecretKeyTest() throws Exception {
//        this.mockMvc.perform(post("/api/auth/reset")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content("{ \"userName\": \"admin\", \"secretKey\": \"123456789\", \"password\": \"12345\" }"))
//                .andDo(print())
//                .andExpect(status().is4xxClientError())
//                .andExpect(jsonPath("message").value("Error: SecretKey does not valid!"));
//    }

}
