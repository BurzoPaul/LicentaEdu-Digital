package com.utcn.edu_digital.auth;

import com.utcn.edu_digital.security.LoginAttemptService;
import com.utcn.edu_digital.user.User;
import com.utcn.edu_digital.user.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private LoginAttemptService loginAttemptService;


    public ResponseEntity<?> register(RegisterRequest request) {
        Optional<User> existing = userRepository.findByEmailOrName(request.getEmail(), request.getName());

        if (existing.isPresent()) {
            return ResponseEntity.badRequest().body("Emailul sau username-ul este deja folosit");
        }

        User newUser = new User();
        newUser.setName(request.getName());
        newUser.setEmail(request.getEmail());

        // 🔐 Criptăm parola
        String hashedPassword = passwordEncoder.encode(request.getPassword());
        newUser.setPassword(hashedPassword);

        userRepository.save(newUser);
        return ResponseEntity.ok("Cont creat cu succes!");
    }


    public ResponseEntity<?> login(LoginRequest request, HttpServletRequest httpRequest) {
        String clientIP = httpRequest.getRemoteAddr();
        String login = request.getLogin();

        Optional<User> userOpt = userRepository.findByEmailOrName(login, login);
        if (userOpt.isEmpty() || !passwordEncoder.matches(request.getPassword(), userOpt.get().getPassword())) {
            loginAttemptService.loginFailed(clientIP);
            return ResponseEntity.status(401).body("Email/Nume sau parolă incorectă");
        }

        loginAttemptService.loginSucceeded(clientIP);
        User user = userOpt.get();

        // Generează JWT
        String token = jwtService.generateToken(user);

        //Returnează token-ul
        return ResponseEntity.ok(token);

    }
}
