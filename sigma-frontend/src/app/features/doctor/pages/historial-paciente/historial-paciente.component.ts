import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { ObstetraService } from '../../../../core/services/obstetra.service';

// Interfaz para tipar los datos de la lista
export interface HistorialItem {
  fecha: string;
  motivo: string;
  diagnostico: string;
  doctor: string;
  archivosUrl?: string; // Opcional, si hay PDF
}

@Component({
  selector: 'app-historial-paciente',
  standalone: true,
  imports: [CommonModule, RouterLink, DatePipe],
  templateUrl: './historial-paciente.component.html',
  styleUrl:   './historial-paciente.component.scss'
})
export class HistorialPacienteComponent implements OnInit {

  // Inyectamos ActivatedRoute para leer la URL
  private route = inject(ActivatedRoute);
  // Usamos ObstetraService (No HistorialService)
  private obstetraService = inject(ObstetraService);

  // Signals para la vista
  public historial = signal<HistorialItem[]>([]);
  public pacienteNombre = signal<string>('');
  public loading = signal<boolean>(true);

  ngOnInit(): void {
    // 1. Obtener el ID que viene en la ruta: /doctor/paciente/5/historial
    const idPaciente = this.route.snapshot.paramMap.get('idPaciente');
    
    if (idPaciente) {
      this.cargarHistorial(+idPaciente);
    }
  }

  cargarHistorial(id: number) {
    this.obstetraService.getHistorialClinico(id).subscribe({
      next: (res: any) => {
        // El backend devuelve un objeto: { paciente: "Nombre", items: [...] }
        this.pacienteNombre.set(res.paciente);
        this.historial.set(res.items);
        this.loading.set(false);
      },
      error: (err) => {
        console.error('Error cargando historial:', err);
        this.loading.set(false);
      }
    });
  }
}