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

package com.angrydwarfs.framework.controllers;

import com.angrydwarfs.framework.models.*;
import com.angrydwarfs.framework.models.Enums.EMainRole;
import com.angrydwarfs.framework.models.Enums.EStatus;
import com.angrydwarfs.framework.models.Enums.ESubRole;
import com.angrydwarfs.framework.models.UserPackage.Status;
import com.angrydwarfs.framework.models.UserPackage.User;
import com.angrydwarfs.framework.payload.request.LoginRequest;
import com.angrydwarfs.framework.payload.request.SignupRequest;
import com.angrydwarfs.framework.payload.response.JwtResponse;
import com.angrydwarfs.framework.payload.response.MessageResponse;
import com.angrydwarfs.framework.repository.*;
import com.angrydwarfs.framework.security.jwt.TokenUtils;
import com.angrydwarfs.framework.security.jwt.UserUtils;
import com.angrydwarfs.framework.service.FacebookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Контроллер доступа.
 * @version 0.001
 * @author habatoo
 *
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    UserRepository userRepository;

    @Autowired
    MainRoleRepository mainRoleRepository;

    @Autowired
    SubRoleRepository subRoleRepository;

    @Autowired
    StatusRepository statusRepository;

    @Autowired
    TokenRepository tokenRepository;

    @Autowired
    TokenUtils tokenUtils;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    UserUtils userUtils;

    @Autowired
    FacebookService facebookService;

    @Value("${dwarfsframework.app.secretKey}")
    private String secretKey;

    @Value("${dwarfsframework.app.jwtExpirationMs}")
    private int jwtExpirationMs;

    //TODO сделать фильтрацию возможности создания пользователей с ролями админ и мод
    /**
     * @method registerUser - при http POST запросе по адресу .../api/auth/register
     * @param signUpRequest - входные данные по текущему аутентифицированному пользователю
     * возвращает данные
     * @return {@code ResponseEntity.ok - User registered successfully!} - ок при успешной регистрации.
     * @return {@code ResponseEntity.badRequest - Error: Role is not found.} - ошибка при указании неправильной роли.
     * @return {@code ResponseEntity.badRequest - Error: Username is already taken!} - ошибка при дублировании username при регистрации.
     * @return {@code ResponseEntity.badRequest - Error: Email is already in use!} - ошибка при дублировании email при регистрации.
     * @see ResponseEntity
     * @see SignupRequest
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest, HttpServletRequest request) {

        if (userRepository.existsByUsername(
                signUpRequest.getUserName()
        )) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }

        if (userRepository.existsByUserEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }

        // Create new user's account
        User user = new User(
                signUpRequest.getUserName(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword())
        );

        Set<String> strRoles = signUpRequest.getRole();
        Set<MainRole> roles = new HashSet<>();

        if (strRoles == null) {
            MainRole userRole = mainRoleRepository.findByMainRoleName(EMainRole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        MainRole adminRole = mainRoleRepository.findByMainRoleName(EMainRole.ROLE_ADMINISTRATOR)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);
                        break;

                    case "mod":
                        MainRole modRole = mainRoleRepository.findByMainRoleName(EMainRole.ROLE_MODERATOR)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(modRole);
                        break;

                    default:
                        MainRole userRole = mainRoleRepository.findByMainRoleName(EMainRole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);
                }
            });
        }

        user.setMainRoles(roles);
        Set<SubRole> subRoleSet = new HashSet<>();
        subRoleSet.add(subRoleRepository.findBySubRoleName(ESubRole.COMMON_USER).get());
        user.setSubRoles(subRoleSet);
        user.setCreationDate(LocalDateTime.now());

        Set<Status> statusSet = new HashSet<>();
        statusSet.add(statusRepository.findByUserStatus(EStatus.COMMON).get());
        user.setUserStatus(statusSet);
        user.setStatusStartDate(LocalDateTime.now());
        user.setStatusEndDate(null);

        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

    /**
     * @method authenticateUser - при http post запросе по адресу .../api/auth/login
     * @param loginRequest - запрос на доступ с параметрами user login+password.
     * возвращает
     * @return {@code ResponseEntity ответ}
     * @see LoginRequest
     */
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        JwtResponse jwtResponse = tokenUtils.makeAuth(loginRequest.getUserName(), loginRequest.getPassword());
        tokenUtils.makeToken(loginRequest.getUserName(), jwtResponse.getToken());
        User user = userRepository.findByUsername(loginRequest.getUserName()).get();
        user.setLastVisitedDate(LocalDateTime.now());
        userRepository.save(user);
        return ResponseEntity.ok(jwtResponse);
    }

    /**
     * @method logoutUser - при http post запросе по адресу .../api/auth/logout
     * @param request - запрос на выход с параметрами user login+password + токен jwt.
     * возвращает
     * @return {@code ResponseEntity ответ}
     * @see LoginRequest
     */
    @GetMapping("/logout")
    @PreAuthorize("hasRole('ROLE_MODERATOR') or hasRole('ROLE_ADMINISTRATOR') or hasRole('ROLE_USER')")
    public ResponseEntity<?> logoutUser(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        String jwt = headerAuth.substring(7, headerAuth.length());

        Token unActiveToken = tokenRepository.findByToken(jwt);
        unActiveToken.setActive(false);
        tokenRepository.save(unActiveToken);

        return ResponseEntity
                .badRequest()
                .body(new MessageResponse("You are logout."));
    }

//    /**
//     * @method resetPassword - при http post запросе по адресу .../api/auth/reset
//     * @param passwordRequest - запрос на сброс пароля с параметрами user login+sekretKey (хранится в env).
//     * возвращает
//     * @return {@code ResponseEntity ответ}
//     * @see LoginRequest
//     */
//    @PostMapping("/reset")
//    public ResponseEntity<?> resetPassword(@Valid @RequestBody PasswordRequest passwordRequest) {
//
//        if (!userRepository.existsByUserName(
//                passwordRequest.getUserName()
//        )) {
//            return ResponseEntity
//                    .badRequest()
//                    .body(new MessageResponse("Error: Username does not exist!"));
//        }
//
//        if (!secretKey.equals(passwordRequest.getSecretKey())) {
//            return ResponseEntity
//                    .badRequest()
//                    .body(new MessageResponse("Error: SecretKey does not valid!"));
//        }
//
//        User user = userRepository.findByUserName(passwordRequest.getUserName()).get();
//        user.setPassword(encoder.encode(passwordRequest.getPassword()));
//        userRepository.save(user);
//
//        return ResponseEntity.ok(new PasswordResponse(
//                passwordRequest.getUserName(),
//                passwordRequest.getPassword()
//        ));
//    }

}
