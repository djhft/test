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
    public String typechange(Long id) {
        QueryWrapper <Categories> qw = new QueryWrapper<>();
        qw.eq("id", id);
        return categoryMapper.selectOne(qw).getName();
    }
}
