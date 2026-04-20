package edu.bi.springdemo.controller;

import edu.bi.springdemo.entity.DTO.JwtResponseDTO;
import edu.bi.springdemo.entity.DTO.LoginDTO;
import edu.bi.springdemo.service.LoginService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class LoginController {

    private final LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponseDTO> login(@RequestBody LoginDTO loginDTO) {
        String token = loginService.login(loginDTO.getUsername(), loginDTO.getPassword());
        return ResponseEntity.ok(new JwtResponseDTO(token));
    }

    @GetMapping("/test")
    public String test() {
        return "Authenticated request works";
    }
}