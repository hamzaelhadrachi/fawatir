package com.hardy.fawatir.repository.implementation;

import com.hardy.fawatir.exception.ApiException;
import com.hardy.fawatir.model.Role;
import com.hardy.fawatir.model.User;
import com.hardy.fawatir.repository.RoleRepository;
import com.hardy.fawatir.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.hardy.fawatir.enumeration.RoleType.ROLE_USER;
import static com.hardy.fawatir.enumeration.VerificationType.ACCOUNT;
import static com.hardy.fawatir.query.UserQuery.*;
import static java.util.Objects.requireNonNull;

@Repository
@RequiredArgsConstructor
@Slf4j
public class UserRepositoryImpl implements UserRepository<User> {

    private final NamedParameterJdbcTemplate jdbc;
    private final RoleRepository<Role> roleRepository;
    private final BCryptPasswordEncoder encoder;

    @Override
    public User create(User user) {

        // check if the email is unique
        if(getEmailCount(user.getEmail().trim().toLowerCase()) > 0 ) throw new ApiException("Email Already in Use ! Please Try Using A Different Email");
        // Save new user
        try{
            user.setEnabled(false);
            user.setNotLocked(true);
            KeyHolder holder  = new GeneratedKeyHolder();
            SqlParameterSource parameterSource  = getSqlParameterSource(user);
            jdbc.update(INSERT_USER_QUERY,parameterSource,holder);
            user.setId(requireNonNull(holder.getKey()).longValue());
            // Add Role to the user
            roleRepository.addRoleToUser(user.getId(),ROLE_USER.name());
            // Send verification URL
            String verificationUrl = getVerificationUrl(UUID.randomUUID().toString(), ACCOUNT.getType());
            // Save verification URL to Verification Table
            jdbc.update(INSERT_ACCOUNT_VERIFICATION_URL_QUERY, Map.of("userId",user.getId(),"url",verificationUrl));
            // Send email to user with verification url
            //emailService.sendVerificationUrl(user.getFirstName(),user.getEmail(),verificationUrl,ACCOUNT);

            // Return the newly created user
            return user;
            // If Anny Error throw Exception with proper Message

        }catch(EmptyResultDataAccessException e){
            throw new ApiException("No role found by name: "+ROLE_USER.name());
        } catch (Exception e){
            log.error(e.toString());
            throw new ApiException("An error occured, Please try again.");
        }
    }



    @Override
    public Collection<User> list(int page, int pageSize) {
        return List.of();
    }

    @Override
    public User get(int id) {
        return null;
    }

    @Override
    public User update(int id, User data) {
        return null;
    }

    @Override
    public Boolean delete(int id) {
        return null;
    }

    private Integer getEmailCount(String email) {
        return jdbc.queryForObject(COUNT_USER_EMAIL_QUERY, Map.of("email",email), Integer.class);
    }
    private SqlParameterSource getSqlParameterSource(User user) {
        return  new MapSqlParameterSource()
                .addValue("firstName", user.getFirstName())
                .addValue("lastName", user.getLastName())
                .addValue("email", user.getEmail())
                .addValue("password", encoder.encode(user.getPassword()))
                .addValue("enabled", user.isEnabled())
                .addValue("nonLocked", user.isNotLocked())
                ;
    }
    private String getVerificationUrl(String key, String type){
        return ServletUriComponentsBuilder.fromCurrentContextPath().path("/user/verify"+type+"/"+key).toUriString();
    }
}
