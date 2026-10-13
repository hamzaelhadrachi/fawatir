package com.hardy.fawatir.utils;

import com.hardy.fawatir.dto.UserDTO;
import com.hardy.fawatir.model.UserPrincipal;
import org.springframework.security.core.Authentication;

public class UserUtils {

    public static UserDTO getAuthenticatedUser(Authentication authentication) {
        return (UserDTO) authentication.getPrincipal();
    }

    public static UserDTO getLoggedInUser(Authentication authentication) {
        return ((UserPrincipal) authentication.getPrincipal()).getUser();
    }
}
