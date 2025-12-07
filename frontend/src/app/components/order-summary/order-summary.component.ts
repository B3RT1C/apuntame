import { Component, inject, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatListModule } from '@angular/material/list';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { OrderItem } from '../../models/order-item.model';
import { Item } from '../../models/item.model';
import { OrderCalculationService } from '../../services/order-calculation.service';

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
  private orderCalculationService = inject(OrderCalculationService);

  @Input() orderItems: OrderItem[] = [];
  @Input() showTotal: boolean = true;
  @Input() editable: boolean = false;
  @Output() addItem = new EventEmitter<Item>();
  @Output() removeItem = new EventEmitter<OrderItem>();

  calculateTotal(): number {
    return this.orderCalculationService.calculateTotalFromItems(this.orderItems);
  }

  onAddItem(item: Item): void {
    this.addItem.emit(item);
  }

  onRemoveItem(orderItem: OrderItem): void {
    this.removeItem.emit(orderItem);
  }
}
