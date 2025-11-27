import { Component, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { OrderService } from '../../services/order.service';
import { Order } from '../../models/order.model';
import { PaymentStatus } from '../../models/order-status.model';
import { OrderSummaryComponent } from '../order-summary/order-summary.component';

@Component({
  selector: 'app-charge-order-dialog',
  imports: [
    CommonModule,
    MatDialogModule,
    MatButtonModule,
    MatIconModule,
    OrderSummaryComponent
  ],
  templateUrl: './charge-order-dialog.component.html',
  styleUrl: './charge-order-dialog.component.scss'
})
export class ChargeOrderDialogComponent {
  constructor(
    private orderService: OrderService,
    public dialogRef: MatDialogRef<ChargeOrderDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public order: Order
  ) {}

  payOrder(): void {
    if (!this.order || !this.order.id) {
      return;
    }

    this.orderService.updatePaymentStatus(this.order.id, PaymentStatus.PAID).subscribe({
      next: () => {
        this.dialogRef.close(true);
      },
      error: (error) => {
        console.error('Error al pagar pedido:', error);
      }
    });
  }

  cancelOrder(): void {
    if (!this.order || !this.order.id) {
      return;
    }

    this.orderService.updatePaymentStatus(this.order.id, PaymentStatus.CANCELLED).subscribe({
      next: () => {
        this.dialogRef.close(true);
      },
      error: (error) => {
        console.error('Error al cancelar pedido:', error);
      }
    });
  }

  close(): void {
    this.dialogRef.close(false);
  }
}
