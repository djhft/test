package com.project.finance.service;

import com.project.finance.entity.Categories;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author pan
 * @since 2025-12-14
 */
public interface ICategoriesService extends IService<Categories> {
    List<Categories> list(Long userId, String type);
    Categories create(Categories c);
    Categories update(Long id, Categories c, Long userId);
    void delete(Long id, Long userId);
}
