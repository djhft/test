package com.project.finance.mapper;

import com.project.finance.entity.Users;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author pan
 * @since 2025-12-14
 */
@Mapper
public interface UsersMapper extends BaseMapper<Users> {
    // 不需要写任何方法，BaseMapper已经提供了
}

