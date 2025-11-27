import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { ItemService } from '../../services/item.service';
import { OrderService } from '../../services/order.service';
import { AuthService } from '../../services/auth.service';
import { Item } from '../../models/item.model';
import { Order } from '../../models/order.model';
import { OrderItem } from '../../models/order-item.model';
import { User } from '../../models/user.model';
import { PaymentStatus, PreparationStatus, DeliveryStatus } from '../../models/order-status.model';
import { OrderSummaryComponent } from '../../components/order-summary/order-summary.component';

@Component({
  selector: 'app-take-order',
  imports: [
    CommonModule,
    FormsModule,
    MatCardModule,
    MatButtonModule,
    MatInputModule,
    MatFormFieldModule,
    OrderSummaryComponent
  ],
  templateUrl: './take-order.component.html',
  styleUrl: './take-order.component.scss'
})
export class TakeOrderComponent implements OnInit {
  items: Item[] = [];
  orderItems: OrderItem[] = [];
  loading = true;
  tableNumber = '';
  currentUser: User | null = null;

  constructor(
    private itemService: ItemService,
    private orderService: OrderService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.currentUser = this.authService.getCurrentUser();
    this.loadItems();
  }

  loadItems(): void {
    this.loading = true;
    this.itemService.getAllItems().subscribe({
      next: (items) => {
        this.items = items;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading items:', error);
        this.loading = false;
      }
    });
  }

  addItemToOrder(item: Item): void {
    const existingOrderItem = this.orderItems.find(oi => oi.item.id === item.id);

    if (existingOrderItem) {
      existingOrderItem.amount++;
    } else {
      this.orderItems.push({
        item: item,
        amount: 1
      });
    }
  }

  removeItemFromOrder(orderItem: OrderItem): void {
    if (orderItem.amount > 1) {
      orderItem.amount--;
    } else {
      const index = this.orderItems.findIndex(oi => oi.item.id === orderItem.item.id);
      if (index > -1) {
        this.orderItems.splice(index, 1);
      }
    }
  }

  sendOrder(): void {
    if (!this.currentUser || !this.tableNumber || this.orderItems.length === 0) {
      return;
    }

    const order: Order = {
      table: this.tableNumber,
      paymentStatus: PaymentStatus.PENDING,
      preparationStatus: PreparationStatus.PENDING,
      deliveryStatus: DeliveryStatus.PENDING,
      takenBy: this.currentUser,
      orderItems: this.orderItems.map(oi => ({
        item: { id: oi.item.id } as Item,
        amount: oi.amount
      }))
    };

    this.orderService.createOrder(order).subscribe({
      next: () => {
        this.orderItems = [];
        this.tableNumber = '';
      },
      error: (error) => {
        console.error('Error al crear pedido:', error);
      }
    });
  }
}
