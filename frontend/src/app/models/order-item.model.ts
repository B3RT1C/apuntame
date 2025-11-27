import { Item } from './item.model';
import { Order } from './order.model';

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
