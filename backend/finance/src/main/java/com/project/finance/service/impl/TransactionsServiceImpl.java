package com.project.finance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.project.finance.DTO.CategoryExpenseDTO;
import com.project.finance.DTO.SummaryDTO;
import com.project.finance.entity.Transactions;
import com.project.finance.mapper.BudgetsMapper;
import com.project.finance.mapper.TransactionsMapper;
import com.project.finance.service.ITransactionsService;
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
public class TransactionsServiceImpl extends ServiceImpl<TransactionsMapper, Transactions> implements ITransactionsService {
    @Autowired
    private TransactionsMapper transactionMapper;

    @Autowired
    private BudgetsMapper budgetsMapper;

    @Override
    public Page<Transactions> pageByUser(Long userId, int page, int pageSize, String start, String end, String type, Long categoryId) {
        QueryWrapper<Transactions> qw = new QueryWrapper<>();
        qw.eq("user_id", userId);
        if (type != null) qw.eq("type", type);
        if (categoryId != null) qw.eq("category_id", categoryId);
        if (start != null) qw.ge("date", start);
        if (end != null) qw.le("date", end);
        qw.orderByDesc("date");
        return transactionMapper.selectPage(new Page<>(page, pageSize), qw);
    }

    @Override
    public Transactions create(Transactions t) {
        System.out.println(t);
        UpdateWrapper updateWrapper = new UpdateWrapper();
        updateWrapper.setSql("used = used + " + t.getAmount());
        updateWrapper.eq("user_id", t.getUserId());
        updateWrapper.eq("category_id", t.getCategoryId());
        budgetsMapper.update(null,updateWrapper);
        transactionMapper.insert(t);
        return t;
    }

    @Override
    public Transactions findById(Long id) {
        return transactionMapper.selectById(id);
    }

    @Override
    public Transactions update(Transactions t) {
        transactionMapper.updateById(t);
        return transactionMapper.selectById(t.getId());
    }

    @Override
    public void delete(Long id) {
        transactionMapper.deleteById(id);
    }

    @Override
    public List<CategoryExpenseDTO> findByUserIdAndTypeAndDateBetween(Long uid, String start, String end) {
        //根据时间获取全部支出数据,并根据category_id进行汇总
        return transactionMapper.findByUserIdAndTypeAndDateBetween(uid, start, end);


    }

    @Override
    public List<SummaryDTO> findExpenseSummaryByUserIdAndTypeAndDateBetween(Long uid, String start, String end) {
        return transactionMapper.findSummaryByUserIdAndTypeAndDateBetween(uid, start, end);
    }

    @Override
    public List<SummaryDTO> findIncomeSummaryByUserIdAndTypeAndDateBetween(Long uid, String start, String end) {
        return transactionMapper.findIncomeSummaryByUserIdAndTypeAndDateBetween(uid, start, end);
    }

    //根据时间获取收支数据
    @Override
    public List<Transactions> findByUserIdAndDateBetween(Long uid, String start, String end) {
        return transactionMapper.findByUserIdAndDateBetween(uid, start, end);
    }
}
