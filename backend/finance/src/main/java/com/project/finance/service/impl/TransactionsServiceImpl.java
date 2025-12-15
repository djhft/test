package com.project.finance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.project.finance.entity.Transactions;
import com.project.finance.mapper.TransactionsMapper;
import com.project.finance.service.ITransactionsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.ibatis.transaction.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
