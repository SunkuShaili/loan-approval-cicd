// import { TestBed } from '@angular/core/testing';
import {
  HttpClientTestingModule,
  HttpTestingController
} from '@angular/common/http/testing';
import { AuthService } from './auth.service';
import { TestBed } from '@angular/core/testing';

describe('AuthService (Jasmine + Karma)', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;

  const BASE_URL = 'http://localhost:8080/api';
  const ADMIN_URL = 'http://localhost:8080/api/admin/users';

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [AuthService]
    });

    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
    localStorage.clear();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should login user', () => {
    const payload = {
      email: 'test@example.com',
      password: 'password'
    };

    service.login(payload as any).subscribe();

    const req = httpMock.expectOne(`${BASE_URL}/auth/login`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(payload);

    req.flush({ token: 'fake-token' });
  });

  it('should fetch logged-in user details', () => {
    service.getMe().subscribe();

    const req = httpMock.expectOne(
      'http://localhost:8080/api/users/me'
    );
    expect(req.request.method).toBe('GET');

    req.flush({});
  });

  it('should fetch all users (admin)', () => {
    service.getAllUsers().subscribe();

    const req = httpMock.expectOne(ADMIN_URL);
    expect(req.request.method).toBe('GET');

    req.flush([]);
  });

  it('should update user active status', () => {
    service.updateUserStatus('123', true).subscribe();

    const req = httpMock.expectOne(`${ADMIN_URL}/123/status`);
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual({ active: true });

    req.flush({});
  });

  it('should create a new user', () => {
    const payload = {
      email: 'new@example.com',
      role: 'ADMIN'
    };

    service.createUser(payload).subscribe();

    const req = httpMock.expectOne(ADMIN_URL);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(payload);

    req.flush({});
  });

  it('should clear localStorage on logout', () => {
    localStorage.setItem('token', 'abc');

    service.logout();

    expect(localStorage.getItem('token')).toBeNull();
  });
});
