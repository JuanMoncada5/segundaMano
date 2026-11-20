import { Component, inject, signal } from '@angular/core';
import { CurrencyPipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { ReporteService } from '../../core/services/reporte.service';
import { AvisoService } from '../../core/services/aviso.service';
import { PublicacionRanking } from '../../core/models/reporte.model';

/** Reporte solo para ADMIN: publicaciones activas con más ofertas. Protegida con adminGuard. */
@Component({
  selector: 'app-ranking',
  imports: [CurrencyPipe],
  templateUrl: './ranking.html',
  styleUrl: './ranking.css'
})
export class Ranking {
  private readonly servicio = inject(ReporteService);
  private readonly avisos = inject(AvisoService);

  protected readonly limite = signal(5);
  protected readonly ranking = signal<PublicacionRanking[] | null>(null);

  constructor() {
    this.cargar();
  }

  cambiarLimite(valor: string): void {
    this.limite.set(Number(valor));
    this.cargar();
  }

  private cargar(): void {
    this.servicio.publicacionesMasOfertadas(this.limite()).subscribe({
      next: lista => this.ranking.set(lista),
      error: (err: HttpErrorResponse) => {
        this.ranking.set([]);
        this.avisos.errorHttp(err);
      }
    });
  }
}
