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

import com.angrydwarfs.framework.payload.request.FacebookLoginRequest;
import com.angrydwarfs.framework.payload.request.LoginRequest;
import com.angrydwarfs.framework.payload.response.JwtResponse;
import com.angrydwarfs.framework.payload.response.MessageResponse;
import com.angrydwarfs.framework.repository.*;
import com.angrydwarfs.framework.security.jwt.TokenUtils;
import com.angrydwarfs.framework.security.jwt.UserUtils;
import com.angrydwarfs.framework.service.FacebookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collections;
import java.util.Map;

/**
 * Контроллер доступа через социальные сети.
 * @version 0.001
 * @author habatoo
 *
 */

@CrossOrigin(origins = "*", maxAge = 3600)
@SpringBootApplication
@RestController
@RequestMapping("/api/auth/social")
public class SocialNetController {
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

    @GetMapping("/facebook")
    public Map<String, Object> user(@AuthenticationPrincipal OAuth2User principal) {
        return Collections.singletonMap("name", principal.getAttribute("name"));
    }


    /**
     * @method authenticateUser - при http post запросе по адресу .../api/auth/login
     * @param facebookLoginRequest - запрос на доступ с параметрами пользователя facebook по facebook Id.
     * возвращает
     * @return {@code ResponseEntity ответ}
     * @see FacebookLoginRequest
     */
//    @PostMapping("/facebook")
//    public ResponseEntity<?> facebookAuth(@Valid @RequestBody FacebookLoginRequest facebookLoginRequest) {
//        //log.info("facebook login {}", facebookLoginRequest);
//        JwtResponse jwtResponse = facebookService.loginUser(facebookLoginRequest.getAccessToken());
//
//        return ResponseEntity.ok(jwtResponse);
//    }

    /**
     * @method authenticateUser - при http post запросе по адресу .../api/oauth2/login
     * @param loginRequest - запрос на доступ с параметрами user login+password.
     * возвращает
     * @return {@code ResponseEntity ответ}
     * @see LoginRequest
     */
//    @PostMapping("/facebooklogin")
//    public ResponseEntity<?> authenticateOauth2User(@Valid @RequestBody LoginRequest loginRequest) {
//
//        return ResponseEntity.ok(new MessageResponse("Post successfully!"));
//    }
//
//    @GetMapping("/facebook")
//    public ResponseEntity<?> testOauth2User() {
//        return ResponseEntity.ok(new MessageResponse("Get successfully!"));
//    }

//    @GetMapping("/")
//    public ResponseEntity<?> index(Model model,
//                        @RegisteredOAuth2AuthorizedClient OAuth2AuthorizedClient authorizedClient,
//                        @AuthenticationPrincipal OAuth2User oauth2User) {
//        model.addAttribute("userName", oauth2User.getName());
//        model.addAttribute("clientName", authorizedClient.getClientRegistration().getClientName());
//        model.addAttribute("userAttributes", oauth2User.getAttributes());
//        return ResponseEntity.ok(new MessageResponse("Oauth successfully!"));
//    }

}
