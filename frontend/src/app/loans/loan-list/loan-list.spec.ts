import { LoanListComponent } from './loan-list';
import { LoanService } from '../../core/services/loan.service';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';

describe('LoanListComponent (Jasmine + Karma)', () => {
  let component: LoanListComponent;

  let loanServiceMock: jasmine.SpyObj<LoanService>;
  let routerMock: any; // Router.url is readonly → mock as any

  beforeEach(() => {
    loanServiceMock = jasmine.createSpyObj('LoanService', [
      'getLoans',
      'updateLoanStatus',
      'deleteLoan'
    ]);

    // Default safe response
    loanServiceMock.getLoans.and.returnValue(
      of({
        content: [],
        totalElements: 0,
        totalPages: 0,
        number: 0,
        size: 10,
        first: true,
        last: true,
        empty: true
      })
    );

    routerMock = {
      url: '/dashboard/loans'
    };

    component = new LoanListComponent(
      loanServiceMock,
      routerMock as Router
    );

    spyOn(window, 'alert').and.stub();
  });

  it('should create component', () => {
    expect(component).toBeTruthy();
  });

  it('should detect admin based on router url', () => {
    routerMock.url = '/admin/review-loans';

    component.ngOnInit();

    expect(component.isAdmin).toBeTrue();
  });

  it('should load loans on init', () => {
    loanServiceMock.getLoans.and.returnValue(
      of({
        content: [{ id: '1' } as any],
        totalElements: 1,
        totalPages: 1,
        number: 0,
        size: 10,
        first: true,
        last: true,
        empty: false
      })
    );

    component.ngOnInit();

    expect(loanServiceMock.getLoans).toHaveBeenCalledWith(0, 10);
    expect(component.loans.length).toBe(1);
    expect(component.loading).toBeFalse();
  });

  it('should handle error when loading loans fails', () => {
    spyOn(console, 'error').and.stub();

    loanServiceMock.getLoans.and.returnValue(
      throwError(() => new Error('API error'))
    );

    component.loadLoans();

    expect(component.loading).toBeFalse();
  });

  it('should submit loan and reload list', () => {
    loanServiceMock.updateLoanStatus.and.returnValue(of({}));

    component.submitLoan('123');

    expect(loanServiceMock.updateLoanStatus).toHaveBeenCalledWith('123', {
      status: 'SUBMITTED',
      comments: 'Submitted by user'
    });
  });

  it('should approve loan after confirmation', () => {
    spyOn(window, 'confirm').and.returnValue(true);
    loanServiceMock.updateLoanStatus.and.returnValue(of({}));

    component.confirmApprove('123');

    expect(loanServiceMock.updateLoanStatus).toHaveBeenCalledWith('123', {
      status: 'APPROVED',
      comments: 'Approved by admin'
    });
  });

  it('should reject loan after confirmation', () => {
    spyOn(window, 'confirm').and.returnValue(true);
    loanServiceMock.updateLoanStatus.and.returnValue(of({}));

    component.confirmReject('123');

    expect(loanServiceMock.updateLoanStatus).toHaveBeenCalledWith('123', {
      status: 'REJECTED',
      comments: 'Rejected by admin'
    });
  });

  it('should delete loan after confirmation', () => {
    spyOn(window, 'confirm').and.returnValue(true);
    loanServiceMock.deleteLoan.and.returnValue(of({}));

    component.confirmDelete('123');

    expect(loanServiceMock.deleteLoan).toHaveBeenCalledWith('123');
  });

  it('should not delete loan if confirmation cancelled', () => {
    spyOn(window, 'confirm').and.returnValue(false);

    component.confirmDelete('123');

    expect(loanServiceMock.deleteLoan).not.toHaveBeenCalled();
  });

  it('should not approve loan if confirmation cancelled', () => {
    spyOn(window, 'confirm').and.returnValue(false);

    component.confirmApprove('123');

    expect(loanServiceMock.updateLoanStatus).not.toHaveBeenCalled();
  });
});
