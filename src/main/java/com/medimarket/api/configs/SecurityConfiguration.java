package com.medimarket.api.configs;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final AuthenticationProvider authProvider;
    private final JwtAuthenticationFilter jwtAuthFilter;
    public static final String[] UN_SECRET_URLS = {
            "/api/auth/**"
    };
    public static final String[] ADMIN_SECRET_URLS = {
            "/api/home/updateInfo/{id}"
    };

    public static final String[] USER_SECRET_URLS = {
            "/api/home/profile"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((reqs) -> reqs
                        .requestMatchers(UN_SECRET_URLS)
                        .permitAll()
                        .requestMatchers(ADMIN_SECRET_URLS)
                        .hasAuthority("USER")
//                        .requestMatchers(USER_SECRET_URLS)
//                        .hasAuthority("USER")
                        .anyRequest()
                        .authenticated())
                .sessionManagement((reqs) -> reqs
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
