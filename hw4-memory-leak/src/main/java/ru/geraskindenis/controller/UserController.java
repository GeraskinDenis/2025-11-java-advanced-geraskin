package ru.geraskindenis.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.geraskindenis.dto.RegistrationRequest;
import ru.geraskindenis.dto.UserResponse;
import ru.geraskindenis.entity.User;
import ru.geraskindenis.service.UserService;

@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/user/register")
    public ResponseEntity<UserResponse> register(@RequestBody RegistrationRequest request) {
        try {
            User user = userService.register(request.login(), request.password());
            return ResponseEntity.ok(UserResponse.fromUser(user));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
