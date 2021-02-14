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

package com.angrydwarfs.framework.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * Модель пользователя. Записывается в БД в таблицу с имененм users.
 * @version 0.001
 * @author habatoo
 *
 * @param "id" - primary key таблицы users.
 * @param "userName" - имя пользователя - предпоалагается строковоя переменная Имя + Фамилия.
 * @param "password" - пароль, в БД хранится в виде хешированном виде.
 * @param "userEmail" - email пользователя.
 * @param "creationDate" - дата создания пользователя.
 * @param "mainRoles" - роли пользователя - определяют возможности доступа - администратор, модератор, пользователь
 * @see MainRole
 * @param "subRoles" - роли пользователя - определяют вид аккаунта - пользовательский, расширенный, дополнительный
 * @see SubRole
 * @param "activationStatus" - статус аакаунта по email - подтвержденный/не подтвержденный
 * @param "activationCode" - код активации для сброса пароля, смены email
 * @param "token" - токен сессии пользователя
 * @see Token (токены пользователя).
 * @param "activities" - активности пользователя
 * @see Activity (активности пользователя).
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "usr")
@ToString(of = {"id", "userName", "password", "userEmail", "creationDate", "activationStatus"})
@EqualsAndHashCode(of = {"id"})
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String socialNetId;
    private String userLocale;

    @NotBlank(message = "Username cannot be empty")
    @JsonView(Views.UserShortData.class)
    private String userName;

    @NotBlank(message = "Password cannot be empty")
    private String password;

    @Email(message = "Email is not correct")
    @NotBlank(message = "Email cannot be empty")
    //@JsonView(Views.AllData.class)
    private String userEmail;

    //@JsonView(Views.AllData.class)
    private boolean activationEmailStatus;
    private String activationEmailCode;

    @Column(updatable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonView(Views.UserShortData.class)
    private LocalDateTime creationDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonView(Views.UserShortData.class)
    private LocalDateTime lastVisitedDate;

    ///////////////////////////////////////////////
    // User status
    @JsonIdentityInfo(
            generator = ObjectIdGenerators.PropertyGenerator.class,
            property = "id")
    @OneToMany(mappedBy = "userCurrentStatus", fetch = FetchType.EAGER, orphanRemoval = true)
    @JsonView(Views.UserShortData.class)
    private Set<Status> userStatus;

    // User status start date
    @Column
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime statusStartDate;

    // User status end date
    @Column
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime statusEndDate;
    ////////////////////////////////////////////////////

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(	name = "user_main_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "main_role_id"))
    private Set<MainRole> mainRoles = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(	name = "user_sub_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "sub_role_id"))
    private Set<SubRole> subRoles = new HashSet<>();

    @JsonIdentityInfo(
            generator = ObjectIdGenerators.PropertyGenerator.class,
            property = "id")
    @OneToMany(mappedBy = "userTokens", fetch = FetchType.EAGER, orphanRemoval = true)
    private Set<Token> tokens;

    @JsonIdentityInfo(
            generator = ObjectIdGenerators.PropertyGenerator.class,
            property = "id")
    @OneToMany(mappedBy = "userActivities", fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<Activity> activities;

    @JsonIdentityInfo(
            generator = ObjectIdGenerators.PropertyGenerator.class,
            property = "id")
    @OneToMany(mappedBy = "userTags", fetch = FetchType.LAZY, orphanRemoval = true)
    //@JsonView(Views.ShortData.class)
    private Set<Tag> tags;

    /**
     * Конструктор для создания пользователя.
     * @param userName - имя пользователя - предпоалагается строковоя переменная Имя + Фамилия.
     * @param userEmail - email пользователя.
     * @param password - пароль, в БД хранится в виде хешированном виде.
     * activationStatus - поле подтверждения email пользователя.
     * userStatus - поле статуса пользователя на возможность чтения и записи и доступа к аккаунту
     *
     */
    public User(String userName, String userEmail, String password) {
        this.userName = userName;
        this.userEmail = userEmail;
        this.password = password;
        this.activationEmailStatus = false;

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }


//    @Override
//    public boolean isAccountNonExpired() {
//        return true;
//    }
//
//    @Override
//    public boolean isAccountNonLocked() {
//        return true;
//    }
//
//    @Override
//    public boolean isCredentialsNonExpired() {
//        return true;
//    }
//
//    @Override
//    public boolean isEnabled() {
//        return isActive();
//    }
//
//    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//        return getRoles();
//    }

}

