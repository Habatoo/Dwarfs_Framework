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

import com.fasterxml.jackson.annotation.*;
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
@Table(name = "USR")
@ToString(of = {"id", "userName", "password", "userEmail", "creationDate", "activationStatus"})
@EqualsAndHashCode(of = {"id"})
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name="USER_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="SOCIAL_NET_ID")
    private String socialNetId;

    @Column(name="USER_LOCALE")
    private String userLocale;

    @NotBlank(message = "Username cannot be empty")
    @Column(name="USER_NAME", length = 100, nullable = false)
    @JsonView(Views.UserShortData.class)
    private String userName;

    @NotBlank(message = "Password cannot be empty")
    @Column(name="USER_PASSWORD", length = 100)
    private String password;

    @Email(message = "Email is not correct")
    @NotBlank(message = "Email cannot be empty")
    @Column(name="USER_EMAIL", length = 100, nullable = false)
    //@JsonView(Views.AllData.class)
    private String userEmail;

    //@JsonView(Views.AllData.class)
    @Column(name="USER_EMAIL_ACTIVATION_STATUS")
    private boolean activationEmailStatus;

    @Column(name="USER_EMAIL_ACTIVATION_CODE")
    private String activationEmailCode;

    @Column(name="USER_CREATION_DATE", updatable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonView(Views.UserShortData.class)
    private LocalDateTime creationDate;

    @Column(name="USER_LAST_VISITED_DATE")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonView(Views.UserShortData.class)
    private LocalDateTime lastVisitedDate;

    ///////////////////////////////////////////////
    // User status
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name="ID_STATUS")
    @JsonView(Views.UserShortData.class)
    private Set<Status> userStatus;

    // User status start date
    @Column(name="USER_STATUS_START_DATE")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime statusStartDate;

    // User status end date
    @Column(name="USER_STATUS_END_DATE")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime statusEndDate;
    ////////////////////////////////////////////////////

    @ManyToMany(fetch = FetchType.EAGER)
    @JsonIdentityReference
    @JsonIdentityInfo(
            generator = ObjectIdGenerators.PropertyGenerator.class,
            property = "id")
    @JoinTable(	name = "USER_MAIN_ROLE",
            joinColumns = @JoinColumn(name = "USER_ID"),
            inverseJoinColumns = @JoinColumn(name = "MAIN_ROLE_ID"))
    private Set<MainRole> mainRoles = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JsonIdentityReference
    @JsonIdentityInfo(
            generator = ObjectIdGenerators.PropertyGenerator.class,
            property = "id")
    @JoinTable(	name = "USER_SUB_ROLE",
        joinColumns = @JoinColumn(name = "USER_ID"),
        inverseJoinColumns = @JoinColumn(name = "SUB_ROLE_ID"))
    private Set<SubRole> subRoles = new HashSet<>();

    @OneToMany(mappedBy = "userTokens", fetch = FetchType.EAGER, orphanRemoval = true)
    @JsonIdentityReference
    @JsonIdentityInfo(
            generator = ObjectIdGenerators.PropertyGenerator.class,
            property = "id")
    @Column(name="USER_TOKENS")
    private Set<Token> tokens = new HashSet<>();

    // TODO список участников
    @OneToMany(mappedBy = "userActivities", fetch = FetchType.EAGER, orphanRemoval = true)
    @JsonIdentityReference
    @JsonIdentityInfo(
            generator = ObjectIdGenerators.PropertyGenerator.class,
            property = "id")

    @Column(name="USER_ACTIVITIES")
    private Set<Activity> activities = new HashSet<>();

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER)
    @JsonIdentityReference
    @JsonIdentityInfo(
            generator = ObjectIdGenerators.PropertyGenerator.class,
            property = "id")
    @JoinTable(name = "USER_TAG",
            joinColumns = @JoinColumn(name = "ID_USER_TAG"),
            inverseJoinColumns = @JoinColumn(name = "ID_TAG_USER")
    )
    @Column(name="USER_TAGS")
    //@JsonView(Views.ShortData.class)
    private Set<Tag> tags = new HashSet<>();

    ////////////////////////////// User Subscriber

    @ManyToMany
    @JoinTable(
            name = "user_subscriptions",
            joinColumns = @JoinColumn(name = "subscriber_id"),
            inverseJoinColumns = @JoinColumn(name = "channel_id")
    )
    //@JsonView(Views.FullProfile.class)
    @JsonIdentityReference
    @JsonIdentityInfo(
            property = "id",
            generator = ObjectIdGenerators.PropertyGenerator.class
    )
    private Set<User> subscriptions = new HashSet<>();

    @ManyToMany
       @JoinTable(
            name = "user_subscriptions",
            joinColumns = @JoinColumn(name = "channel_id"),
            inverseJoinColumns = @JoinColumn(name = "subscriber_id")
    )
    //@JsonView(Views.FullProfile.class)
    @JsonIdentityReference
    @JsonIdentityInfo(
            property = "id",
            generator = ObjectIdGenerators.PropertyGenerator.class
    )
    private Set<User> subscribers = new HashSet<>();

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
        if (id == null) {
            if (user.id != null)
                return false;
        } else if (!id.equals(user.id))
            return false;
        return true;
        //return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
        //return Objects.hash(id);
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

