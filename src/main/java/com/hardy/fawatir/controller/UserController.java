package com.hardy.fawatir.controller;

import com.hardy.fawatir.dto.UserDTO;
import com.hardy.fawatir.form.LoginForm;
import com.hardy.fawatir.model.HttpResponse;
import com.hardy.fawatir.model.User;
import com.hardy.fawatir.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

import static java.time.LocalTime.now;
import static java.util.Map.of;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;


@Controller
@RequestMapping(path = "/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;


    @PostMapping("/login")
    public ResponseEntity<HttpResponse> login(@RequestBody @Valid() LoginForm loginForm) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginForm.getEmail(), loginForm.getPassword()));
            UserDTO userDTO = userService.getUserByEmail(loginForm.getEmail());
            return ResponseEntity.ok().body(
                    HttpResponse.builder()
                            .timeStamp(now().toString())
                            .data(of("user", userDTO))
                            .message("Login Successful!")
                            .status(OK)
                            .statusCode(OK.value())
                            .build());
        } catch (AuthenticationException exception) {
            // Log the full exception to get more details, like the specific exception type (e.g., BadCredentialsException, DisabledException)
            log.error("Authentication failed for user '{}'", loginForm.getEmail(), exception);
            return ResponseEntity.status(UNAUTHORIZED).body(
                    HttpResponse.builder()
                            .timeStamp(now().toString())
                            .message(exception.getMessage())
                            .status(UNAUTHORIZED)
                            .statusCode(UNAUTHORIZED.value()).build());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<HttpResponse> saveUser(@RequestBody @Valid() User user) {
        UserDTO dto = userService.createUser(user);

        return ResponseEntity.created(getUri()).body(
                HttpResponse.builder()
                        .timeStamp(now().toString())
                        .data(of("user",dto))
                        .message("user created")
                        .status(CREATED)
                        .statusCode(CREATED.value())
                        .build()
        );
    }

    private URI getUri() {
        return URI.create(
                ServletUriComponentsBuilder.fromCurrentContextPath().path("/user/get/<userId>").toUriString()
                );
    }
}
