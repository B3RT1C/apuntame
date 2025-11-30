import { Category } from './category.model';
import { Section } from './section.model';

export interface Item {
  id: number;
  name: string;
  price: number;
  categories?: Category[];
  sections?: Section[];
}
