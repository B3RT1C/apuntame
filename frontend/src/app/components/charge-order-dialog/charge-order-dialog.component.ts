import { Component, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { OrderService } from '../../services/order.service';
import { Order } from '../../models/order.model';
import { OrderItem } from '../../models/order-item.model';
import { Item } from '../../models/item.model';
import { OrderSummaryComponent } from '../order-summary/order-summary.component';
import { forkJoin } from 'rxjs';

interface PendingChange {
  type: 'update' | 'delete';
  itemId: number;
  newAmount?: number;
}

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
  pendingChanges: Map<number, PendingChange> = new Map();
  originalItems: OrderItem[] = [];

  constructor(
    private orderService: OrderService,
    public dialogRef: MatDialogRef<ChargeOrderDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public order: Order
  ) {
    this.originalItems = JSON.parse(JSON.stringify(order.orderItems));
  }

  onAddItem(item: Item): void {
    const existingItem = this.order.orderItems.find(oi => oi.item.id === item.id);
    if (existingItem) {
      existingItem.amount += 1;
      this.trackChange(item.id!, existingItem.amount);
    }
  }

  onRemoveItem(orderItem: OrderItem): void {
    if (orderItem.amount > 1) {
      orderItem.amount -= 1;
      this.trackChange(orderItem.item.id!, orderItem.amount);
    } else {
      this.order.orderItems = this.order.orderItems.filter(oi => oi.item.id !== orderItem.item.id);
      this.pendingChanges.set(orderItem.item.id!, { type: 'delete', itemId: orderItem.item.id! });
    }
  }

  private trackChange(itemId: number, newAmount: number): void {
    const original = this.originalItems.find(oi => oi.item.id === itemId);
    if (original && original.amount === newAmount) {
      this.pendingChanges.delete(itemId);
    } else {
      this.pendingChanges.set(itemId, { type: 'update', itemId, newAmount });
    }
  }

  hasChanges(): boolean {
    return this.pendingChanges.size > 0;
  }

  confirm(): void {
    if (!this.order.id || this.pendingChanges.size === 0) {
      this.dialogRef.close(false);
      return;
    }

    const requests: any[] = [];

    this.pendingChanges.forEach((change) => {
      if (change.type === 'delete') {
        requests.push(this.orderService.removeItemFromOrder(this.order.id!, change.itemId));
      } else if (change.type === 'update') {
        const orderItem = this.order.orderItems.find(oi => oi.item.id === change.itemId);
        if (orderItem) {
          requests.push(this.orderService.updateOrderItem(this.order.id!, change.itemId, { ...orderItem, amount: change.newAmount! }));
        }
      }
    });

    if (requests.length === 0) {
      this.dialogRef.close(false);
      return;
    }

    forkJoin(requests).subscribe({
      next: () => {
        this.dialogRef.close(true);
      },
      error: (error) => {
        console.error('Error al guardar cambios:', error);
      }
    });
  }

  close(): void {
    this.dialogRef.close(false);
  }
}
