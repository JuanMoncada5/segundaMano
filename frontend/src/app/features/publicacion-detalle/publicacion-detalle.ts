import { Component, computed, inject, signal } from '@angular/core';
import { CurrencyPipe } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { PublicacionService } from '../../core/services/publicacion.service';
import { AuthService } from '../../core/services/auth.service';
import { AvisoService } from '../../core/services/aviso.service';
import { Oferta, Publicacion } from '../../core/models/publicacion.model';

/**
 * Detalle de una publicación + operación de negocio del COMPRADOR: ofertar.
 * El formulario valida, apaga el botón mientras envía y traduce los errores HTTP con avisos.
 */
@Component({
  selector: 'app-publicacion-detalle',
  imports: [CurrencyPipe, RouterLink, ReactiveFormsModule],
  templateUrl: './publicacion-detalle.html',
  styleUrl: './publicacion-detalle.css'
})
export class PublicacionDetalle {
  private readonly route = inject(ActivatedRoute);
  private readonly servicio = inject(PublicacionService);
  private readonly auth = inject(AuthService);
  private readonly avisos = inject(AvisoService);
  private readonly fb = inject(FormBuilder);

  protected readonly publicacion = signal<Publicacion | null>(null);
  protected readonly noEncontrada = signal(false);
  protected readonly enviando = signal(false);
  protected readonly ofertaEnviada = signal<Oferta | null>(null);

  /** El dueño no puede ofertar sobre su propia publicación: no se le muestra el formulario. */
  protected readonly esMia = computed(() => this.publicacion()?.correoDueno === this.auth.correo());
  /** Regla del negocio: la oferta mínima es el 50% del precio. */
  protected readonly minimo = computed(() => Math.ceil((this.publicacion()?.valor ?? 0) / 2));

  protected readonly form = this.fb.group({
    montoPropuesto: this.fb.control<number | null>(null, [Validators.required, Validators.min(1)])
  });

  constructor() {
    const codigo = this.route.snapshot.paramMap.get('codigo') ?? '';
    this.servicio.consultar(codigo).subscribe({
      next: p => this.publicacion.set(p),
      error: (err: HttpErrorResponse) => {
        this.noEncontrada.set(true);
        this.avisos.errorHttp(err);
      }
    });
  }

  ofertar(): void {
    const publicacion = this.publicacion();
    const monto = this.form.controls.montoPropuesto.value;
    if (!publicacion || this.form.invalid || monto == null) {
      this.form.markAllAsTouched();
      return;
    }
    this.enviando.set(true);
    this.servicio.ofertar(publicacion.codigo, { montoPropuesto: monto }).subscribe({
      next: oferta => {
        this.enviando.set(false);
        this.ofertaEnviada.set(oferta);
        this.form.reset();
        this.avisos.exito(`¡Oferta enviada! ${oferta.id} quedó ${oferta.estado}.`);
      },
      error: (err: HttpErrorResponse) => {
        this.enviando.set(false);
        this.avisos.errorHttp(err); // 400, 404, 422 (ej: menos del 50%) traducidos a un mensaje claro
      }
    });
  }
}
