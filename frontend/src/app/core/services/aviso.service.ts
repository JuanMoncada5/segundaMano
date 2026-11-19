import { Injectable, signal } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { ErrorApi } from '../models/error-api.model';

export interface Aviso {
  id: number;
  tipo: 'success' | 'danger' | 'warning' | 'info';
  texto: string;
}

/** Avisos para el usuario (se ven arriba a la derecha y se cierran solos). */
@Injectable({ providedIn: 'root' })
export class AvisoService {
  private siguienteId = 1;
  readonly avisos = signal<Aviso[]>([]);

  exito(texto: string): void { this.mostrar('success', texto); }
  error(texto: string): void { this.mostrar('danger', texto); }
  advertencia(texto: string): void { this.mostrar('warning', texto); }

  cerrar(id: number): void {
    this.avisos.update(lista => lista.filter(a => a.id !== id));
  }

  /** Traduce el código HTTP a un mensaje entendible para el usuario. */
  errorHttp(err: HttpErrorResponse): void {
    const cuerpo = err.error as ErrorApi | null;
    switch (err.status) {
      case 0:
        this.error('No se pudo conectar con el servidor. ¿Está corriendo el backend?');
        break;
      case 400:
        this.error('Datos inválidos: ' + (cuerpo?.detalles?.join(', ') || cuerpo?.mensaje || 'revisa el formulario'));
        break;
      case 401:
        break; // lo maneja el interceptor (sesión vencida)
      case 403:
        this.error('No tienes permiso para hacer esto.');
        break;
      case 404:
        this.error(cuerpo?.mensaje || 'No se encontró lo que buscabas.');
        break;
      case 422:
        this.advertencia(cuerpo?.mensaje || 'La operación no cumple las reglas del negocio.');
        break;
      default:
        this.error('Error inesperado del servidor. Intenta más tarde.');
    }
  }

  private mostrar(tipo: Aviso['tipo'], texto: string): void {
    const id = this.siguienteId++;
    this.avisos.update(lista => [...lista, { id, tipo, texto }]);
    setTimeout(() => this.cerrar(id), 5000);
  }
}
