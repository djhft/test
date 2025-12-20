package com.project.finance.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.project.finance.DTO.CategoryExpenseDTO;
import com.project.finance.DTO.SummaryDTO;
import com.project.finance.entity.Transactions;
import com.project.finance.service.ITransactionsService;
import com.project.finance.util.JsonUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.ibatis.transaction.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
    public Transactions create(@RequestBody Map<String, Object> body, HttpServletRequest request) throws JsonProcessingException {

        Long uid = (Long) request.getAttribute("userId");
        if (uid == null) {
            throw new RuntimeException("用户未登录");
        }

        Transactions t = new Transactions();

        // 金额
        if (body.get("amount") == null) {
            throw new RuntimeException("金额不能为空");
        }
        t.setAmount(new java.math.BigDecimal(String.valueOf(body.get("amount"))));

        // 货币
        t.setCurrency(body.getOrDefault("currency", "CNY").toString());

        // 类型
        if (body.get("type") == null) {
            throw new RuntimeException("交易类型不能为空");
        }
        t.setType(body.get("type").toString());

        // 日期 - 修复这里
        if (body.get("transactionDate") == null) {
            throw new RuntimeException("交易日期不能为空");
        }
        try {
            t.setDate(java.time.LocalDate.parse(body.get("transactionDate").toString()));
        } catch (Exception e) {
            throw new RuntimeException("日期格式错误，请使用 yyyy-MM-dd 格式");
        }

        // 分类ID
        if (body.containsKey("category")) {
            try {
                t.setCategoryId(Long.valueOf(String.valueOf(body.get("category"))));
            } catch (NumberFormatException e) {
                throw new RuntimeException("分类ID格式错误");
            }
        }

        // 备注
        if (body.containsKey("description")) {
            t.setNote((String) body.get("description"));
        }

        // 标签
        if (body.containsKey("tags")) {
            t.setTags(JsonUtil.toJson(body.get("tags")));
        }

        // 附件
        if (body.containsKey("attachmentUrls")) {
            t.setTags(JsonUtil.toJson(body.get("attachmentUrls")));
        }

        // 用户ID
        t.setUserId(uid);

        try {
            return transactionService.create(t);
        } catch (Exception e) {
            throw new RuntimeException("创建交易记录失败: " + e.getMessage());
        }
    }


    @GetMapping("/{id}")
    public Transactions get(@PathVariable Long id, HttpServletRequest request) {
        Long uid = (Long) request.getAttribute("userId");
        Transactions t = transactionService.findById(id);
        if (t == null || !t.getUserId().equals(uid)) throw new RuntimeException("Not found");
        return t;
    }

    @PutMapping("/{id}")
    public Transactions update(@PathVariable Long id, @RequestBody Map<String,Object> body, HttpServletRequest request) throws JsonProcessingException {
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

    //支出交易查询（按分类查询）
    @GetMapping("/spending")
    public List<CategoryExpenseDTO> spending(
            @RequestParam(required = false) String start,
            @RequestParam(required = false) String end,
            HttpServletRequest request) {
        Long uid = (Long) request.getAttribute("userId");
        return transactionService.findByUserIdAndTypeAndDateBetween(uid, start, end);
    }

    //支出消费查询汇总（按时间）
    @GetMapping("/spending/expense_summary")
    public List<SummaryDTO> spendingExpenseSummary(
            @RequestParam(required = false) String start,
            @RequestParam(required = false) String end,
            HttpServletRequest request) {
        Long uid = (Long) request.getAttribute("userId");
        return transactionService.findExpenseSummaryByUserIdAndTypeAndDateBetween(uid, start, end);
    }

    @GetMapping("/spending/income_summary")
    public List<SummaryDTO> spendingIncomeSummary(
            @RequestParam(required = false) String start,
            @RequestParam(required = false) String end,
            HttpServletRequest request) {
        try{
            System.out.println("11111111");
            System.out.println(start);
            System.out.println(end);
            Long uid = (Long) request.getAttribute("userId");
            System.out.println(uid);
            return transactionService.findIncomeSummaryByUserIdAndTypeAndDateBetween(uid, start, end);
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
        return null;
    }
}