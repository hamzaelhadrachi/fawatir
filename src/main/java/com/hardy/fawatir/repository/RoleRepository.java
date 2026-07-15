package com.hardy.fawatir.repository;

import com.hardy.fawatir.model.Role;

import java.util.Collection;

public interface RoleRepository <T extends Role> {

    /* Basic CURD Operations */
    T create(T data);
    Collection<T> list(int page, int pageSize);
    T get(int id);
    T update(int id, T data);
    Boolean delete(int id);

    /* More Complex Operations */
    void addRoleToUser(Long userId, String roleName);
    Role getRoleByUserId(Long userId);
    Role getRoleByUserEmail(String email);
    void updateUserRole(Long userId, String roleName);
}
