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
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Модель токенов пользователей с указанием статуса токена и срока его действия
 * @version 0.001
 * @author habatoo
 *
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "TOKENS")
@ToString(of = {"id", "token", "creationDate", "expiryDate", "active"})
@EqualsAndHashCode(of = {"id"})
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TOKEN_ID")
    private Long id;

    @Column(name = "TOKEN", nullable = false, unique = true, length = 500)
    private String token;

    @Column(name = "TOKEN_CREATION_DATE", updatable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime creationDate;

    @Column(name = "TOKEN_EXPIRY_DATE", updatable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expiryDate;

    @Column(name = "TOKEN_STATUS")
    private boolean active;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TOKEN_USER_ID")
    private User userTokens;

    public Token(String token, User userTokens) {
        this.token = token;
        this.userTokens = userTokens;
    }

}
