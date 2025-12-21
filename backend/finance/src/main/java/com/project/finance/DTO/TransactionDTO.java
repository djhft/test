package com.project.finance.DTO;

import lombok.Data;

@Data
public class TransactionDTO {
    private String type;
    private Integer categoryId;
    private String amount;
    private String date;
    private String time;
    private String description;
}
