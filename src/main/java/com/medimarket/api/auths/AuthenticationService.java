package com.medimarket.api.auths;

import com.medimarket.api.configs.JwtService;
import com.medimarket.api.customer.CustomerRepository;
import com.medimarket.api.customer.CustomerToken;
import com.medimarket.api.exceptions.CustomerTokenException;
import com.medimarket.api.exceptions.UserLoginException;
import com.medimarket.api.exceptions.UserNotFoundException;
import com.medimarket.api.exceptions.UserRegistrationException;
import com.medimarket.api.users.Role;
import com.medimarket.api.users.User;
import com.medimarket.api.users.UserDto;
import com.medimarket.api.users.UserRepository;
import com.medimarket.api.customer.EmailDto;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder encoder;
    private final JavaMailSender javaMailSender;
    private final AuthenticationManager authManager;
    private final CustomerRepository customerRepository;
    private static final int EXPIRATION = 60*24;

    public AuthenticationResponse register(UserDto userDto, String siteURL)
            throws UserRegistrationException, MessagingException, UnsupportedEncodingException {
        Optional<User> exitsUsername = this.userRepository.findByUsername(userDto.getUsername());
        Optional<User> exitsEmail = this.userRepository.findByEmail(userDto.getEmail());
        if (exitsUsername.isPresent()) {
            throw new UserRegistrationException(userDto.getUsername() + " already exits");
        }
        if (exitsEmail.isPresent()) {
            throw new UserRegistrationException(userDto.getEmail() + " already exits");
        }

        String randomToken = UUID.randomUUID().toString();

        User _user = User.builder()
                .username(userDto.getUsername())
                .password(encoder.encode(userDto.getPassword()))
                .email(userDto.getEmail())
                .name(userDto.getName())
                .phone(userDto.getPhone())
                .address(userDto.getAddress())
                .role(Role.USER)
                .enabled(false)
                .build();
        this.userRepository.save(_user);
        createNewTokenForUser(randomToken,_user);
        String token = jwtService.generateToken(_user);

        sendVerificationEmail(_user,randomToken, siteURL);

        return AuthenticationResponse.builder()
                .token(token)
                .build();
    }

    public AuthenticationResponse login(AuthenticationDto request) throws UserLoginException {
        try {
            authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            User _user = this.userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new UserLoginException("Username or password is invalid"));

            var token = jwtService.generateToken(_user);
            return AuthenticationResponse.builder()
                    .token(token)
                    .build();
        } catch (AuthenticationException e) {
            throw new UserLoginException("Username or password is invalid");
        }
    }

    private void sendVerificationEmail(User user,String token, String siteUrl)
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
                .queryParam("token", token)
                .build().toUriString();
        content = content.replace("[[URL]]", verifyUrl);
        helper.setText(content, true);
        javaMailSender.send(message);
    }



    public void resendVerifyEmail(EmailDto request, String siteUrl)
            throws UserNotFoundException, MessagingException, UnsupportedEncodingException, CustomerTokenException {
        Optional<User> user = this.userRepository.findByEmail(request.getEmail());
        if (user.isPresent()) {
            User _user = user.get();
            String token = UUID.randomUUID().toString();
            RefreshTokenForUser(token, _user);
            sendVerificationEmail(_user,token, siteUrl);
        } else {
            throw new UserNotFoundException("Email not found with: " + request.getEmail());
        }
    }
    public void createNewTokenForUser( String token,User user) {
        CustomerToken myToken = new CustomerToken(token, user);
        this.customerRepository.save(myToken);
    }

    public void RefreshTokenForUser(String token, User user) throws CustomerTokenException {
        Optional<CustomerToken> myToken = this.customerRepository.findByUserId(user.getId());
        if(myToken.isPresent()) {
            CustomerToken _myToken = myToken.get();
            _myToken.setToken(token);
            _myToken.setExpirydate(calculateExpiryDate(EXPIRATION));
            this.customerRepository.save(_myToken);
        } else {
            throw new CustomerTokenException("User not found!");

        }
    }

    public String verify(String token) throws CustomerTokenException {
        CustomerToken customerToken = this.customerRepository.findByToken(token);

        if (customerToken == null) {
            throw new CustomerTokenException("Invalid token");
        }
        if (customerToken.getExpirydate().getTime() - System.currentTimeMillis() <= 0) {
            throw new CustomerTokenException("Token expired");
        }
        Calendar cal = Calendar.getInstance();
        User user = customerToken.getUser();
        if (user == null) {
            throw new CustomerTokenException("User not found");
        }
        if (user.isEnabled()) {
            throw new CustomerTokenException("this Email verified");
        } else {
            user.setEnabled(true);
            customerToken.setToken(null);
            this.userRepository.save(user);
            return "Verify success!";
        }
    }

    private Date calculateExpiryDate(int expiryTimeInMinutes) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.MINUTE, expiryTimeInMinutes);
        return new Date(cal.getTime().getTime());
    }
}
