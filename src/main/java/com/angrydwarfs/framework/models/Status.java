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

import com.angrydwarfs.framework.models.Enums.EStatus;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.*;

import javax.persistence.*;

/**
 * Модель текущего статуса пользователя с указанием срока начала и окончания действия статуса
 * @version 0.001
 * @author habatoo
 *
 */
@Entity
@Getter
@Setter
@Table(name = "STATUS")
@ToString(of = {"id", "statusName", "activationDate", "endDate"})
@EqualsAndHashCode(of = {"id"})
public class Status {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "STATUS_ID")
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(name = "USER_STATUS", length = 20)
    private EStatus userStatus;

    public Status() {
        this.userStatus = EStatus.COMMON;
    }

    public Status(EStatus userStatus) {
        this.userStatus = userStatus;
    }

    @Override
    public String toString() {
        return this.userStatus.toString();
    }

}
