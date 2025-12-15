package com.project.finance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.project.finance.entity.Categories;
import com.project.finance.mapper.CategoriesMapper;
import com.project.finance.service.ICategoriesService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author pan
 * @since 2025-12-14
 */
@Service
public class CategoriesServiceImpl extends ServiceImpl<CategoriesMapper, Categories> implements ICategoriesService {
    @Autowired
    private CategoriesMapper categoryMapper;

    @Override
    public List<Categories> list(Long userId, String type) {
        QueryWrapper<Categories> qw = new QueryWrapper<>();
        if (type != null) qw.eq("type", type);
        // system (user_id is null) OR user_id = current user
        qw.and(wrapper -> wrapper.isNull("user_id").or().eq("user_id", userId));
        return categoryMapper.selectList(qw);
    }

    @Override
    public Categories create(Categories c) {
        categoryMapper.insert(c);
        return c;
    }

    @Override
    public Categories update(Long id, Categories c, Long userId) {
        Categories exist = categoryMapper.selectById(id);
        if (exist == null) throw new RuntimeException("Category not found");
        if (exist.getUserId() != null && !exist.getUserId().equals(userId)) throw new RuntimeException("Forbidden");
        c.setId(id);
        c.setUserId(exist.getUserId());
        categoryMapper.updateById(c);
        return categoryMapper.selectById(id);
    }

    @Override
    public void delete(Long id, Long userId) {
        Categories exist = categoryMapper.selectById(id);
        if (exist == null) throw new RuntimeException("Category not found");
        if (exist.getUserId() != null && !exist.getUserId().equals(userId)) throw new RuntimeException("Forbidden");
        categoryMapper.deleteById(id);
    }
}
