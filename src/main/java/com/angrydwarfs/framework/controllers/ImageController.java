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

import com.angrydwarfs.framework.models.Activity;
import com.angrydwarfs.framework.models.UserPackage.User;
import com.angrydwarfs.framework.repository.ActivityRepository;
import com.angrydwarfs.framework.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;


// TODO file size control, tests
/**
 * Контроллер работы с изображениями.
 * @version 0.001
 * @author habatoo
 */
@RestController
@RequestMapping("/api/auth/img")
public class ImageController {

    //@Value("${upload.path}")
    private String path = "/home/habatoo/IdeaProjects/Dwarfs_Framework/";

    private final UserRepository userRepository;
    private final ActivityRepository activityRepository;

    @Autowired
    public ImageController(
            UserRepository userRepository,
            ActivityRepository activityRepository
    ) {
        this.userRepository = userRepository;
        this.activityRepository = activityRepository;
    }

    private String fileUpload(MultipartFile file) throws IOException {
        if (file != null && !file.getOriginalFilename().isEmpty()) {
            File uploadDir = new File(path);

            if (!uploadDir.exists()) {
                uploadDir.mkdir();
            }

            String uuidFile = UUID.randomUUID().toString();
            String resultFilename = uuidFile + "." + file.getOriginalFilename();

            file.transferTo(new File(path + "/" + resultFilename));
            return resultFilename;

        } else { return null; }
    }

    @PostMapping("/avatar")
    public void addAvatar(
            Authentication authentication,
            @RequestParam("file") MultipartFile file
    ) throws IOException {
        User user = userRepository.findByUsername(authentication.getName()).get();
        String resultFilename;

        resultFilename = fileUpload(file);
        if (resultFilename != null) {
            user.setAvatarFileName(resultFilename);
        }

        userRepository.save(user);
    }

    @PostMapping("/image")
    public void addActivityImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam("id") Activity activity
    ) throws IOException {
        String resultFilename;

        resultFilename = fileUpload(file);
        if (resultFilename != null) {
            activity.setActivityFileName(resultFilename);
        }

        activityRepository.save(activity);
    }
}
