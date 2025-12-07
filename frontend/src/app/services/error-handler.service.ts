import { Injectable, inject } from '@angular/core';
import { NotificationService } from './notification.service';
import { UI_MESSAGES } from '../constants/ui-messages.constants';

@Injectable({
  providedIn: 'root'
})
export class ErrorHandlerService {
  private notificationService = inject(NotificationService);

  handleHttpError(err: any, defaultMessage: string, reloadFn?: () => void): void {
    console.error(err);

    if (err.status === 404 && reloadFn) {
      reloadFn();
      this.notificationService.info(UI_MESSAGES.ELEMENT_ALREADY_DELETED);
      return;
    }

    let message = defaultMessage;

    if (err.error?.message) {
      message = err.error.message;
    } else if (err.status === 404) {
      message = UI_MESSAGES.RESOURCE_NOT_FOUND;
    } else if (err.status === 409) {
      message = UI_MESSAGES.RESOURCE_EXISTS;
    } else if (err.status === 400) {
      message = err.error?.message || UI_MESSAGES.INVALID_DATA;
    } else if (err.status === 500) {
      message = UI_MESSAGES.SERVER_ERROR;
    }

    this.notificationService.error(message);
  }
}
