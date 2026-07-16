package com.hardy.fawatir.service.implementation;

import com.hardy.fawatir.dto.UserDTO;
import com.hardy.fawatir.dto.mapper.UserDTOMapper;
import com.hardy.fawatir.model.User;
import com.hardy.fawatir.repository.UserRepository;
import com.hardy.fawatir.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository<User> userRepository;

    @Override
    public UserDTO createUser(User user) {
        return UserDTOMapper.fromUser(userRepository.create(user));
    }
}
