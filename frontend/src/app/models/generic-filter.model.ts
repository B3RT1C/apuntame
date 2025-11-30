import { FilterableItem } from './filterable-item.model';

export interface GenericFilterConfig {
  selectedIds: number[];
  filterMode: 'OR' | 'AND';
}

export interface GenericFilterDialogData<T extends FilterableItem> {
  title: string;
  searchPlaceholder: string;
  items: T[];
  filterConfig: GenericFilterConfig;
}

export interface GenericFilterDialogResult {
  filterConfig: GenericFilterConfig;
}
