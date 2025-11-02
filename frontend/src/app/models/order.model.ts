import { PaymentStatus, PreparationStatus, DeliveryStatus } from './order-status.model';
import { User } from './auth.model';

export interface OrderItem {
  id?: OrderItemId;
  item: Item;
  amount: number;
  order?: Order;
}

export interface OrderItemId {
  orderId: number;
  itemId: number;
}

export interface Item {
  id: number;
  name: string;
  price: number;
}

export interface Order {
  id?: number;
  table: string;
  paymentStatus: PaymentStatus;
  preparationStatus: PreparationStatus;
  deliveryStatus: DeliveryStatus;
  creationDate: string;
  paidAt?: string;
  preparedAt?: string;
  deliveredAt?: string;
  takenBy: User;
  orderItems: OrderItem[];
}
