import { Component, OnInit, inject, signal, computed } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule, DatePipe } from '@angular/common';
import { AuthService, JwtPayload } from '../../../../core/services/auth';
import { CitaService, CitaRequest, CitaResponse } from '../../../../core/services/cita.service';
import { PatientService } from '../../../../core/services/patient.service';
import { StaffService } from '../../../../core/services/staff.service';

@Component({
  selector: 'app-mis-citas',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    CommonModule,
    DatePipe
  ],
  templateUrl: './mis-citas.component.html',
  styleUrl: './mis-citas.component.scss'
})
export class MisCitasComponent implements OnInit {

  private fb = inject(FormBuilder);
  private citaService = inject(CitaService);
  private authService = inject(AuthService);
  private patientService = inject(PatientService);
  private staffService = inject(StaffService);

  // --- Signals de Datos ---
  public citas = signal<CitaResponse[]>([]);
  public doctores = signal<any[]>([]); 
  public pacienteId = signal<number | null>(null);
  public cantidadCitasActivas = computed(() => 
    this.citas().filter(c => c.estadoCita === 'Programada').length
  );
  
  // --- Lógica de Horarios ---
  public horariosDisponibles: string[] = [];
  public horarioSeleccionado: string | null = null;

  // --- Formulario ---
  public citaForm: FormGroup;
  public minDate: string = ''; 

  // --- Mensajes y Estados ---
  public loading = signal<boolean>(false);
  public successMessage: string | null = null;
  public errorMessage: string | null = null;

  // --- Modal de Cancelación ---
  public showModal = signal<boolean>(false);
  public citaIdToDelete = signal<number | null>(null);

  constructor() {
    this.citaForm = this.fb.group({
      id_obstetra: ['', [Validators.required]], 
      fecha: ['', [Validators.required]],       
      hora: ['', [Validators.required]], 
      motivo: ['', [Validators.required, Validators.minLength(5)]]
    });
  }

  ngOnInit(): void {
    this.minDate = new Date().toISOString().split('T')[0]; // Bloquear pasado

    this.generarHorarios();

    // Cargar Doctores para el Select
    this.staffService.getObstetras().subscribe({ // <--- Método nuevo
      next: (docs) => {
        console.log('Doctores cargados:', docs); // Debug
        this.doctores.set(docs);
      },
      error: (err) => console.error('Error cargando doctores', err)
    });

    // Identificar al Paciente
    this.gestionarIdPaciente();
  }

  // ===========================================================
  // LÓGICA DE NEGOCIO PRINCIPAL (AGENDAR)
  // ===========================================================
  agendarCita() {
    if (this.citaForm.invalid) {
      this.citaForm.markAllAsTouched();
      return;
    }

    const pacienteId = this.patientService.currentPatientId();
    if (!pacienteId) {
      this.errorMessage = "Error de sesión. Recargue la página.";
      return;
    }

    // --- VALIDACIÓN 1: LÍMITE DE 2 CITAS ---
    // Contamos cuántas citas tiene en estado 'Programada'
    const citasActivas = this.citas().filter(c => c.estadoCita === 'Programada').length;

    if (citasActivas >= 2) {
      this.errorMessage = "Límite alcanzado. Solo puedes tener 2 citas programadas al mismo tiempo. Completa o cancela una para continuar.";
      setTimeout(() => this.errorMessage = null, 6000);
      return; // <--- Detenemos el proceso aquí
    }

    // --- VALIDACIÓN 2: ANTI-SPAM (Una cita por día) ---
    const fechaSeleccionada = this.citaForm.value.fecha; // "2025-11-29"
    
    const yaTieneCitaEseDia = this.citas().some(cita => {
      // Extraemos la parte de la fecha YYYY-MM-DD
      const fechaExistente = cita.fechaCita.split('T')[0]; 
      // Si es el mismo día Y NO está cancelada, bloqueamos.
      return fechaExistente === fechaSeleccionada && 
             cita.estadoCita !== 'Cancelada';
    });

    if (yaTieneCitaEseDia) {
      this.errorMessage = "Ya tienes una cita programada para esa fecha. Selecciona otro día.";
      setTimeout(() => this.errorMessage = null, 5000);
      return; // <--- Detenemos el proceso aquí
    }

    // --- SI PASA LAS VALIDACIONES, GUARDAMOS ---
    const fechaFinal = `${this.citaForm.value.fecha}T${this.citaForm.value.hora}:00`;

    const request: CitaRequest = {
      pacienteId: pacienteId,
      obstetraId: Number(this.citaForm.value.id_obstetra),
      fechaCita: fechaFinal,
      motivoConsulta: this.citaForm.value.motivo
    };

    this.loading.set(true); // Bloqueamos UI un momento

    this.citaService.crearCita(request).subscribe({
      next: (res) => {
        this.successMessage = "¡Cita agendada con éxito!";
        this.citaForm.reset();
        this.horarioSeleccionado = null; // Limpiar selección visual
        this.cargarCitas(pacienteId); // Recargar lista para ver la nueva
        this.loading.set(false);
        setTimeout(() => this.successMessage = null, 4000);
      },
      error: (err) => {
        this.errorMessage = "Error del servidor al agendar la cita.";
        console.error(err);
        this.loading.set(false);
        setTimeout(() => this.errorMessage = null, 4000);
      }
    });
  }

