import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { CalificacionRequest, Transaccion, UniCambista } from '../models/transaccion.model';

/** Servicio del grupo /api/transacciones (incluye la funcionalidad propia: calificar). */
@Injectable({ providedIn: 'root' })
export class TransaccionService {
  private readonly http = inject(HttpClient);
  private readonly url = `${environment.apiUrl}/transacciones`;

  listarDe(correo: string): Observable<Transaccion[]> {
    return this.http.get<Transaccion[]>(this.url, { params: new HttpParams().set('correo', correo) });
  }

  calificar(id: string, calificacion: CalificacionRequest): Observable<UniCambista> {
    return this.http.post<UniCambista>(`${this.url}/${id}/calificacion`, calificacion);
  }
}

