import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators, FormControl } from '@angular/forms';
import { AdminService } from '../../../../core/services/admin.service';
import { debounceTime, distinctUntilChanged } from 'rxjs';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-gestion-pacientes',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './gestion-pacientes.html',
  styleUrl: './gestion-pacientes.scss' 
})
export class GestionPacientes implements OnInit {

  private adminService = inject(AdminService);
  private fb = inject(FormBuilder);

  // Estados
  public pacientes = signal<any[]>([]);
  public pacientesFiltrados = signal<any[]>([]);
  public loading = signal<boolean>(true);
  public searchControl = new FormControl('');

  // Modal
  public showModal = signal<boolean>(false);
  public isEditing = signal<boolean>(false);
  public currentId = signal<number | null>(null);
  public pacienteForm: FormGroup;

  constructor() {
    this.pacienteForm = this.fb.group({
      nombre: ['', Validators.required],
      apellido: ['', Validators.required],
      dni: ['', [Validators.required, Validators.minLength(8)]],
      email: ['', [Validators.required, Validators.email]],
      password: [''] 
    });
  }

  ngOnInit(): void {
    this.cargarPacientes();
    this.searchControl.valueChanges.pipe(
      debounceTime(300), distinctUntilChanged()
    ).subscribe(term => this.filtrar(term || ''));
  }

  cargarPacientes() {
    this.loading.set(true);
    this.adminService.listarPacientes().subscribe({
      next: (data) => {
        this.pacientes.set(data);
        this.filtrar(this.searchControl.value || '');
        this.loading.set(false);
      },
      error: () => this.loading.set(false)
    });
  }

  filtrar(termino: string) {
    const term = termino.toLowerCase();
    const filtrados = this.pacientes().filter(p => 
      p.nombreCompleto.toLowerCase().includes(term) || 
      p.dni.includes(term)
    );
    this.pacientesFiltrados.set(filtrados);
  }

  alternarEstado(paciente: any) {
    
    const estadoActual = paciente.ultimaVisita === 'Inactivo' ? 'Inactivo' : 'Activo';
    const accion = estadoActual === 'Activo' ? 'desactivar' : 'activar';

    Swal.fire({
      title: `¿${accion.charAt(0).toUpperCase() + accion.slice(1)} paciente?`,
      text: `El usuario perderá acceso al sistema.`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#3b82f6',
      confirmButtonText: 'Sí, confirmar'
    }).then((result) => {
      if (result.isConfirmed) {
        this.adminService.cambiarEstadoPaciente(paciente.idPaciente).subscribe({
          next: () => {
            this.cargarPacientes();
            Swal.fire('Hecho', 'Estado actualizado.', 'success');
          },
          error: () => Swal.fire('Error', 'No se pudo cambiar el estado.', 'error')
        });
      }
    });
  }

  // --- MODAL ---

  abrirModalCrear() {
    this.isEditing.set(false);
    this.currentId.set(null);
    this.pacienteForm.reset();
    this.pacienteForm.get('email')?.enable();
    this.showModal.set(true);
  }

  abrirModalEditar(paciente: any) {
    this.isEditing.set(true);
    this.currentId.set(paciente.idPaciente);
    
  
    
    this.pacienteForm.patchValue({
      nombre: paciente.nombreCompleto.split(' ')[0],
      apellido: paciente.nombreCompleto.split(' ').slice(1).join(' '), 
      dni: paciente.dni,
      
      email: 'usuario@email.com'
    });
    this.showModal.set(true);
  }

  cerrarModal() { this.showModal.set(false); }

  guardar() {
    if (this.pacienteForm.invalid) {
      this.pacienteForm.markAllAsTouched();
      return;
    }
    const val = this.pacienteForm.value;
    
    const request$ = this.isEditing()
      ? this.adminService.actualizarPaciente(this.currentId()!, val)
      : this.adminService.crearPaciente(val);

    request$.subscribe({
      next: () => {
        this.cerrarModal();
        this.cargarPacientes();
        Swal.fire('Guardado', 'Operación exitosa', 'success');
      },
      error: (e) => Swal.fire('Error', 'No se pudo guardar', 'error')
    });
  }
}