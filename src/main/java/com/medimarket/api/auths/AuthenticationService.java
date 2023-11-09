package com.medimarket.api.auths;

import com.medimarket.api.configs.JwtService;
import com.medimarket.api.exceptions.UserLoginException;
import com.medimarket.api.exceptions.UserRegistrationException;
import com.medimarket.api.users.Role;
import com.medimarket.api.users.User;
import com.medimarket.api.users.UserDto;
import com.medimarket.api.users.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authManager;

    public AuthenticationRepose register(UserDto userDto) throws UserRegistrationException {
        Optional<User> exitsUsername = this.userRepository.findByUsername(userDto.getUsername());
        Optional<User> exitsEmail = this.userRepository.findByEmail(userDto.getEmail());
        if (exitsUsername.isPresent()) {
            throw new UserRegistrationException("Username : " + userDto.getUsername() + " already exits");
        }
        if (exitsEmail.isPresent()) {
            throw new UserRegistrationException("Email : " + userDto.getEmail() + " already exits");
        }

        User _user = User.builder()
                .username(userDto.getUsername())
                .password(encoder.encode(userDto.getPassword()))
                .email(userDto.getEmail())
                .name(userDto.getName())
                .phone(userDto.getPhone())
                .address(userDto.getAddress())
                .role(Role.USER)
                .build();
        this.userRepository.save(_user);
        String token = jwtService.generateToken(_user);

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
}
