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

import com.angrydwarfs.framework.models.Token;
import com.angrydwarfs.framework.payload.response.JwtResponse;
import com.angrydwarfs.framework.repository.TokenRepository;
import com.angrydwarfs.framework.repository.UserRepository;
import com.angrydwarfs.framework.security.services.UserDetailsImpl;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class TokenUtils {
    @Value("${dwarfsframework.app.jwtSecret}")
    private String jwtSecret;

    @Value("${dwarfsframework.app.jwtExpirationMs}")
    private int jwtExpirationMs;

    @Autowired
    TokenRepository tokenRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtUtils jwtUtils;

    /**
     * Записывет в таблицу Token значения токена с датой создания и срока действи токена
     * @param userName - имя пользователя
     * @param strToken - токен - генерируется из имени пользователя и пароля
     */
    public void makeToken(String userName, String strToken) {
        Token token = new Token(strToken, userRepository.findByUsername(userName).get());
        token.setActive(true);
        Date date = new Date();
        LocalDateTime createDate = Instant.ofEpochMilli(date.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        LocalDateTime expireDate = Instant.ofEpochMilli(date.getTime() + jwtExpirationMs)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        token.setCreationDate(createDate);
        token.setExpiryDate(expireDate);

        token.setUserTokens(userRepository.findByUsername(userName).get());
        tokenRepository.save(token);
    }

    /**
     * Записывет в таблицу Token значения токена с датой создания и срока действи токена гарантированной старше текущей даты
     * Нужно для тестирования очистки токенов с истекшим сроком
     * @param userName
     * @param password
     */
    public void makeOldToken(String userName, String password) {

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String strStartDate = "2016-03-04 11:30:00";
        String strExpDate = "2016-05-04 11:30:00";
        try {
            Date dateStartDate = formatter.parse(strStartDate);
            Date dateExpDate = formatter.parse(strExpDate);

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userName,password));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
            String jwt = Jwts.builder().setSubject((userPrincipal.getUsername())).setIssuedAt(dateStartDate)
                    .setExpiration(dateExpDate).signWith(SignatureAlgorithm.HS512, jwtSecret)
                    .compact();

            Token token = new Token(jwt, userRepository.findByUsername(userName).get());
            token.setActive(true);

            DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            token.setCreationDate(LocalDateTime.parse(strStartDate, formatter2));
            token.setExpiryDate(LocalDateTime.parse(strExpDate, formatter2));
            token.setUserTokens(userRepository.findByUsername(userName).get());
            tokenRepository.save(token);
        } catch (Exception e) {

        }

    }

    public JwtResponse makeAuth(String username, String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        return new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles);
    }
}

