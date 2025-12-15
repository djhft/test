package com.project.finance.controller;


import com.project.finance.entity.Budgets;
import com.project.finance.mapper.BudgetsMapper;
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
@RequestMapping("/api/budgets")
public class BudgetsController {
    @Autowired
    private BudgetsMapper budgetMapper;

    @GetMapping
    public List<Budgets> list(@RequestParam(required = false) String month, HttpServletRequest request) {
        Long uid = (Long) request.getAttribute("userId");
        // 简单实现：返回所有用户预算，前端可过滤
        return budgetMapper.selectList(null).stream().filter(b -> b.getUserId().equals(uid)).toList();
    }

    @PostMapping
    public Budgets create(@RequestBody Budgets b, HttpServletRequest request) {
        Long uid = (Long) request.getAttribute("userId");
        b.setUserId(uid);
        budgetMapper.insert(b);
        return b;
    }

    @PutMapping("/{id}")
    public Budgets update(@PathVariable Long id, @RequestBody Budgets b, HttpServletRequest request) {
        Long uid = (Long) request.getAttribute("userId");
        Budgets exist = budgetMapper.selectById(id);
        if (exist == null || !exist.getUserId().equals(uid)) throw new RuntimeException("Not found");
        b.setId(id);
        b.setUserId(uid);
        budgetMapper.updateById(b);
        return budgetMapper.selectById(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id, HttpServletRequest request) {
        Long uid = (Long) request.getAttribute("userId");
        Budgets exist = budgetMapper.selectById(id);
        if (exist == null || !exist.getUserId().equals(uid)) throw new RuntimeException("Not found");
        budgetMapper.deleteById(id);
    }
}
