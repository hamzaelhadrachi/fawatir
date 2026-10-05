package com.hardy.fawatir.controller;

import com.hardy.fawatir.dto.UserDTO;
import com.hardy.fawatir.exception.ApiException;
import com.hardy.fawatir.form.LoginForm;
import com.hardy.fawatir.model.HttpResponse;
import com.hardy.fawatir.model.User;
import com.hardy.fawatir.model.UserPrincipal;
import com.hardy.fawatir.provider.TokenProvider;
import com.hardy.fawatir.service.RoleService;
import com.hardy.fawatir.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

import static com.hardy.fawatir.dto.mapper.UserDTOMapper.toUser;
import static com.hardy.fawatir.utils.ExceptionUtils.processError;
import static java.time.LocalTime.now;
import static java.util.Map.of;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.*;


@Controller
@RequestMapping(path = "/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final RoleService roleService;
    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;
    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private static final String TOKEN_PREFIX = "Bearer ";

    @PostMapping("/login")
    public ResponseEntity<HttpResponse> login(@RequestBody @Valid() LoginForm loginForm) {
        Authentication authentication = authenticate(loginForm.getEmail(), loginForm.getPassword());
        log.info(String.valueOf(authentication));
        UserDTO userDTO = getAuthenticatedUser(authentication);
        return userDTO.isUsingMfa() ? sendVerificationCode(userDTO) : sendResponse(userDTO);
    }

    @PostMapping("/register")
    public ResponseEntity<HttpResponse> saveUser(@RequestBody @Valid() User user) {
        UserDTO dto = userService.createUser(user);

        return ResponseEntity.created(getUri()).body(
                HttpResponse.builder()
                        .timeStamp(now().toString())
                        .data(of("user", dto))
                        .message("user created")
                        .status(CREATED)
                        .statusCode(CREATED.value())
                        .build()
        );
    }

    @GetMapping("/profile")
    public ResponseEntity<HttpResponse> getProfile(Authentication authentication) {
        UserDTO userDTO = userService.getUserByEmail(authentication.getName());
        log.info("Profile of the user is: {}", authentication.getPrincipal());
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(now().toString())
                        .data(of(
                                "user", userDTO
                        ))
                        .message("Profile Found!")
                        .status(OK)
                        .statusCode(OK.value())
                        .build());
    }

    @GetMapping("/resetpassword/{email}")
    public ResponseEntity<HttpResponse> resetPassword(@PathVariable("email") String email) {
        userService.resetPassword(email);
        log.info("Reseting password for: {}", email);
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(now().toString())
                        .message("Please check your email to reset your password ! an Email was Sent!")
                        .status(OK)
                        .statusCode(OK.value())
                        .build());
    }

    @GetMapping("/verify/password/{key}")
    public ResponseEntity<HttpResponse> verifyPasswordURL(@PathVariable("key") String key) {
        UserDTO user =  userService.verifyPasswordKey(key);
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(now().toString())
                        .data(of("user",user))
                        .message("Please Enter a new password")
                        .status(OK)
                        .statusCode(OK.value())
                        .build());
    }

    @PostMapping("/resetPassword/{key}/{password}/{confirmPassword}")
    public ResponseEntity<HttpResponse> resetPasswordWithKey(@PathVariable("key") String key,@PathVariable("password") String password,@PathVariable("confirmPassword") String confirmPassword) {
        userService.renewPassword(key,password,confirmPassword);
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(now().toString())
                        .message("Password has been renewed successfully!")
                        .status(OK)
                        .statusCode(OK.value())
                        .build());
    }

    @GetMapping("/verify/code/{email}/{code}")
    public ResponseEntity<HttpResponse> verifyCode(@PathVariable("email") String email, @PathVariable("code") String code) {
        UserDTO userDTO = userService.verifyCode(email, code);

        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(now().toString())
                        .data(of(
                                "user", userDTO,
                                "access_token", tokenProvider.createAccessToken(getUserPrincipal(userDTO)),
                                "refresh_token", tokenProvider.createRefreshToken(getUserPrincipal(userDTO))
                        ))
                        .message("Login Successful!")
                        .status(OK)
                        .statusCode(OK.value())
                        .build());
    }

    private URI getUri() {
        return URI.create(
                ServletUriComponentsBuilder.fromCurrentContextPath().path("/user/get/<userId>").toUriString()
        );
    }

    private ResponseEntity<HttpResponse> sendResponse(UserDTO userDTO) {
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(now().toString())
                        .data(of(
                                "user", userDTO,
                                "access_token", tokenProvider.createAccessToken(getUserPrincipal(userDTO)),
                                "refresh_token", tokenProvider.createRefreshToken(getUserPrincipal(userDTO))
                        ))
                        .message("Login Successful!")
                        .status(OK)
                        .statusCode(OK.value())
                        .build());
    }

    private UserPrincipal getUserPrincipal(UserDTO userDTO) {
        return new UserPrincipal(toUser(userService.getUserByEmail(userDTO.getEmail())), roleService.getRoleByUserId(userDTO.getId()));
    }

    private ResponseEntity<HttpResponse> sendVerificationCode(UserDTO userDTO) {
        userService.sendVerificationCode(userDTO);
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(now().toString())
                        .data(of("user", userDTO))
                        .message("Verification code sent")
                        .status(OK)
                        .statusCode(OK.value())
                        .build());
    }

    @GetMapping("/verify/account/{key}")
    public ResponseEntity<HttpResponse> verifyAccount(@PathVariable("key") String key) {

        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(now().toString())
                        .message(
                                userService.verifyAccoutKey(key).isEnabled() ? "Account Already verified !" : "Account Verified"
                                )
                        .status(OK)
                        .statusCode(OK.value())
                        .build());
    }

    @GetMapping("/refresh/token")
    public ResponseEntity<HttpResponse> refreshToken(HttpServletRequest request) {

        if (isHeaderAndTokenValid(request)){
            String token = request.getHeader(AUTHORIZATION).substring(TOKEN_PREFIX.length());
            UserDTO userDTO = userService.getUserByEmail(tokenProvider.getSubject(token,request));

            return ResponseEntity.ok().body(
                    HttpResponse.builder()
                            .timeStamp(now().toString())
                            .data(of(
                                    "user", userDTO,
                                    "access_token", tokenProvider.createAccessToken(getUserPrincipal(userDTO)),
                                    "refresh_token", token
                            ))
                            .message("Token Refreshed!")
                            .status(OK)
                            .statusCode(OK.value())
                            .build());
        }

        return ResponseEntity.badRequest().body(
                HttpResponse.builder()
                        .timeStamp(now().toString())
                        .message("Token missing or invalid !")
                        .developerMessage("Token missing or invalid !")
                        .status(BAD_REQUEST)
                        .statusCode(BAD_REQUEST.value())
                        .build());
    }

    private boolean isHeaderAndTokenValid(HttpServletRequest request) {
        return request.getHeader(AUTHORIZATION) != null
                && request.getHeader(AUTHORIZATION).startsWith(TOKEN_PREFIX)
                && tokenProvider.isTokenValid(tokenProvider.getSubject(request.getHeader(AUTHORIZATION).substring(TOKEN_PREFIX.length()) , request ),
                request.getHeader(AUTHORIZATION).substring(TOKEN_PREFIX.length()));

    }


    @RequestMapping("/error")
    public ResponseEntity<HttpResponse> handleError(HttpServletRequest request) {
        return ResponseEntity.badRequest().body(
                HttpResponse.builder()
                        .timeStamp(now().toString())
                        .message("An error occurred! no mapping for the " + request.getMethod() + " request PATH on the server")
                        .status(BAD_REQUEST)
                        .statusCode(BAD_REQUEST.value())
                        .build());
    }

    private Authentication authenticate(@NotEmpty String email, @NotEmpty String password) {
        try {
            Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
            return authenticate;
        } catch (Exception e) {
            processError(request, response, e);
            throw new ApiException(e.getMessage());
        }
    }

    private UserDTO getAuthenticatedUser(Authentication authentication) {
        return ((UserPrincipal) authentication.getPrincipal()).getUser();
    }
}
