/** Espejo de PublicacionResponse. */
export interface Publicacion {
  codigo: string;
  correoDueno: string;
  titulo: string;
  valor: number;
  estado: string;
  etiquetaVigenciaAcademica: string | null;
}

/** Espejo de PaginaResponse<T> (la paginación la hace el backend). */
export interface Pagina<T> {
  contenido: T[];
  totalElementos: number;
  totalPaginas: number;
  pagina: number;
  tamano: number;
}

/** Filtros de GET /api/publicaciones/buscar (todos opcionales). */
export interface FiltrosBusqueda {
  texto?: string;
  curso?: string;
  valorMin?: number | null;
  valorMax?: number | null;
}

/** Espejo de OfertarArticuloBody (POST /api/publicaciones/{codigo}/ofertas). */
export interface OfertarRequest {
  montoPropuesto: number;
}

/** Espejo de OfertaResponse. */
export interface Oferta {
  id: string;
  codigoPublicacion: string;
  correoOferente: string;
  montoPropuesto: number;
  estado: string;
}
