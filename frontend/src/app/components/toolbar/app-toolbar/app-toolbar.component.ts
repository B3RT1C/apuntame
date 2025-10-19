import { Component, Input, Output, EventEmitter } from '@angular/core';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';

@Component({
  selector: 'app-toolbar',
  imports: [
    MatToolbarModule,
    MatIconModule,
    MatButtonModule
  ],
  templateUrl: './app-toolbar.component.html',
  styleUrl: './app-toolbar.component.scss'
})
export class AppToolbarComponent {
  @Input() title: string = '';
  @Input() username: string = '';
  @Input() showMenuButton: boolean = false;
  @Output() menuToggled = new EventEmitter<void>();
  @Output() logoutClicked = new EventEmitter<void>();
}
