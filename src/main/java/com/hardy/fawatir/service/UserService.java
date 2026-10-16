package com.hardy.fawatir.service;

import com.hardy.fawatir.dto.UserDTO;
import com.hardy.fawatir.form.UpdateForm;
import com.hardy.fawatir.model.User;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public interface UserService {

    UserDTO createUser(User user);

    UserDTO getUserByEmail(String email);

    void sendVerificationCode(UserDTO userDTO);

    UserDTO verifyCode(String email, String code);

    void resetPassword(String email);

    UserDTO verifyPasswordKey(String key);

    void renewPassword(String key, String password, String confirmPassword);

    UserDTO verifyAccoutKey(String key);

    UserDTO updateUserDetails(UpdateForm user);

    UserDTO getuserById(Long userId);

    void updatePassword(Long id, String currentPassword, String newPassword, String confirmNewPassword);

    void updateUserRole(Long id, String roleName);

    void updateAccountSettings(Long id, @NotNull(message = "Enabled cannot be null or empty !") Boolean enabled, @NotNull(message = "Not Locked cannot be null or empty !") Boolean notLocked);
}
