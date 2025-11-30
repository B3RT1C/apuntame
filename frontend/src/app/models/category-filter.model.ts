import { Category } from './category.model';

export interface CategoryFilterConfig {
  selectedCategories: number[];
  filterMode: 'OR' | 'AND';
}

export interface CategoryFilterDialogData {
  filterConfig: CategoryFilterConfig;
  categories: Category[];
}
