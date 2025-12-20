package com.project.finance.config;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 这里需要实现从数据库加载用户信息的逻辑
        // 示例代码，需要根据你的实际用户表结构调整
        return User.withUsername(username)
                .password("") // 密码应该从数据库获取
                .roles("USER") // 角色应该从数据库获取
                .build();
    }
}
