import { Order } from './order.model';

export interface OrderResponse {
  order: Order;
  serverTimestamp: string;
}

export interface OrderListResponse {
  orders: Order[];
  serverTimestamp: string;
}
