package com.hardy.fawatir.service.implementation;

import com.hardy.fawatir.model.Role;
import com.hardy.fawatir.repository.RoleRepository;
import com.hardy.fawatir.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    @Override
    public Role getRoleByUserId(Long id) {
        return roleRepository.getRoleByUserId(id);
    }
}
