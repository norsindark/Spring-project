package com.medimarket.api.auths;

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

    @PostMapping("/register")
    public ResponseEntity<AuthenticationRepose> register(@Valid @RequestBody UserDto request, HttpServletRequest req)
            throws Exception , MessagingException, UnsupportedEncodingException  {
        return ResponseEntity.ok(authService.register(request, getVerifyEmail(req)));
    }
    public String getVerifyEmail(HttpServletRequest req)
            throws MessagingException, UnsupportedEncodingException {
        String siteUrl = req.getRequestURL().toString();
        return siteUrl.replace(req.getServletPath(),"");
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationRepose> login(@Valid @RequestBody AuthenticationDto request){
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/verify")
    public ResponseEntity<String > verifyUser(@Param("token") String token) {
        if (authService.verify(token)){
            return ResponseEntity.ok("verify success");
        }
        else {
            return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body("verify failed");
        }
    }

    @PostMapping("/resend-verify-email")
    public ResponseEntity<String> resendVerifyEmail(@Valid @RequestBody VerifyRequest request, HttpServletRequest req)
            throws MessagingException, UnsupportedEncodingException, UserNotFoundException {
        authService.resendVerifyEmail(request, getVerifyEmail(req));
        return ResponseEntity.ok("Resend verify email success");
    }
}
