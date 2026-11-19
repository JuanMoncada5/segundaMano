import { Injectable, computed, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { LoginRequest, LoginResponse } from '../models/auth.model';

const CLAVE_SESION = 'sesion';

/** Maneja la sesión: guarda el token en localStorage y expone el estado con Signals. */
@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);
  private readonly _sesion = signal<LoginResponse | null>(this.leerSesionGuardada());

  readonly sesion = this._sesion.asReadonly();
  readonly autenticado = computed(() => this._sesion() !== null);
  readonly esAdmin = computed(() => this._sesion()?.rol === 'ADMIN');
  readonly correo = computed(() => this._sesion()?.correoInstitucional ?? '');

  login(datos: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${environment.apiUrl}/auth/login`, datos).pipe(
      tap(respuesta => {
        localStorage.setItem(CLAVE_SESION, JSON.stringify(respuesta));
        this._sesion.set(respuesta);
      })
    );
  }

  logout(): void {
    localStorage.removeItem(CLAVE_SESION);
    this._sesion.set(null);
    this.router.navigate(['/login']);
  }

  token(): string | null {
    return this._sesion()?.token ?? null;
  }

  /** Al recargar la página se recupera la sesión, salvo que el token ya haya vencido. */
  private leerSesionGuardada(): LoginResponse | null {
    try {
      const guardada = localStorage.getItem(CLAVE_SESION);
      if (!guardada) return null;
      const sesion = JSON.parse(guardada) as LoginResponse;
      if (new Date(sesion.expiraEn).getTime() <= Date.now()) {
        localStorage.removeItem(CLAVE_SESION);
        return null;
      }
      return sesion;
    } catch {
      return null;
    }
  }
}
