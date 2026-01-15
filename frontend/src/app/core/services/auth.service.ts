import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { User } from '../../models/user.model';
import { LoginRequestModel } from '../../models/login-request.model';
import { LoginResponseModel } from '../../models/login-response.model';
import { API_BASE_URL } from '../../core/api.config';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private authUrl = `${API_BASE_URL}/auth`;
  private adminUsersUrl = `${API_BASE_URL}/admin/users`;
  private usersUrl = `${API_BASE_URL}/users`;

  constructor(private http: HttpClient) {}

  login(data: LoginRequestModel): Observable<LoginResponseModel> {
    return this.http.post<LoginResponseModel>(
      `${this.authUrl}/login`,
      data
    );
  }

  getMe() {
    return this.http.get<User>(`${this.usersUrl}/me`);
  }

  getAllUsers() {
    return this.http.get<any[]>(this.adminUsersUrl);
  }

  updateUserStatus(userId: string, active: boolean) {
    return this.http.put(
      `${this.adminUsersUrl}/${userId}/status`,
      { active }
    );
  }

  createUser(data: any) {
    return this.http.post(this.adminUsersUrl, data);
  }

  logout() {
    localStorage.clear();
  }
}
