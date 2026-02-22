import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormControl, FormGroup, ReactiveFormsModule, Validators, FormBuilder } from '@angular/forms';
import { Router } from '@angular/router'; 
import { ObstetraService, PacienteCardDTO } from '../../../../core/services/obstetra.service';
import { debounceTime, distinctUntilChanged } from 'rxjs';
import Swal from 'sweetalert2'; 

@Component({
  selector: 'app-mis-pacientes',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './mis-pacientes.html',
  styleUrl: './mis-pacientes.scss'
})
export class MisPacientes implements OnInit {

  private obstetraService = inject(ObstetraService);
  private fb = inject(FormBuilder);
  private router = inject(Router);

  public minDate: string = '';
  public horariosLaborales: string[] = [];
  
  // --- Estados de la Vista ---
  public pacientes = signal<PacienteCardDTO[]>([]);
  public loading = signal<boolean>(false);
  public searchControl = new FormControl('');

  // --- Estados del Modal ---
  public showModal = signal<boolean>(false);
  public pacienteSeleccionado = signal<PacienteCardDTO | null>(null);
  public citaForm: FormGroup;

  constructor() {
    // Inicializamos el formulario del modal
    this.citaForm = this.fb.group({
      fecha: ['', Validators.required],
      hora: ['', Validators.required],
      motivo: ['', [Validators.required, Validators.minLength(5)]]
    });
  }

  ngOnInit(): void {
    // 1. Cargar lista inicial
    this.buscarPacientes();

    // 2. Escuchar cambios en el buscador (espera 500ms antes de buscar)
    this.searchControl.valueChanges.pipe(
      debounceTime(500), 
      distinctUntilChanged()
    ).subscribe(val => this.buscarPacientes(val || ''));
    this.configurarRestricciones();
  }
  configurarRestricciones() {
    // A. Fecha Mínima: Hoy (Formato YYYY-MM-DD)
    const hoy = new Date();
    // Ajuste de zona horaria simple para que no reste un día
    const fechaLocal = new Date(hoy.getTime() - (hoy.getTimezoneOffset() * 60000));
    this.minDate = fechaLocal.toISOString().split('T')[0];

    // Generar Horas Laborales (Ej: 8:00 AM a 6:00 PM cada 30 min)
    const horaInicio = 8; 
    const horaFin = 18;   
    
    for (let h = horaInicio; h < horaFin; h++) {
      // Hora en punto (ej: 08:00)
      this.horariosLaborales.push(`${h.toString().padStart(2, '0')}:00`);
     
      
    }
  }

  // --- BÚSQUEDA ---
  buscarPacientes(termino: string = '') {
    this.loading.set(true);
    this.obstetraService.listarPacientes(termino).subscribe({
      next: (res) => {
        this.pacientes.set(res);
        this.loading.set(false);
      },
      error: (err) => {
        console.error('Error buscando pacientes:', err);
        this.loading.set(false);
      }
    });
  }

  verHistorial(id: number) {
    console.log('🔵 Click en Historial. ID:', id); // <--- AGREGA ESTO
    this.router.navigate(['/doctor/paciente', id, 'historial']);
  }

  abrirModalCita(paciente: PacienteCardDTO) {
    console.log('🟢 Click en Nueva Cita. Paciente:', paciente.nombreCompleto); // <--- AGREGA ESTO
    this.pacienteSeleccionado.set(paciente);
    this.citaForm.reset();
    
    // Forzamos el true
    this.showModal.set(true);
    console.log('Estado del Modal:', this.showModal()); // <--- ¿Dice true?
  }
  

  cerrarModal() {
    this.showModal.set(false);
    this.pacienteSeleccionado.set(null);
  }

  guardarCita() {
    if (this.citaForm.invalid) {
      this.citaForm.markAllAsTouched(); // Muestra errores rojos si faltan datos
      return;
    }

    const val = this.citaForm.value;
    
    // Combinamos Fecha y Hora para el formato que pide Java (LocalDateTime)
    // Ejemplo: "2025-11-30T10:30:00"
    const fechaFinal = `${val.fecha}T${val.hora}:00`;

    const request = {
      pacienteId: this.pacienteSeleccionado()?.idPaciente || 0,
      fechaCita: fechaFinal,
      motivoConsulta: val.motivo
    };

    // Llamada al Backend
    // IMPORTANTE: Asegúrate que 'agendarCitaPaciente' exista en ObstetraService
    this.obstetraService.agendarCitaPaciente(request).subscribe({
      next: () => {
        Swal.fire({
          icon: 'success',
          title: 'Cita Agendada',
          text: `Cita creada para ${this.pacienteSeleccionado()?.nombreCompleto}`,
          timer: 2000,
          showConfirmButton: false
        });
        
        this.cerrarModal();
        
        // Recargamos la lista por si quieres actualizar la fecha de "Última visita"
        this.buscarPacientes(this.searchControl.value || ''); 
      },
      error: (err) => {
        console.error(err);
        Swal.fire('Error', 'No se pudo agendar la cita.', 'error');
      }
    });
  }
}