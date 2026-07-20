package com.hardy.fawatir.form;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class LoginForm {
    @NotEmpty
    private String password;
    @NotEmpty
    private String email;
}
