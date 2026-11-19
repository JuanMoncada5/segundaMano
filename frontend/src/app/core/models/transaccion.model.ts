/** Espejo de TransaccionResponse. */
export interface Transaccion {
  id: string;
  codigoPublicacion: string;
  tituloPublicacion: string;
  correoComprador: string;
  correoVendedor: string;
  montoAcordado: number;
  estado: string;
  zonaEncuentro: string | null;
  referenciaEncuentro: string | null;
  calificadaPorComprador: boolean;
  calificadaPorVendedor: boolean;
}

/** Espejo de CalificacionBody (POST /api/transacciones/{id}/calificacion). */
export interface CalificacionRequest {
  estrellas: number;
}

/** Espejo de UniCambistaResponse. */
export interface UniCambista {
  correoInstitucional: string;
  nombre: string;
  verificado: boolean;
  promedioEstrellas: number;
  totalTransacciones: number;
}
