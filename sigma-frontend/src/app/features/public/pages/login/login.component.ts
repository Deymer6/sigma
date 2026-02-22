import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../../../core/services/auth';


@Component({
  selector: 'app-login',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink, CommonModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {

  hidePassword = true;
  loginForm: FormGroup;
  errorMessage: string | null = null;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required]]
    });
  }

  togglePassword() {
    this.hidePassword = !this.hidePassword;
  }

  onSubmit(): void {
    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }

    this.authService.login(this.loginForm.value).subscribe({
      next: (userData: any) => { 
        
      

        
        let role = '';
        if (userData.authorities && userData.authorities.length > 0) {
          role = userData.authorities[0];
        } else if (userData.role) {
          role = userData.role;
        }

       

        // --- REDIRECCIONES ---
        
        if (['ROL_OBSTETRA', 'OBSTETRA', 'ROLE_OBSTETRA'].includes(role)) {
          this.router.navigate(['/doctor/dashboard']);
        }
        else if (['ROL_PACIENTE', 'PACIENTE', 'ROLE_PACIENTE', 'ROLE_USER'].includes(role)) {
          this.router.navigate(['/patient/dashboard']);
        }
        else if (['ROL_ADMINISTRATIVO', 'ADMIN', 'ROLE_ADMIN'].includes(role)) {
          this.router.navigate(['/admin/dashboard']);
        }
        else {
          console.warn('Rol no reconocido:', role);
          this.router.navigate(['/']); 
        }
      },
      error: (err) => {
        console.error('Error Login:', err);
        this.errorMessage = 'Email o contraseña incorrectos.';
      }
    });
  }
}