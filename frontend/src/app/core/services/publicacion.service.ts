import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { FiltrosBusqueda, Oferta, OfertarRequest, Pagina, Publicacion } from '../models/publicacion.model';

/** Servicio del grupo /api/publicaciones. El token lo agrega el interceptor, no este servicio. */
@Injectable({ providedIn: 'root' })
export class PublicacionService {
  private readonly http = inject(HttpClient);
  private readonly url = `${environment.apiUrl}/publicaciones`;

  /** Catálogo paginado y filtrado en el backend (nunca trae las eliminadas). */
  buscar(filtros: FiltrosBusqueda, pagina: number, tamano: number): Observable<Pagina<Publicacion>> {
    let params = new HttpParams().set('pagina', pagina).set('tamano', tamano);
    if (filtros.texto?.trim()) params = params.set('texto', filtros.texto.trim());
    if (filtros.curso?.trim()) params = params.set('curso', filtros.curso.trim());
    if (filtros.valorMin != null) params = params.set('valorMin', filtros.valorMin);
    if (filtros.valorMax != null) params = params.set('valorMax', filtros.valorMax);
    return this.http.get<Pagina<Publicacion>>(`${this.url}/buscar`, { params });
  }

  consultar(codigo: string): Observable<Publicacion> {
    return this.http.get<Publicacion>(`${this.url}/${codigo}`);
  }

  /** Operación de negocio: ofertar. Quién oferta sale del token en el backend. */
  ofertar(codigo: string, oferta: OfertarRequest): Observable<Oferta> {
    return this.http.post<Oferta>(`${this.url}/${codigo}/ofertas`, oferta);
  }
}
