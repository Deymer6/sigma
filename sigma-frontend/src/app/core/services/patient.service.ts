
import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment'; 
import { DashboardPacienteDTO } from '../interfaces/dashboard.interface';

@Injectable({
  providedIn: 'root'
})
export class PatientService {
  
  private http = inject(HttpClient);
  
  public currentPatientId = signal<number | null>(null);
  private apiUrl = environment.apiUrl; 

  getDashboardData(): Observable<DashboardPacienteDTO> {
    return this.http.get<DashboardPacienteDTO>(`${this.apiUrl}/pacientes/dashboard-info`);
  }
}