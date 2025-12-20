package com.project.finance.controller;


import com.project.finance.entity.SavingsAccounts;
import com.project.finance.mapper.SavingsAccountsMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


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
@RequestMapping("/api/savings_accounts")
public class SavingsController {
    @Autowired
    private SavingsAccountsMapper savingsMapper;

    @GetMapping
    public List<SavingsAccounts> list(HttpServletRequest request) {
        try {
            System.out.println("1111");
            Long uid = (Long) request.getAttribute("userId");
            System.out.println(savingsMapper.selectList(null).stream().filter(s -> s.getUserId().equals(uid)).toList());
            return savingsMapper.selectList(null).stream().filter(s -> s.getUserId().equals(uid)).toList();
        } catch (Exception e) {
            throw new RuntimeException("Not found");
        }

    }

    @PostMapping
    public SavingsAccounts create(@RequestBody SavingsAccounts s, HttpServletRequest request) {
        try {
            System.out.println(s);
            Long uid = (Long) request.getAttribute("userId");
            s.setUserId(uid);
            savingsMapper.insert(s);
            return s;
        }catch (Exception e){
            System.out.println(e);
            throw new RuntimeException("Not found");
        }

    }

    @PutMapping("/{id}")
    public SavingsAccounts update(@PathVariable Long id, @RequestBody SavingsAccounts s, HttpServletRequest request) {
        Long uid = (Long) request.getAttribute("userId");
        SavingsAccounts exist = savingsMapper.selectById(id);
        if (exist == null || !exist.getUserId().equals(uid)) throw new RuntimeException("Not found");
        s.setId(id);
        s.setUserId(uid);
        savingsMapper.updateById(s);
        return savingsMapper.selectById(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id, HttpServletRequest request) {
        System.out.println("delete");
        System.out.println(id);
        Long uid = (Long) request.getAttribute("userId");
        SavingsAccounts exist = savingsMapper.selectById(id);
        if (exist == null || !exist.getUserId().equals(uid)) throw new RuntimeException("Not found");
        savingsMapper.deleteById(id);
    }
}
