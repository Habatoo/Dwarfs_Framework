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

package com.angrydwarfs.framework.service;

import com.angrydwarfs.framework.exceptions.InternalServerException;
import com.angrydwarfs.framework.models.Enums.EMainRole;
import com.angrydwarfs.framework.models.Enums.EStatus;
import com.angrydwarfs.framework.models.Enums.ESubRole;
import com.angrydwarfs.framework.models.*;
import com.angrydwarfs.framework.payload.response.JwtResponse;
import com.angrydwarfs.framework.repository.*;
import com.angrydwarfs.framework.security.jwt.JwtUtils;
import com.angrydwarfs.framework.security.jwt.TokenUtils;
import com.angrydwarfs.framework.security.services.UserDetailsServiceImpl;
import com.angrydwarfs.framework.service.client.FacebookClient;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class FacebookService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    private FacebookClient facebookClient;

    @Autowired
    MainRoleRepository mainRoleRepository;

    @Autowired
    StatusRepository statusRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    SubRoleRepository subRoleRepository;

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    TokenUtils tokenUtils;

    @Autowired
    TokenRepository tokenRepository;

    public JwtResponse loginUser(String fbAccessToken) {
        var facebookUser = facebookClient.getUser(fbAccessToken);

        if (facebookUser.getId() != null) {
            throw new InternalServerException("Unable to login facebook user id " + facebookUser.getFirstName() + " " + facebookUser.getLastName());
        } else {
            List newUserAndToken = convertTo(facebookUser);
            User user = (User) newUserAndToken.get(0);
            JwtResponse jwt =  (JwtResponse) newUserAndToken.get(1);

            return jwt;
        }
    }

    private List<Object> convertTo(FacebookUser facebookUser) {
        // Create new user's account
        String password = generatePassword(8);
        User user = new User(
                generateUsername(facebookUser.getFirstName(), facebookUser.getLastName()),
                facebookUser.getEmail(),
                encoder.encode(password)
        );

        Set<MainRole> roles = new HashSet<>();
        MainRole userRole = mainRoleRepository.findByMainRoleName(EMainRole.ROLE_USER).get();
        roles.add(userRole);
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
        user.setSocialNetId(facebookUser.getId());

        userRepository.save(user);
        List<Object> newUserAndToken  = new ArrayList<>();
        newUserAndToken.add(user);
        newUserAndToken.add(tokenUtils.makeAuth(user.getUsername(), password));

        return newUserAndToken;
    }

    private String generateUsername(String firstName, String lastName) {
        Random rnd = new Random();
        int number = rnd.nextInt(999999);

        return String.format("%s.%s.%06d", firstName, lastName, number);
    }

    private String generatePassword(int length) {
        String capitalCaseLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowerCaseLetters = "abcdefghijklmnopqrstuvwxyz";
        String specialCharacters = "!@#$";
        String numbers = "1234567890";
        String combinedChars = capitalCaseLetters + lowerCaseLetters + specialCharacters + numbers;
        Random random = new Random();
        char[] password = new char[length];

        password[0] = lowerCaseLetters.charAt(random.nextInt(lowerCaseLetters.length()));
        password[1] = capitalCaseLetters.charAt(random.nextInt(capitalCaseLetters.length()));
        password[2] = specialCharacters.charAt(random.nextInt(specialCharacters.length()));
        password[3] = numbers.charAt(random.nextInt(numbers.length()));

        for(int i = 4; i< length ; i++) {
            password[i] = combinedChars.charAt(random.nextInt(combinedChars.length()));
        }
        return new String(password);
    }
}
