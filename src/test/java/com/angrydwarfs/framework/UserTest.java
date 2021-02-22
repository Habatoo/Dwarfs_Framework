package com.angrydwarfs.framework;

import com.angrydwarfs.framework.controllers.UserController;
import com.angrydwarfs.framework.models.*;
import com.angrydwarfs.framework.models.Enums.*;
import com.angrydwarfs.framework.payload.response.JwtResponse;
import com.angrydwarfs.framework.repository.TokenRepository;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = { "spring.config.location=classpath:application-test.yml" })
@Sql(value = {"/create-user-before.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/create-user-after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class UserTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    TokenRepository tokenRepository;

    @Autowired
    private UserController userController;

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
        assertThat(userRepository).isNotNull();
        assertThat(userController).isNotNull();
        assertThat(tokenRepository).isNotNull();
    }

    @Test
    @DisplayName("Проверяет создание нового пользователя USER.")
    public void createNewUser() {
        User user = userRepository.findByUserName("user").get();
        Set<Status> userStatus = new HashSet<>();
        userStatus.add(new Status(EStatus.READ_ONLY));
        user.setUserStatus(userStatus);
        user.setStatusStartDate(LocalDateTime.now());
        user.setStatusEndDate(null);

        assertEquals("user", user.getUserName());
        assertEquals("user@user.com", user.getUserEmail());
        assertEquals(false, user.isActivationEmailStatus());
        assertEquals(true, user.getUserStatus().toString().contains("READ_ONLY"));
        assertEquals(LocalDateTime.now().getYear(), user.getStatusStartDate().getYear());
        assertEquals(LocalDateTime.now().getMonth(), user.getStatusStartDate().getMonth());
        assertEquals(LocalDateTime.now().getDayOfMonth(), user.getStatusStartDate().getDayOfMonth());
        assertEquals(null, user.getStatusEndDate());
    }

    @Test
    @DisplayName("Проверяет создание нового пользователя MODERATOR.")
    public void createNewMod() {
        User user = userRepository.findByUserName("mod").get();
        Set<Status> userStatus = new HashSet<>();
        userStatus.add(new Status(EStatus.COMMON));
        user.setUserStatus(userStatus);
        user.setStatusStartDate(LocalDateTime.now());
        user.setStatusEndDate(null);

        assertEquals("mod", user.getUserName());
        assertEquals("mod@mod.com", user.getUserEmail());
        assertEquals(true, user.isActivationEmailStatus());
        assertEquals(true, user.getUserStatus().toString().contains("COMMON"));
        assertEquals(LocalDateTime.now().getYear(), user.getStatusStartDate().getYear());
        assertEquals(LocalDateTime.now().getMonth(), user.getStatusStartDate().getMonth());
        assertEquals(LocalDateTime.now().getDayOfMonth(), user.getStatusStartDate().getDayOfMonth());
        assertEquals(null, user.getStatusEndDate());
    }

    @Test
    @DisplayName("Проверяет создание нового пользователя ROLE_ADMINISTRATOR.")
    public void createNewAdmin() {
        User user = userRepository.findByUserName("admin").get();
        Set<Status> userStatus = new HashSet<>();
        userStatus.add(new Status(EStatus.BAN));
        user.setUserStatus(userStatus);
        user.setStatusStartDate(LocalDateTime.now());
        user.setStatusEndDate(LocalDateTime.now().plusDays(1));

        assertEquals("admin", user.getUserName());
        assertEquals("admin@admin.com", user.getUserEmail());
        assertEquals(true, user.isActivationEmailStatus());
        assertEquals(true, user.getUserStatus().toString().contains("BAN"));
        assertEquals(LocalDateTime.now().getYear(), user.getStatusStartDate().getYear());
        assertEquals(LocalDateTime.now().getMonth(), user.getStatusStartDate().getMonth());
        assertEquals(LocalDateTime.now().getDayOfMonth(), user.getStatusStartDate().getDayOfMonth());
        assertEquals(LocalDateTime.now().getYear(), user.getStatusEndDate().getYear());
        assertEquals(LocalDateTime.now().getMonth(), user.getStatusEndDate().getMonth());
        assertEquals(LocalDateTime.now().getDayOfMonth() + 1, user.getStatusEndDate().getDayOfMonth());
    }

    @Test
    @DisplayName("Проверяет создание токена для пользователя.")
    public void createUserToken() {
        User user = userRepository.findByUserName(username).get();
        JwtResponse jwtResponse = tokenUtils.makeAuth(user.getUserName(), password);
        tokenUtils.makeToken(username, jwtResponse.getToken());

        assertEquals(true, tokenRepository.existsByToken(jwtResponse.getToken()));
    }

    @Test
    @DisplayName("Проверяет создание ролей для пользователя.")
    public void createUserMainRole() {
        User user = userRepository.findByUserName(username).get();
        Set<MainRole> mainRole = new HashSet<>();
        MainRole role_1 = new MainRole(EMainRole.ROLE_ADMINISTRATOR);
        role_1.setId(1);
        MainRole role_2 = new MainRole(EMainRole.ROLE_MODERATOR);
        role_2.setId(2);
        MainRole role_3 = new MainRole(EMainRole.ROLE_USER);
        role_3.setId(3);
        mainRole.add(role_1);
        mainRole.add(role_2);
        mainRole.add(role_3);
        user.setMainRoles(mainRole);

        Assert.assertTrue(user.getMainRoles().toString().contains("ROLE_ADMINISTRATOR"));
        Assert.assertTrue(user.getMainRoles().toString().contains("ROLE_MODERATOR"));
        Assert.assertTrue(user.getMainRoles().toString().contains("ROLE_USER"));
    }

    @Test
    @DisplayName("Проверяет создание суб ролей для пользователя.")
    public void createUserSubRole() {
        User user = userRepository.findByUserName(username).get();
        Set<SubRole> subRole = new HashSet<>();
        SubRole role_1 = new SubRole(ESubRole.COMMON_USER);
        role_1.setId(1);
        SubRole role_2 = new SubRole(ESubRole.SILVER_USER);
        role_2.setId(2);
        SubRole role_3 = new SubRole(ESubRole.GOLD_USER);
        role_3.setId(3);
        subRole.add(role_1);
        subRole.add(role_2);
        subRole.add(role_3);
        user.setSubRoles(subRole);

        Assert.assertTrue(user.getSubRoles().toString().contains("COMMON_USER"));
        Assert.assertTrue(user.getSubRoles().toString().contains("SILVER_USER"));
        Assert.assertTrue(user.getSubRoles().toString().contains("GOLD_USER"));
    }

    @Test
    @DisplayName("Проверяет создание тэгов для пользователя.")
    public void createUserTag() {
        User user = userRepository.findByUserName(username).get();
        Set<Tag> tag = new HashSet<>();
        Tag tag_1 = new Tag(ETag.FITNESS);
        tag_1.setId(new Long(1));
        tag_1.setTagLevel(new Level(ELevel.FIRST_LEVEL));
        Tag tag_2 = new Tag(ETag.CROSSFIT);
        tag_2.setId(new Long(2));
        tag_2.setTagLevel(new Level(ELevel.SECOND_LEVEL));
        Tag tag_3 = new Tag(ETag.JOGGING);
        tag_3.setId(new Long(3));
        tag_3.setTagLevel(new Level(ELevel.THIRD_LEVEL));
        tag.add(tag_1);
        tag.add(tag_2);
        tag.add(tag_3);
        user.setTags(tag);

        Assert.assertTrue(user.getTags().toString().contains("FITNESS"));
        Assert.assertTrue(user.getTags().toString().contains("FIRST_LEVEL"));
        Assert.assertTrue(user.getTags().toString().contains("CROSSFIT"));
        Assert.assertTrue(user.getTags().toString().contains("SECOND_LEVEL"));
        Assert.assertTrue(user.getTags().toString().contains("JOGGING"));
        Assert.assertTrue(user.getTags().toString().contains("THIRD_LEVEL"));
    }

    @Test
    @DisplayName("Проверяет изменение своих данных пользователем с правами ADMIN.")
    public void testChangeMyAdminData() throws Exception{
        String id = "1";
        JwtResponse jwtResponse = tokenUtils.makeAuth(username, password);
        tokenUtils.makeToken(username, jwtResponse.getToken());

        //System.out.println("MyAdminData " + userRepository.findByUserName(username));

        this.mockMvc.perform(put("/api/auth/users/" + id)
                .header("Authorization", "Bearer " + jwtResponse.getToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"userName\": \"admin2\", \"userEmail\": \"admin2@admin2.com\", \"password\": \"12345\" }"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("message").value("User data was update successfully!"));

    }

    @Test
    @DisplayName("Проверяет изменение не своих данных пользователем с правами ADMIN.")
    public void testChangeUserData() throws Exception{
        String id = "2";
        JwtResponse jwtResponse = tokenUtils.makeAuth(username, password);
        tokenUtils.makeToken(username, jwtResponse.getToken());

        this.mockMvc.perform(put("/api/auth/users/" + id)
                .header("Authorization", "Bearer " + jwtResponse.getToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"userName\": \"user2\", \"userEmail\": \"user2@user2.com\", \"password\": \"12345\" }"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("message").value("User data was update successfully!"));
    }

    @Test
    @DisplayName("Проверяет изменение своих данных пользователем с правами USER.")
    public void testChangeMyUserData() throws Exception{
        JwtResponse jwtResponse = tokenUtils.makeAuth("user", password);
        tokenUtils.makeToken(username, jwtResponse.getToken());

        this.mockMvc.perform(put("/api/auth/users/3")
                .header("Authorization", "Bearer " + jwtResponse.getToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"userName\": \"user2\", \"userEmail\": \"user2@user2.com\", \"password\": \"12345\" }"))
                .andExpect(status().is(200))
                .andExpect(jsonPath("message").value("User data was update successfully!"));

    }

    @Test
    @DisplayName("Проверяет изменение не своих данных пользователем с правами USER.")
    public void testChangeNotMyUserData() throws Exception{
        String id = "1";
        JwtResponse jwtResponse = tokenUtils.makeAuth("user", password);
        tokenUtils.makeToken(username, jwtResponse.getToken());

        this.mockMvc.perform(put("/api/auth/users/" + id)
                .header("Authorization", "Bearer " + jwtResponse.getToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"userName\": \"admin2\", \"userEmail\": \"admin2@admin2.com\", \"password\": \"12345\"] }"))
                .andExpect(status().is(400));
        //.andExpect(jsonPath("message").value("You can edit only yourself data."));
    }

    @Test
    @DisplayName("Проверяет удаление пользователя автором с ролью ADMIN.")
    public void testDeleteUserByAdmin() throws Exception{
        String id = "2";
        JwtResponse jwtResponse = tokenUtils.makeAuth(username, password);
        tokenUtils.makeToken(username, jwtResponse.getToken());

        this.mockMvc.perform(delete("/api/auth/users/" + id)
                .header("Authorization", "Bearer " + jwtResponse.getToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("message").value("User was deleted successfully!"));
    }

    @Test
    @DisplayName("Проверяет удаление пользователя автором с ролью USER.")
    public void testFailDeleteUserByUser() throws Exception{
        String id = "1";
        JwtResponse jwtResponse = tokenUtils.makeAuth("user", password);
        tokenUtils.makeToken(username, jwtResponse.getToken());

        this.mockMvc.perform(delete("/api/auth/users/" + id)
                .header("Authorization", "Bearer " + jwtResponse.getToken()))
                .andExpect(status().is(403));
    }

    @Test
    @DisplayName("Проверяет отображение списка всех пользователей.")
    public void testShowAllUsers() throws Exception{
        JwtResponse jwtResponse = tokenUtils.makeAuth(username, password);
        tokenUtils.makeToken(username, jwtResponse.getToken());
        Date date = new Date();
        String resp = "[{\"roles\":[{\"id\":1,\"mainRoleName\":\"ROLE_ADMINISTRATOR\"},{\"id\":2,\"mainRoleName\":\"ROLE_MODERATOR\"},{\"id\":3,\"mainRoleName\":\"ROLE_USER\"}],\"userEmail\":\"admin@admin.com\",\"id\":1,\"creationDate\":\"" + date + "\",\"userName\":\"admin\"},{\"roles\":[{\"id\":2,\"mainRoleName\":\"ROLE_MODERATOR\"},{\"id\":3,\"mainRoleName\":\"ROLE_USER\"}],\"userEmail\":\"mod@mod.com\",\"id\":2,\"creationDate\":\"" + date + "\",\"userName\":\"mod\"},{\"roles\":[{\"id\":3,\"mainRoleName\":\"ROLE_USER\"}],\"userEmail\":\"user@user.com\",\"id\":3,\"creationDate\":\"" + date + "\",\"userName\":\"user\"}]";

        this.mockMvc.perform(get("/api/auth/users/")
                .header("Authorization", "Bearer " + jwtResponse.getToken()))
                .andExpect(status().is(200))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
                .andReturn().getResponse().getContentAsString().equals(resp);
    }

    @Test
    @DisplayName("Проверяет отображение информации о текущем пользователе.")
    public void testShowCurrentUserInfo() throws Exception{
        JwtResponse jwtResponse = tokenUtils.makeAuth(username, password);
        tokenUtils.makeToken(username, jwtResponse.getToken());

        this.mockMvc.perform(get("/api/auth/users/getUserInfo")
                .header("Authorization", "Bearer " + jwtResponse.getToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("userName").value("admin"))
                .andExpect(jsonPath("userEmail").value("admin@admin.com"))
                .andExpect(jsonPath("userLocale").isEmpty())
                .andExpect(jsonPath("activationEmailStatus").value(true))
                .andExpect((jsonPath("userStatus", Matchers.empty())));
    }

    @Test
    @DisplayName("Проверяет срок действия токенов на валидном токене.")
    public void testTokensDataCheck() throws Exception{
        JwtResponse jwtResponse = tokenUtils.makeAuth(username, password);
        tokenUtils.makeToken(username, jwtResponse.getToken());

        this.mockMvc.perform(delete("/api/auth/users/tokens")
                .header("Authorization", "Bearer " + jwtResponse.getToken()))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("message").value("All tokens have valid expiry date!"));
    }

    @Test
    @DisplayName("Проверяет срок действия токенов и очистку базу токенов.")
    public void testTokensDataClean() throws Exception{
        JwtResponse jwtResponse = tokenUtils.makeAuth(username, password);
        tokenUtils.makeToken(username, jwtResponse.getToken());

        // Create old token
        Assert.assertEquals(1, tokenRepository.findAll().size());
        tokenUtils.makeOldToken(username, password);
        Assert.assertEquals(2, tokenRepository.findAll().size());

        this.mockMvc.perform(delete("/api/auth/users/tokens")
                .header("Authorization", "Bearer " + jwtResponse.getToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("message").value("Tokens with expiry date was deleted successfully!"));

        Assert.assertEquals(1, tokenRepository.findAll().size());
    }

}
