package com.utcn.edu_digital.user;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/create")
    public ResponseEntity<User> createUser(@RequestParam String username, @RequestParam String password, @RequestParam String email) {
        try{
            User user = userService.saveUser(username, password, email);
            return ResponseEntity.ok(user);
        }catch(Exception e){
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Optional<User>> getUser(@PathVariable int id) {
        Optional<User> user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<User> deleteUser(@PathVariable int id) {
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }

}
