package com.hardy.fawatir.form;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UpdateForm {

    @NotNull(message = "ID cannot be null or empty!")
    private Long id;
    @NotEmpty(message = "First Name cannot be empty!")
    private String firstName;
    @NotEmpty(message = "Last Name cannot be empty!")
    private String lastName;
    @NotEmpty(message = "Email cannot be empty")
    @Email(message = "Please Enter a valid email address")
    private String email;
    @Pattern(regexp = "^\\d{12}$", message = "Please Enter a valid ePhone Number!")
    private String phone;
    private String address;
    private String title;
    private String bio;

}
