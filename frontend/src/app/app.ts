import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Navbar } from './shared/navbar/navbar';
import { Avisos } from './shared/avisos/avisos';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, Navbar, Avisos],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {}
