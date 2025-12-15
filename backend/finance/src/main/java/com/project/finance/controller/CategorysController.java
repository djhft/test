package com.project.finance.controller;


import com.project.finance.entity.Categories;
import com.project.finance.service.ICategoriesService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author pan
 * @since 2025-12-14
 */
@RestController
@RequestMapping("/api/categories")
public class CategorysController {
    @Autowired
    private ICategoriesService categoryService;

    @GetMapping
    public List<Categories> list(@RequestParam(required = false) String type, HttpServletRequest request) {
        Long uid = (Long) request.getAttribute("userId");
        return categoryService.list(uid, type);
    }

    @PostMapping
    public Categories create(@RequestBody Categories c, HttpServletRequest request) {
        Long uid = (Long) request.getAttribute("userId");
        c.setUserId(uid);
        return categoryService.create(c);
    }

    @PutMapping("/{id}")
    public Categories update(@PathVariable Long id, @RequestBody Categories c, HttpServletRequest request) {
        Long uid = (Long) request.getAttribute("userId");
        return categoryService.update(id, c, uid);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id, HttpServletRequest request) {
        Long uid = (Long) request.getAttribute("userId");
        categoryService.delete(id, uid);
    }
}
