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

import com.angrydwarfs.framework.models.Enums.ELevel;
import com.angrydwarfs.framework.models.Enums.ETag;
import com.angrydwarfs.framework.models.Tag;
import com.angrydwarfs.framework.models.User;
import com.angrydwarfs.framework.payload.request.UserEditRequest;
import com.angrydwarfs.framework.payload.response.MessageResponse;
import com.angrydwarfs.framework.repository.*;
import com.angrydwarfs.framework.security.jwt.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Контроллер работы с тегами и уровнями тэгов.
 * @version 0.001
 * @author habatoo
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth/tags")
public class TagController {
    @Value("${dwarfsframework.app.remoteAddr}")
    private String remoteAddr;

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;

    @Autowired
    public TagController(
            UserRepository userRepository,
            TokenRepository tokenRepository
    ) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
    }

    @Autowired
    TagRepository tagRepository;

    @Autowired
    LevelRepository levelRepository;

    @Autowired
    MainRoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    UserUtils userUtils;

    /**
     * @method addUserTags - при http PUT запросе по адресу .../api/auth/tags/addTags добавляет тэг 1 уровня для пользователя
     * метод доступен пользоватлю без привелегий, служит указанием интереса пользователя.
     * @param userEditRequest
     * @param authentication
     * @return
     */
    @PutMapping("/addTags")
    @PreAuthorize("hasRole('ROLE_ADMINISTRATOR') or hasRole('ROLE_MODERATOR') or hasRole('ROLE_USER')")
    public ResponseEntity<?> addUserTags(
            @RequestBody UserEditRequest userEditRequest,
            Authentication authentication) {

        User user = userRepository.findByUserName(authentication.getName()).get();
        Set<Tag> tags = new HashSet<>();
        Set<String> strTags = userEditRequest.getTags();
        if (strTags != null) {
            for (String tag : strTags) {
                Tag tempTag = tagRepository.findByTagName(ETag.valueOf(tag)).get();
                tempTag.setTagLevel(levelRepository.findByLevelName(ELevel.FIRST_LEVEL).get());
                tags.add(tempTag);
            }
        }
        user.setTags(tags);
        //userRepository.save(user);
        return ResponseEntity.ok(new MessageResponse("Tags was added successfully!"));

//        // check ID current user = ID edit user
//        User user = userRepository.findByUserName(authentication.getName()).get();
//        if((userRepository.findByUserName(userEditRequest.getUserName()).equals(userRepository.findByUserName(authentication.getName()).get().getId()))) {
//            // admin check
//            if(userRepository.findByUserName(authentication.getName()).get().getMainRoles().size() == 3) {
//                Set<Tag> tags = new HashSet<>();
//                try {
//                    Set<String> strTags = userEditRequest.getTags();
//                    if (strTags != null) {
//                        for (String tag : strTags) {
//                            Tag tempTag = tagRepository.findByTagName(ETag.valueOf(tag)).get();
//                            tempTag.setTagLevel(levelRepository.findByLevelName(ELevel.FIRST_LEVEL).get());
//                            tags.add(tempTag);
//                        }
//                    }
//                    user.setTags(tags);
//                } catch (Exception e) {
//                    return ResponseEntity.ok(new MessageResponse("Tags is not exist!" + e));
//                }
//                //////////////////////////////////
//                userRepository.save(user);
//                return ResponseEntity.ok(new MessageResponse("Tags was added successfully!"));
//            }
//            return ResponseEntity
//                    .badRequest()
//                    .body(new MessageResponse("You can edit only yourself data."));
//        } else {
//            //BeanUtils.copyProperties(user, userRepository.findById(userFromDb.getId()).get(), "id");
//            Set<Tag> tags = new HashSet<>();
//            try {
//                Set<String> strTags = userEditRequest.getTags();
//                if (strTags != null) {
//                    for (String tag : strTags) {
//                        Tag tempTag = tagRepository.findByTagName(ETag.valueOf(tag)).get();
//                        tempTag.setTagLevel(levelRepository.findByLevelName(ELevel.FIRST_LEVEL).get());
//                        tags.add(tempTag);
//                    }
//                }
//
//            } catch (Exception e) {
//                return ResponseEntity
//                        .badRequest()
//                        .body(new MessageResponse("Tags is not exist!" + e));
//            }
//            //////////////////////////////////
//            user.setTags(tags);
//            System.out.println("User " + user.getTags());
//            System.out.println("User " + user.getUserName());
//            System.out.println("User " + user.getUserEmail());
//            System.out.println("User " + user.getMainRoles());
//            System.out.println("User " + user.getCreationDate());
//            //userRepository.save(user);
//            return ResponseEntity.ok(new MessageResponse("Tags was added successfully!"));
//        }

    }
}
