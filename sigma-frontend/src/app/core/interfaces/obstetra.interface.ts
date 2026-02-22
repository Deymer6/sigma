export interface ObstetraDTO {
  id: number;
  nombre: string;
  apellido: string;
  especialidad: string;
  numColegiatura: string; 
  
  
  email: string;
  estado: string; // 'Activo' | 'Inactivo'
}