import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { AvisoService } from '../services/aviso.service';

/** Guard 1: solo entra quien inició sesión. */
export const authGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  if (auth.autenticado()) return true;
  inject(AvisoService).advertencia('Debes iniciar sesión para ver esta página.');
  return inject(Router).createUrlTree(['/login']);
};

/** Guard 2: el login solo es para quien NO ha iniciado sesión. */
export const invitadoGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  return auth.autenticado() ? inject(Router).createUrlTree(['/catalogo']) : true;
};

/** Guard por rol (extra): solo ADMIN. */
export const adminGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  if (auth.esAdmin()) return true;
  inject(AvisoService).error('Esta página es solo para administradores.');
  return inject(Router).createUrlTree(['/catalogo']);
};
