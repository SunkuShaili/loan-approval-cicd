import { TestBed } from '@angular/core/testing';
import {
  HttpClientTestingModule,
  HttpTestingController
} from '@angular/common/http/testing';
import { LoanService } from './loan.service';

describe('LoanService (Jasmine + Karma)', () => {
  let service: LoanService;
  let httpMock: HttpTestingController;

  const baseUrl = 'http://localhost:8080/api/loans';

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [LoanService]
    });

    service = TestBed.inject(LoanService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
    localStorage.clear();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should fetch loans with page and size', () => {
    service.getLoans(0, 10).subscribe();

    const req = httpMock.expectOne(
      (request) =>
        request.method === 'GET' &&
        request.url === baseUrl &&
        request.params.get('page') === '0' &&
        request.params.get('size') === '10'
    );

    expect(req.request.method).toBe('GET');
    req.flush({ content: [] });
  });

  it('should fetch loans with status filter', () => {
    service.getLoans(0, 10, 'APPROVED').subscribe();

    const req = httpMock.expectOne(
      (request) =>
        request.method === 'GET' &&
        request.params.get('status') === 'APPROVED'
    );

    expect(req.request.method).toBe('GET');
    req.flush({ content: [] });
  });

  it('should create a loan', () => {
    const payload = { amount: 100000 };

    service.createLoan(payload).subscribe();

    const req = httpMock.expectOne(baseUrl);

    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(payload);

    req.flush({});
  });

  it('should update loan status with authorization header', () => {
    localStorage.setItem('token', 'test-token');

    const payload = { status: 'APPROVED' };

    service.updateLoanStatus('123', payload).subscribe();

    const req = httpMock.expectOne(`${baseUrl}/123/status`);

    expect(req.request.method).toBe('PATCH');
    expect(req.request.headers.get('Authorization')).toBe(
      'Bearer test-token'
    );
    expect(req.request.body).toEqual(payload);

    req.flush({});
  });

  it('should delete loan with authorization header', () => {
    localStorage.setItem('token', 'test-token');

    service.deleteLoan('123').subscribe();

    const req = httpMock.expectOne(`${baseUrl}/123`);

    expect(req.request.method).toBe('DELETE');
    expect(req.request.headers.get('Authorization')).toBe(
      'Bearer test-token'
    );

    req.flush({});
  });
});
