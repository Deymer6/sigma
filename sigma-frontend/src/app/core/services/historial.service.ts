import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { environment } from '../../../environments/environment';
import { HistorialDetalleDTO } from '../interfaces/historial.interface';

@Injectable({
  providedIn: 'root'
})
export class HistorialService {

  private http = inject(HttpClient);
  private apiUrl = environment.apiUrl;

  constructor() { }

  getHistorialPorPaciente(pacienteId: number): Observable<HistorialDetalleDTO[]> {
    return this.http.get<any[]>(`${this.apiUrl}/historiales/paciente/${pacienteId}`).pipe(
      // Transformamos la data cruda del backend al formato que necesita la vista
      map(data => data.map(h => ({
        idHistorial: h.idHistorial,
        fechaAtencion: h.fechaAtencion,
        
        nombreDoctor: h.obstetraNombre || 'Dr. Asignado', 
        especialidad: 'Obstetricia',
        diagnostico: h.diagnostico,
        prescripciones: h.prescripciones ? h.prescripciones.split(',') : [],
        // Aquí mapearíamos los archivos si el backend los enviara en este endpoint
        archivos: [] 
      })))
    );
  }
}