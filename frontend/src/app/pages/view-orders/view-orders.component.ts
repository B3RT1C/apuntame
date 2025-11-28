import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatChipsModule } from '@angular/material/chips';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { OrderService } from '../../services/order.service';
import { WebsocketService } from '../../services/websocket.service';
import { OrderEventDTO, OrderEventType } from '../../models/order-event.model';
import { TimeSyncService } from '../../services/time-sync.service';
import { OrderMapperService } from '../../services/order-mapper.service';
import { OrderCalculationService } from '../../services/order-calculation.service';
import { Order } from '../../models/order.model';
import { PaymentStatus, PreparationStatus, DeliveryStatus } from '../../models/order-status.model';
import { OrderTimerComponent } from '../../components/order-timer/order-timer.component';
import { PaymentStatusPipe } from '../../pipes/payment-status.pipe';
import { PreparationStatusPipe } from '../../pipes/preparation-status.pipe';
import { DeliveryStatusPipe } from '../../pipes/delivery-status.pipe';
import { OrderFilterDialogComponent } from '../../components/order-filter-dialog/order-filter-dialog.component';
import { OrderFilterConfig, OrderViewConfig, OrderActionConfig, OrderFilterDialogData } from '../../models/order-filter.model';
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
  compactMode: 'normal' | 'compact' | 'extra-compact' = 'normal';
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
  actionConfig: OrderActionConfig = {
    updatePaymentOnClick: false,
    updatePreparationOnClick: false,
    updateDeliveryOnClick: true
  };

  constructor(
    private orderService: OrderService,
    private websocketService: WebsocketService,
    private timeSyncService: TimeSyncService,
    private orderMapper: OrderMapperService,
    private orderCalculation: OrderCalculationService,
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

        this.allOrders = response.orders.sort((orderA, orderB) => {
          const creationDateA = this.timeSyncService.parseTimestamp(orderA.creationDate!);
          const creationDateB = this.timeSyncService.parseTimestamp(orderB.creationDate!);
          return creationDateA.getTime() - creationDateB.getTime();
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
    if (event.eventType === OrderEventType.CREATED) {
      this.handleOrderCreated(event);
    } else if (event.eventType === OrderEventType.UPDATED) {
      this.handleOrderUpdated(event);
    }
  }

  private handleOrderCreated(event: OrderEventDTO): void {
    const newOrder = this.orderMapper.convertDTOToOrder(event);
    this.allOrders.push(newOrder);
    this.applyFilters();
  }

  private handleOrderUpdated(event: OrderEventDTO): void {
    const index = this.allOrders.findIndex(o => o.id === event.id);

    if (index === -1) {
      console.warn('⚠️ Pedido no encontrado en la lista local:', event.id);
      return;
    }

    this.allOrders[index] = this.orderMapper.convertDTOToOrder(event);
    this.applyFilters();

    if (event.deliveryStatus === 'DELIVERED') {
      this.orderTimerColors.delete(event.id);
    }
  }

  calculateTotal(order: Order): number {
    return this.orderCalculation.calculateTotal(order);
  }

  formatStateTimestamp(order: Order, stateTimestamp: string | undefined): string {
    if (!stateTimestamp || !order.creationDate) {
      return '';
    }
    return this.timeSyncService.formatElapsedTime(order.creationDate, stateTimestamp);
  }

  onOrderClick(order: Order): void {
    if (!order.id) {
      console.error('Order ID is missing');
      return;
    }

    const paymentStatus = this.actionConfig.updatePaymentOnClick ? PaymentStatus.PAID : undefined;
    const preparationStatus = this.actionConfig.updatePreparationOnClick ? PreparationStatus.READY : undefined;
    const deliveryStatus = this.actionConfig.updateDeliveryOnClick ? DeliveryStatus.DELIVERED : undefined;

    if (paymentStatus || preparationStatus || deliveryStatus) {
      this.orderService.updateMultipleStatuses(order.id, paymentStatus, preparationStatus, deliveryStatus).subscribe({
        error: (error) => {
          console.error('Error updating order status:', error);
        }
      });
    }
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
        viewConfig: { ...this.viewConfig },
        actionConfig: { ...this.actionConfig }
      } as OrderFilterDialogData
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.filterConfig = result.filterConfig;
        this.viewConfig = result.viewConfig;
        this.actionConfig = result.actionConfig;
        this.applyFilters();
      }
    });
  }

  applyFilters(): void {
    this.orders = this.allOrders.filter(order => this.matchesAllFilters(order));
  }

  private matchesAllFilters(order: Order): boolean {
    return this.matchesStatusFilter(order.paymentStatus, this.filterConfig.paymentStatus) &&
           this.matchesStatusFilter(order.preparationStatus, this.filterConfig.preparationStatus) &&
           this.matchesStatusFilter(order.deliveryStatus, this.filterConfig.deliveryStatus);
  }

  private matchesStatusFilter(orderStatus: string, filterStatus: string): boolean {
    return filterStatus === 'ANY' || orderStatus === filterStatus;
  }

  toggleCompactMode(): void {
    if (this.compactMode === 'normal') {
      this.compactMode = 'compact';
    } else if (this.compactMode === 'compact') {
      this.compactMode = 'extra-compact';
    } else {
      this.compactMode = 'normal';
    }
  }
}
