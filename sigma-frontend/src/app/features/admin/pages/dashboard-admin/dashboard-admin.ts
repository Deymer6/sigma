import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AdminService, DashboardAdminDTO } from '../../../../core/services/admin.service';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { 
  faUserDoctor, 
  faUsers, 
  faCalendarCheck, 
  faChartLine,
  faClipboardList,
  faPlusCircle,
  faCog
} from '@fortawesome/free-solid-svg-icons';

@Component({
  selector: 'app-dashboard-admin',
  imports: [CommonModule, FontAwesomeModule],
  templateUrl: './dashboard-admin.html',
  styleUrl: './dashboard-admin.scss',
})
export class DashboardAdmin {
  private adminService = inject(AdminService);

  
  public data = signal<DashboardAdminDTO | null>(null);
  public loading = signal<boolean>(true);
  // 
  public faUserDoctor = faUserDoctor;
  public faUsers = faUsers;
  public faCalendarCheck = faCalendarCheck;
  public faChartLine = faChartLine;
  // Iconos para actividad reciente
  public faClipboardList = faClipboardList;
  public faPlusCircle = faPlusCircle;
  public faCog = faCog;

  public chartData = signal<{ mes: string, valor: number }[]>([]);

  ngOnInit(): void {
    this.cargarDatos();
  }

  cargarDatos() {
    
    this.adminService.getDashboard().subscribe({
      next: (res) => {
        this.data.set(res);
        if (res.citasPorMes) {
          
          const chartArray = Object.keys(res.citasPorMes).map(key => ({
            mes: key,
            valor: res.citasPorMes[key]
          }));
          this.chartData.set(chartArray);
        }
        this.loading.set(false);
      },
      error: (err) => {
        console.error(err);
        this.loading.set(false);
      }
    });
  }
  
  getTypeOfActivity(texto: string): 'cita' | 'paciente' | 'sistema' {
    const t = texto.toLowerCase();
    if (t.includes('cita') || t.includes('agendada')) return 'cita';
    if (t.includes('paciente') || t.includes('registrado')) return 'paciente';
    return 'sistema'; 
  }
  
}
