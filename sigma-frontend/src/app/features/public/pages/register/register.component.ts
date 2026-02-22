import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../../core/services/auth';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    RouterLink
  ],
  templateUrl: './register.component.html',
  styleUrl: './register.component.scss'
})
export class RegisterComponent {
  // Control para mostrar u ocultar la contraseña
  hidePassword = true;
  // Formulario de registro
  registerForm: FormGroup;
  // Mensaje de error para mostrar en la interfaz
  errorMessage: string | null = null;
  
  // Inyección de dependencias y configuración del formulario
  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    // Definición del formulario con validaciones
    this.registerForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required]],
      nombre: ['', [Validators.required]],
      apellido: ['', [Validators.required]],
      
      // Validaciones para el campo de documento de identificación
      documentoIdentificacion: ['', [
        Validators.required,
        Validators.pattern('^[0-9]*$'), 
        Validators.minLength(8),        
        Validators.maxLength(8)         
      ]],
      
      fechaNacimiento: ['', [Validators.required]]
    });
  }
  // Método para alternar la visibilidad de la contraseña
  togglePassword() {
    this.hidePassword = !this.hidePassword;
  }

  // Método para permitir solo la entrada de números en el campo de documento de identificación
  onlyNumbers(event: any): boolean {
    const charCode = (event.which) ? event.which : event.keyCode;
    
    if (charCode > 31 && (charCode < 48 || charCode > 57)) {
      return false;
    }
    return true;
  }
  // Método para manejar el envío del formulario de registro
  onSubmit(): void {
    if (this.registerForm.invalid) {
      this.registerForm.markAllAsTouched();
      return;
    }
    
   
    this.errorMessage = null; 
    // Llamada al servicio de autenticación para registrar al usuario
    this.authService.register(this.registerForm.value).subscribe({
      next: (response) => {
        this.router.navigate(['/patient/dashboard']); 
      },
      // Manejo de errores en caso de fallo en el registro
      error: (err: HttpErrorResponse) => {
        if (err.error && err.error.message) {
          this.errorMessage = err.error.message;
        } else {
          this.errorMessage = 'Error al registrar. Por favor, intente de nuevo.';
        }
        console.error('Error en el registro:', err);
      }
    });
  }
}