/** Espejo de ErrorApi: así responde el backend todos los errores (400, 401, 403, 404, 422...). */
export interface ErrorApi {
  estado: number;
  error: string;
  mensaje: string;
  detalles: string[];
}
