package com.project.finance.mapper;

import com.project.finance.DTO.CategoryExpenseDTO;
import com.project.finance.DTO.SummaryDTO;
import com.project.finance.entity.Transactions;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author pan
 * @since 2025-12-14
 */
@Mapper
public interface TransactionsMapper extends BaseMapper<Transactions> {

    List<CategoryExpenseDTO> findByUserIdAndTypeAndDateBetween(Long uid, String start, String end);

    List<SummaryDTO> findSummaryByUserIdAndTypeAndDateBetween(Long uid, String start, String end);

    List<SummaryDTO> findIncomeSummaryByUserIdAndTypeAndDateBetween(Long uid, String start, String end);
}
