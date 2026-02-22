import { Component, inject } from '@angular/core';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../../core/services/auth';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [
    RouterLink,
    RouterLinkActive
  ],
  templateUrl: './navbar.html',
  styleUrl: './navbar.scss'
})
export class Navbar {
  
  
  private authService = inject(AuthService);
  private router = inject(Router);

  
  isLoggedIn = this.authService.isLoggedIn;

 
  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}