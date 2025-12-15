package com.project.finance.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.project.finance.entity.Transactions;
import com.project.finance.mapper.TransactionsMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/stats")
public class StatsController {
    @Autowired
    private TransactionsMapper transactionMapper;

    @GetMapping("/summary")
    public Map<String,Object> summary(@RequestParam String start, @RequestParam String end, HttpServletRequest request) {
        Long uid = (Long) request.getAttribute("userId");
        QueryWrapper<Transactions> qw = new QueryWrapper<>();
        qw.eq("user_id", uid).ge("date", start).le("date", end);
        List<Transactions> list = transactionMapper.selectList(qw);
        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal totalExpense = BigDecimal.ZERO;
        Map<Long, BigDecimal> byCategory = new HashMap<>();
        Map<Long, String> catNames = new HashMap<>();
        for (Transactions t : list) {
            if ("income".equalsIgnoreCase(t.getType())) totalIncome = totalIncome.add(t.getAmount());
            else if ("expense".equalsIgnoreCase(t.getType())) totalExpense = totalExpense.add(t.getAmount());
            if (t.getCategoryId() != null) {
                byCategory.putIfAbsent(t.getCategoryId(), BigDecimal.ZERO);
                byCategory.put(t.getCategoryId(), byCategory.get(t.getCategoryId()).add(t.getAmount()));
            }
        }
        List<Map<String,Object>> byCategoryList = new ArrayList<>();
        for (Map.Entry<Long, BigDecimal> e : byCategory.entrySet()) {
            Map<String,Object> m = new HashMap<>();
            m.put("categoryId", e.getKey());
            m.put("name", "Category " + e.getKey()); // 前端显示可进一步 join categories 表
            m.put("amount", e.getValue());
            byCategoryList.add(m);
        }
        Map<String,Object> res = new HashMap<>();
        res.put("totalIncome", totalIncome);
        res.put("totalExpense", totalExpense);
        res.put("net", totalIncome.subtract(totalExpense));
        res.put("byCategory", byCategoryList);
        return res;
    }

    @GetMapping("/series")
    public Map<String,Object> series(@RequestParam String start, @RequestParam String end, @RequestParam(defaultValue = "daily") String period, HttpServletRequest request) {
        Long uid = (Long) request.getAttribute("userId");
        // 简单 daily 实现：构造日期 labels，并按日汇总 expense
        LocalDate startDate = LocalDate.parse(start);
        LocalDate endDate = LocalDate.parse(end);
        List<String> labels = new ArrayList<>();
        List<Number> series = new ArrayList<>();
        for (LocalDate d = startDate; !d.isAfter(endDate); d = d.plusDays(1)) {
            labels.add(d.toString());
            QueryWrapper<Transactions> qw = new QueryWrapper<>();
            qw.eq("user_id", uid).eq("date", d.toString()).eq("type", "expense");
            List<Transactions> items = transactionMapper.selectList(qw);
            BigDecimal sum = items.stream().map(Transactions::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
            series.add(sum);
        }
        return Map.of("labels", labels, "series", series);
    }
}