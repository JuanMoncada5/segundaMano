import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { PublicacionRanking } from '../models/reporte.model';

/** Servicio del grupo /api/admin/reportes (solo rol ADMIN; el backend responde 403 a los demás). */
@Injectable({ providedIn: 'root' })
export class ReporteService {
  private readonly http = inject(HttpClient);
  private readonly url = `${environment.apiUrl}/admin/reportes`;

  publicacionesMasOfertadas(limite: number): Observable<PublicacionRanking[]> {
    const params = new HttpParams().set('limite', limite);
    return this.http.get<PublicacionRanking[]>(`${this.url}/publicaciones-mas-ofertadas`, { params });
  }
}
