export interface AnalisisClinicoDTO {
  idAnalisis: number;
  historialId: number;
  tipoAnalisis: string;
  fechaRealizacion: string; // Viene como string fecha ISO
  valoresObservaciones: string;
  archivoAdjuntoUrl: string | null; 
}