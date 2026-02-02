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


    String typechange(Long id);
}
