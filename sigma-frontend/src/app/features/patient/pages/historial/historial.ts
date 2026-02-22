import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { HistorialService } from '../../../../core/services/historial.service';
import { PatientService } from '../../../../core/services/patient.service';
import { AuthService } from '../../../../core/services/auth'; // <--- 1. IMPORTAR AUTH
import { HistorialDetalleDTO } from '../../../../core/interfaces/historial.interface';
import { RouterLink } from '@angular/router';



@Component({
  selector: 'app-historial',
  standalone: true,
  imports: [CommonModule,DatePipe],
  templateUrl: './historial.html',
  styleUrl: './historial.scss'
})
export class HistorialComponent implements OnInit {

  private historialService = inject(HistorialService);
  private patientService = inject(PatientService);
  private authService = inject(AuthService); 

  public historiales = signal<HistorialDetalleDTO[]>([]);
  public loading = signal<boolean>(true);

  ngOnInit(): void {
    
    this.gestionarIdPaciente();
  }

  // --- LÓGICA DE RECUPERACIÓN DE ID ---
  private gestionarIdPaciente() {
    // ¿Ya tenemos el ID en memoria?
    const idEnMemoria = this.patientService.currentPatientId();

    if (idEnMemoria) {
      this.cargarHistorial(idEnMemoria);
    } 
    else {
      // ¿Tenemos el ID guardado en el token?
      const payload = this.authService.getUserPayload();
      
      if (payload && payload.sub) {
        // Reutilizamos el endpoint del Dashboard que ya nos devuelve el ID por el token
        this.patientService.getDashboardData().subscribe({
          next: (data) => {
             if (data.idPaciente) {
               // Guardamos el ID para que no se pierda de nuevo
               this.patientService.currentPatientId.set(data.idPaciente);
               
               //  Cargamos el historial
               this.cargarHistorial(data.idPaciente);
             }
          },
          error: (err) => {
            console.error("Error recuperando sesión", err);
            this.loading.set(false); 
          }
        });
      } else {
        this.loading.set(false);
      }
    }
  }
  

  private cargarHistorial(id: number) {
    this.historialService.getHistorialPorPaciente(id).subscribe({
      next: (data) => {
        
        const ordenados = data.sort((a, b) => 
          new Date(b.fechaAtencion).getTime() - new Date(a.fechaAtencion).getTime()
        );
        this.historiales.set(ordenados);
        this.loading.set(false);
      },
      error: (err) => {
        console.error(err);
        this.loading.set(false);
      }
    });
  }
}