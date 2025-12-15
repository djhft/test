package com.project.finance.service;

import com.project.finance.entity.Users;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author pan
 * @since 2025-12-14
 */
public interface IUsersService extends IService<Users> {
    Users findByEmailOrUsernameOrPhone(String key);
    Users createUser(Users user);
    Users findById(Long id);
    Users updateUser(Users user);
}
