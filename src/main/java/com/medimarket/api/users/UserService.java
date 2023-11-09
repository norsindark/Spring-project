package com.medimarket.api.users;

import com.medimarket.api.exceptions.UserNotFoundException;
import com.medimarket.api.utils.GetUserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public Optional<User> getUserByToken() throws UserNotFoundException {
        GetUserUtil userUtil = new GetUserUtil();
        String username = userUtil.getUsername();
        Optional<User> user = this.userRepository.findByUsername(username);
        if (user.isEmpty()) {
            throw new UserNotFoundException("User not found with: "+username);
        }
        return user;
    }
}
