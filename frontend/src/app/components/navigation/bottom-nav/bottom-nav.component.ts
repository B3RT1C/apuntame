import { Component, Input, Output, EventEmitter } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { ListItem } from '../../../models/list-item.model';

@Component({
  selector: 'app-bottom-nav',
  imports: [
    MatButtonModule,
    MatIconModule
  ],
  templateUrl: './bottom-nav.component.html',
  styleUrl: './bottom-nav.component.scss'
})
export class BottomNavComponent {
  @Input() menuItems: ListItem[] = [];
  @Output() itemClicked = new EventEmitter<string>();
}
