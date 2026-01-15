package com.java.spr.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LoanStatusRequest {

    private String status;     // SUBMITTED / UNDER_REVIEW / APPROVED / REJECTED
    private String comments;

}

