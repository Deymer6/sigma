import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Observable } from 'rxjs';


export interface CitaDiaDTO {
  idCita: number;
  hora: string;          
  pacienteNombre: string;
  motivo: string;        
  estado: string;        
}

export interface DashboardStaffDTO {
  idObstetra: number;
  nombreCompleto: string;
  especialidad: string;
  cantidadCitasHoy: number;
  cantidadPacientesMes: number;
  citasDeHoy: CitaDiaDTO[];
}
export interface DetalleAtencionDTO {
  idCita: number;
  horaCita: string;          
  pacienteNombre: string;
  edad: number;
  semanasEmbarazo: string;   
  tipoSangre: string;        
  alergias: string;          
  ultimasConsultas: string[];
}
export interface PacienteCardDTO {
  idPaciente: number;
  nombreCompleto: string;
  dni: string;
  ultimaVisita: string;
}

export interface AgendarCitaDTO {
    pacienteId: number;
    fechaCita: string; // Formato ISO: "2025-11-30T10:00:00"
    motivoConsulta: string;
}



@Injectable({
  providedIn: 'root'
})
export class ObstetraService {
  
  private http = inject(HttpClient);
  // environmentURL base http://localhost:8080/api
  private apiUrl = environment.apiUrl; 

  getDashboard(fecha?: string): Observable<DashboardStaffDTO> {
    let params = {};
    if (fecha) {
      params = { fecha: fecha };
    }
    return this.http.get<DashboardStaffDTO>(`${this.apiUrl}/staff/dashboard`, { params });
  }
  getDetalleAtencion(idCita: number): Observable<DetalleAtencionDTO> {
    return this.http.get<DetalleAtencionDTO>(`${this.apiUrl}/staff/atencion/${idCita}`);
  }

  finalizarAtencion(formData: FormData): Observable<any> {
    return this.http.post(`${this.apiUrl}/staff/atencion/finalizar`, formData, {
      responseType: 'text' 
    });
  }


  listarPacientes(query: string = ''): Observable<PacienteCardDTO[]> {
    
    const params: any = {};
    if (query) {
      params.query = query;
    }

    return this.http.get<PacienteCardDTO[]>(`${this.apiUrl}/staff/pacientes`, { params });
  }

  agendarCitaPaciente(datos: AgendarCitaDTO): Observable<any> {
    
    return this.http.post(`${this.apiUrl}/citas`, datos);
  }

  getHistorialClinico(idPaciente: number): Observable<any> {
    
    return this.http.get<any>(`${this.apiUrl}/staff/paciente/${idPaciente}/historial`);
  }
 
}