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

/**
 * Уровни видимости для пользователя USER с разной степенью детализации
 * Уровни видимости для пользователей с разными уровнями доступа USER, MODERATOR, ADMINISTRATOR
 */
public final class Views {
    public interface UserShortData {}

    public interface UserMiddleData extends UserShortData {}

    public interface UserAllData extends UserMiddleData {}

    public interface ModData extends UserAllData {}

    public interface AdminData extends ModData {}

}
