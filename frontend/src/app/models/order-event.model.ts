export enum OrderEventType {
  CREATED = 'CREATED',
  UPDATED = 'UPDATED'
}

export interface OrderItemDTO {
  itemId: number;
  itemName: string;
  itemPrice: number;
  amount: number;
}

export interface OrderEventDTO {
  eventType: OrderEventType;
  id: number;
  table: string;
  paymentStatus: string;
  preparationStatus: string;
  deliveryStatus: string;
  creationDate: string;
  paidAt?: string;
  preparedAt?: string;
  deliveredAt?: string;
  takenBy: string;
  chargedBy?: string;
  preparedBy?: string;
  deliveredBy?: string;
  orderItems: OrderItemDTO[];
}