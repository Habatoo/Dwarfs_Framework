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

import com.angrydwarfs.framework.models.Enums.EMainRole;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.*;

import javax.persistence.*;

/**
 * Модель ролей по доступу к данным пользователей и возможности редактирования и удаления данных.
 * @version 0.001
 * @author habatoo
 * @param "id" - primary key таблицы roles.
 * @param "mainRoleName" - наименовение роли.
 * @see EMainRole (перечень возможных ролей пользователя).
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "MAIN_ROLES")
@ToString(of = {"id", "mainRoleName"})
@EqualsAndHashCode(of = {"id"})
public class MainRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="MAIN_ROLE_ID")
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(name="MAIN_ROLE_NAME", length = 20)
    private EMainRole mainRoleName;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="MAIN_ROLE_USER")
    private User mainRoleUser;

    public MainRole(EMainRole mainRoleName) {
        this.mainRoleName = mainRoleName;
    }

}
