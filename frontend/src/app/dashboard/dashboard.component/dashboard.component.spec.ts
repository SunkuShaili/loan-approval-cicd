import { DashboardComponent } from './dashboard.component';
import { Router } from '@angular/router';

describe('DashboardComponent (Jasmine + Karma)', () => {
  let component: DashboardComponent;
  let routerMock: jasmine.SpyObj<Router>;

  beforeEach(() => {
    routerMock = jasmine.createSpyObj('Router', ['navigate']);

    component = new DashboardComponent(routerMock);

    // Clear localStorage before each test
    localStorage.clear();
  });

  it('should create component', () => {
    expect(component).toBeTruthy();
  });

  it('should load email and role from localStorage on init', () => {
    localStorage.setItem('email', 'test@example.com');
    localStorage.setItem('role', 'ADMIN');

    component.ngOnInit();

    expect(component.email).toBe('test@example.com');
    expect(component.role).toBe('ADMIN');
  });

  it('should set empty values if localStorage is empty', () => {
    component.ngOnInit();

    expect(component.email).toBe('');
    expect(component.role).toBe('');
  });

  it('should clear localStorage and navigate to login on logout', () => {
    localStorage.setItem('email', 'test@example.com');
    localStorage.setItem('role', 'ADMIN');

    component.logout();

    expect(localStorage.getItem('email')).toBeNull();
    expect(localStorage.getItem('role')).toBeNull();
    expect(routerMock.navigate).toHaveBeenCalledWith(['/login']);
  });
});
