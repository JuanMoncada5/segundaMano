import { Routes } from '@angular/router';
import { authGuard, invitadoGuard } from './core/guards/auth.guards';
import { Login } from './features/login/login';
import { Catalogo } from './features/catalogo/catalogo';
import { MisTransacciones } from './features/mis-transacciones/mis-transacciones';

export const routes: Routes = [
  { path: 'mis-transacciones', component: MisTransacciones, canActivate: [authGuard] },
  { path: '', redirectTo: 'catalogo', pathMatch: 'full' },
  { path: 'login', component: Login, canActivate: [invitadoGuard] },
  { path: 'catalogo', component: Catalogo, canActivate: [authGuard] },
  { path: '**', redirectTo: 'catalogo' }
];
