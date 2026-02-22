
export interface DashboardPacienteDTO {
  idPaciente: number;
  pacienteNombre: string;
  embarazo: {
    semanas: number;
    diasFaltantes: number;
    trimestre: string;
    fechaProbableParto: string; 
    porcentajeProgreso: number;
  };
  proximaCita: {
    fecha: string;
    hora: string;
    doctorNombre: string;
    especialidad: string;
  } | null;
  obstetra: {
    nombre: string;
    especialidad: string;
    fotoUrl?: string;
    telefono: string;
    whatsapp: string;
  } | null;
  ultimosResultados: Array<{
    nombre: string;
    fecha: string; // o Date
    archivoUrl: string;
    tipo: 'pdf' | 'imagen';
  }>;
  indicaciones: Array<{
    medicamento: string;
    dosis: string;
  }>;
}