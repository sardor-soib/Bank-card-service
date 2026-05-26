package com.example.bankcards.security;

import com.example.bankcards.config.CustomUserDetails;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class JwtToUserAuthenticationConverter implements Converter<Jwt, UsernamePasswordAuthenticationToken> {

    private final UserDetailsService userDetailsService;

    public JwtToUserAuthenticationConverter(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    public UsernamePasswordAuthenticationToken convert(Jwt jwt) {
        String email = jwt.getSubject();
        CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(email);
        return new UsernamePasswordAuthenticationToken(userDetails, jwt, userDetails.getAuthorities());
    }
}
