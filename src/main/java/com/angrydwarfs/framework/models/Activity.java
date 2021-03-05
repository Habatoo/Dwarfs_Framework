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
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Модель активностей пользователей
 * @version 0.013
 * @author habatoo
 *
 * @param "id" - primary key таблицы activity.
 * @param "activityIndex" - UUID activity.
 * @param "activityTitle" - заголовок activity.
 * @param "activityDescription" - описание activity.
 * @param "latitude" - координата широта activity.
 * @param "longitude" - координата долгота activity.
 * @param "activityAddress" - адрес activity.
 * @param "creationDate" - дата создания activity.
 * @param "dateOfActivity" - дата проведения activity.
 * @param "activityAddress" - адрес activity.
 * @param "userActivities" - пользователь создатель activity.
 * @param "userActivitiesParticipant" - участники activity.     // TODO список участников
 * @param "tags" - теги activity.
 * @see User (пользователи).
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "ACTIVITY")
@ToString(of = {"id", "activityTitle", "activityBody", "creationDate", "activityCode"})
@EqualsAndHashCode(of = {"id"})
public class Activity implements Serializable {

    private static final long serialVersionUID = 2L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ACTIVITY_ID")
    private Long id;

    @NotBlank(message = "ActivityIndex cannot be empty")
    @Column(name = "ACTIVITY_INDEX")
    private String activityIndex = UUID.randomUUID().toString();

    @JsonView(Views.UserShortData.class)
    @NotBlank(message = "Title cannot be empty")
    @Column(name = "ACTIVITY_TITLE")
    private String activityTitle;

    @JsonView(Views.UserShortData.class)
    @NotBlank(message = "Description cannot be empty")
    @Column(name = "ACTIVITY_DESCRIPTION")
    private String activityDescription;

    @JsonView(Views.UserShortData.class)
    @Column(name = "LATITUDE")
    private String latitude;

    @JsonView(Views.UserShortData.class)
    @Column(name = "LONGITUDE")
    private String longitude ;

    @JsonView(Views.UserShortData.class)
    @Column(name = "ACTIVITY_ADDRESS")
    private String activityAddress;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonView(Views.UserShortData.class)
    @Column(name = "ACTIVITY_CREATION_DATE", updatable = false)
    private LocalDateTime creationDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonView(Views.UserShortData.class)
    @Column(name = "DATE_OF_ACTIVITY", updatable = false)
    private LocalDateTime dateOfActivity;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ACTIVITY_USER_ID")
    @JsonView(Views.UserShortData.class)
    private User userActivities;

    // TODO список участников
//    @ManyToOne(fetch = FetchType.EAGER)
//    @JoinColumn(name = "ACTIVITY_USER_PARTICIPANT_ID")
//    private Set<User> userActivitiesParticipant = new HashSet<>();

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "ACTIVITY_TAG_ID")
    @JsonView(Views.UserShortData.class)
    private Set<Tag> tags = new HashSet<>();

    public Activity(String activityTitle, String activityDescription, User user) {
        this.activityTitle = activityTitle;
        this.activityDescription = activityDescription;
        this.creationDate = LocalDateTime.now();
        this.userActivities = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Activity activity = (Activity) o;
        if (id == null) {
            if (activity.id != null)
                return false;
        } else if (!id.equals(activity.id))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 37;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

}
