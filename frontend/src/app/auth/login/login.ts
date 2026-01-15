import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.html',
  styleUrls: ['./login.css']
})
export class Login {

  email = '';
  password = '';

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}
login() {
  this.authService.login({
    email: this.email,
    password: this.password
  }).subscribe({
    next: (res) => {
      // Step 1: Save token FIRST
      localStorage.setItem('token', res.token);

      //  Step 2: NOW fetch user info
      this.authService.getMe().subscribe(user => {
        localStorage.setItem('email', user.email);
        localStorage.setItem('role', user.role);

        //  Step 3: Route based on role
        if (user.role === 'ADMIN') {
          this.router.navigate(['/dashboard'], { queryParams: { role: 'ADMIN' } });
        } else {
          this.router.navigate(['/dashboard'], { queryParams: { role: 'USER' } });
        }

      });
    },
    error: () => alert('Invalid credentials')
  });
}

}
