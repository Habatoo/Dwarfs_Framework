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
import com.angrydwarfs.framework.models.Enums.ETag;
import com.angrydwarfs.framework.payload.request.ActivityRequest;
import com.angrydwarfs.framework.payload.request.SignupRequest;
import com.angrydwarfs.framework.payload.response.MessageResponse;
import com.angrydwarfs.framework.repository.*;
import com.angrydwarfs.framework.security.jwt.UserUtils;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Контроллер работы с activity пользователя.
 * @version 0.001
 * @author habatoo
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth/users/activities")
public class ActivityController {
    @Value("${dwarfsframework.app.remoteAddr}")
    private String remoteAddr;

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final ActivityRepository activityRepository;

    @Autowired
    public ActivityController(
            UserRepository userRepository,
            TokenRepository tokenRepository,
            ActivityRepository activityRepository
    ) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.activityRepository = activityRepository;
    }

    @Autowired
    MainRoleRepository roleRepository;

    @Autowired
    TagRepository tagRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    UserUtils userUtils;

    /**
     * @method userActivityList - при http GET запросе по адресу .../api/auth/users/activity
     * @return {@code List<activities>} - список всех активностей пользователя с данными пользователя.
     * @see User
     */
    @GetMapping
    @ResponseBody
    @JsonView(Views.UserShortData.class)
    public ResponseEntity<?> userActivityList(Authentication authentication) {

        Optional optionalUser = userRepository.findByUserName(authentication.getName());
        if(!optionalUser.isPresent()) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: can not find user data."));
        }

        return ResponseEntity.ok(activityRepository.findByUserActivities(userRepository.findByUserName(authentication.getName()).get()));
    }

    /**
     * Выбор activity по его id при GET запросе по адресу .../api/auth/users/activities/id
     * @param activity
     * @return
     */
    @GetMapping("{id}")
    @ResponseBody
    @JsonView(Views.UserShortData.class)
    public ResponseEntity<?> userOneActivity(@PathVariable("id") Activity activity) {
        return ResponseEntity.ok(activityRepository.findById(activity.getId()));
    }

    //TODO доделать тест тэгов
    /**
     * Создание activity при POST запросе по адресу .../api/auth/users/activities/newActivity
     * @param activityRequest
     * @param authentication
     * @return
     */
    @PostMapping("/newActivity")
    public ResponseEntity<?> createNewActivity(
            @Valid @RequestBody ActivityRequest activityRequest,
            Authentication authentication,
            HttpServletRequest request) {

        User user = userRepository.findByUserName(authentication.getName()).get();
        Activity activity = new Activity(activityRequest.getActivityTitle(), activityRequest.getActivityBody(), user);
        activity.setCreationDate(activityRequest.getCreationDate());

        ////////////////////////////////// tags
        Set<Tag> tags = new HashSet<>();
        try {
            Set<String> strTags = activityRequest.getTags();

            if (strTags != null) {
                for (String tag : strTags) {
                    tags.add(tagRepository.findByTagName(ETag.valueOf(tag)).get());
                }
                activity.setTags(tags);
            }
        } catch (Exception e) {
            return ResponseEntity.ok(new MessageResponse("Tags is not exist!" + e));
        }
        //////////////////////////////////
        activityRepository.save(activity);

        return ResponseEntity.ok(new MessageResponse("Activity create successfully!"));
    }

    //TODO рефакторить повторение редактирования
    /**
     * Редактирование activity, доступно только автору activity и пользователям с ролью MOD, ADMIN
     * @param activityFromDb
     * @param activity
     * @param authentication
     * @return
     */
    @PutMapping("{id}")
    @PreAuthorize("hasRole('ROLE_ADMINISTRATOR') or hasRole('ROLE_MODERATOR') or hasRole('ROLE_USER')")
    public ResponseEntity<?> changeActivity(
            @PathVariable("id") Activity activityFromDb,
            @RequestBody Activity activity,
            Authentication authentication) {

        activityFromDb = activityRepository.findById(activityFromDb.getId()).get();
        // check ID current user = ID edit user
        if(!(authentication.getName().equals(activityFromDb.getUserActivities().getUserName()))) {
            // admin check
            if(userRepository.findByUserName(authentication.getName()).get().getMainRoles().size() >= 3) {
                //BeanUtils.copyProperties(activity, activityFromDb, "id");
                activityFromDb.setActivityTitle(activity.getActivityTitle());
                activityFromDb.setActivityBody(activity.getActivityBody());
                activityRepository.save(activityFromDb);
                return ResponseEntity.ok(new MessageResponse("Activity was update successfully!"));
            }
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("You can edit only yourself data!"));
        } else {
            //BeanUtils.copyProperties(activity, activityFromDb, "id");
            activityFromDb.setActivityTitle(activity.getActivityTitle());
            activityFromDb.setActivityBody(activity.getActivityBody());
            activityRepository.save(activityFromDb);
            return ResponseEntity.ok(new MessageResponse("Activity was update successfully!"));
        }

    }

    //TODO рефакторить повторение удаления
    /**
     * Удаление activity пользователя, доступно только автору activity и пользователям с ролью MOD, ADMIN
     * @param activity
     * @return
     */
    @DeleteMapping("{id}")
    @PreAuthorize("hasRole('ROLE_ADMINISTRATOR') or hasRole('ROLE_MODERATOR') or hasRole('ROLE_USER')")
    public ResponseEntity<?>  deleteUser(@PathVariable("id") Activity activity, Authentication authentication) {

        // check ID current user = ID edit user
        if(!(authentication.getName().equals(activity.getUserActivities().getUserName()))) {
            // admin check
            if(userRepository.findByUserName(authentication.getName()).get().getMainRoles().size() >= 3) {
                try {
                    activityRepository.delete(activity);
                    return ResponseEntity.ok(new MessageResponse("Activity was deleted successfully!"));
                } catch (Exception e) {
                    return ResponseEntity
                            .badRequest()
                            .body(new MessageResponse("Error: Activity was not deleted!"));
                }
            }
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("You can delete only yourself data!"));
        } else {
            try {
                activityRepository.delete(activity);
                return ResponseEntity.ok(new MessageResponse("Activity was deleted successfully!"));
            } catch (Exception e) {
                return ResponseEntity
                        .badRequest()
                        .body(new MessageResponse("Error: Activity was not deleted!"));
            }
        }
    }
}
