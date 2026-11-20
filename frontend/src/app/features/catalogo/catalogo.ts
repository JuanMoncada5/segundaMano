import { Component, computed, inject, signal } from '@angular/core';
import { CurrencyPipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { takeUntilDestroyed, toObservable } from '@angular/core/rxjs-interop';
import { catchError, debounceTime, of, switchMap, tap } from 'rxjs';
import { PublicacionService } from '../../core/services/publicacion.service';
import { AvisoService } from '../../core/services/aviso.service';
import { Pagina, Publicacion } from '../../core/models/publicacion.model';

/**
 * Catálogo: búsqueda reactiva con Signals.
 * Cada vez que cambia un filtro o la página, se vuelve a consultar al BACKEND
 * (la paginación y el filtrado los hace Spring, no el navegador).
 */
@Component({
  selector: 'app-catalogo',
  imports: [CurrencyPipe, RouterLink],
  templateUrl: './catalogo.html',
  styleUrl: './catalogo.css'
})
export class Catalogo {
  private readonly servicio = inject(PublicacionService);
  private readonly avisos = inject(AvisoService);

  protected readonly tamano = 6;

  // Estado de la búsqueda (Signals)
  protected readonly texto = signal('');
  protected readonly curso = signal('');
  protected readonly valorMax = signal<number | null>(null);
  protected readonly pagina = signal(0);

  protected readonly resultado = signal<Pagina<Publicacion> | null>(null);
  protected readonly cargando = signal(false);

  protected readonly totalPaginas = computed(() => Math.max(this.resultado()?.totalPaginas ?? 1, 1));
  protected readonly hayAnterior = computed(() => this.pagina() > 0);
  protected readonly haySiguiente = computed(() => this.pagina() + 1 < (this.resultado()?.totalPaginas ?? 0));
  protected readonly sinResultados = computed(() => this.resultado()?.contenido.length === 0);

  /** Junta filtros y página: si cualquiera cambia, se dispara una nueva consulta. */
  private readonly consulta = computed(() => ({
    filtros: { texto: this.texto(), curso: this.curso(), valorMax: this.valorMax() },
    pagina: this.pagina()
  }));

  constructor() {
    toObservable(this.consulta).pipe(
      debounceTime(300),
      tap(() => this.cargando.set(true)),
      switchMap(c => this.servicio.buscar(c.filtros, c.pagina, this.tamano).pipe(
        catchError((err: HttpErrorResponse) => {
          this.avisos.errorHttp(err);
          return of(null);
        })
      )),
      takeUntilDestroyed()
    ).subscribe(respuesta => {
      this.resultado.set(respuesta);
      this.cargando.set(false);
    });
  }

  cambiarTexto(valor: string): void {
    this.texto.set(valor);
    this.pagina.set(0);
  }

  cambiarCurso(valor: string): void {
    this.curso.set(valor);
    this.pagina.set(0);
  }

  cambiarValorMax(valor: string): void {
    this.valorMax.set(valor ? Number(valor) : null);
    this.pagina.set(0);
  }

  limpiar(): void {
    this.texto.set('');
    this.curso.set('');
    this.valorMax.set(null);
    this.pagina.set(0);
  }

  anterior(): void {
    if (this.hayAnterior()) this.pagina.update(p => p - 1);
  }

  siguiente(): void {
    if (this.haySiguiente()) this.pagina.update(p => p + 1);
  }
}
