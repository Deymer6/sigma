
export interface HistorialDetalleDTO {
  idHistorial: number;
  fechaAtencion: string; 
  nombreDoctor: string;
  especialidad: string;
  diagnostico: string;
  prescripciones: string[]; 
  archivos: Array<{
    nombre: string;
    url: string;
    tipo: 'pdf' | 'imagen';
  }>;
}