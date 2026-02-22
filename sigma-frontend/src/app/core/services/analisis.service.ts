import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { AnalisisClinicoDTO } from '../interfaces/analisis.interface';

@Injectable({
  providedIn: 'root'
})
export class AnalisisService {

  private http = inject(HttpClient);
  private apiUrl = environment.apiUrl; 

  constructor() { }


    //
  subirAnalisis(datos: FormData): Observable<AnalisisClinicoDTO> {
    
    return this.http.post<AnalisisClinicoDTO>(`${this.apiUrl}/analisis`, datos);
  }

  // Obtener análisis por historial clínico
  getAnalisisPorHistorial(historialId: number): Observable<AnalisisClinicoDTO[]> {
    return this.http.get<AnalisisClinicoDTO[]>(`${this.apiUrl}/analisis/historial/${historialId}`);
  }

  // Eliminar análisis por ID
  eliminarAnalisis(idAnalisis: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/analisis/${idAnalisis}`);
  }

  // Obtener TODOS los análisis de un paciente 
  getAnalisisPorPaciente(pacienteId: number): Observable<AnalisisClinicoDTO[]> {
    return this.http.get<AnalisisClinicoDTO[]>(`${this.apiUrl}/analisis/paciente/${pacienteId}`);
  }
}