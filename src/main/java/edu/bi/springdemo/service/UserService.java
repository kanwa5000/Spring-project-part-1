package edu.bi.springdemo.service;

import edu.bi.springdemo.entity.DTO.CreateUserDTO;
import edu.bi.springdemo.entity.User;
import edu.bi.springdemo.entity.exception.InvalidRequestException;
import edu.bi.springdemo.entity.exception.UserAlreadyExistsException;
import edu.bi.springdemo.entity.exception.UserNotFoundException;
import edu.bi.springdemo.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User addUser(CreateUserDTO dto) {
        validateCreateUserDTO(dto, true);

        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new UserAlreadyExistsException("User with username '" + dto.getUsername() + "' already exists");
        }

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new UserAlreadyExistsException("User with email '" + dto.getEmail() + "' already exists");
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setEmail(dto.getEmail());
        user.setName(dto.getName());
        user.setRole(normalizeRole(dto.getRole()));

        return userRepository.save(user);
    }

    public User updateUser(Integer id, CreateUserDTO dto) {
        if (id == null || id <= 0) {
            throw new InvalidRequestException("User id must be a positive number");
        }

        validateCreateUserDTO(dto, false);

        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with id " + id + " not found"));

        if (!existingUser.getUsername().equals(dto.getUsername()) &&
                userRepository.existsByUsername(dto.getUsername())) {
            throw new UserAlreadyExistsException("User with username '" + dto.getUsername() + "' already exists");
        }

        if (!existingUser.getEmail().equals(dto.getEmail()) &&
                userRepository.existsByEmail(dto.getEmail())) {
            throw new UserAlreadyExistsException("User with email '" + dto.getEmail() + "' already exists");
        }

        existingUser.setUsername(dto.getUsername());
        existingUser.setEmail(dto.getEmail());
        existingUser.setName(dto.getName());
        existingUser.setRole(normalizeRole(dto.getRole()));

        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            existingUser.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        return userRepository.save(existingUser);
    }

    public void deleteUser(Integer id) {
        if (id == null || id <= 0) {
            throw new InvalidRequestException("User id must be a positive number");
        }

        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("User with id " + id + " not found");
        }

        userRepository.deleteById(id);
    }

    public Iterable<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Integer id) {
        if (id == null || id <= 0) {
            throw new InvalidRequestException("User id must be a positive number");
        }

        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with id " + id + " not found"));
    }

    private void validateCreateUserDTO(CreateUserDTO dto, boolean passwordRequired) {
        if (dto == null) {
            throw new InvalidRequestException("User body cannot be null");
        }

        if (dto.getUsername() == null || dto.getUsername().isBlank()) {
            throw new InvalidRequestException("Username cannot be blank");
        }

        if (passwordRequired && (dto.getPassword() == null || dto.getPassword().isBlank())) {
            throw new InvalidRequestException("Password cannot be blank");
        }

        if (dto.getEmail() == null || dto.getEmail().isBlank()) {
            throw new InvalidRequestException("Email cannot be blank");
        }

        if (!dto.getEmail().contains("@")) {
            throw new InvalidRequestException("Email format is invalid");
        }

        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new InvalidRequestException("Name cannot be blank");
        }
    }

    private String normalizeRole(String role) {
        if (role == null || role.isBlank()) {
            return "ROLE_READER";
        }

        String normalized = role.trim().toUpperCase();

        if (!normalized.startsWith("ROLE_")) {
            normalized = "ROLE_" + normalized;
        }

        if (!normalized.equals("ROLE_READER") && !normalized.equals("ROLE_LIBRARIAN")) {
            throw new InvalidRequestException("Role must be READER or LIBRARIAN");
        }

        return normalized;
    }
}