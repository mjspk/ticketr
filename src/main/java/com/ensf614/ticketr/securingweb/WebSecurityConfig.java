package com.ensf614.ticketr.securingweb;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import com.ensf614.ticketr.model.AppUserDetailsService;

@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
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
                                                                "/payment",
                                                                "/userinfo",
                                                                "/checkout",
                                                                "/search",
                                                                "/news",
                                                                "/cancel",
                                                                "/ticket/cancel",
                                                                "/myticketsguest")
                                                .permitAll()
                                                .anyRequest().authenticated())
                                .formLogin((form) -> form
                                                .loginPage("/login")
                                                .usernameParameter("email")
                                                .successHandler(new MyAuthenticationSuccessHandler())
                                                .permitAll())
                                .logout((logout) -> logout.logoutUrl("/logout")
                                                .logoutSuccessUrl("/login").permitAll());

                return http.build();
        }

        @Bean
        public UserDetailsService userDetailsService() {
                return new AppUserDetailsService();
        }

}