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

        if (sub.endsWith("@clients")) {
            return new UsernamePasswordAuthenticationToken(
                    sub, jwt, List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
            );
        }

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

        CustomUserDetails details = new CustomUserDetails(user);
        return new UsernamePasswordAuthenticationToken(details, jwt, details.getAuthorities());
    }
}