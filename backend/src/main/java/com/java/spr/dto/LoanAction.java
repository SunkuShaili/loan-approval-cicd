package com.java.spr.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;

@Data
@Builder
public class LoanAction {

    private String actionBy;
    private String action;
    private String comments;
    private LocalDateTime actionAt;
}
