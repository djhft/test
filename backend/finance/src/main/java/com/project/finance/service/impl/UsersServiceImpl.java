package com.project.finance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.project.finance.entity.Users;
import com.project.finance.mapper.UsersMapper;
import com.project.finance.service.IUsersService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author pan
 * @since 2025-12-14
 */
@Service
public class UsersServiceImpl extends ServiceImpl<UsersMapper, Users> implements IUsersService {
    @Autowired
    private UsersMapper userMapper;

    @Override
    public Users findByEmailOrUsernameOrPhone(String key) {
        if (key == null) return null;
        try {
            QueryWrapper<Users> qw = new QueryWrapper<>();
            qw.eq("email", key)
                    .or()
                    .eq("username", key)
                    .or()
                    .eq("phone", key);
            Users user = userMapper.selectOne(qw);
            return user;
        } catch (Exception e) {
            System.out.println("Error occurred: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }


    @Override
    public Users createUser(Users user) {
        System.out.println("8888");
        userMapper.insert(user);
        System.out.println("Mapper instance: " + userMapper);
        return user;
    }

    @Override
    public Users findById(Long id) {
        return userMapper.selectById(id);
    }

    @Override
    public Users updateUser(Users user) {
        userMapper.updateById(user);
        return userMapper.selectById(user.getId());
    }
}
