import { Component, inject, signal } from '@angular/core';
import { CurrencyPipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { TransaccionService } from '../../core/services/transaccion.service';
import { AuthService } from '../../core/services/auth.service';
import { AvisoService } from '../../core/services/aviso.service';
import { Transaccion } from '../../core/models/transaccion.model';

/**
 * Funcionalidad propia: después de una Transacción COMPLETADA, cada participante
 * califica al otro de 1 a 5 estrellas y eso actualiza su RachaConfianza en el backend.
 */
@Component({
  selector: 'app-mis-transacciones',
  imports: [CurrencyPipe],
  templateUrl: './mis-transacciones.html',
  styleUrl: './mis-transacciones.css'
})
export class MisTransacciones {
  private readonly servicio = inject(TransaccionService);
  private readonly auth = inject(AuthService);
  private readonly avisos = inject(AvisoService);

  protected readonly transacciones = signal<Transaccion[] | null>(null);
  /** Estrellas elegidas en cada transacción (id -> 1..5) mientras la persona decide. */
  protected readonly estrellas = signal<Record<string, number>>({});
  /** id de la transacción que se está calificando (para apagar su botón). */
  protected readonly enviando = signal<string | null>(null);
  protected readonly opciones = [1, 2, 3, 4, 5];

  constructor() {
    this.cargar();
  }

  soyComprador(t: Transaccion): boolean {
    return t.correoComprador === this.auth.correo();
  }

  contraparte(t: Transaccion): string {
    return this.soyComprador(t) ? t.correoVendedor : t.correoComprador;
  }

  yaCalifique(t: Transaccion): boolean {
    return this.soyComprador(t) ? t.calificadaPorComprador : t.calificadaPorVendedor;
  }

  puedeCalificar(t: Transaccion): boolean {
    return t.estado === 'COMPLETADA' && !this.yaCalifique(t);
  }

  /** Cuántas estrellas eligió la persona en esta transacción (0 si todavía ninguna). */
  elegidas(t: Transaccion): number {
    return this.estrellas()[t.id] || 0;
  }

  elegir(t: Transaccion, n: number): void {
    this.estrellas.update(mapa => ({ ...mapa, [t.id]: n }));
  }

  calificar(t: Transaccion): void {
    const n = this.elegidas(t);
    if (!n) return;
    this.enviando.set(t.id);
    this.servicio.calificar(t.id, { estrellas: n }).subscribe({
      next: calificado => {
        this.enviando.set(null);
        this.avisos.exito(
          `Calificaste a ${calificado.nombre} con ${n} ★. Su promedio ahora es ${calificado.promedioEstrellas.toFixed(1)}.`);
        this.cargar();
      },
      error: (err: HttpErrorResponse) => {
        this.enviando.set(null);
        this.avisos.errorHttp(err); // 422 si ya calificó o la transacción no está completada
      }
    });
  }

  private cargar(): void {
    this.servicio.listarDe(this.auth.correo()).subscribe({
      next: lista => this.transacciones.set(lista),
      error: (err: HttpErrorResponse) => {
        this.transacciones.set([]);
        this.avisos.errorHttp(err);
      }
    });
  }
}
