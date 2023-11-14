package com.medimarket.api.users;

import com.medimarket.api.auths.AuthenticationService;
import com.medimarket.api.customer.CustomerRepository;
import com.medimarket.api.customer.CustomerToken;
import com.medimarket.api.customer.EmailDto;
import com.medimarket.api.customer.PasswordDto;
import com.medimarket.api.exceptions.CustomerTokenException;
import com.medimarket.api.exceptions.UserNotFoundException;
import com.medimarket.api.utils.GetUserUtil;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final CustomerRepository customerRepository;
    private final JavaMailSender javaMailSender;
    private final AuthenticationService authService;

    public Optional<User> getUserByToken() throws UserNotFoundException {
        GetUserUtil userUtil = new GetUserUtil();
        String username = userUtil.getUsername();
        Optional<User> user = this.userRepository.findByUsername(username);
        if (user.isEmpty()) {
            throw new UserNotFoundException("User not found with: " + username);
        }
        return user;
    }

    public User updateUserInfo(int id, UserDto request) {
        Optional<User> user = this.userRepository.findById(id);
        if (user.isEmpty()) {
            throw new RuntimeException("No user found with id " + id);
        }
        User _user = user.get();
        _user.setUsername(request.getUsername());
        _user.setPassword(encoder.encode(request.getPassword()));
        _user.setAddress(request.getAddress());
        _user.setPhone(request.getPhone());
        _user.setName(request.getName());
        _user.setEmail(request.getEmail());

        return _user;
    }
    public void resetPassword(EmailDto email, String siteUrl)
            throws UserNotFoundException, MessagingException, UnsupportedEncodingException, CustomerTokenException {
        Optional<User> user = this.userRepository.findByEmail(email.getEmail());
        if (user.isPresent()) {
            User _user = user.get();
            String token = UUID.randomUUID().toString();
            authService.RefreshTokenForUser(token, _user);
            sendTokenResetPass(_user, token, siteUrl);
        } else {
            throw new UserNotFoundException("Email not found with: " + email.getEmail());
        }
    }

    private void sendTokenResetPass(User user, String token, String siteUrl)
            throws MessagingException, UnsupportedEncodingException {
        String toAddress = user.getEmail();
        String fromAddress = "dvan78281@gmail.com";
        String senderName = "Medicine Market";
        String subject = "Please click this url to change your password";
        String content = "Dear [[name]],<br>"
                + "Please click the link below to verify your registration:<br>"
                + "<h3><a href=\"[[URL]]\" target=\"_self\">CHANGE PASSWORD</a></h3>"
                + "Thank you,<br>"
                + "SinD.";
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom(fromAddress, senderName);
        helper.setTo(toAddress);
        helper.setSubject(subject);
        content = content.replace("[[name]]", user.getName());
        String resetPassUrl = UriComponentsBuilder.fromUriString(siteUrl)
                .path("/verify")
                .queryParam("token", token)
                .build().toUriString();
        content = content.replace("[[URL]]", resetPassUrl);
        helper.setText(content, true);
        javaMailSender.send(message);
    }

    public User changePassword(PasswordDto request, String token) throws CustomerTokenException {
        CustomerToken customerToken = this.customerRepository.findByToken(token);
        if (customerToken == null) {
            throw new CustomerTokenException("Invalid token");
        }
        if (customerToken.getExpirydate().getTime() - System.currentTimeMillis() <= 0) {
            throw new CustomerTokenException("Token expired");
        }
        User user = customerToken.getUser();
        if (user == null) {
            throw new CustomerTokenException("User not found");
        }
        user.setPassword(encoder.encode(request.getPassword()));
        customerToken.setToken(null);
        this.userRepository.save(user);
        return user;
    }
}
