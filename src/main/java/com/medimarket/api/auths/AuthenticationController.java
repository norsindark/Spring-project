package com.medimarket.api.auths;

import com.medimarket.api.customer.PasswordDto;
import com.medimarket.api.exceptions.CustomerTokenException;
import com.medimarket.api.exceptions.UserNotFoundException;
import com.medimarket.api.users.User;
import com.medimarket.api.users.UserDto;
import com.medimarket.api.customer.EmailDto;
import com.medimarket.api.users.UserService;

import com.medimarket.api.exceptions.UserNotFoundException;
import com.medimarket.api.users.UserDto;
import com.medimarket.api.verifyEmails.VerifyRequest;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final AuthenticationService authService;
    private final UserService userService;

    @PostMapping("/register")

    public ResponseEntity<AuthenticationResponse> register(@Valid @RequestBody UserDto request, HttpServletRequest req)
            throws Exception, MessagingException, UnsupportedEncodingException {
        return ResponseEntity.ok(authService.register(request, getUrlBase(req)));
    }

    public String getUrlBase(HttpServletRequest req)
            throws MessagingException, UnsupportedEncodingException {
        String siteUrl = req.getRequestURL().toString();
        return siteUrl.replace(req.getServletPath(), "");
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@Valid @RequestBody AuthenticationDto request)
            throws Exception {
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/verify")
    public ResponseEntity<String> verifyUser(@Param("token") String token) throws CustomerTokenException {
            return ResponseEntity.ok(authService.verify(token));
    }

    @PostMapping("/resend-verify-email")
    public ResponseEntity<String> resendVerifyEmail(
            @Valid @RequestBody EmailDto request, HttpServletRequest req)
            throws MessagingException, UnsupportedEncodingException, UserNotFoundException, CustomerTokenException {
        authService.resendVerifyEmail(request, getUrlBase(req));
        return ResponseEntity.ok("Resend verify email success");
    }
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(
            @Valid @RequestBody EmailDto email, HttpServletRequest request)
            throws MessagingException, UnsupportedEncodingException, UserNotFoundException, CustomerTokenException {
        this.userService.resetPassword(email, getUrlBase(request));
        return ResponseEntity.ok("Reset password success!");
    }

    @PostMapping("/change-password")
    private ResponseEntity<User> changePassword(
            @Valid @RequestBody PasswordDto newPass, @Param("token") String token
            ) throws Exception
    {
        return ResponseEntity.ok(userService.changePassword(newPass,token));
    }

}
