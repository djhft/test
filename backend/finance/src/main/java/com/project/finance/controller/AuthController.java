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
        Users exist = userService.findByEmailOrUsernameOrPhone(p.getEmail());
        if (exist != null) {
            throw new RuntimeException("邮箱已存在");
        }
        exist = userService.findByEmailOrUsernameOrPhone(p.getPhone());
        if (exist != null) {
            throw new RuntimeException("手机号已存在");
        }

        Users u = new Users();
        u.setEmail(p.getEmail());
        u.setPhone(p.getPhone());
        u.setUsername(p.getUsername());
        u.setPasswordHash(passwordEncoder.encode(p.getPassword()));
        u.setNickname(p.getUsername());
        userService.createUser(u);
        String token = jwtUtil.generateToken(u.getId());
        Map<String, Object> res = new HashMap<>();
        res.put("user", u);
        res.put("token", token);
        return res;
    }

    @PostMapping("/login")
    public Map<String, Object> login(@Valid @RequestBody LoginParam p) {
        try {
            Users u = userService.findByEmailOrUsernameOrPhone(p.getUsernameOrEmailOrPhone());
            if (u == null) {
                throw new RuntimeException("该用户不存在");
            }

            if (!passwordEncoder.matches(p.getPassword(), u.getPasswordHash())) {
                throw new RuntimeException("密码错误");
            }

            String token = jwtUtil.generateToken(u.getId());

            Map<String, Object> res = new HashMap<>();
            Map<String, Object> userDto = Map.of(
                    "id", u.getId(),
                    "nickname", u.getNickname(),
                    "email", u.getEmail(),
                    "username", u.getUsername()
            );
            res.put("user", userDto);
            res.put("token", token);

            return res;
        } catch (Exception e) {
            System.out.println("登录出错: " + e.getMessage());
            e.printStackTrace();
            throw e; // 重新抛出异常
        }
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