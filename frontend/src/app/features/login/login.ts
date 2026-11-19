import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { AuthService } from '../../core/services/auth.service';
import { AvisoService } from '../../core/services/aviso.service';

@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule],
  templateUrl: './login.html',
  styleUrl: './login.css'
})
export class Login {
  private readonly fb = inject(FormBuilder);
  private readonly auth = inject(AuthService);
  private readonly avisos = inject(AvisoService);
  private readonly router = inject(Router);

  protected readonly enviando = signal(false);

  protected readonly form = this.fb.nonNullable.group({
    correoInstitucional: ['', [Validators.required, Validators.email]],
    contrasena: ['', Validators.required]
  });

  ingresar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.enviando.set(true);
    this.auth.login(this.form.getRawValue()).subscribe({
      next: respuesta => {
        this.avisos.exito(`Bienvenido(a), ${respuesta.correoInstitucional}`);
        this.router.navigate(['/catalogo']);
      },
      error: (err: HttpErrorResponse) => {
        this.enviando.set(false);
        if (err.status === 401) {
          this.avisos.error('Correo o contraseña incorrectos.');
        } else {
          this.avisos.errorHttp(err);
        }
      }
    });
  }
}
