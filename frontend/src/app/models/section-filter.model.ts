import { Section } from './section.model';

export interface SectionFilterConfig {
  selectedSections: number[];
  filterMode: 'OR' | 'AND';
}

export interface SectionFilterDialogData {
  filterConfig: SectionFilterConfig;
  sections: Section[];
}
