import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatListModule } from '@angular/material/list';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { OrderItem } from '../../models/order-item.model';
import { Item } from '../../models/item.model';

@Component({
  selector: 'app-order-summary',
  imports: [
    CommonModule,
    MatListModule,
    MatButtonModule,
    MatIconModule
  ],
  templateUrl: './order-summary.component.html',
  styleUrl: './order-summary.component.scss'
})
export class OrderSummaryComponent {
  @Input() orderItems: OrderItem[] = [];
  @Input() showTotal: boolean = true;
  @Input() editable: boolean = false;
  @Output() addItem = new EventEmitter<Item>();
  @Output() removeItem = new EventEmitter<OrderItem>();

  calculateTotal(): number {
    if (!this.orderItems || this.orderItems.length === 0) {
      return 0;
    }
    return this.orderItems.reduce((total, orderItem) => {
      return total + (orderItem.item.price * orderItem.amount);
    }, 0);
  }

  onAddItem(item: Item): void {
    this.addItem.emit(item);
  }

  onRemoveItem(orderItem: OrderItem): void {
    this.removeItem.emit(orderItem);
  }
}
