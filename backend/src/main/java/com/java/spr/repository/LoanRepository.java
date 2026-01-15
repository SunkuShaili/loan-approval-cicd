package com.java.spr.repository;

import com.java.spr.model.Loan;
import com.java.spr.model.enums.LoanStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface LoanRepository extends MongoRepository<Loan, String> {


    // -------- USER --------


    Page<Loan> findByApplicantEmailAndDeletedFalse(
            String applicantEmail,
            Pageable pageable
    );


    Page<Loan> findByApplicantEmailAndStatusAndDeletedFalse(
            String applicantEmail,
            LoanStatus status,
            Pageable pageable
    );

    // -------- ADMIN --------


    Page<Loan> findByDeletedFalse(Pageable pageable);


    Page<Loan> findByStatusAndDeletedFalse(
            LoanStatus status,
            Pageable pageable
    );
}
