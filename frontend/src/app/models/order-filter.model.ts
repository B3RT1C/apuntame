export interface OrderFilterConfig {
  paymentStatus: string;
  preparationStatus: string;
  deliveryStatus: string;
}

export interface OrderViewConfig {
  showOrderItems: boolean;
  showPaymentStatus: boolean;
  showPreparationStatus: boolean;
  showDeliveryStatus: boolean;
  showTotal: boolean;
  showTable: boolean;
}

export interface OrderActionConfig {
  updatePaymentOnClick: boolean;
  updatePreparationOnClick: boolean;
  updateDeliveryOnClick: boolean;
}

export interface OrderFilterDialogData {
  filterConfig: OrderFilterConfig;
  viewConfig: OrderViewConfig;
  actionConfig: OrderActionConfig;
}