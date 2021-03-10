///*
// * Copyright 2021 the original author or authors.
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *      https://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package com.angrydwarfs.framework.service;
//
//import com.angrydwarfs.framework.exceptions.InternalServerException;
//import com.angrydwarfs.framework.models.FacebookUser;
//import com.angrydwarfs.framework.models.MainRole;
//import com.angrydwarfs.framework.models.Profile;
//import com.angrydwarfs.framework.models.User;
//import com.angrydwarfs.framework.security.jwt.JwtUtils;
//import com.angrydwarfs.framework.service.client.FacebookClient;
//import lombok.var;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.Optional;
//import java.util.Random;
//
//@Service
//public class FacebookService {
//    @Autowired
//    private FacebookClient facebookClient;
//    @Autowired
//    private UserService userService;
//    @Autowired
//    private JwtUtils tokenProvider;
//
//    public String loginUser(String fbAccessToken) {
////        var facebookUser = facebookClient.getUser(fbAccessToken);
//        FacebookUser facebookUser = facebookClient.getUser(fbAccessToken);
//
////        var arrayList = new ArrayList<String>();
////        ArrayList<String> arrayList = new ArrayList<>();
//
//        return userService(facebookUser.getId())
//                .or(() -> Optional.ofNullable(userService.registerUser(convertTo(facebookUser), MainRole.FACEBOOK_USER)))
//                .map(InstaUserDetails::new)
//                .map(userDetails -> new UsernamePasswordAuthenticationToken(
//                        userDetails, null, userDetails.getAuthorities()))
//                .map(tokenProvider::generateJwtToken)
//                .orElseThrow(() ->
//                        new InternalServerException("unable to login facebook user id " + facebookUser.()));
//    }
//
//    private User convertTo(FacebookUser facebookUser) {
//        return User.builder()
//                .id(new Long(facebookUser.getId()))
//                .userEmail(facebookUser.getEmail())
//                .userName(generateUsername(facebookUser.getFirstName(), facebookUser.getLastName()))
//                .password(generatePassword(8))
//                .userProfile(Profile.builder()
//                        .displayName(String
//                                .format("%s %s", facebookUser.getFirstName(), facebookUser.getLastName()))
//                        //.profilePictureUrl(facebookUser.getPicture().getData().getUrl())
//                        .build())
//                .build();
//    }
//
//    private String generateUsername(String firstName, String lastName) {
//        Random rnd = new Random();
//        int number = rnd.nextInt(999999);
//
//        return String.format("%s.%s.%06d", firstName, lastName, number);
//    }
//
//    private String generatePassword(int length) {
//        String capitalCaseLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
//        String lowerCaseLetters = "abcdefghijklmnopqrstuvwxyz";
//        String specialCharacters = "!@#$";
//        String numbers = "1234567890";
//        String combinedChars = capitalCaseLetters + lowerCaseLetters + specialCharacters + numbers;
//        Random random = new Random();
//        char[] password = new char[length];
//
//        password[0] = lowerCaseLetters.charAt(random.nextInt(lowerCaseLetters.length()));
//        password[1] = capitalCaseLetters.charAt(random.nextInt(capitalCaseLetters.length()));
//        password[2] = specialCharacters.charAt(random.nextInt(specialCharacters.length()));
//        password[3] = numbers.charAt(random.nextInt(numbers.length()));
//
//        for(int i = 4; i< length ; i++) {
//            password[i] = combinedChars.charAt(random.nextInt(combinedChars.length()));
//        }
//        return new String(password);
//    }
//}
