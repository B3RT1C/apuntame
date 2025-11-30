import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { ItemService } from '../../services/item.service';
import { CategoryService } from '../../services/category.service';
import { OrderService } from '../../services/order.service';
import { AuthService } from '../../services/auth.service';
import { Item } from '../../models/item.model';
import { Category } from '../../models/category.model';
import { Order } from '../../models/order.model';
import { OrderItem } from '../../models/order-item.model';
import { User } from '../../models/user.model';
import { PaymentStatus, PreparationStatus, DeliveryStatus } from '../../models/order-status.model';
import { OrderSummaryComponent } from '../../components/order-summary/order-summary.component';
import { GenericFilterDialogComponent } from '../../components/generic-filter-dialog/generic-filter-dialog.component';
import { GenericFilterDialogData, GenericFilterDialogResult } from '../../models/generic-filter.model';
import { FILTER_DIALOG_CONFIG } from '../../constants/dialog-config.constants';

@Component({
  selector: 'app-take-order',
  imports: [
    CommonModule,
    FormsModule,
    MatCardModule,
    MatButtonModule,
    MatInputModule,
    MatFormFieldModule,
    MatIconModule,
    MatDialogModule,
    OrderSummaryComponent
  ],
  templateUrl: './take-order.component.html',
  styleUrl: './take-order.component.scss'
})
export class TakeOrderComponent implements OnInit {
  items: Item[] = [];
  categories: Category[] = [];
  selectedCategories: Category[] = [];
  filterMode: 'OR' | 'AND' = 'OR';
  orderItems: OrderItem[] = [];
  loading = true;
  tableNumber = '';
  currentUser: User | null = null;
  cardSize: 'compact' | 'normal' | 'large' = 'normal';

  constructor(
    private itemService: ItemService,
    private categoryService: CategoryService,
    private orderService: OrderService,
    private authService: AuthService,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.currentUser = this.authService.getCurrentUser();
    this.loadCategories();
    this.loadItems();
  }

  loadCategories(): void {
    this.categoryService.getAllCategories().subscribe({
      next: (categories) => {
        this.categories = categories;
      },
      error: (error) => {
        console.error('Error loading categories:', error);
      }
    });
  }

  loadItems(): void {
    this.loading = true;
    const categoryIds = this.selectedCategories.map(c => c.id);

    if (categoryIds.length > 0) {
      this.itemService.getItemsByCategories(categoryIds, this.filterMode).subscribe({
        next: (items) => {
          this.items = items;
          this.loading = false;
        },
        error: (error) => {
          console.error('Error loading items:', error);
          this.loading = false;
        }
      });
    } else {
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
  }

  openCategoryFilterDialog(): void {
    const dialogRef = this.dialog.open(GenericFilterDialogComponent<Category>, {
      ...FILTER_DIALOG_CONFIG,
      data: {
        title: 'Filtrar Categorías',
        searchPlaceholder: 'Buscar categoría',
        items: this.categories,
        filterConfig: {
          selectedIds: this.selectedCategories.map(c => c.id),
          filterMode: this.filterMode
        }
      } as GenericFilterDialogData<Category>
    });

    dialogRef.afterClosed().subscribe((result: GenericFilterDialogResult | undefined) => {
      if (result) {
        this.selectedCategories = this.categories.filter(c =>
          result.filterConfig.selectedIds.includes(c.id)
        );
        this.filterMode = result.filterConfig.filterMode;
        this.loadItems();
      }
    });
  }

  toggleCardSize(): void {
    if (this.cardSize === 'normal') {
      this.cardSize = 'large';
    } else if (this.cardSize === 'large') {
      this.cardSize = 'compact';
    } else {
      this.cardSize = 'normal';
    }
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
