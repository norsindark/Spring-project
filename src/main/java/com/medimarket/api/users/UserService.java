package com.medimarket.api.users;

import com.medimarket.api.exceptions.UserNotFoundException;
import com.medimarket.api.utils.GetUserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    public Optional<User> getUserByToken() throws UserNotFoundException {
        GetUserUtil userUtil = new GetUserUtil();
        String username = userUtil.getUsername();
        Optional<User> user = this.userRepository.findByUsername(username);
        if (user.isEmpty()) {
            throw new UserNotFoundException("User not found with: "+username);
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
}
