import { Component, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatDialogModule, MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatTabsModule } from '@angular/material/tabs';
import { MatRadioModule } from '@angular/material/radio';
import { PaymentStatus, PreparationStatus, DeliveryStatus } from '../../../../models/order-status.model';

export interface EditOrderFilterConfig {
  filterId: string;
  filterPaymentStatus: string;
  filterPreparationStatus: string;
  filterDeliveryStatus: string;
  filterMinTotal: number | null;
  filterMaxTotal: number | null;
  sortBy: 'id' | 'date' | 'total';
  sortOrder: 'asc' | 'desc';
}

@Component({
  selector: 'app-edit-order-filter-dialog',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatDialogModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatTabsModule,
    MatRadioModule
  ],
  templateUrl: './edit-order-filter-dialog.component.html',
  styleUrl: './edit-order-filter-dialog.component.scss'
})
export class EditOrderFilterDialogComponent {
  filterConfig: EditOrderFilterConfig;
  minTotalText: string = '';
  maxTotalText: string = '';

  paymentStatusOptions = [
    { value: 'ANY', label: 'Cualquiera' },
    { value: PaymentStatus.PENDING, label: 'Pendiente' },
    { value: PaymentStatus.PAID, label: 'Pagado' },
    { value: PaymentStatus.CANCELLED, label: 'Cancelado' },
    { value: PaymentStatus.REFUNDED, label: 'Reembolsado' }
  ];

  preparationStatusOptions = [
    { value: 'ANY', label: 'Cualquiera' },
    { value: PreparationStatus.PENDING, label: 'Pendiente' },
    { value: PreparationStatus.READY, label: 'Preparado' }
  ];

  deliveryStatusOptions = [
    { value: 'ANY', label: 'Cualquiera' },
    { value: DeliveryStatus.PENDING, label: 'Pendiente' },
    { value: DeliveryStatus.DELIVERED, label: 'Entregado' }
  ];

  sortByOptions = [
    { value: 'id', label: 'ID' },
    { value: 'date', label: 'Fecha' },
    { value: 'total', label: 'Total' }
  ];

  sortOrderOptions = [
    { value: 'asc', label: 'Ascendente' },
    { value: 'desc', label: 'Descendente' }
  ];

  constructor(
    public dialogRef: MatDialogRef<EditOrderFilterDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: EditOrderFilterConfig
  ) {
    this.filterConfig = { ...data };
    this.minTotalText = data.filterMinTotal !== null ? data.filterMinTotal.toString() : '';
    this.maxTotalText = data.filterMaxTotal !== null ? data.filterMaxTotal.toString() : '';
  }

  private parsePrice(value: string): number | null {
    if (!value || value.trim() === '') {
      return null;
    }
    const normalized = value.replace(',', '.');
    const parsed = parseFloat(normalized);
    return isNaN(parsed) ? null : parsed;
  }

  onCancel(): void {
    this.dialogRef.close();
  }

  onApply(): void {
    this.filterConfig.filterMinTotal = this.parsePrice(this.minTotalText);
    this.filterConfig.filterMaxTotal = this.parsePrice(this.maxTotalText);
    this.dialogRef.close(this.filterConfig);
  }

  onClear(): void {
    this.filterConfig = {
      filterId: '',
      filterPaymentStatus: 'ANY',
      filterPreparationStatus: 'ANY',
      filterDeliveryStatus: 'ANY',
      filterMinTotal: null,
      filterMaxTotal: null,
      sortBy: 'id',
      sortOrder: 'asc'
    };
    this.minTotalText = '';
    this.maxTotalText = '';
  }
}
