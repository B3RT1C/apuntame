import { Section } from './section.model';

export interface OrderFilterConfig {
  paymentStatus: string;
  preparationStatus: string;
  deliveryStatus: string;
}

export interface OrderSectionFilterConfig {
  selectedSections: number[];
  filterMode: 'OR' | 'AND';
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
  sectionFilterConfig: OrderSectionFilterConfig;
  viewConfig: OrderViewConfig;
  actionConfig: OrderActionConfig;
  sections: Section[];
}