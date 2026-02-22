import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Sidebar } from '../../components/sidebar/sidebar'; // 1. Importa tu Sidebar

@Component({
  selector: 'app-secure-layout',
  standalone: true,
  imports: [
    RouterOutlet,
    Sidebar 
  ],
  templateUrl: './secure-layout.html',
  styleUrl: './secure-layout.scss'
})
export class SecureLayout {
  
}