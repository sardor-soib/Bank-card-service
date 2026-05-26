package com.example.bankcards.security;

import com.example.bankcards.config.CustomUserDetails;
import org.springframework.security.core.Authentication;

public class SecurityUtils {

    private SecurityUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static boolean isAdmin(Authentication auth) {
        validateAuth(auth);
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    public static boolean isUser(Authentication auth) {
        validateAuth(auth);
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER"));
    }

    public static Long getCurrentUserId(Authentication auth) {
        validateAuth(auth);
        return ((CustomUserDetails) auth.getPrincipal()).getId();
    }

    private static void validateAuth(Authentication auth) {
        if (auth == null || auth.getPrincipal() == null || auth.getAuthorities() == null) {
            throw new IllegalArgumentException("Authentication object cannot be null or have null principal or authorities");
        }
    }
}
