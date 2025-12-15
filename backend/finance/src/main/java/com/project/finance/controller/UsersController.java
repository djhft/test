package com.project.finance.controller;


import com.project.finance.entity.Users;
import com.project.finance.service.IUsersService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;

import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author pan
 * @since 2025-12-14
 */
@RestController
@RequestMapping("/api/users")
public class UsersController {
    @Autowired
    private IUsersService userService;

    @GetMapping("/me")
    public Users me(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return userService.findById(userId);
    }

    @PutMapping("/me")
    public Users updateMe(@RequestBody Map<String, Object> body, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Users u = userService.findById(userId);
        if (body.containsKey("nickname")) u.setNickname((String) body.get("nickname"));
        if (body.containsKey("email")) u.setEmail((String) body.get("email"));
        if (body.containsKey("phone")) u.setPhone((String) body.get("phone"));
        if (body.containsKey("avatar")) u.setAvatar((String) body.get("avatar"));
        if (body.containsKey("password")) {
            // 密码更新要用 BCrypt，在此示例中假设 controller 注入了 encoder
            throw new RuntimeException("Use dedicated endpoint to change password"); // 简化示例
        }
        return userService.updateUser(u);
    }
}
