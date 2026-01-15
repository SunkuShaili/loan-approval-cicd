import { ApplyLoanComponent } from './apply-loan';
import { LoanService } from '../../core/services/loan.service';
import { Router } from '@angular/router';
import { FormBuilder } from '@angular/forms';
import { of, throwError } from 'rxjs';

describe('ApplyLoanComponent (Jasmine + Karma)', () => {
  let component: ApplyLoanComponent;

  let loanServiceMock: jasmine.SpyObj<LoanService>;
  let routerMock: jasmine.SpyObj<Router>;

  beforeEach(() => {
    loanServiceMock = jasmine.createSpyObj('LoanService', ['createLoan']);
    routerMock = jasmine.createSpyObj('Router', ['navigate']);

    component = new ApplyLoanComponent(
      new FormBuilder(),
      loanServiceMock,
      routerMock
    );
  });

  it('should create component', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize form with required controls', () => {
    expect(component.loanForm).toBeTruthy();
    expect(component.loanForm.get('clientName')).toBeTruthy();
    expect(component.loanForm.get('financials')).toBeTruthy();
  });

  it('should mark form invalid when empty', () => {
    expect(component.loanForm.invalid).toBeTrue();
  });

  it('should calculate EMI preview when valid values are provided', () => {
    component.loanForm.patchValue({
      amount: 100000,
      tenureMonths: 12,
      interestRate: 12
    });

    component.calculateEmiPreview();

    expect(component.emi).not.toBeNull();
    expect(component.totalInterest).not.toBeNull();
    expect(component.totalPayable).not.toBeNull();
  });

  it('should reset EMI preview when values are missing', () => {
    component.loanForm.patchValue({
      amount: null,
      tenureMonths: null,
      interestRate: null
    });

    component.calculateEmiPreview();

    expect(component.emi).toBeNull();
    expect(component.totalInterest).toBeNull();
    expect(component.totalPayable).toBeNull();
  });

  it('should submit loan successfully and navigate', () => {
    component.loanForm.patchValue({
      clientName: 'Test',
      amount: 100000,
      tenureMonths: 12,
      interestRate: 12,
      loanType: 'WorkingCapital',
      financials: {
        revenue: 500000,
        ebitda: 100000,
        rating: 'A'
      }
    });

    loanServiceMock.createLoan.and.returnValue(of({}));

    component.submitLoan();

    expect(loanServiceMock.createLoan).toHaveBeenCalled();
    expect(routerMock.navigate).toHaveBeenCalledWith(['/dashboard/loans']);
    expect(component.loading).toBeFalse();
  });

  it('should handle error when loan submission fails', () => {
    spyOn(console, 'error').and.stub();

    component.loanForm.patchValue({
      clientName: 'Test',
      amount: 100000,
      tenureMonths: 12,
      interestRate: 12,
      loanType: 'WorkingCapital',
      financials: {
        revenue: 500000,
        ebitda: 100000,
        rating: 'A'
      }
    });

    loanServiceMock.createLoan.and.returnValue(
      throwError(() => new Error('API error'))
    );

    component.submitLoan();

    expect(component.loading).toBeFalse();
    expect(component.errorMsg).toBe('Loan submission failed');
  });

  it('should not submit loan if form is invalid', () => {
    component.submitLoan();

    expect(loanServiceMock.createLoan).not.toHaveBeenCalled();
  });

  it('should confirm and submit loan when user accepts confirmation', () => {
    spyOn(window, 'confirm').and.returnValue(true);
    spyOn(component, 'submitLoan');

    component.loanForm.patchValue({
      clientName: 'Test',
      amount: 100000,
      tenureMonths: 12,
      interestRate: 12,
      loanType: 'WorkingCapital',
      financials: {
        revenue: 500000,
        ebitda: 100000,
        rating: 'A'
      }
    });

    component.confirmSubmitLoan();

    expect(component.submitLoan).toHaveBeenCalled();
  });

  it('should not submit loan when confirmation is cancelled', () => {
    spyOn(window, 'confirm').and.returnValue(false);
    spyOn(component, 'submitLoan');

    component.confirmSubmitLoan();

    expect(component.submitLoan).not.toHaveBeenCalled();
  });
});
