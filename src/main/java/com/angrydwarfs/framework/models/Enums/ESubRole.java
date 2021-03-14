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

package com.angrydwarfs.framework.models.Enums;

import com.angrydwarfs.framework.models.SubRole;

/**
 * Перечень возможных ролей по пользователя по доступам к сервисам и необходимым полям
 * по предоставлению своих данных.
 * @see SubRole (таблица ролей).
 * @version 0.001
 * @author habatoo
 */
public enum ESubRole {
    COMMON_USER,
    SILVER_USER,
    GOLD_USER;

    public String getSubRoleName() {
        return name();
    }
}
