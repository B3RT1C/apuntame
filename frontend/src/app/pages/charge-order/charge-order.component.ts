import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatCardModule } from '@angular/material/card';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { OrderService } from '../../services/order.service';
import { OrderCalculationService } from '../../services/order-calculation.service';
import { Order } from '../../models/order.model';
import { ChargeOrderDialogComponent } from '../../components/charge-order-dialog/charge-order-dialog.component';
import { EditOrderFilterDialogComponent, EditOrderFilterConfig } from './components/edit-order-filter-dialog/edit-order-filter-dialog.component';
import { FILTER_DIALOG_CONFIG } from '../../constants/dialog-config.constants';
import { PaymentStatusPipe } from '../../pipes/payment-status.pipe';
import { PreparationStatusPipe } from '../../pipes/preparation-status.pipe';
import { DeliveryStatusPipe } from '../../pipes/delivery-status.pipe';

@Component({
  selector: 'app-charge-order',
  imports: [
    CommonModule,
    MatTableModule,
    MatCardModule,
    MatDialogModule,
    MatIconModule,
    MatButtonModule,
    PaymentStatusPipe,
    PreparationStatusPipe,
    DeliveryStatusPipe
  ],
  templateUrl: './charge-order.component.html',
  styleUrl: './charge-order.component.scss'
})
export class ChargeOrderComponent implements OnInit {
  allOrders: Order[] = [];
  filteredOrders: Order[] = [];
  displayedColumns: string[] = ['id', 'paymentStatus', 'preparationStatus', 'deliveryStatus', 'table', 'creationDate', 'takenBy', 'total'];
  loading = true;

  filterConfig: EditOrderFilterConfig = {
    filterId: '',
    filterPaymentStatus: 'ANY',
    filterPreparationStatus: 'ANY',
    filterDeliveryStatus: 'ANY',
    filterMinTotal: null,
    filterMaxTotal: null,
    sortBy: 'id',
    sortOrder: 'asc'
  };

  constructor(
    private orderService: OrderService,
    private orderCalculation: OrderCalculationService,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.loadOrders();
  }

  loadOrders(): void {
    this.loading = true;
    this.orderService.getAllOrders().subscribe({
      next: (response) => {
        this.allOrders = response.orders;
        this.applyFilters();
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading orders:', error);
        this.loading = false;
      }
    });
  }

  openFilterDialog(): void {
    const dialogRef = this.dialog.open(EditOrderFilterDialogComponent, {
      ...FILTER_DIALOG_CONFIG,
      data: { ...this.filterConfig }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.filterConfig = result;
        this.applyFilters();
      }
    });
  }

  hasActiveFilters(): boolean {
    return this.filterConfig.filterId !== '' ||
           this.filterConfig.filterPaymentStatus !== 'ANY' ||
           this.filterConfig.filterPreparationStatus !== 'ANY' ||
           this.filterConfig.filterDeliveryStatus !== 'ANY' ||
           this.filterConfig.filterMinTotal !== null ||
           this.filterConfig.filterMaxTotal !== null;
  }

  applyFilters(): void {
    let result = this.allOrders.filter(order => {
      if (this.filterConfig.filterId && !order.id?.toString().includes(this.filterConfig.filterId)) {
        return false;
      }

      if (this.filterConfig.filterPaymentStatus !== 'ANY' && order.paymentStatus !== this.filterConfig.filterPaymentStatus) {
        return false;
      }

      if (this.filterConfig.filterPreparationStatus !== 'ANY' && order.preparationStatus !== this.filterConfig.filterPreparationStatus) {
        return false;
      }

      if (this.filterConfig.filterDeliveryStatus !== 'ANY' && order.deliveryStatus !== this.filterConfig.filterDeliveryStatus) {
        return false;
      }

      const total = this.calculateTotal(order);
      if (this.filterConfig.filterMinTotal !== null && total < this.filterConfig.filterMinTotal) {
        return false;
      }

      if (this.filterConfig.filterMaxTotal !== null && total > this.filterConfig.filterMaxTotal) {
        return false;
      }

      return true;
    });

    result = this.sortOrders(result);
    this.filteredOrders = result;
  }

  private sortOrders(orders: Order[]): Order[] {
    const multiplier = this.filterConfig.sortOrder === 'asc' ? 1 : -1;

    return [...orders].sort((a, b) => {
      let comparison = 0;

      switch (this.filterConfig.sortBy) {
        case 'id':
          comparison = (a.id || 0) - (b.id || 0);
          break;
        case 'date': {
          const dateA = a.creationDate ? new Date(a.creationDate).getTime() : 0;
          const dateB = b.creationDate ? new Date(b.creationDate).getTime() : 0;
          comparison = dateA - dateB;
          break;
        }
        case 'total':
          comparison = this.calculateTotal(a) - this.calculateTotal(b);
          break;
      }

      return comparison * multiplier;
    });
  }

  calculateTotal(order: Order): number {
    return this.orderCalculation.calculateTotal(order);
  }

  selectOrder(order: Order): void {
    const dialogRef = this.dialog.open(ChargeOrderDialogComponent, {
      width: '600px',
      data: JSON.parse(JSON.stringify(order))
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.loadOrders();
      }
    });
  }
}
