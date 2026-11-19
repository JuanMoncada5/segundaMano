import { Component, inject } from '@angular/core';
import { AvisoService } from '../../core/services/aviso.service';

@Component({
  selector: 'app-avisos',
  templateUrl: './avisos.html',
  styleUrl: './avisos.css'
})
export class Avisos {
  protected readonly avisoService = inject(AvisoService);
}
