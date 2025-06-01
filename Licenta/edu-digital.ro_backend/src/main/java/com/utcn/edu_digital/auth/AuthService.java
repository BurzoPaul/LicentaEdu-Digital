package com.utcn.edu_digital.auth;

import com.utcn.edu_digital.user.User;
import com.utcn.edu_digital.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {


    @Autowired
    private UserRepository userRepository;

    public ResponseEntity<?> register(RegisterRequest request) {
        Optional<User> existing = userRepository.findByEmailOrName(request.getEmail(), request.getName());

        if (existing.isPresent()) {
            return ResponseEntity.badRequest().body("Emailul sau username-ul este deja folosit");
        }

        User newUser = new User();
        newUser.setName(request.getName());
        newUser.setEmail(request.getEmail());
        newUser.setPassword(request.getPassword()); // Recomand BCrypt aici

        userRepository.save(newUser);
        return ResponseEntity.ok("Cont creat cu succes!");
    }


    public ResponseEntity<?> login(LoginRequest request) {
        Optional<User> userOpt = userRepository.findByEmailOrName(request.getLogin(), request.getLogin());

        if (userOpt.isEmpty() || !userOpt.get().getPassword().equals(request.getPassword())) {
            return ResponseEntity.status(401).body("Email/Nume sau parolă incorectă");
        }

        return ResponseEntity.ok("Autentificare reușită!");
    }
}
