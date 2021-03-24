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

package com.angrydwarfs.framework.config;

import com.angrydwarfs.framework.models.UserPackage.User;
import com.angrydwarfs.framework.repository.UserRepository;
import com.angrydwarfs.framework.security.jwt.AuthEntryPointJwt;
import com.angrydwarfs.framework.security.jwt.AuthTokenFilter;
import com.angrydwarfs.framework.security.services.UserDetailsServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.boot.autoconfigure.security.oauth2.resource.PrincipalExtractor;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import javax.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
@EnableOAuth2Sso
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
//    @Autowired
//    OAuth2ClientContext oauth2ClientContext;

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    @Override
    public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    //@Bean(BeanIds.AUTHENTICATION_MANAGER)
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


//    @Bean
//    public CorsFilter corsFilter() {
//        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        final CorsConfiguration config = new CorsConfiguration();
//        config.setAllowCredentials(true);
//        config.addAllowedOrigin("*");
//        config.addAllowedHeader("*");
//        config.addAllowedMethod("OPTIONS");
//        config.addAllowedMethod("HEAD");
//        config.addAllowedMethod("GET");
//        config.addAllowedMethod("PUT");
//        config.addAllowedMethod("POST");
//        config.addAllowedMethod("DELETE");
//        config.addAllowedMethod("PATCH");
//        source.registerCorsConfiguration("/**", config);
//        return new CorsFilter(source);
//    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .mvcMatchers("/").permitAll()
                .anyRequest().authenticated()
                .and()
                .csrf().disable();
    }

    @Bean
    public PrincipalExtractor principalExtractor(UserRepository userDetailsRepo) {
        return map -> {
            return new User();
        };
    }
}
////@SpringBootApplication
////@EnableOAuth2Client
//@RestController
////@EnableResourceServer
//
//@Configuration
//@EnableWebSecurity
//@EnableGlobalMethodSecurity(
//        // securedEnabled = true,
//        // jsr250Enabled = true,
//        prePostEnabled = true)
//public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
////    @Autowired
////    OAuth2ClientContext oauth2ClientContext;
//
////    @Autowired
////    UserDetailsServiceImpl userDetailsService;
////
////    @Autowired
////    private AuthEntryPointJwt unauthorizedHandler;
////
////    @Bean
////    public AuthTokenFilter authenticationJwtTokenFilter() {
////        return new AuthTokenFilter();
////    }
////
////    @Override
////    public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
////        authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
////    }
////
////    //@Bean(BeanIds.AUTHENTICATION_MANAGER)
////    @Bean
////    @Override
////    public AuthenticationManager authenticationManagerBean() throws Exception {
////        return super.authenticationManagerBean();
////    }
////
////    @Bean
////    public PasswordEncoder passwordEncoder() {
////        return new BCryptPasswordEncoder();
////    }
////
////
////    @Bean
////    public CorsFilter corsFilter() {
////        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
////        final CorsConfiguration config = new CorsConfiguration();
////        config.setAllowCredentials(true);
////        config.addAllowedOrigin("*");
////        config.addAllowedHeader("*");
////        config.addAllowedMethod("OPTIONS");
////        config.addAllowedMethod("HEAD");
////        config.addAllowedMethod("GET");
////        config.addAllowedMethod("PUT");
////        config.addAllowedMethod("POST");
////        config.addAllowedMethod("DELETE");
////        config.addAllowedMethod("PATCH");
////        source.registerCorsConfiguration("/**", config);
////        return new CorsFilter(source);
////    }
////
////    @Override
////    protected void configure(HttpSecurity http) throws Exception {
////        http
////                .cors().and()
////                .csrf().disable()
////                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
////                .and()
////                .exceptionHandling().authenticationEntryPoint((req, rsp, e) -> rsp.sendError(HttpServletResponse.SC_UNAUTHORIZED))
////                .and()
////                //.addFilterBefore(new JwtTokenAuthenticationFilter(jwtConfig, tokenProvider, userService), UsernamePasswordAuthenticationFilter.class)
////                .addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class)
////                .authorizeRequests()
////                .antMatchers(HttpMethod.POST, "/api/auth/register").permitAll()
////                .antMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
////                .antMatchers(HttpMethod.POST, "/api/auth/social").permitAll()
////                //.antMatchers(HttpMethod.POST, "/users").anonymous()
////                .anyRequest().authenticated();
////    }
//
////    @Override
////    protected void configure(HttpSecurity http) throws Exception {
////        http.cors().and().csrf().disable()
////                .exceptionHandling().authenticationEntryPoint(unauthorizedHandler)
////                .and()
////                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
////                .and()
////                .authorizeRequests()
////                .antMatchers("/").permitAll()
////                .antMatchers("/api/auth/**").permitAll()
////                .anyRequest().authenticated();
////
////        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
////    }
//
///////////////
/////// Oauth2
///////////////
//
////    private OAuth2ClientAuthenticationProcessingFilter makeNetFilter(String socialNet) {
////        AuthorizationCodeResourceDetails authorizationCodeResourceDetails;
////        ResourceServerProperties resourceServerProperties;
////
////        OAuth2ClientAuthenticationProcessingFilter netFilter = new OAuth2ClientAuthenticationProcessingFilter(
////                "/connect/" + socialNet);
////        if(socialNet == "facebook") {
////            authorizationCodeResourceDetails = facebook();
////            resourceServerProperties = facebookResource();
////        } else if (socialNet == "google") {
////            authorizationCodeResourceDetails = google();
////            resourceServerProperties = googleResource();
////        } else { return null; }
////        OAuth2RestTemplate netTemplate = new OAuth2RestTemplate(authorizationCodeResourceDetails, oauth2ClientContext);
////        netFilter.setRestTemplate(netTemplate);
////        UserInfoTokenServices tokenServices = new UserInfoTokenServices(resourceServerProperties.getUserInfoUri(),
////                facebook().getClientId());
////        tokenServices.setRestTemplate(netTemplate);
////        netFilter.setTokenServices(tokenServices);
////
////        System.out.println("Oauth2SecurityConfig.makeNetFilter " + netFilter);
////
////        return netFilter;
////    }
////
////    private Filter ssoFilter() {
////        CompositeFilter filter = new CompositeFilter();
////        List<Filter> filters = new ArrayList<>();
////
////        OAuth2ClientAuthenticationProcessingFilter facebookFilter = makeNetFilter("facebook");
////        OAuth2ClientAuthenticationProcessingFilter googleFilter = makeNetFilter("google");
////        filters.add(facebookFilter);
////        filters.add(googleFilter);
////        filter.setFilters(filters);
////
////        return filter;
////    }
////
////    @Bean
////    @ConfigurationProperties("facebook.client")
////    public AuthorizationCodeResourceDetails facebook() {
////        return new AuthorizationCodeResourceDetails();
////    }
////
////    @Bean
////    @ConfigurationProperties("facebook.resource")
////    public ResourceServerProperties facebookResource() {
////        return new ResourceServerProperties();
////    }
////
////    @Bean
////    @ConfigurationProperties("google.client")
////    public AuthorizationCodeResourceDetails google() {
////        return new AuthorizationCodeResourceDetails();
////    }
////
////    @Bean
////    @ConfigurationProperties("google.resource")
////    public ResourceServerProperties googleResource() {
////        return new ResourceServerProperties();
////    }
////
////    @Bean
////    public FilterRegistrationBean oauth2ClientFilterRegistration(OAuth2ClientContextFilter filter) {
////        FilterRegistrationBean registration = new FilterRegistrationBean();
////        registration.setFilter(filter);
////        registration.setOrder(-100);
////        return registration;
////    }
//
//}
