package com.project.finance.DTO;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CategoryExpenseDTO {
    private Long categoryId;
    private BigDecimal totalAmount;
}
