package com.project.finance.DTO;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class SummaryDTO {
    private LocalDate date;        // 使用LocalDate类型
    private String type;          // 交易类型
    private BigDecimal totalAmount; // 总金额
}
