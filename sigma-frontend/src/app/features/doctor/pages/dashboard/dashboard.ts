import { Component, OnInit, inject, signal, computed } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ObstetraService, DashboardStaffDTO } from '../../../../core/services/obstetra.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink, ],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.scss',
})
export class Dashboard implements OnInit {

  
  private obstetraService = inject(ObstetraService); 
  public data = signal<DashboardStaffDTO | null>(null);
  public loading = signal<boolean>(true);
  
  public totalCitas = computed(() => 
    this.data()?.cantidadCitasHoy || 0
  );

  public citasPendientes = computed(() => 
    this.data()?.citasDeHoy.filter(c => c.estado === 'Programada').length || 0
  );

  public citasFinalizadas = computed(() => 
    this.data()?.citasDeHoy.filter(c => c.estado === 'Realizada').length || 0
  );
  public currentDate: string = new Date().toISOString().split('T')[0];

  ngOnInit(): void {
    this.loadDashboardData(this.currentDate);
  }

  public loadDashboardData(fecha: string) {
    this.loading.set(true);
    
    this.obstetraService.getDashboard(fecha).subscribe({
      next: (res) => {
        this.data.set(res);
        this.loading.set(false);
      },
      error: (err) => {
        this.loading.set(false);
      }
    });
  }
  onDateChange(event: any) {
    const nuevaFecha = event.target.value;
    this.currentDate = nuevaFecha;
    this.loadDashboardData(nuevaFecha); 
  }
}