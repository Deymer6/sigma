import { Injectable, PLATFORM_ID, inject, signal } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Observable, tap, map } from 'rxjs'; 
import { environment } from '../../../environments/environment';
import { jwtDecode } from 'jwt-decode';
import { Router } from '@angular/router';

export interface AuthResponse { token: string; }
export interface LoginRequest { email?: string; password?: string; }
export interface RegisterRequest { email?: string; password?: string; nombre?: string; apellido?: string; documentoIdentificacion?: string; fechaNacimiento?: string; }

export interface JwtPayload {
  sub: string;
  authorities: string[];
  iat: number;
  exp: number;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private router = inject(Router);

  private apiUrl = `${environment.apiUrl}/auth`;
  private platformId = inject(PLATFORM_ID);
  public isLoggedIn = signal<boolean>(this.hasToken());

  constructor(private http: HttpClient) {
    // --- AGREGA ESTO AQUÍ ---
    // Al iniciar el servicio, verificamos si el token sigue siendo válido (fecha)
    this.tryRestoreSession();
  }

  // --- AGREGA ESTE MÉTODO NUEVO ---
  private tryRestoreSession(): void {
    if (isPlatformBrowser(this.platformId)) {
      const token = this.getToken();
      if (token) {
        const payload = this.decodeToken(token);
        if (payload) {
          // Verificar si expiró (exp está en segundos, Date.now() en milisegundos)
          const isExpired = payload.exp * 1000 < Date.now();
          
          if (isExpired) {
            this.logout(); // Si expiró, lo sacamos
          } else {
            this.isLoggedIn.set(true); // Si es válido, confirmamos login
          }
        } else {
          this.logout(); // Token corrupto
        }
      }
    }
  }
  // --------------------------------

  register(data: RegisterRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/register`, data).pipe(
      tap(response => this.saveToken(response.token))
    );
  }

  login(data: LoginRequest): Observable<JwtPayload | null> { 
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, data).pipe(
      tap(response => {
        this.saveToken(response.token);
      }),
      map(response => {
        return this.decodeToken(response.token); 
      })
    );
  }

  logout(): void {
    if (isPlatformBrowser(this.platformId)) {
      localStorage.removeItem('sigma_token');
      this.router.navigate(['/login']);
      this.isLoggedIn.set(false);
    }
  }

  private saveToken(token: string): void {
    if (isPlatformBrowser(this.platformId)) {
      localStorage.setItem('sigma_token', token);
      this.isLoggedIn.set(true);
    }
  }

  private hasToken(): boolean {
    if (isPlatformBrowser(this.platformId)) {
      return localStorage.getItem('sigma_token') !== null;
    }
    return false;
  }
  
  private decodeToken(token: string | null): JwtPayload | null { 
    if (!token) return null; 
    try {
      return jwtDecode<JwtPayload>(token);
    } catch (e) {
      console.error("Error decodificando el token", e);
      return null;
    }
  }

  getUserPayload(): JwtPayload | null {
    const token = this.getToken();
    return this.decodeToken(token); 
  }

  getToken(): string | null {
    if (isPlatformBrowser(this.platformId)) {
      return localStorage.getItem('sigma_token');
    }
    return null;
  }
}