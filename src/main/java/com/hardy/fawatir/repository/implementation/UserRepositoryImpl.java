package com.hardy.fawatir.repository.implementation;

import com.hardy.fawatir.dto.UserDTO;
import com.hardy.fawatir.exception.ApiException;
import com.hardy.fawatir.model.Role;
import com.hardy.fawatir.model.User;
import com.hardy.fawatir.model.UserPrincipal;
import com.hardy.fawatir.repository.RoleRepository;
import com.hardy.fawatir.repository.UserRepository;
import com.hardy.fawatir.rowmapper.UserRowMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static com.hardy.fawatir.enumeration.RoleType.ROLE_USER;
import static com.hardy.fawatir.enumeration.VerificationType.ACCOUNT;
import static com.hardy.fawatir.query.UserQuery.*;
import static java.util.Map.of;
import static java.util.Objects.requireNonNull;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.apache.commons.lang3.time.DateFormatUtils.format;
import static org.apache.commons.lang3.time.DateUtils.addDays;

;

@Repository
@RequiredArgsConstructor
@Slf4j
public class UserRepositoryImpl implements UserRepository<User>, UserDetailsService {

    private static final String DATE_FORMAT = "yyyy-MM-dd hh:mm:ss";
    private final NamedParameterJdbcTemplate jdbc;
    private final RoleRepository<Role> roleRepository;
    private final BCryptPasswordEncoder encoder;

    @Override
    public User create(User user) {

        // check if the email is unique
        if(getEmailCount(user.getEmail().trim().toLowerCase()) > 0 ) throw new ApiException("Email Already in Use ! Please Try Using A Different Email");
        // Save new user
        try{
            user.setEnabled(true);
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
            jdbc.update(INSERT_ACCOUNT_VERIFICATION_URL_QUERY, of("userId",user.getId(),"url",verificationUrl));
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
        return jdbc.queryForObject(COUNT_USER_EMAIL_QUERY, of("email",email), Integer.class);
    }
    private SqlParameterSource getSqlParameterSource(User user) {
        return  new MapSqlParameterSource()
                .addValue("firstName", user.getFirstName())
                .addValue("lastName", user.getLastName())
                .addValue("email", user.getEmail())
                .addValue("password", encoder.encode(user.getPassword()))
                .addValue("nonLocked", user.isNotLocked())
                .addValue("enabled", user.isEnabled())
                ;
    }
    private String getVerificationUrl(String key, String type){
        return ServletUriComponentsBuilder.fromCurrentContextPath().path("/user/verify"+type+"/"+key).toUriString();
    }

    @Override
    public UserDetails loadUserByUsername(@NonNull String email) throws UsernameNotFoundException {
        User user = getUserByEmail(email);
        if(user == null) {
            log.info("user {} not found in the database!", email);
            throw new UsernameNotFoundException("user not found in the database!");
        }else {
            log.info("user {} found in the database", email);
            return new UserPrincipal(user,roleRepository.getRoleByUserId(user.getId()).getPermission());
        }
    }

    @Override
    public User getUserByEmail(String email) {
        try{
            User user = jdbc.queryForObject(SELECT_USER_BY_IMAIL_QUERY,of("email",email), new UserRowMapper());
            return user;
        }catch (EmptyResultDataAccessException e){
            throw new ApiException("No User found by Email: "+email);
        }catch (Exception e){
            log.error(e.getMessage());
            throw new ApiException("Error occurred please try again.");
        }
    }

    @Override
    public void sendVerificationCode(UserDTO userDTO) {
        String expirationDate = format(addDays(new Date(), 1), DATE_FORMAT);
        String verificationCode = randomAlphanumeric(6).toUpperCase();
        try{
            jdbc.update(DELETE_VERIFICATION_CODE_BY_USER_ID_QUERY,of("id",userDTO.getId()));

            jdbc.update(INSERT_ACCOUNT_VERIFICATION_CODE_QUERY,
                    of(
                            "user_id",userDTO.getId(),
                            "code",verificationCode,
                            "expiration_date", expirationDate)
            );
            sendSMS(userDTO.getPhone(), "From: Fawatir \nVerification code \n"+verificationCode);
        }catch (Exception e){
            log.error(e.getMessage());
            throw new ApiException("Error occurred please try again.");
        }
    }

    @Override
    public User verifyCode(String email, String code) {
        if(isVerificationCodeExpired(code)) throw new ApiException("This Code is Expired ! please log in again.");

        try {
            User userByCode = jdbc.queryForObject(SELECT_USER_BY_USER_CODE_QUERY,of("code",code),new UserRowMapper());
            User userByEmail = jdbc.queryForObject(SELECT_USER_BY_IMAIL_QUERY,of("email",email),new UserRowMapper());

            if(userByCode.getEmail().equalsIgnoreCase(userByEmail.getEmail())){
                jdbc.update(DELETE_FROM_TWO_FACTOR_VERIFICATION_QUERY,of("code",code));
                return userByCode;
            }else {
                throw new ApiException("Code invalid. Please try again");
            }
        }catch (EmptyResultDataAccessException e){
            throw new ApiException("Unable to find Record");
        }catch (Exception e){
            log.error(e.getMessage());
            throw new ApiException("Error occurred please try again.");
        }
    }

    private boolean isVerificationCodeExpired(String code) {
        try {
            return Boolean.TRUE.equals(jdbc.queryForObject(SELECT_EXPIRATION_DATE_BY_CODE_QUERY, of("code", code), Boolean.class));

        }catch (EmptyResultDataAccessException e){
            throw new ApiException("This code is not Valid. please log in again");
        }catch (Exception e){
            log.error(e.getMessage());
            throw new ApiException("Error occurred please try again.");
        }
    }

    private void sendSMS(String phone, String s) {
        log.info("Verification code {} was sent to phone number: {}",s,phone);
    }


}
