import { Injectable } from '@angular/core';
import { OrderEventDTO } from '../models/order-event.model';
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
      takenBy: this.mapUser(dto.takenBy),
      chargedBy: dto.chargedBy ? this.mapUser(dto.chargedBy) : undefined,
      preparedBy: dto.preparedBy ? this.mapUser(dto.preparedBy) : undefined,
      deliveredBy: dto.deliveredBy ? this.mapUser(dto.deliveredBy) : undefined,
      orderItems: this.mapOrderItems(dto)
    };
  }

  private mapUser(username: string) {
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
      amount: item.amount,
      prepared: item.prepared
    }));
  }
}
