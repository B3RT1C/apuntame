import { Injectable } from '@angular/core';
import { Order } from '../models/order.model';

@Injectable({
  providedIn: 'root'
})
export class OrderCalculationService {

  calculateTotal(order: Order): number {
    if (!order.orderItems || order.orderItems.length === 0) {
      return 0;
    }
    return order.orderItems.reduce((total, orderItem) => {
      return total + (orderItem.item.price * orderItem.amount);
    }, 0);
  }
}
