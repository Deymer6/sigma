import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators, FormControl } from '@angular/forms';

import { AdminService } from '../../../../core/services/admin.service';       
import { ObstetraDTO } from '../../../../core/interfaces/obstetra.interface'; 
import { debounceTime, distinctUntilChanged } from 'rxjs';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-gestion-obstetras',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './gestion-obstetras.html',
  styleUrl: './gestion-obstetras.scss'
})
export class GestionObstetras implements OnInit {

  
  private adminService = inject(AdminService);
  private fb = inject(FormBuilder);

  // --- ESTADOS ---
  public obstetras = signal<any[]>([]); 
  public obstetrasFiltrados = signal<any[]>([]); 
  public loading = signal<boolean>(true);
  public searchControl = new FormControl('');

  // --- MODAL ---
  public showModal = signal<boolean>(false);
  public isEditing = signal<boolean>(false);
  public currentId = signal<number | null>(null);
  public obstetraForm: FormGroup;

  constructor() {
    this.obstetraForm = this.fb.group({
      nombre: ['', Validators.required],
      apellido: ['', Validators.required],
      dni: ['', [Validators.required, Validators.minLength(8)]],
      numColegiatura: ['', Validators.required],
      especialidad: ['Ginecología y Obstetricia', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      password: [''] 
    });
  }

  ngOnInit(): void {
    this.cargarObstetras();

    this.searchControl.valueChanges.pipe(
      debounceTime(300), distinctUntilChanged()
    ).subscribe(term => this.filtrar(term || ''));
  }

  cargarObstetras() {
    this.loading.set(true);
    this.adminService.listarObstetras().subscribe({
      next: (data: any[]) => { 
        this.obstetras.set(data);
        this.filtrar(this.searchControl.value || '');
        this.loading.set(false);
      },
      error: () => this.loading.set(false)
    });
  }

  filtrar(termino: string) {
    const term = termino.toLowerCase();
    const filtrados = this.obstetras().filter(obs => 
      obs.nombre.toLowerCase().includes(term) || 
      obs.apellido.toLowerCase().includes(term) ||
      obs.numColegiatura.toLowerCase().includes(term)
    );
    this.obstetrasFiltrados.set(filtrados);
  }

  // --- ACCIONES DE TABLA ---

  alternarEstado(obstetra: any) {
    const accion = obstetra.estado === 'Activo' ? 'desactivar' : 'activar';
    
    Swal.fire({
      title: `¿${accion.charAt(0).toUpperCase() + accion.slice(1)} médico?`,
      text: `Se cambiará el estado de ${obstetra.nombre} a ${accion === 'activar' ? 'Activo' : 'Inactivo'}.`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#3b82f6',
      confirmButtonText: 'Sí, cambiar'
    }).then((result) => {
      if (result.isConfirmed) {
        this.adminService.cambiarEstadoObstetra(obstetra.id).subscribe({
          next: () => {
            this.cargarObstetras(); 
            Swal.fire('Actualizado', 'El estado ha sido cambiado.', 'success');
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
    this.obstetraForm.reset({ especialidad: 'Ginecología y Obstetricia' });
    this.obstetraForm.get('email')?.enable(); 
    this.showModal.set(true);
  }

  abrirModalEditar(obstetra: any) {
    this.isEditing.set(true);
    this.currentId.set(obstetra.id);
    
    this.obstetraForm.patchValue({
      nombre: obstetra.nombre,
      apellido: obstetra.apellido,
      dni: '00000000', 
      numColegiatura: obstetra.numColegiatura,
      especialidad: obstetra.especialidad,
      email: obstetra.email
    });
    
    
    this.showModal.set(true);
  }

  cerrarModal() {
    this.showModal.set(false);
  }

  guardar() {
    if (this.obstetraForm.invalid) {
      this.obstetraForm.markAllAsTouched();
      return;
    }

    const formValue = this.obstetraForm.value;

    
    if (!this.isEditing() && !formValue.password) {
      Swal.fire('Falta Contraseña', 'Debe asignar una contraseña temporal.', 'warning');
      return;
    }

    const request$ = this.isEditing()
      ? this.adminService.actualizarObstetra(this.currentId()!, formValue)
      : this.adminService.crearObstetra(formValue);

    request$.subscribe({
      next: () => {
        Swal.fire('¡Éxito!', `Obstetra ${this.isEditing() ? 'actualizado' : 'registrado'} correctamente.`, 'success');
        this.cerrarModal();
        this.cargarObstetras();
      },
      error: (err) => {
        console.error(err);
        Swal.fire('Error', 'Ocurrió un problema al guardar.', 'error');
      }
    });
  }
}