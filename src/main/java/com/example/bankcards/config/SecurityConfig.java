package com.example.bankcards.config;

import com.example.bankcards.security.JwtToUserAuthenticationConverter;
import com.example.bankcards.util.Role;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.oauth2.server.resource.web.access.BearerTokenAccessDeniedHandler;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private static final String ADMIN = Role.ADMIN.name();
    private static final String USER = Role.USER.name();

    private final JwtToUserAuthenticationConverter jwtToUserAuthenticationConverter;

    public SecurityConfig(JwtToUserAuthenticationConverter jwtToUserAuthenticationConverter) {
        this.jwtToUserAuthenticationConverter = jwtToUserAuthenticationConverter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-ui.html",
                                "/api/v1/auth/**" // Allow access to authentication endpoints
                        ).permitAll()

                        // Card user actions & user transactions
                        .requestMatchers("/api/v1/users/me/cards/**", "/api/v1/transactions/me").hasAnyRole(USER)

                        // User management: admin only
                        .requestMatchers("/api/v1/users/**").hasRole(ADMIN)

                        // Card admin actions
                        .requestMatchers("/api/v1/cards/**").hasRole(ADMIN)

                        // Transfers: admin only
                        .requestMatchers("/api/v1/transactions/**").hasRole(ADMIN)

                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(new BearerTokenAuthenticationEntryPoint())
                        .accessDeniedHandler(new BearerTokenAccessDeniedHandler())
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtToUserAuthenticationConverter)));
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(authenticationProvider);
    }
}