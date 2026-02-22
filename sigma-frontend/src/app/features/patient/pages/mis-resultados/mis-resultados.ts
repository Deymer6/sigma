import { Component, OnInit, inject, signal, computed } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AnalisisService } from '../../../../core/services/analisis.service';
import { PatientService } from '../../../../core/services/patient.service';
import { AuthService } from '../../../../core/services/auth';
import { AnalisisClinicoDTO } from '../../../../core/interfaces/analisis.interface';

@Component({
  selector: 'app-mis-resultados',
  standalone: true,
  imports: [CommonModule, FormsModule, DatePipe],
  templateUrl: './mis-resultados.html',
  styleUrl: './mis-resultados.scss',
})
export class MisResultados implements OnInit {

  private analisisService = inject(AnalisisService);
  private patientService = inject(PatientService);
  private authService = inject(AuthService);

  public resultados = signal<AnalisisClinicoDTO[]>([]);
  public loading = signal<boolean>(true);

  public searchTerm = signal<string>('');
  public activeFilter = signal<'todos' | 'eco' | 'lab'>('todos');

  public filteredResultados = computed(() => {
    const term = this.searchTerm().toLowerCase();
    const filter = this.activeFilter();
    
    return this.resultados().filter(item => {
      const matchesSearch = item.tipoAnalisis.toLowerCase().includes(term) || 
                            item.valoresObservaciones.toLowerCase().includes(term);

      let matchesCategory = true;
      if (filter === 'eco') {
        matchesCategory = item.tipoAnalisis.toLowerCase().includes('eco'); 
      } else if (filter === 'lab') {
        matchesCategory = !item.tipoAnalisis.toLowerCase().includes('eco');
      }

      return matchesSearch && matchesCategory;
    });
  });

  ngOnInit(): void {
    this.cargarResultados();
  }

  private cargarResultados() {
    const pacienteId = this.patientService.currentPatientId();

    if (pacienteId) {
      this.obtenerAnalisis(pacienteId);
    } else {
      const payload = this.authService.getUserPayload();
      if (payload && payload.sub) {
        this.patientService.getDashboardData().subscribe({
          next: (data) => {
            if (data.idPaciente) {
              this.patientService.currentPatientId.set(data.idPaciente);
              this.obtenerAnalisis(data.idPaciente);
            }
          },
          error: (err) => {
            console.error(err);
            this.loading.set(false);
          }
        });
      } else {
        this.loading.set(false);
      }
    }
  }

  private obtenerAnalisis(idPaciente: number) {
    this.loading.set(true);
    this.analisisService.getAnalisisPorPaciente(idPaciente).subscribe({
      next: (data) => {
        this.resultados.set(data);
        this.loading.set(false);
      },
      error: (err) => {
        console.error(err);
        this.loading.set(false);
      }
    });
  }

  setFilter(filter: 'todos' | 'eco' | 'lab') {
    this.activeFilter.set(filter);
  }

  isPdf(url: string | null): boolean {
    return url ? url.toLowerCase().endsWith('.pdf') : false;
  }
}