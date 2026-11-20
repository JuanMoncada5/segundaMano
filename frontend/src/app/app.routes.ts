import { Routes } from '@angular/router';
import { adminGuard, authGuard, invitadoGuard } from './core/guards/auth.guards';
import { Login } from './features/login/login';
import { Catalogo } from './features/catalogo/catalogo';
import { PublicacionDetalle } from './features/publicacion-detalle/publicacion-detalle';
import { Publicar } from './features/publicar/publicar';
import { MisTransacciones } from './features/mis-transacciones/mis-transacciones';
import { Ranking } from './features/ranking/ranking';

export const routes: Routes = [
  { path: '', redirectTo: 'catalogo', pathMatch: 'full' },
  { path: 'login', component: Login, canActivate: [invitadoGuard] },
  { path: 'catalogo', component: Catalogo, canActivate: [authGuard] },
  { path: 'publicaciones/:codigo', component: PublicacionDetalle, canActivate: [authGuard] },
  { path: 'publicar', component: Publicar, canActivate: [authGuard] },
  { path: 'mis-transacciones', component: MisTransacciones, canActivate: [authGuard] },
  { path: 'admin/ranking', component: Ranking, canActivate: [authGuard, adminGuard] },
  { path: '**', redirectTo: 'catalogo' }
];
