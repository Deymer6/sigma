import { Component, Input, inject, signal } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AnalisisService } from '../../../../core/services/analisis.service';

@Component({
  selector: 'app-subir-analisis',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule],
  templateUrl: './subir-analisis.html',
  styleUrl: './subir-analisis.scss'
})
export class SubirAnalisisComponent {

  // ID del historial al que vamos a adjuntar el archivo (Debe venir del padre)
  @Input({ required: true }) historialId!: number;

  private fb = inject(FormBuilder);
  private analisisService = inject(AnalisisService);

  public uploadForm: FormGroup;
  public selectedFile: File | null = null;
  
  // Estados de la UI
  public isUploading = signal<boolean>(false);
  public message = signal<{ text: string, type: 'success' | 'error' } | null>(null);

  constructor() {
    this.uploadForm = this.fb.group({
      tipoAnalisis: ['', Validators.required],
      fecha: [new Date().toISOString().split('T')[0], Validators.required], // Fecha hoy por defecto
      observaciones: ['', Validators.required]
    });
  }

  // 1. CAPTURAR EL ARCHIVO
  onFileSelected(event: any) {
    const file: File = event.target.files[0];
    if (file) {
      // Validar tamaño (ej: máx 5MB) o tipo si quieres
      this.selectedFile = file;
    }
  }

  // 2. ENVIAR AL BACKEND
  onSubmit() {
    if (this.uploadForm.invalid || !this.selectedFile) {
      this.message.set({ text: 'Faltan datos o el archivo.', type: 'error' });
      return;
    }

    this.isUploading.set(true);
    this.message.set(null);

    // CREAR FORM DATA (La clave para enviar archivos)
    const formData = new FormData();
    formData.append('file', this.selectedFile);
    formData.append('idHistorial', this.historialId.toString());
    formData.append('tipoAnalisis', this.uploadForm.value.tipoAnalisis);
    formData.append('observaciones', this.uploadForm.value.observaciones);
    formData.append('fecha', this.uploadForm.value.fecha);

    this.analisisService.subirAnalisis(formData).subscribe({
      next: (res) => {
        this.isUploading.set(false);
        this.message.set({ text: '¡Análisis subido correctamente!', type: 'success' });
        this.uploadForm.reset();
        this.selectedFile = null;
        // Aquí podrías emitir un evento para que el padre recargue la lista
      },
      error: (err) => {
        console.error(err);
        this.isUploading.set(false);
        this.message.set({ text: 'Error al subir el archivo.', type: 'error' });
      }
    });
  }
}