import { Injectable } from '@angular/core';
import { Order } from '../models/order.model';
import { OrderItem } from '../models/order-item.model';

@Injectable({
  providedIn: 'root'
})
export class OrderCalculationService {

  calculateTotal(order: Order): number {
    return this.calculateTotalFromItems(order.orderItems);
  }

  calculateTotalFromItems(orderItems: OrderItem[]): number {
    if (!orderItems || orderItems.length === 0) {
      return 0;
    }
    return orderItems.reduce((total, orderItem) => {
      return total + (orderItem.item.price * orderItem.amount);
    }, 0);
  }
}
