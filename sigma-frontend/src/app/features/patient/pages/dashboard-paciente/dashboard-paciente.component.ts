import { Component, OnInit, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { DatePipe, CommonModule } from '@angular/common'; 
import { AuthService, JwtPayload } from '../../../../core/services/auth';
import { DashboardPacienteDTO } from '../../../../core/interfaces/dashboard.interface';
import { PatientService } from '../../../../core/services/patient.service';

@Component({
  selector: 'app-dashboard-paciente',
  standalone: true,
  imports: [
    RouterLink,
    DatePipe,    
    CommonModule 
  ],
  templateUrl: './dashboard-paciente.component.html',
  styleUrl: './dashboard-paciente.component.scss'
})
export class DashboardPacienteComponent implements OnInit {

  private authService = inject(AuthService);
  private patientService = inject(PatientService);
  
  public userName = signal<string>("Paciente");
  
  // Signal que contendrá toda la data real
  public dashboardData = signal<DashboardPacienteDTO | null>(null);
  public loading = signal<boolean>(true);

  ngOnInit(): void {
    // Cargar nombre temporal del token 
    const payload: JwtPayload | null = this.authService.getUserPayload();
    if (payload && payload.sub) {
      this.userName.set(payload.sub.split('@')[0]);
    }

    // Cargar datos 
    this.patientService.getDashboardData().subscribe({
      next: (data) => {
        console.log('Datos recibidos:', data); 
        this.dashboardData.set(data);
        this.loading.set(false);
      },
      error: (err) => {
        console.error('Error al cargar dashboard:', err);
        this.loading.set(false);
      }
    });
  }
}