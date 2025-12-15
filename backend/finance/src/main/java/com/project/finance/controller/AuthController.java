package com.project.finance.controller;

import com.project.finance.config.JwtUtil;
import com.project.finance.entity.Users;
import com.project.finance.service.IUsersService;
import jakarta.validation.Valid;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private IUsersService userService;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public Map<String, Object> register(@Valid @RequestBody RegisterParam p) {
        System.out.println(p);
        System.out.println("1111");
        Users exist = userService.findByEmailOrUsernameOrPhone(p.getEmail());
        System.out.println("2222");
        if (exist != null) {
            throw new RuntimeException("Email already exists");
        }
        System.out.println("3333");
        Users u = new Users();
        u.setEmail(p.getEmail());
        u.setPhone(p.getPhone());
        u.setUsername(p.getUsername());
        u.setPasswordHash(passwordEncoder.encode(p.getPassword()));
        u.setNickname(p.getUsername());
        userService.createUser(u);
        System.out.println("4444");
        String token = jwtUtil.generateToken(u.getId());
        Map<String, Object> res = new HashMap<>();
        res.put("user", u);
        res.put("token", token);
        return res;
    }

    @PostMapping("/login")
    public Map<String, Object> login(@Valid @RequestBody LoginParam p) {
        Users u = userService.findByEmailOrUsernameOrPhone(p.getUsernameOrEmailOrPhone());
        if (u == null) throw new RuntimeException("Invalid credentials");
        if (!passwordEncoder.matches(p.getPassword(), u.getPasswordHash())) throw new RuntimeException("Invalid credentials");
        String token = jwtUtil.generateToken(u.getId());
        Map<String, Object> res = new HashMap<>();
        Map<String, Object> userDto = Map.of(
                "id", u.getId(),
                "nickname", u.getNickname(),
                "email", u.getEmail(),
                "username", u.getUsername(),
                "avatar", u.getAvatar()
        );
        res.put("user", userDto);
        res.put("token", token);
        return res;
    }

    @Data
    static class RegisterParam {
        private String email;
        private String phone;
        private String password;
        private String username;
    }

    @Data
    static class LoginParam {
        private String usernameOrEmailOrPhone;
        private String password;
    }
}