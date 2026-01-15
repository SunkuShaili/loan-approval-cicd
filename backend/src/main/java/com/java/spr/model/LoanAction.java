package com.java.spr.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoanAction {

    private String by;          // email
    private String action;      // SUBMITTED / APPROVED / REJECTED
    private String comments;
    private LocalDateTime timestamp;
}
