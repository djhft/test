package com.project.finance.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.project.finance.DTO.CategoryExpenseDTO;
import com.project.finance.DTO.SummaryDTO;
import com.project.finance.entity.Transactions;
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
public interface ITransactionsService extends IService<Transactions> {
    Page<Transactions> pageByUser(Long userId, int page, int pageSize, String start, String end, String type, Long categoryId);


    Transactions create(Transactions t);

    Transactions findById(Long id);
    Transactions update(Transactions t);
    void delete(Long id);

    List<CategoryExpenseDTO> findByUserIdAndTypeAndDateBetween(Long uid, String start, String end);

    List<SummaryDTO> findExpenseSummaryByUserIdAndTypeAndDateBetween(Long uid, String start, String end);

    List<SummaryDTO> findIncomeSummaryByUserIdAndTypeAndDateBetween(Long uid, String start, String end);
}
