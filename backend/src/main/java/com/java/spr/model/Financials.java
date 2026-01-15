package com.java.spr.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Financials {

    private BigDecimal revenue;
    private BigDecimal ebitda;
    private String rating;
}

