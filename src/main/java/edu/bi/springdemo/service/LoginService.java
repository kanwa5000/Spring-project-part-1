package edu.bi.springdemo.service;

import edu.bi.springdemo.entity.User;
import edu.bi.springdemo.entity.exception.InvalidRequestException;
import edu.bi.springdemo.entity.exception.LoginPasswordException;
import edu.bi.springdemo.repository.UserRepository;
import edu.bi.springdemo.security.JwtTokenService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class LoginService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;

    public LoginService(UserRepository userRepository,
                        PasswordEncoder passwordEncoder,
                        JwtTokenService jwtTokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenService = jwtTokenService;
    }

    public String login(String username, String password) {
        if (username == null || username.isBlank()) {
            throw new InvalidRequestException("Username cannot be blank");
        }

        if (password == null || password.isBlank()) {
            throw new InvalidRequestException("Password cannot be blank");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new LoginPasswordException("Invalid username or password"));

        boolean passwordMatches = passwordEncoder.matches(password, user.getPassword());
        if (!passwordMatches) {
            throw new LoginPasswordException("Invalid username or password");
        }

        return jwtTokenService.generateToken(user.getUsername(), user.getRole());
    }
}