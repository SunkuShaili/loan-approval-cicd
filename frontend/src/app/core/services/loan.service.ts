import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { PageResponse } from '../../models/page-response.model';
import { Loan } from '../../models/loan.model';
import { API_BASE_URL } from '../../core/api.config';

@Injectable({ providedIn: 'root' })
export class LoanService {

  private baseUrl = `${API_BASE_URL}/loans`;

  constructor(private http: HttpClient) {}

  getLoans(page = 0, size = 10, status?: string): Observable<PageResponse<Loan>> {
    let params = new HttpParams()
      .set('page', page)
      .set('size', size);

    if (status) {
      params = params.set('status', status);
    }

    return this.http.get<PageResponse<Loan>>(this.baseUrl, { params });
  }

  createLoan(payload: any) {
    return this.http.post(this.baseUrl, payload);
  }

  updateLoanStatus(loanId: string, payload: any) {
    const token = localStorage.getItem('token');

    return this.http.patch(
      `${this.baseUrl}/${loanId}/status`,
      payload,
      {
        headers: {
          Authorization: `Bearer ${token}`
        }
      }
    );
  }

  deleteLoan(loanId: string) {
    const token = localStorage.getItem('token');

    return this.http.delete(
      `${this.baseUrl}/${loanId}`,
      {
        headers: {
          Authorization: `Bearer ${token}`
        }
      }
    );
  }
}
