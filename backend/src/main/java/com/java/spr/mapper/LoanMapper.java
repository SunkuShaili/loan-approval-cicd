package com.java.spr.mapper;

import com.java.spr.dto.LoanAction;
import com.java.spr.dto.LoanResponse;
import com.java.spr.model.Loan;
import jakarta.validation.constraints.NotNull;

import java.util.stream.Collectors;
public class LoanMapper {

    public static LoanResponse toDto(Loan loan) {

        return LoanResponse.builder()
                .id(loan.getId())
                .applicantEmail(loan.getApplicantEmail())
                .amount(loan.getAmount())
                .tenureMonths(loan.getTenureMonths())
                .interestRate(loan.getInterestRate())

                // pricing
                .emi(loan.getEmi())
                .totalPayableAmount(loan.getTotalPayableAmount())
                .totalInterest(loan.getTotalInterest())


                .clientName(loan.getClientName())
                .loanType(loan.getLoanType())
                .financials(loan.getFinancials())

                // status & audit
                .status(loan.getStatus())
                .createdAt(loan.getCreatedAt())
                .actions(
                        loan.getActions() == null ? null :
                                loan.getActions().stream()
                                        .map(a -> com.java.spr.dto.LoanAction.builder()
                                                .actionBy(a.getBy())
                                                .action(a.getAction())
                                                .comments(a.getComments())
                                                .actionAt(a.getTimestamp())
                                                .build())
                                        .toList()
                )
                .build();
    }
}





