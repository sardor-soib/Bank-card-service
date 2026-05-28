package com.example.bankcards.security;

import com.example.bankcards.config.CustomUserDetails;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.UserRepository;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class JwtToUserAuthenticationConverter implements Converter<Jwt, UsernamePasswordAuthenticationToken> {

    private final UserRepository userRepository;

    public JwtToUserAuthenticationConverter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UsernamePasswordAuthenticationToken convert(Jwt jwt) {
        String sub = jwt.getSubject();

        // M2M client-credentials token — no real user, grant admin for system/testing use
        if (sub.endsWith("@clients")) {
            return new UsernamePasswordAuthenticationToken(
                    sub, jwt, List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
            );
        }

        // Look up by sub first; on first login fall back to email and auto-link the sub
        User user = userRepository.findBySub(sub)
                .or(() -> {
                    String email = jwt.getClaimAsString("email");
                    if (email == null) return Optional.empty();
                    return userRepository.findByEmail(email).map(u -> {
                        u.setSub(sub);
                        return userRepository.save(u);
                    });
                })
                .orElseThrow(() -> new IllegalArgumentException(
                        "No local user found for sub=" + sub + ". An admin must create the account first."));

        return new UsernamePasswordAuthenticationToken(new CustomUserDetails(user), jwt, new CustomUserDetails(user).getAuthorities());
    }
}