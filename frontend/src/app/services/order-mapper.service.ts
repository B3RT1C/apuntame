import { Injectable } from '@angular/core';
import { OrderEventDTO } from '../models/order-event.model';
import { Order } from '../models/order.model';
import { PaymentStatus, PreparationStatus, DeliveryStatus } from '../models/order-status.model';
import { User } from '../models/user.model';
import { OrderItem } from '../models/order-item.model';

@Injectable({
  providedIn: 'root'
})
export class OrderMapperService {

  convertDTOToOrder(dto: OrderEventDTO): Order {
    return {
      id: dto.id,
      table: dto.table,
      paymentStatus: dto.paymentStatus as PaymentStatus,
      preparationStatus: dto.preparationStatus as PreparationStatus,
      deliveryStatus: dto.deliveryStatus as DeliveryStatus,
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

  private mapUser(username: string): User {
    return {
      username: username,
      role: ''
    };
  }

  private mapOrderItems(dto: OrderEventDTO): OrderItem[] {
    return dto.orderItems.map(item => ({
      id: { orderId: dto.id, itemId: item.itemId },
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
