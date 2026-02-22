import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ObstetraService, DetalleAtencionDTO } from '../../../../core/services/obstetra.service';
import Swal from 'sweetalert2';
@Component({
  selector: 'app-atencion-medica',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './atencion-medica.html',
  styleUrl: './atencion-medica.scss',
})
export class AtencionMedica implements OnInit {

  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private fb = inject(FormBuilder);
  private obstetraService = inject(ObstetraService);

  public data = signal<DetalleAtencionDTO | null>(null);

  public archivoSeleccionado: File | null = null;
  public listaMedicamentos: { nombre: string, indicaciones: string }[] = [];

  public consultaForm: FormGroup;
  public medicamentoForm: FormGroup;

  constructor() {
    this.consultaForm = this.fb.group({
      diagnostico: ['', [Validators.required, Validators.minLength(10)]],
      observaciones: [''],
      nombreArchivo: ['']
    });

    this.medicamentoForm = this.fb.group({
      nombre: ['', Validators.required],
      indicaciones: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    const idCita = this.route.snapshot.paramMap.get('idCita');
    if (idCita) {
      this.cargarDatos(+idCita);
    }
  }

  cargarDatos(id: number) {
    this.obstetraService.getDetalleAtencion(id).subscribe({
      next: (res) => {
        this.data.set(res);
      },
      error: (err) => {
        console.error(err);
      }
    });
  }

  agregarMedicamento() {
    if (this.medicamentoForm.valid) {
      this.listaMedicamentos.push(this.medicamentoForm.value);
      this.medicamentoForm.reset();
    } else {
      this.medicamentoForm.markAllAsTouched();
    }
  }

  eliminarMedicamento(index: number) {
    this.listaMedicamentos.splice(index, 1);
  }
  onFileSelected(event: any) {
    const file: File = event.target.files[0];
    if (file) {
      this.archivoSeleccionado = file;
    }
  }

  finalizarConsulta() {
    if (this.consultaForm.invalid) {
      
      Swal.fire({
        icon: 'error',
        title: 'Faltan datos',
        text: 'El diagnóstico es obligatorio para cerrar la consulta.',
        confirmButtonColor: '#3b82f6'
      });
      this.consultaForm.markAllAsTouched();
      return;
    }

    // Confirmación antes de enviar
    Swal.fire({
      title: '¿Finalizar Consulta?',
      text: "Se guardará el historial y se cerrará la cita.",
      icon: 'question',
      showCancelButton: true,
      confirmButtonColor: '#10b981',
      cancelButtonColor: '#ef4444',
      confirmButtonText: 'Sí, finalizar',
      cancelButtonText: 'Cancelar'
    }).then((result) => {
      
      if (result.isConfirmed) {
        // PROCESO DE ENVÍO
        Swal.fire({ title: 'Guardando...', didOpen: () => Swal.showLoading() }); // Loading

        const formData = new FormData();
        const datosDTO = {
          idCita: this.data()?.idCita,
          diagnostico: this.consultaForm.get('diagnostico')?.value,
          observaciones: this.consultaForm.get('observaciones')?.value,
          nombreArchivo: this.consultaForm.get('nombreArchivo')?.value,
          receta: this.listaMedicamentos
        };

        formData.append('datos', JSON.stringify(datosDTO));
        if (this.archivoSeleccionado) formData.append('archivo', this.archivoSeleccionado);

        this.obstetraService.finalizarAtencion(formData).subscribe({
          next: (res) => {
            Swal.fire({
              icon: 'success',
              title: '¡Consulta Finalizada!',
              showConfirmButton: false,
              timer: 1500
            }).then(() => {
              this.router.navigate(['/doctor/dashboard']);
            });
          },
          error: (err) => {
            Swal.fire('Error', 'No se pudo guardar la consulta', 'error');
          }
        });
      }
    });
  }

 

  cancelar() {
    this.router.navigate(['/doctor/dashboard']);
  }
}