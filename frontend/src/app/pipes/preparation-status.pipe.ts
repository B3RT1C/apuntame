import { Pipe, PipeTransform } from '@angular/core';
import { PreparationStatus } from '../models/order-status.model';

@Pipe({
  name: 'preparationStatus',
  standalone: true
})
export class PreparationStatusPipe implements PipeTransform {
  private translations: { [key: string]: string } = {
    'PENDING': 'Pendiente',
    'READY': 'Listo',
    'CANCELLED': 'Cancelado'
  };

  transform(value: PreparationStatus | string): string {
    return this.translations[value] || value;
  }
}
