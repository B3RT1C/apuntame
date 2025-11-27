import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatChipsModule } from '@angular/material/chips';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { OrderService } from '../../services/order.service';
import { WebsocketService, OrderEventDTO } from '../../services/websocket.service';
import { TimeSyncService } from '../../services/time-sync.service';
import { Order } from '../../models/order.model';
import { DeliveryStatus } from '../../models/order-status.model';
import { OrderTimerComponent } from '../../components/order-timer/order-timer.component';
import { PaymentStatusPipe } from '../../pipes/payment-status.pipe';
import { PreparationStatusPipe } from '../../pipes/preparation-status.pipe';
import { DeliveryStatusPipe } from '../../pipes/delivery-status.pipe';
import { OrderFilterDialogComponent, OrderFilterConfig, OrderViewConfig, DialogData } from '../../components/order-filter-dialog/order-filter-dialog.component';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-view-orders',
  imports: [
    CommonModule,
    MatCardModule,
    MatChipsModule,
    MatIconModule,
    MatButtonModule,
    MatDialogModule,
    OrderTimerComponent,
    PaymentStatusPipe,
    PreparationStatusPipe,
    DeliveryStatusPipe
  ],
  templateUrl: './view-orders.component.html',
  styleUrl: './view-orders.component.scss'
})
export class ViewOrdersComponent implements OnInit, OnDestroy {
  orders: Order[] = [];
  allOrders: Order[] = [];
  loading = true;
  private wsSubscription?: Subscription;
  orderTimerColors: Map<number, string> = new Map();
  filterConfig: OrderFilterConfig = {
    paymentStatus: 'ANY',
    preparationStatus: 'ANY',
    deliveryStatus: 'ANY'
  };
  viewConfig: OrderViewConfig = {
    showOrderItems: false,
    showPaymentStatus: true,
    showPreparationStatus: true,
    showDeliveryStatus: true,
    showTotal: true,
    showTable: true
  };

  constructor(
    private orderService: OrderService,
    private websocketService: WebsocketService,
    private timeSyncService: TimeSyncService,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.loadOrders();
    this.connectWebSocket();
  }

  ngOnDestroy(): void {
    if (this.wsSubscription) {
      this.wsSubscription.unsubscribe();
    }
    this.websocketService.disconnect();
  }

  loadOrders(): void {
    this.loading = true;
    this.orderService.getAllOrders().subscribe({
      next: (response) => {
        this.timeSyncService.syncWithServer(response.serverTimestamp);

        this.allOrders = response.orders.sort((a, b) => {
          const dateA = new Date(a.creationDate!.replace(' ', 'T'));
          const dateB = new Date(b.creationDate!.replace(' ', 'T'));
          return dateA.getTime() - dateB.getTime(); // Oldest first
        });
        this.applyFilters();
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading orders:', error);
        this.loading = false;
      }
    });
  }

  connectWebSocket(): void {
    this.websocketService.connect();
    this.wsSubscription = this.websocketService.messages$.subscribe({
      next: (event: OrderEventDTO) => {
        this.handleOrderEvent(event);
      },
      error: (error) => {
        console.error('WebSocket error:', error);
      }
    });
  }

  handleOrderEvent(event: OrderEventDTO): void {
    if (event.eventType === 'CREATED') {
      const newOrder = this.convertDTOToOrder(event);
      this.allOrders.push(newOrder);
      this.applyFilters();

    } else if (event.eventType === 'UPDATED') {
      const index = this.allOrders.findIndex(o => o.id === event.id);

      if (index !== -1) {
        this.allOrders[index] = this.convertDTOToOrder(event);
        this.applyFilters();
        if (event.deliveryStatus === 'DELIVERED') {
          this.orderTimerColors.delete(event.id);
        }
      } else {
        console.warn('⚠️ Pedido no encontrado en la lista local:', event.id);
      }
    }
  }

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
      takenBy: {
        username: dto.takenBy,
        role: ''
      },
      orderItems: dto.orderItems.map(item => ({
        id: { orderId: dto.id, itemId: item.itemId },
        order: null as any,
        item: {
          id: item.itemId,
          name: item.itemName,
          price: item.itemPrice
        },
        amount: item.amount
      }))
    };
  }

  calculateTotal(order: Order): number {
    if (!order.orderItems || order.orderItems.length === 0) {
      return 0;
    }
    return order.orderItems.reduce((total, orderItem) => {
      return total + (orderItem.item.price * orderItem.amount);
    }, 0);
  }

  formatTimestamp(timestamp: string): string {
    // Parse timestamp format "yyyy-MM-dd HH:mm:ss"
    const date = new Date(timestamp.replace(' ', 'T'));
    const hours = date.getHours().toString().padStart(2, '0');
    const minutes = date.getMinutes().toString().padStart(2, '0');
    return `${hours}:${minutes}`;
  }

  calculateDuration(order: Order, stateTimestamp?: string): string {
    if (!stateTimestamp || !order.creationDate) {
      return '';
    }

    const creationDate = new Date(order.creationDate.replace(' ', 'T'));
    const stateDate = new Date(stateTimestamp.replace(' ', 'T'));

    const diffMs = stateDate.getTime() - creationDate.getTime();
    const diffMinutes = Math.floor(diffMs / 60000);
    const hours = Math.floor(diffMinutes / 60);
    const minutes = diffMinutes % 60;

    if (hours > 0) {
      return `${hours}h ${minutes}m`;
    }
    return `${minutes}m`;
  }

  onOrderClick(order: Order): void {
    if (!order.id) {
      console.error('Order ID is missing');
      return;
    }

    this.orderService.updateDeliveryStatus(order.id, DeliveryStatus.DELIVERED).subscribe({
      next: () => {

      },
      error: (error) => {
        console.error('Error updating delivery status:', error);
      }
    });
  }

  onTimerColorChange(orderId: number, color: string): void {
    this.orderTimerColors.set(orderId, color);
  }

  getTimerColor(orderId: number): string {
    return this.orderTimerColors.get(orderId) || 'green';
  }

  isOrderCompleted(order: Order): boolean {
    return !!(order.paidAt && order.preparedAt && order.deliveredAt);
  }

  openFilterDialog(): void {
    const dialogRef = this.dialog.open(OrderFilterDialogComponent, {
      width: '450px',
      data: {
        filterConfig: { ...this.filterConfig },
        viewConfig: { ...this.viewConfig }
      } as DialogData
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.filterConfig = result.filterConfig;
        this.viewConfig = result.viewConfig;
        this.applyFilters();
      }
    });
  }

  applyFilters(): void {
    this.orders = this.allOrders.filter(order => {
      if (this.filterConfig.paymentStatus !== 'ANY') {
        if (order.paymentStatus !== this.filterConfig.paymentStatus) {
          return false;
        }
      }

      if (this.filterConfig.preparationStatus !== 'ANY') {
        if (order.preparationStatus !== this.filterConfig.preparationStatus) {
          return false;
        }
      }

      if (this.filterConfig.deliveryStatus !== 'ANY') {
        if (order.deliveryStatus !== this.filterConfig.deliveryStatus) {
          return false;
        }
      }

      return true;
    });
  }
}
