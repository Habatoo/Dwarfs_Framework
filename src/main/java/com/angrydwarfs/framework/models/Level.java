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

import com.angrydwarfs.framework.models.Enums.ELevel;
import lombok.*;

import javax.persistence.*;

/**
 * Уровни тэгов пользователя. Записывается в БД в таблицу с имененм level.
 * @version 0.001
 * @author habatoo
 *
 * @param "id" - primary key таблицы users.
 * @param "levelName" - наименование уровня - связь с значениями в
 * @see ELevel (значения уровня).
 * @param "tagLevel" - связь с таблицей тэгов.
 * @see Tag (пользователи).
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "LEVELS")
@ToString(of = {"id", "levelName"})
@EqualsAndHashCode(of = {"id"})
public class Level {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="LEVEL_ID")
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(name = "LEVEL_NAME", length = 20)
    private ELevel levelName;

    @OneToOne(mappedBy = "tagLevel", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Tag levelTag;

    public Level(ELevel levelName) {
        this.levelName = levelName;
    }

    @Override
    public String toString() {
        return this.levelName.toString();
    }
}
