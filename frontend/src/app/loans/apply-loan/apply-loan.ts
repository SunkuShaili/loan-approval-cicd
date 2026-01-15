import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  FormBuilder,
  FormGroup,
  Validators,
  ReactiveFormsModule,
  FormsModule
} from '@angular/forms';
import { Router } from '@angular/router';
import { LoanService } from '../../core/services/loan.service';

@Component({
  selector: 'app-apply-loan',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule],
  templateUrl: './apply-loan.html',
  styleUrls: ['./apply-loan.css']
})
export class ApplyLoanComponent {

  loanForm: FormGroup;
  loading = false;
  successMsg = '';
  errorMsg = '';

  // 🔹 NEW: EMI preview values
  emi: number | null = null;
  totalInterest: number | null = null;
  totalPayable: number | null = null;

  constructor(
    private fb: FormBuilder,
    private loanService: LoanService,
    private router: Router
  ) {
    this.loanForm = this.fb.group({
      clientName: ['', Validators.required],
      amount: ['', Validators.required],
      tenureMonths: ['', Validators.required],
      interestRate: ['', Validators.required],
      loanType: ['WorkingCapital', Validators.required],
      financials: this.fb.group({
        revenue: ['', Validators.required],
        ebitda: ['', Validators.required],
        rating: ['', Validators.required]
      })
    });

    this.loanForm.valueChanges.subscribe(() => {
      this.calculateEmiPreview();
    });
  }

  
  calculateEmiPreview() {
    const amount = this.loanForm.get('amount')?.value;
    const tenure = this.loanForm.get('tenureMonths')?.value;
    const interest = this.loanForm.get('interestRate')?.value;

    if (!amount || !tenure || !interest) {
      this.emi = this.totalInterest = this.totalPayable = null;
      return;
    }

    const monthlyRate = interest / 12 / 100;
    const n = tenure;

    const emi =
      (amount * monthlyRate * Math.pow(1 + monthlyRate, n)) /
      (Math.pow(1 + monthlyRate, n) - 1);

    const totalPayable = emi * n;
    const totalInterest = totalPayable - amount;

    this.emi = Math.round(emi);
    this.totalPayable = Math.round(totalPayable);
    this.totalInterest = Math.round(totalInterest);
  }

  
  submitLoan() {
    if (this.loanForm.invalid) {
      return;
    }

    this.loading = true;
    this.errorMsg = '';
    this.successMsg = '';

    this.loanService.createLoan(this.loanForm.value).subscribe({
      next: () => {
        this.loading = false;
        this.router.navigate(['/dashboard/loans']);
      },
      error: (err) => {
        console.error(err);
        this.loading = false;
        this.errorMsg = 'Loan submission failed';
      }
    });
  }



  confirmSubmitLoan() {
    if (this.loanForm.invalid) {
      this.loanForm.markAllAsTouched();
      return;
    }

    const confirmed = window.confirm(
      'Are you sure you want to submit this loan?\n\nYou will not be able to edit it after submission.'
    );

    if (!confirmed) {
      return;
    }

    this.submitLoan(); // existing method 
  }



}
