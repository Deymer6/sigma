import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Observable } from 'rxjs';



export interface DashboardAdminDTO {
  totalObstetras: number;
  totalPacientes: number;
  citasEsteMes: number;
  citasPorMes: { [key: string]: number }; 
  actividadReciente: string[];
}

export interface RegistroObstetraRequest {
  nombre: string;
  apellido: string;
  dni?: string;
  numColegiatura: string;
  especialidad: string;
  email: string;
  password?: string; 
}

@Injectable({
  providedIn: 'root'
})
export class AdminService {

  private http = inject(HttpClient);
  private apiUrl = environment.apiUrl; 

  // OBTENER DASHBOARD (KPIs y Gráficos)
  getDashboard(): Observable<DashboardAdminDTO> {
    return this.http.get<DashboardAdminDTO>(`${this.apiUrl}/admin/dashboard`);
  }

  //  CREAR OBSTETRA 
  crearObstetra(datos: RegistroObstetraRequest): Observable<any> {
    return this.http.post(`${this.apiUrl}/admin/obstetras`, datos, {
      responseType: 'text' // El backend devuelve un String simple ("Obstetra registrado...")
    });
  }

  // ACTUALIZAR OBSTETRA 
  actualizarObstetra(id: number, datos: RegistroObstetraRequest): Observable<any> {
    return this.http.put(`${this.apiUrl}/admin/obstetras/${id}`, datos, {
      responseType: 'text'
    });
  }

  // CAMBIAR ESTADO (PATCH) - Activar/Desactivar
  cambiarEstadoObstetra(id: number): Observable<any> {
    
    return this.http.patch(`${this.apiUrl}/admin/obstetras/${id}/estado`, {}, {
      responseType: 'text'
    });
  }

  listarObstetras(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/admin/obstetras`);
  }


  // PACIENTES
  listarPacientes(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/admin/pacientes`);
  }

  crearPaciente(data: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/admin/pacientes`, data, { responseType: 'text' });
  }

  actualizarPaciente(id: number, data: any): Observable<any> {
    return this.http.put(`${this.apiUrl}/admin/pacientes/${id}`, data, { responseType: 'text' });
  }

  cambiarEstadoPaciente(id: number): Observable<any> {
    return this.http.patch(`${this.apiUrl}/admin/pacientes/${id}/estado`, {}, { responseType: 'text' });
  }
}