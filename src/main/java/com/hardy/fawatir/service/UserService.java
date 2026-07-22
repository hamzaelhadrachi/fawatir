package com.hardy.fawatir.service;

import com.hardy.fawatir.dto.UserDTO;
import com.hardy.fawatir.model.User;

public interface UserService {

    UserDTO createUser(User user);

    UserDTO getUserByEmail(String email);

    void sendVerificationCode(UserDTO userDTO);

    UserDTO verifyCode(String email, String code);
}
