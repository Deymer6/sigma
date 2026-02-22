import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

// 1. Interfaces que coinciden con tu Backend Java
export interface CitaResponse {
  idCita: number;
  pacienteId: number;
  obstetraId: number;
  pacienteNombre: string;
  obstetraNombre: string;
  fechaCita: string; 
  motivoConsulta: string;
  estadoCita: string;
}

export interface CitaRequest {
  pacienteId: number;
  obstetraId: number;
  fechaCita: string; 
  motivoConsulta: string;
}

@Injectable({
  providedIn: 'root'
})
export class CitaService {

  private http = inject(HttpClient);
  private apiUrl = environment.apiUrl; 

  constructor() { }

  // GET: Obtener lista
  getCitasPorPaciente(pacienteId: number): Observable<CitaResponse[]> {
    return this.http.get<CitaResponse[]>(`${this.apiUrl}/citas/paciente/${pacienteId}`);
  }

  // POST: Crear nueva
  crearCita(datosCita: CitaRequest): Observable<CitaResponse> {
    return this.http.post<CitaResponse>(`${this.apiUrl}/citas`, datosCita);
  }
  // PUT: Cancelar cita
  cancelarCita(idCita: number): Observable<CitaResponse> {
    
    return this.http.put<CitaResponse>(`${this.apiUrl}/citas/${idCita}/cancelar`, {});
  }

  
  
}