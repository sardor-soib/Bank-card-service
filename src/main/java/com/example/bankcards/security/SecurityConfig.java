package com.example.bankcards.security;

import com.example.bankcards.util.Role;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.oauth2.server.resource.web.access.BearerTokenAccessDeniedHandler;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final String ADMIN = Role.ADMIN.getDisplayName();
    private static final String USER = Role.USER.getDisplayName();

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-ui.html"
                        ).permitAll()

                        // Card user actions: authenticated users
                        .requestMatchers(HttpMethod.GET, "/api/v1/users/*/cards/**").hasRole("USER")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/users/*/cards/**").hasRole("USER")

                        // User management: admin only
                        .requestMatchers("/api/v1/users/**").hasRole(ADMIN)

                        // Card admin actions
                        .requestMatchers(HttpMethod.POST, "/api/v1/cards/**").hasRole(ADMIN)
                        .requestMatchers(HttpMethod.PUT, "/api/v1/cards/**").hasRole(ADMIN)
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/cards/**").hasRole(ADMIN)

                        // Card read endpoints: both roles, ownership must be checked in service/controller
                        .requestMatchers(HttpMethod.GET, "/api/v1/cards/**").hasAnyRole(ADMIN, USER)

                        // Transfers: authenticated users; service must check ownership
                        .requestMatchers("/api/v1/transactions/**").hasAnyRole(ADMIN, USER)

                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(new BearerTokenAuthenticationEntryPoint())
                        .accessDeniedHandler(new BearerTokenAccessDeniedHandler())
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));
        return http.build();
    }

}
