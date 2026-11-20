import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { PublicacionService } from '../../core/services/publicacion.service';
import { AvisoService } from '../../core/services/aviso.service';

/** Operación de negocio del VENDEDOR: publicar un artículo (POST protegido con token). */
@Component({
  selector: 'app-publicar',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './publicar.html',
  styleUrl: './publicar.css'
})
export class Publicar {
  private readonly fb = inject(FormBuilder);
  private readonly servicio = inject(PublicacionService);
  private readonly avisos = inject(AvisoService);
  private readonly router = inject(Router);

  protected readonly enviando = signal(false);

  protected readonly form = this.fb.group({
    titulo: this.fb.nonNullable.control('', [Validators.required, Validators.maxLength(100)]),
    valor: this.fb.control<number | null>(null, [Validators.required, Validators.min(1)]),
    curso: this.fb.nonNullable.control(''),
    semestre: this.fb.nonNullable.control('', Validators.pattern(/^\d{4}-[12]$/))
  });

  publicar(): void {
    const { titulo, valor, curso, semestre } = this.form.getRawValue();
    if (this.form.invalid || valor == null) {
      this.form.markAllAsTouched();
      return;
    }
    this.enviando.set(true);
    this.servicio.publicar({
      titulo: titulo.trim(),
      valor,
      curso: curso.trim() || null,
      semestre: semestre.trim() || null
    }).subscribe({
      next: creada => {
        this.avisos.exito(`Publicaste "${creada.titulo}". Ya aparece en el catálogo.`);
        this.router.navigate(['/publicaciones', creada.codigo]);
      },
      error: (err: HttpErrorResponse) => {
        this.enviando.set(false);
        this.avisos.errorHttp(err);
      }
    });
  }
}
