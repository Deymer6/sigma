
export interface CitaResponse {
  idCita: number;
  pacienteId: number;
  obstetraId: number;
  pacienteNombre: string;
  obstetraNombre: string;
  fechaCita: string; 
  motivoConsulta: string;
  estadoCita: string;
}


export interface CitaRequest {
  pacienteId: number;
  obstetraId: number;
  fechaCita: string; 
  motivoConsulta: string;
}