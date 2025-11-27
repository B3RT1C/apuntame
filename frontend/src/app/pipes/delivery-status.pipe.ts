import { Pipe, PipeTransform } from '@angular/core';
import { DeliveryStatus } from '../models/order-status.model';

@Pipe({
  name: 'deliveryStatus',
  standalone: true
})
export class DeliveryStatusPipe implements PipeTransform {
  private translations: { [key: string]: string } = {
    'PENDING': 'Pendiente',
    'DELIVERED': 'Entregado',
    'CANCELLED': 'Cancelado'
  };

  transform(value: DeliveryStatus | string): string {
    return this.translations[value] || value;
  }
}
