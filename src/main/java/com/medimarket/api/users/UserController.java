package com.medimarket.api.users;

import com.medimarket.api.exceptions.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/home")
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<Optional<User>> getUserProfile() throws UserNotFoundException {
        return ResponseEntity.ok(this.userService.getUserByToken());
    }
}
