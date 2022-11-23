package com.ensf614.ticketr.securingweb;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import com.ensf614.ticketr.model.AppUserDetailsService;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                .authorizeHttpRequests((requests) -> requests
                                                .antMatchers("/", "/home",
                                                                "/about",
                                                                "/contact",
                                                                "/register",
                                                                "/select/{movieId}",
                                                                "/saveuser",
                                                                "/userinfo",
                                                                "/checkout")
                                                .permitAll()
                                                .anyRequest().authenticated())
                                .formLogin((form) -> form
                                                .loginPage("/login")
                                                .usernameParameter("email")
                                                .successForwardUrl("/")
                                                .permitAll())
                                .logout((logout) -> logout.permitAll());

                return http.build();
        }

        @Bean
        public UserDetailsService userDetailsService() {
                return new AppUserDetailsService();
        }

}