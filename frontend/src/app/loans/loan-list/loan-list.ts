import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LoanService } from '../../core/services/loan.service';
import { Loan } from '../../models/loan.model';
import { PageResponse } from '../../models/page-response.model';
import { Router } from '@angular/router';




@Component({
  selector: 'app-loan-list',
  standalone: true,
  imports: [CommonModule],   
  templateUrl: './loan-list.html',
  styleUrls: ['./loan-list.css']
})
export class LoanListComponent implements OnInit {

  loans: Loan[] = [];
  loading = true;

  page = 0;
  size = 10;

  constructor(
  private loanService: LoanService,
  private router: Router
) {}

isAdmin = false;




ngOnInit() {
  this.isAdmin = this.router.url.includes('review-loans');
  this.loadLoans();
}


  loadLoans(): void {
    this.loading = true;

    this.loanService.getLoans(this.page, this.size).subscribe({
      next: (res: PageResponse<Loan>) => {
        this.loans = res.content;
        this.loading = false;
      },
      error: (err) => {
        console.error(err);
        this.loading = false;
      }
    });
  }

  submitLoan(loanId: string) {
    const payload = {
      status: 'SUBMITTED',
      comments: 'Submitted by user'
    };

    this.loanService.updateLoanStatus(loanId, payload).subscribe({
      next: () => {
        this.loadLoans();
      },
      error: (err) => {
        console.error(err);
        alert('Failed to submit loan');
      }
    });
  }


  reviewLoan(loanId: string) {
  const payload = {
    status: 'UNDER_REVIEW',
    comments: 'Review started by admin'
  };

  this.loanService.updateLoanStatus(loanId, payload).subscribe({
    next: () => this.loadLoans(),
    error: (err) => {
      console.error(err);
      alert('Failed to move loan to review');
    }
  });
}

approveLoan(loanId: string) {
  const payload = {
    status: 'APPROVED',
    comments: 'Approved by admin'
  };

  this.loanService.updateLoanStatus(loanId, payload).subscribe({
    next: () => this.loadLoans(),
    error: (err) => {
      console.error(err);
      alert('Failed to approve loan');
    }
  });
}

rejectLoan(loanId: string) {
  const payload = {
    status: 'REJECTED',
    comments: 'Rejected by admin'
  };

  this.loanService.updateLoanStatus(loanId, payload).subscribe({
    next: () => this.loadLoans(),
    error: (err) => {
      console.error(err);
      alert('Failed to reject loan');
    }
  });
}


deleteLoan(loanId: string) {
  const confirmDelete = confirm(
    'Are you sure you want to delete this loan? This action cannot be undone.'
  );

  if (!confirmDelete) {
    return;
  }

  this.loanService.deleteLoan(loanId).subscribe({
    next: () => {
      this.loadLoans();   // refresh table
    },
    error: (err) => {
      console.error(err);
      alert('Failed to delete loan');
    }
  });
}



confirmReview(loanId: string) {
  if (!confirm('Move this loan to review?')) return;
  this.reviewLoan(loanId);
}

confirmApprove(loanId: string) {
  if (!confirm('Are you sure you want to APPROVE this loan?')) return;
  this.approveLoan(loanId);
}

confirmReject(loanId: string) {
  if (!confirm('Are you sure you want to REJECT this loan?')) return;
  this.rejectLoan(loanId);
}

confirmDelete(loanId: string) {
  if (!confirm('This action is irreversible. Delete this loan?')) return;
  this.deleteLoan(loanId);
}

confirmSubmit(loanId: string) {
  if (!confirm('Submit this loan for review?')) return;
  this.submitLoan(loanId);
}



}
