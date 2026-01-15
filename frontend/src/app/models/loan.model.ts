import { Financials } from './financials.model';

export interface Loan {
  id: string;
  applicantEmail: string;
  amount: number;
  tenureMonths: number;
  interestRate: number;
  emi: number;
  totalPayableAmount: number;
  totalInterest: number;
  clientName: string;
  loanType: string;
  financials: Financials;
  status: 'DRAFT' | 'SUBMITTED' | 'UNDER_REVIEW' | 'APPROVED' | 'REJECTED';
  createdAt: string;
}
