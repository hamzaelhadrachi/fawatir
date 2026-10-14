package com.hardy.fawatir.service;

import com.hardy.fawatir.model.Role;

import java.util.Collection;

public interface RoleService {
    Role getRoleByUserId(Long id);

    Collection<Role> getRoles();
}
