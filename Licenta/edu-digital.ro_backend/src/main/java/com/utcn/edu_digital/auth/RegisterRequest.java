package com.utcn.edu_digital.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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
    @Size(min = 6, message = "Parola trebuie să aibă cel puțin 8 caractere")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$",
            message = "Parola trebuie să conțină litere mari, litere mici și cel puțin o cifră"
    )
    private String password;
}
