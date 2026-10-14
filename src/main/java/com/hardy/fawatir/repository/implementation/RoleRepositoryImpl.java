package com.hardy.fawatir.repository.implementation;

import com.hardy.fawatir.exception.ApiException;
import com.hardy.fawatir.model.Role;
import com.hardy.fawatir.repository.RoleRepository;
import com.hardy.fawatir.rowmapper.RoleRowMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.hardy.fawatir.enumeration.RoleType.ROLE_USER;
import static com.hardy.fawatir.query.RoleQuery.*;
import static java.util.Map.of;
import static java.util.Objects.requireNonNull;

@Repository
@RequiredArgsConstructor
@Slf4j
public class RoleRepositoryImpl implements RoleRepository<Role> {

    private final NamedParameterJdbcTemplate jdbc;

    @Override
    public Role create(Role data) {
        return null;
    }

    @Override
    public Collection<Role> list() {
        log.info("Fetching All Roles from the DB");
        try{
            return jdbc.query(SELECT_ROLES_QUERY, new RoleRowMapper());
        } catch (Exception e){
            log.error(e.getMessage());
            throw new ApiException("An error occured, Please try again.");
        }
    }

    @Override
    public Role get(int id) {
        return null;
    }

    @Override
    public Role update(int id, Role data) {
        return null;
    }

    @Override
    public Boolean delete(int id) {
        return null;
    }

    @Override
    public void addRoleToUser(Long userId, String roleName) {
        log.info("Adding Role {} to User {}", roleName, userId);
        try{
            Role role = jdbc.queryForObject(SELECT_ROLE_BY_NAME_QUERY, of("name",roleName),new RoleRowMapper());
            jdbc.update(INSERT_ROLE_TO_USER_QUERY, of("userId",userId,"roleId",requireNonNull(role).getId()));
        }catch(EmptyResultDataAccessException e){
            throw new ApiException("No role found by name: "+ROLE_USER.name());
        } catch (Exception e){
            log.error(e.getMessage());
            throw new ApiException("An error occured, Please try again.");
        }
    }

    @Override
    public Role getRoleByUserId(Long userId) {
        log.info("Fetching Role for User {}", userId);
        try{
            return jdbc.queryForObject(INSERT_ROLE_BY_ID_QUERY, of("id",userId),new RoleRowMapper());
        }catch(EmptyResultDataAccessException e){
            throw new ApiException("No role found by name: "+ROLE_USER.name());
        } catch (Exception e){
            log.error(e.getMessage());
            throw new ApiException("An error occured, Please try again.");
        }
    }

    @Override
    public Role getRoleByUserEmail(String email) {
        return null;
    }

    @Override
    public void updateUserRole(Long userId, String roleName) {
        log.info("Updating Role for User {}", userId);
        try{
            Role role = jdbc.queryForObject(SELECT_ROLE_BY_NAME_QUERY, of("name",roleName),new RoleRowMapper());
            jdbc.update(UPDATE_USER_ROLE_QUERY, of("roleId",role.getId(),"userId", userId));
        }catch(EmptyResultDataAccessException e){
            throw new ApiException("No role found by name: "+ROLE_USER.name());
        } catch (Exception e){
            log.error(e.getMessage());
            throw new ApiException("An error occured, Please try again.");
        }
    }
}
