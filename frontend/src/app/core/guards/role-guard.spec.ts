import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { Observable, of, throwError } from 'rxjs';
import { roleGuard } from './role-guard';
import { AuthService } from '../services/auth.service';

describe('roleGuard (Jasmine + Karma)', () => {
  let authServiceMock: jasmine.SpyObj<AuthService>;
  let routerMock: jasmine.SpyObj<Router>;

  beforeEach(() => {
    authServiceMock = jasmine.createSpyObj('AuthService', ['getMe']);
    routerMock = jasmine.createSpyObj('Router', ['navigate']);

    TestBed.configureTestingModule({
      providers: [
        { provide: AuthService, useValue: authServiceMock },
        { provide: Router, useValue: routerMock }
      ]
    });
  });

  it('should allow access if user role is allowed', (done) => {
    authServiceMock.getMe.and.returnValue(
      of({ role: 'ADMIN' } as any)
    );

    const route: any = {
      data: { roles: ['ADMIN'] }
    };

    const result$ = TestBed.runInInjectionContext(() =>
      roleGuard(route, {} as any)
    ) as Observable<boolean>;

    result$.subscribe((result: boolean) => {
      expect(result).toBeTrue();
      done();
    });
  });

  it('should deny access and navigate to dashboard if role is not allowed', (done) => {
    authServiceMock.getMe.and.returnValue(
      of({ role: 'USER' } as any)
    );

    const route: any = {
      data: { roles: ['ADMIN'] }
    };

    const result$ = TestBed.runInInjectionContext(() =>
      roleGuard(route, {} as any)
    ) as Observable<boolean>;

    result$.subscribe((result: boolean) => {
      expect(result).toBeFalse();
      expect(routerMock.navigate).toHaveBeenCalledWith(['/dashboard']);
      done();
    });
  });

  it('should allow access when no roles are defined', (done) => {
    authServiceMock.getMe.and.returnValue(
      of({ role: 'USER' } as any)
    );

    const route: any = {
      data: {}
    };

    const result$ = TestBed.runInInjectionContext(() =>
      roleGuard(route, {} as any)
    ) as Observable<boolean>;

    result$.subscribe((result: boolean) => {
      expect(result).toBeTrue();
      done();
    });
  });

  it('should redirect to login on error', (done) => {
    authServiceMock.getMe.and.returnValue(
      throwError(() => new Error('Unauthorized'))
    );

    const route: any = {
      data: { roles: ['ADMIN'] }
    };

    const result$ = TestBed.runInInjectionContext(() =>
      roleGuard(route, {} as any)
    ) as Observable<boolean>;

    result$.subscribe((result: boolean) => {
      expect(result).toBeFalse();
      expect(routerMock.navigate).toHaveBeenCalledWith(['/login']);
      done();
    });
  });
});
