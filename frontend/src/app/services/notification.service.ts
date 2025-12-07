import { Injectable, inject } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { UI_MESSAGES } from '../constants/ui-messages.constants';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  private snackBar = inject(MatSnackBar);

  success(message: string): void {
    this.snackBar.open(message, UI_MESSAGES.CLOSE, { duration: 3000 });
  }

  error(message: string): void {
    this.snackBar.open(message, UI_MESSAGES.CLOSE, { duration: 5000 });
  }

  info(message: string): void {
    this.snackBar.open(message, UI_MESSAGES.CLOSE, { duration: 3000 });
  }
}
