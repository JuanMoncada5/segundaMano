/** Espejo de PublicacionRankingResponse (GET /api/admin/reportes/publicaciones-mas-ofertadas). */
export interface PublicacionRanking {
  posicion: number;
  codigo: string;
  titulo: string;
  valor: number;
  correoDueno: string;
  cantidadOfertas: number;
}
