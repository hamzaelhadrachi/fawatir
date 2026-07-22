package com.hardy.fawatir.service.implementation;

import com.hardy.fawatir.dto.UserDTO;
import com.hardy.fawatir.model.Role;
import com.hardy.fawatir.model.User;
import com.hardy.fawatir.repository.RoleRepository;
import com.hardy.fawatir.repository.UserRepository;
import com.hardy.fawatir.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.hardy.fawatir.dto.mapper.UserDTOMapper.fromUser;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository<User> userRepository;
    private final RoleRepository<Role> roleRepository;


    @Override
    public UserDTO createUser(User user) {
        return mapToUserDTO(userRepository.create(user));
    }

    @Override
    public UserDTO getUserByEmail(String email) {
        return mapToUserDTO(userRepository.getUserByEmail(email));
    }

    @Override
    public void sendVerificationCode(UserDTO userDTO) {
        userRepository.sendVerificationCode(userDTO);
    }

    @Override
    public UserDTO verifyCode(String email, String code) {
        return mapToUserDTO(userRepository.verifyCode(email, code)) ;
    }

    private UserDTO mapToUserDTO(User user){
        return fromUser(user,roleRepository.getRoleByUserId(user.getId()));
    }
}
