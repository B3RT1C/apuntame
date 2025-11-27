import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatCardModule } from '@angular/material/card';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { OrderService } from '../../services/order.service';
import { Order } from '../../models/order.model';
import { PaymentStatus } from '../../models/order-status.model';
import { ChargeOrderDialogComponent } from '../../components/charge-order-dialog/charge-order-dialog.component';

@Component({
  selector: 'app-charge-order',
  imports: [
    CommonModule,
    MatTableModule,
    MatCardModule,
    MatDialogModule
  ],
  templateUrl: './charge-order.component.html',
  styleUrl: './charge-order.component.scss'
})
export class ChargeOrderComponent implements OnInit {
  orders: Order[] = [];
  displayedColumns: string[] = ['id', 'paymentStatus', 'preparationStatus', 'table', 'creationDate', 'takenBy', 'total'];
  loading = true;

  constructor(
    private orderService: OrderService,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.loadPendingOrders();
  }

  loadPendingOrders(): void {
    this.loading = true;
    this.orderService.getAllOrders().subscribe({
      next: (response) => {
        this.orders = response.orders.filter(order => order.paymentStatus === PaymentStatus.PENDING);
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading orders:', error);
        this.loading = false;
      }
    });
  }

  calculateTotal(order: Order): number {
    if (!order.orderItems || order.orderItems.length === 0) {
      return 0;
    }
    return order.orderItems.reduce((total, orderItem) => {
      return total + (orderItem.item.price * orderItem.amount);
    }, 0);
  }

  selectOrder(order: Order): void {
    const dialogRef = this.dialog.open(ChargeOrderDialogComponent, {
      width: '600px',
      data: order
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.loadPendingOrders();
      }
    });
  }
}
