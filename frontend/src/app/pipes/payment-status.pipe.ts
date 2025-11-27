import { Pipe, PipeTransform } from '@angular/core';
import { PaymentStatus } from '../models/order-status.model';

@Pipe({
  name: 'paymentStatus',
  standalone: true
})
export class PaymentStatusPipe implements PipeTransform {
  private translations: { [key: string]: string } = {
    'PENDING': 'Pendiente',
    'PAID': 'Pagado',
    'REFUNDED': 'Devuelto',
    'CANCELLED': 'Cancelado'
  };

  transform(value: PaymentStatus | string): string {
    return this.translations[value] || value;
  }
}
