import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { ConfigService } from './config.service';
import { Order } from '../models/order.model';
import { OrderItem } from '../models/order-item.model';
import { PaymentStatus, PreparationStatus, DeliveryStatus } from '../models/order-status.model';
import { OrderResponse, OrderListResponse } from '../models/order-response.model';

@Injectable({
  providedIn: 'root'
})
export class OrderService {
  private http = inject(HttpClient);
  private config = inject(ConfigService);
  private baseUrl = `${this.config.apiUrl}/orders`;

  getAllOrders(limit?: number): Observable<OrderListResponse> {
    const url = limit ? `${this.baseUrl}?limit=${limit}` : this.baseUrl;
    return this.http.get<OrderListResponse>(url);
  }

  getOrderById(id: number): Observable<Order> {
    return this.http.get<OrderResponse>(`${this.baseUrl}/${id}`).pipe(
      map(response => response.order)
    );
  }

  createOrder(order: Order): Observable<Order> {
    return this.http.post<OrderResponse>(this.baseUrl, order).pipe(
      map(response => response.order)
    );
  }

  updateOrder(id: number, order: Order): Observable<Order> {
    return this.http.put<Order>(`${this.baseUrl}/${id}`, order);
  }

  deleteOrder(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  updatePaymentStatus(orderId: number, status: PaymentStatus): Observable<Order> {
    return this.http.patch<OrderResponse>(`${this.baseUrl}/${orderId}/payment-status`, `"${status}"`, {
      headers: { 'Content-Type': 'application/json' }
    }).pipe(
      map(response => response.order)
    );
  }

  updatePreparationStatus(orderId: number, status: PreparationStatus): Observable<Order> {
    return this.http.patch<OrderResponse>(`${this.baseUrl}/${orderId}/preparation-status`, `"${status}"`, {
      headers: { 'Content-Type': 'application/json' }
    }).pipe(
      map(response => response.order)
    );
  }

  updateDeliveryStatus(orderId: number, status: DeliveryStatus): Observable<Order> {
    return this.http.patch<OrderResponse>(`${this.baseUrl}/${orderId}/delivery-status`, `"${status}"`, {
      headers: { 'Content-Type': 'application/json' }
    }).pipe(
      map(response => response.order)
    );
  }

  updateMultipleStatuses(
    orderId: number,
    paymentStatus?: PaymentStatus,
    preparationStatus?: PreparationStatus,
    deliveryStatus?: DeliveryStatus
  ): Observable<Order> {
    const payload = {
      paymentStatus,
      preparationStatus,
      deliveryStatus
    };
    return this.http.patch<OrderResponse>(`${this.baseUrl}/${orderId}/statuses`, payload).pipe(
      map(response => response.order)
    );
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
