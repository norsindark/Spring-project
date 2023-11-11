package com.medimarket.api.users;

import com.medimarket.api.exceptions.UserNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PutMapping("/updateInfo/{id}")
    public ResponseEntity<User> updateUserInfo(@Valid @RequestBody UserDto userDto, @PathVariable int id) {
        return ResponseEntity.ok(this.userService.updateUserInfo(id,userDto));
    }
}
