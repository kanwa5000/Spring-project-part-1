package edu.bi.springdemo.config;

import edu.bi.springdemo.entity.User;
import edu.bi.springdemo.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        String adminUsername = "admin";
        String adminPassword = "admin123";

        User admin = userRepository.findByUsername(adminUsername)
                .orElse(new User());

        admin.setUsername(adminUsername);
        admin.setPassword(passwordEncoder.encode(adminPassword));
        admin.setRole("ROLE_LIBRARIAN");
        admin.setEmail("admin@example.com");
        admin.setName("Admin User");

        userRepository.save(admin);

        System.out.println("Admin user is ready.");
        System.out.println("Username: admin");
        System.out.println("Password: admin123");
    }
}