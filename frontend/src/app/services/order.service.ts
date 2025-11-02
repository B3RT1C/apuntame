import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ConfigService } from './config.service';
import { Order, OrderItem } from '../models/order.model';
import { PaymentStatus, PreparationStatus, DeliveryStatus } from '../models/order-status.model';

@Injectable({
  providedIn: 'root'
})
export class OrderService {
  private http = inject(HttpClient);
  private config = inject(ConfigService);
  private baseUrl = `${this.config.apiUrl}/orders`;

  getAllOrders(limit?: number): Observable<Order[]> {
    const url = limit ? `${this.baseUrl}?limit=${limit}` : this.baseUrl;
    return this.http.get<Order[]>(url);
  }

  getOrderById(id: number): Observable<Order> {
    return this.http.get<Order>(`${this.baseUrl}/${id}`);
  }

  createOrder(order: Order): Observable<Order> {
    return this.http.post<Order>(this.baseUrl, order);
  }

  updateOrder(id: number, order: Order): Observable<Order> {
    return this.http.put<Order>(`${this.baseUrl}/${id}`, order);
  }

  deleteOrder(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  updatePaymentStatus(orderId: number, status: PaymentStatus): Observable<Order> {
    return this.http.patch<Order>(`${this.baseUrl}/${orderId}/payment-status`, status);
  }

  updatePreparationStatus(orderId: number, status: PreparationStatus): Observable<Order> {
    return this.http.patch<Order>(`${this.baseUrl}/${orderId}/preparation-status`, status);
  }

  updateDeliveryStatus(orderId: number, status: DeliveryStatus): Observable<Order> {
    return this.http.patch<Order>(`${this.baseUrl}/${orderId}/delivery-status`, status);
  }

  addItemToOrder(orderId: number, orderItem: OrderItem): Observable<OrderItem> {
    return this.http.post<OrderItem>(`${this.baseUrl}/${orderId}/items`, orderItem);
  }

  addMultipleItemsToOrder(orderId: number, orderItems: OrderItem[]): Observable<OrderItem[]> {
    return this.http.post<OrderItem[]>(`${this.baseUrl}/${orderId}/items/batch`, orderItems);
  }

  getOrderItem(orderId: number, itemId: number): Observable<OrderItem> {
    return this.http.get<OrderItem>(`${this.baseUrl}/${orderId}/items/${itemId}`);
  }

  updateOrderItem(orderId: number, itemId: number, orderItem: OrderItem): Observable<OrderItem> {
    return this.http.put<OrderItem>(`${this.baseUrl}/${orderId}/items/${itemId}`, orderItem);
  }

  removeItemFromOrder(orderId: number, itemId: number, amount?: number): Observable<void> {
    const url = amount
      ? `${this.baseUrl}/${orderId}/items/${itemId}?amount=${amount}`
      : `${this.baseUrl}/${orderId}/items/${itemId}`;
    return this.http.delete<void>(url);
  }
}
