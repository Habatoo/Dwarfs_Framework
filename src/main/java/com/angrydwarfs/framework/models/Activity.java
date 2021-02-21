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
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

/**
 * Модель активностей пользователей
 * @version 0.013
 * @author habatoo
 *
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "activity")
@ToString(of = {"id", "activityTitle", "activityBody", "creationDate", "activityCode"})
@EqualsAndHashCode(of = {"id"})
public class Activity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "ActivityIndex cannot be empty")
    private String activityIndex = UUID.randomUUID().toString();

    @JsonView(Views.UserShortData.class)
    @NotBlank(message = "Title cannot be empty")
    private String activityTitle;
    @JsonView(Views.UserShortData.class)
    @NotBlank(message = "Body cannot be empty")
    private String activityBody;

    @Column(updatable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonView(Views.UserShortData.class)
    private LocalDateTime creationDate;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonView(Views.UserShortData.class)
    private User userActivities;

    @JsonIdentityInfo(
            generator = ObjectIdGenerators.PropertyGenerator.class,
            property = "id")
    @OneToMany(mappedBy = "userTags", fetch = FetchType.EAGER, orphanRemoval = true)
    @JsonView(Views.UserShortData.class)
    private Set<Tag> tags;

    public Activity(String activityTitle, String activityBody, User user) {
        this.activityTitle = activityTitle;
        this.activityBody = activityBody;
        this.creationDate = LocalDateTime.now();
        this.userActivities = user;
    }

}
