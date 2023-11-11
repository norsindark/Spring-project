package com.medimarket.api.auths;

import com.medimarket.api.configs.JwtService;
import com.medimarket.api.exceptions.UserNotFoundException;
import com.medimarket.api.exceptions.UserRegistrationException;
import com.medimarket.api.users.Role;
import com.medimarket.api.users.User;
import com.medimarket.api.users.UserDto;
import com.medimarket.api.users.UserRepository;
import com.medimarket.api.verifyEmails.VerifyRequest;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.UnsupportedEncodingException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder encoder;
    private final JavaMailSender javaMailSender;
    private final AuthenticationManager authManager;

    public AuthenticationRepose register(UserDto userDto, String siteURL)
            throws UserRegistrationException, MessagingException, UnsupportedEncodingException {
        Optional<User> exitsUsername = this.userRepository.findByUsername(userDto.getUsername());
        Optional<User> exitsEmail = this.userRepository.findByEmail(userDto.getEmail());
        if (exitsUsername.isPresent()) {
            throw new UserRegistrationException(userDto.getUsername() + " already exits");
        }
        if (exitsEmail.isPresent()) {
            throw new UserRegistrationException(userDto.getEmail() + " already exits");
        }

        String randomToken = RandomStringUtils.randomAlphanumeric(64);

        User _user = User.builder()
                .username(userDto.getUsername())
                .password(encoder.encode(userDto.getPassword()))
                .email(userDto.getEmail())
                .name(userDto.getName())
                .phone(userDto.getPhone())
                .address(userDto.getAddress())
                .role(Role.USER)
                .verificationToken(randomToken)
                .enabled(false)
                .build();
        this.userRepository.save(_user);
        String token = jwtService.generateToken(_user);

        sendVerificationEmail(_user, siteURL);

        return AuthenticationRepose.builder()
                .token(token)
                .build();
    }

    public AuthenticationRepose login(AuthenticationDto request) {
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        User _username = this.userRepository.findByUsername(request.getUsername()).orElseThrow();
//                .orElseThrow(() -> new UserLoginException("username or password invalid"));

        var token = jwtService.generateToken(_username);
        return AuthenticationRepose.builder()
                .token(token)
                .build();
    }

    private void sendVerificationEmail(User user, String siteUrl)
            throws MessagingException, UnsupportedEncodingException {
        String toAddress = user.getEmail();
        String fromAddress = "dvan78281@gmail.com";
        String senderName = "Medicine Market";
        String subject = "Please verify your email";
        String content = "Dear [[name]],<br>"
                + "Please click the link below to verify your registration:<br>"
                + "<h3><a href=\"[[URL]]\" target=\"_self\">VERIFY</a></h3>"
                + "Thank you,<br>"
                + "SinD.";
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom(fromAddress, senderName);
        helper.setTo(toAddress);
        helper.setSubject(subject);
        content = content.replace("[[name]]", user.getName());
        String verifyUrl = UriComponentsBuilder.fromUriString(siteUrl)
                .path("/verify")
                .queryParam("token", user.getVerificationToken())
                .build().toUriString();
        content = content.replace("[[URL]]", verifyUrl);
        helper.setText(content, true);
        javaMailSender.send(message);
    }

    public boolean verify(String token) {
        User user = this.userRepository.findByVerificationToken(token);
        if (user == null || user.isEnabled()) {
            return false;
        } else {
            user.setVerificationToken(null);
            user.setEnabled(true);
            this.userRepository.save(user);
        }
        return true;
    }

    public void resendVerifyEmail(VerifyRequest request, String siteUrl)
            throws UserNotFoundException, MessagingException, UnsupportedEncodingException {
        Optional<User> user = this.userRepository.findByEmail(request.getEmail());
        if (user.isPresent()) {
            User _user = user.get();
            _user.setVerificationToken(RandomStringUtils.randomAlphanumeric(64));
            sendVerificationEmail(_user, siteUrl);
        } else {
            throw new UserNotFoundException("User not found with: " + request.getEmail());
        }
    }
}
