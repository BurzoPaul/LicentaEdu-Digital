package com.utcn.edu_digital.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.springframework.web.bind.annotation.DeleteMapping;

@Data
public class RegisterRequest {
    @NotBlank(message = "Numele este obligatoriu")
    private String name;

    @NotBlank(message = "Emailul este obligatoriu")
    @Pattern(
            regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
            message = "Emailul nu este valid"
    )
    private String email;


    @NotBlank(message = "Parola este obligatorie")
    private String password;
}
