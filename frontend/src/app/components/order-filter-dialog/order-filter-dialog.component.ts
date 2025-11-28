import { Component, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatDialogModule, MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatSelectModule } from '@angular/material/select';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatTabsModule } from '@angular/material/tabs';
import { FormsModule } from '@angular/forms';
import { PaymentStatus, PreparationStatus, DeliveryStatus } from '../../models/order-status.model';
import { OrderFilterConfig, OrderViewConfig, OrderActionConfig, OrderFilterDialogData } from '../../models/order-filter.model';

@Component({
  selector: 'app-order-filter-dialog',
  imports: [
    CommonModule,
    MatDialogModule,
    MatButtonModule,
    MatSelectModule,
    MatFormFieldModule,
    MatCheckboxModule,
    MatSlideToggleModule,
    MatTabsModule,
    FormsModule
  ],
  templateUrl: './order-filter-dialog.component.html',
  styleUrl: './order-filter-dialog.component.scss'
})

export class OrderFilterDialogComponent {
  filterConfig: OrderFilterConfig;
  viewConfig: OrderViewConfig;
  actionConfig: OrderActionConfig;

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
    { value: PreparationStatus.READY, label: 'Listo' }
  ];

  deliveryStatusOptions = [
    { value: 'ANY', label: 'Cualquiera' },
    { value: DeliveryStatus.PENDING, label: 'Pendiente' },
    { value: DeliveryStatus.DELIVERED, label: 'Entregado' }
  ];

  constructor(
    public dialogRef: MatDialogRef<OrderFilterDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: OrderFilterDialogData
  ) {
    this.filterConfig = { ...data.filterConfig };
    this.viewConfig = { ...data.viewConfig };
    this.actionConfig = { ...data.actionConfig };
  }

  onCancel(): void {
    this.dialogRef.close();
  }

  onApply(): void {
    this.dialogRef.close({ filterConfig: this.filterConfig, viewConfig: this.viewConfig, actionConfig: this.actionConfig });
  }
}
