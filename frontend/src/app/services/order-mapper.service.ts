import { Injectable } from '@angular/core';
import { OrderEventDTO } from './websocket.service';
import { Order } from '../models/order.model';

@Injectable({
  providedIn: 'root'
})
export class OrderMapperService {

  convertDTOToOrder(dto: OrderEventDTO): Order {
    return {
      id: dto.id,
      table: dto.table,
      paymentStatus: dto.paymentStatus as any,
      preparationStatus: dto.preparationStatus as any,
      deliveryStatus: dto.deliveryStatus as any,
      creationDate: dto.creationDate,
      paidAt: dto.paidAt,
      preparedAt: dto.preparedAt,
      deliveredAt: dto.deliveredAt,
      takenBy: this.mapTakenBy(dto.takenBy),
      orderItems: this.mapOrderItems(dto)
    };
  }

  private mapTakenBy(username: string) {
    return {
      username: username,
      role: ''
    };
  }

  private mapOrderItems(dto: OrderEventDTO) {
    return dto.orderItems.map(item => ({
      id: { orderId: dto.id, itemId: item.itemId },
      order: null as any,
      item: {
        id: item.itemId,
        name: item.itemName,
        price: item.itemPrice
      },
      amount: item.amount
    }));
  }
}
