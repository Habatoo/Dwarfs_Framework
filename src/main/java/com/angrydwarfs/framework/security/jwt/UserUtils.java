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

package com.angrydwarfs.framework.security.jwt;

import com.angrydwarfs.framework.models.User;
import com.angrydwarfs.framework.payload.response.MessageResponse;
import com.angrydwarfs.framework.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UserUtils {
    @Value("${dwarfsframework.app.jwtSecret}")
    private String jwtSecret;

    @Value("${dwarfsframework.app.jwtExpirationMs}")
    private int jwtExpirationMs;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder encoder;

    /**
     * Проверяет username и email на уникальность и отсуствие аналогов в существующей базе
     * @param user - данные пользователя для изменений
     * @param userFromDb - данные пользователя с дб
     */
    public ResponseEntity<?>  checkUserNameAndEmail(User user, User userFromDb) {
        if (!(user.getUsername().equals(userFromDb.getUsername())) & (userRepository.existsByUsername(user.getUsername()))) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }

        if (!(user.getUserEmail().equals(userFromDb.getUserEmail())) & (userRepository.existsByUserEmail(user.getUserEmail()))) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }

        userFromDb.setUsername(user.getUsername());
        userFromDb.setUserEmail(user.getUserEmail());
        userFromDb.setPassword(encoder.encode(user.getPassword()));

        userRepository.save(userFromDb);
        return ResponseEntity.ok(new MessageResponse("User data was update successfully!"));

    }


    //TODO сделать универсальный метод проверки уникальности
    /**
     * Проверяет username и email на уникальность и отсуствие аналогов в существующей базе
     * @param user - данные пользователя для изменений
     */
    public ResponseEntity<?>  checkRegisterUserNameAndEmail(User user) {

        if (userRepository.existsByUsername(
                user.getUsername()
        )) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }

        if (userRepository.existsByUserEmail(user.getUserEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }

        return ResponseEntity
                .ok()
                .body(new MessageResponse("OK"));
    }


}