  // ===========================================================
  // MÉTODOS AUXILIARES
  // ===========================================================

  private generarHorarios() {
    const horaInicio = 8; 
    const horaFin = 17;   
    this.horariosDisponibles = [];

    for (let h = horaInicio; h <= horaFin; h++) {
      if (h === 13) continue; // 1 PM Almuerzo (puedes cambiarlo a 12 si prefieres)
      const horaFormato = h < 10 ? `0${h}:00` : `${h}:00`;
      this.horariosDisponibles.push(horaFormato);
    }
  }

  seleccionarHorario(hora: string) {
    this.horarioSeleccionado = hora;
    this.citaForm.get('hora')?.setValue(hora);
  }

  // Verifica visualmente si una hora ya está ocupada en el Frontend
  esHoraOcupada(hora: string): boolean {
    const fechaSeleccionada = this.citaForm.value.fecha;
    if (!fechaSeleccionada) return false;

    return this.citas().some(cita => {
      const [fecha, horaFull] = cita.fechaCita.split('T'); 
      const horaSimple = horaFull.substring(0, 5); 
      return fecha === fechaSeleccionada && horaSimple === hora && cita.estadoCita !== 'Cancelada';
    });
  }

  private gestionarIdPaciente() {
    const idEnMemoria = this.patientService.currentPatientId();

    if (idEnMemoria) {
      this.pacienteId.set(idEnMemoria);
      this.cargarCitas(idEnMemoria);
    } 
    else {
      const payload = this.authService.getUserPayload();
      if (payload && payload.sub) {
        this.patientService.getDashboardData().subscribe({
          next: (data) => {
             if (data.idPaciente) {
               const idReal = data.idPaciente;
               this.patientService.currentPatientId.set(idReal);
               this.pacienteId.set(idReal);
               this.cargarCitas(idReal);
             }
          },
          error: (err) => console.error("Error obteniendo ID", err)
        });
      }
    }
  }

  private cargarCitas(id: number) {
    this.loading.set(true);
    this.citaService.getCitasPorPaciente(id).subscribe({
      next: (data) => {
        // Ordenar: Programadas primero, luego por fecha
        const mapaPrioridad: any = { 'Programada': 1, 'Realizada': 2, 'Cancelada': 3 };

        const citasOrdenadas = data.sort((a, b) => {
          const prioA = mapaPrioridad[a.estadoCita] || 99;
          const prioB = mapaPrioridad[b.estadoCita] || 99;
          if (prioA !== prioB) return prioA - prioB;
          return new Date(b.fechaCita).getTime() - new Date(a.fechaCita).getTime();
        });

        this.citas.set(citasOrdenadas);
        this.loading.set(false);
      },
      error: (err) => {
        console.error("Error cargando citas", err);
        this.loading.set(false);
      }
    });
  }

  // --- MODAL DE CANCELACIÓN ---
  abrirModalCancelar(idCita: number) {
    this.citaIdToDelete.set(idCita);
    this.showModal.set(true);
  }

  cerrarModal() {
    this.showModal.set(false);
    this.citaIdToDelete.set(null);
  }

  confirmarCancelacion() {
    const id = this.citaIdToDelete();
    if (id) {
      this.citaService.cancelarCita(id).subscribe({
        next: () => {
          const pid = this.patientService.currentPatientId();
          if(pid) this.cargarCitas(pid);
          this.cerrarModal();
        },
        error: (err) => alert('Error al cancelar la cita')
      });
    }
  }
}