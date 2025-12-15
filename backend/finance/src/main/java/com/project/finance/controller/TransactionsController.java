package com.project.finance.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.project.finance.entity.Transactions;
import com.project.finance.service.ITransactionsService;
import com.project.finance.util.JsonUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.ibatis.transaction.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;

import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author pan
 * @since 2025-12-14
 */
@RestController
@RequestMapping("/api/transactions")
public class TransactionsController {
    @Autowired
    private ITransactionsService transactionService;

    @GetMapping
    public Page<Transactions> list(
            @RequestParam(required = false) String start,
            @RequestParam(required = false) String end,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer pageSize,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Long categoryId,
            HttpServletRequest request) {

        Long uid = (Long) request.getAttribute("userId");
        return transactionService.pageByUser(uid, page, pageSize, start, end, type, categoryId);
    }

    @PostMapping
    public Transactions create(@RequestBody Map<String, Object> body, HttpServletRequest request) {
        Long uid = (Long) request.getAttribute("userId");
        Transactions t = new Transactions();
        if (body.get("amount") == null) throw new RuntimeException("amount required");
        t.setAmount(new java.math.BigDecimal(String.valueOf(body.get("amount"))));
        t.setCurrency(body.getOrDefault("currency","CNY").toString());
        t.setType(body.get("type").toString());
        t.setDate(java.time.LocalDate.parse(body.get("date").toString()));
        if (body.containsKey("categoryId")) t.setCategoryId(Long.valueOf(String.valueOf(body.get("categoryId"))));
        if (body.containsKey("note")) t.setNote((String) body.get("note"));
        if (body.containsKey("tags")) t.setTags(JsonUtil.toJson(body.get("tags")));
        t.setUserId(uid);
        return transactionService.create(t);
    }

    @GetMapping("/{id}")
    public Transactions get(@PathVariable Long id, HttpServletRequest request) {
        Long uid = (Long) request.getAttribute("userId");
        Transactions t = transactionService.findById(id);
        if (t == null || !t.getUserId().equals(uid)) throw new RuntimeException("Not found");
        return t;
    }

    @PutMapping("/{id}")
    public Transactions update(@PathVariable Long id, @RequestBody Map<String,Object> body, HttpServletRequest request) {
        Long uid = (Long) request.getAttribute("userId");
        Transactions exist = transactionService.findById(id);
        if (exist == null || !exist.getUserId().equals(uid)) throw new RuntimeException("Not found");
        if (body.containsKey("amount")) exist.setAmount(new java.math.BigDecimal(String.valueOf(body.get("amount"))));
        if (body.containsKey("currency")) exist.setCurrency(String.valueOf(body.get("currency")));
        if (body.containsKey("type")) exist.setType(String.valueOf(body.get("type")));
        if (body.containsKey("date")) exist.setDate(java.time.LocalDate.parse(String.valueOf(body.get("date"))));
        if (body.containsKey("categoryId")) exist.setCategoryId(Long.valueOf(String.valueOf(body.get("categoryId"))));
        if (body.containsKey("note")) exist.setNote(String.valueOf(body.get("note")));
        if (body.containsKey("tags")) exist.setTags(JsonUtil.toJson(body.get("tags")));
        return transactionService.update(exist);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id, HttpServletRequest request) {
        Long uid = (Long) request.getAttribute("userId");
        Transactions t = transactionService.findById(id);
        if (t == null || !t.getUserId().equals(uid)) throw new RuntimeException("Not found");
        transactionService.delete(id);
    }
}