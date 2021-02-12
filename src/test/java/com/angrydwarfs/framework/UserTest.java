package com.angrydwarfs.framework;

import com.angrydwarfs.framework.controllers.UserController;
import com.angrydwarfs.framework.models.*;
import com.angrydwarfs.framework.repository.UserRepository;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assert.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashSet;
import java.util.Set;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
@Sql(value = {"/create-user-before.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/create-user-after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class UserTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserController userController;

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

    }

    @Test
    @DisplayName("Проверяет создание нового пользователя.")
    public void createNewUser() {
        User user = new User("user", "user@user.com", "1234567890");
        assertThat(userRepository).isNotNull();
        assertEquals("user", user.getUserName());
        assertEquals("user@user.com", user.getUserEmail());
        assertEquals("1234567890", user.getPassword());
        assertEquals(false, user.isActivationEmailStatus());
    }


    @Test
    @DisplayName("Проверяет создание токена для пользователя.")
    public void createUserToken() {
//        JwtResponse jwtResponse = tokenUtils.makeAuth(username, password);
//        tokenUtils.makeToken(username, jwtResponse.getAccessToken());

//        User user = new User("user", "user@user.com", "1234567890");
//        assertThat(userRepository).isNotNull();
//        assertEquals("user", user.getUserName());
//        assertEquals("user@user.com", user.getUserEmail());
//        assertEquals("1234567890", user.getPassword());
//        assertEquals(false, user.isActivationStatus());
    }

    @Test
    @DisplayName("Проверяет создание ролей для пользователя.")
    public void createUserMainRole() {
        User user = new User("user", "user@user.com", "1234567890");
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
        User user = new User("user", "user@user.com", "1234567890");
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
//        User user = new User("user", "user@user.com", "1234567890");
//        Set<Tag> tag = new HashSet<>();
//        Tag tag_1 = new Tag(ETag.FITNESS);
//        tag_1.setId(new Long(1));
//        Tag tag_2 = new Tag(ETag.CROSSFIT);
//        tag_2.setId(new Long(2));
//        Tag tag_3 = new Tag(ETag.JOGGING);
//        tag_3.setId(new Long(3));
//        tag.add(tag_1);
//        tag.add(tag_2);
//        tag.add(tag_3);
//        user.setTags(tag);
//
//        Assert.assertTrue(user.getTags().toString().contains("FITNESS"));
//        Assert.assertTrue(user.getTags().toString().contains("CROSSFIT"));
//        Assert.assertTrue(user.getTags().toString().contains("JOGGING"));
    }

}
