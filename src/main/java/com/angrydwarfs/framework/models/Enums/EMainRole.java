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

import com.angrydwarfs.framework.models.MainRole;
import org.springframework.security.core.GrantedAuthority;

/**
 * Перечень возможных ролей по доступу пользователя.
 * @see MainRole (таблица ролей).
 * @version 0.001
 * @author habatoo
 */
public enum EMainRole implements GrantedAuthority {
    ROLE_USER,
    FACEBOOK_USER,
    GOOGLE_USER,
    VK_USER,
    ROLE_MODERATOR,
    ROLE_ADMINISTRATOR;

    @Override
    public String getAuthority() {
        return name();
    }
}
