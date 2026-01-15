import { Component, OnInit } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule,RouterModule],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {

  email = '';
  role = '';

  constructor(private router: Router) {}

  ngOnInit() {
    this.email = localStorage.getItem('email') || '';
    this.role = localStorage.getItem('role') || '';
  }

  logout() {
    localStorage.clear();
    this.router.navigate(['/login']);
  }
}
