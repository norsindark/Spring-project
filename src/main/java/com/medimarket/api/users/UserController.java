package com.medimarket.api.users;

import com.medimarket.api.customer.EmailDto;
import com.medimarket.api.exceptions.UserNotFoundException;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/home")
public class UserController {

    private final UserService userService;

    public String getResetPass(HttpServletRequest req)
            throws MessagingException, UnsupportedEncodingException {
        String siteUrl = req.getRequestURL().toString();
        return siteUrl.replace(req.getServletPath(),"");
    }

    @GetMapping("/profile")
    public ResponseEntity<Optional<User>> getUserProfile() throws UserNotFoundException {
        return ResponseEntity.ok(this.userService.getUserByToken());
    }

    @PutMapping("/updateInfo/{id}")
    public ResponseEntity<User> updateUserInfo(@Valid @RequestBody UserDto userDto, @PathVariable int id) {
        return ResponseEntity.ok(this.userService.updateUserInfo(id,userDto));
    }
}
