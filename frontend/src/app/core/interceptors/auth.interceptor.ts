import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { AuthService } from '../services/auth.service';
import { AvisoService } from '../services/aviso.service';

/**
 * 1) Agrega "Authorization: Bearer <token>" a cada petición (menos al login).
 * 2) Si el backend responde 401 (token vencido o inválido): avisa, cierra la sesión y manda al login.
 */
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const auth = inject(AuthService);
  const avisos = inject(AvisoService);
  const esLogin = req.url.endsWith('/auth/login');
  const token = auth.token();

  const peticion = token && !esLogin
    ? req.clone({ setHeaders: { Authorization: `Bearer ${token}` } })
    : req;

  return next(peticion).pipe(
    catchError((err: HttpErrorResponse) => {
      if (err.status === 401 && !esLogin) {
        avisos.advertencia('Tu sesión venció. Inicia sesión de nuevo.');
        auth.logout();
      }
      return throwError(() => err);
    })
  );
};
