import { Component, Input, Output, EventEmitter, OnInit, OnChanges, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatButtonModule } from '@angular/material/button';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import { MatChipsModule } from '@angular/material/chips';
import { MatIconModule } from '@angular/material/icon';
import { FilterableItem } from '../../models/filterable-item.model';

@Component({
  selector: 'app-generic-filter',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatCheckboxModule,
    MatButtonModule,
    MatButtonToggleModule,
    MatChipsModule,
    MatIconModule
  ],
  templateUrl: './generic-filter.component.html',
  styleUrl: './generic-filter.component.scss'
})
export class GenericFilterComponent<T extends FilterableItem> implements OnInit, OnChanges {
  @Input() items: T[] = [];
  @Input() selectedIds: number[] = [];
  @Input() filterMode: 'OR' | 'AND' = 'OR';
  @Input() searchPlaceholder: string = 'Buscar';
  @Input() showFilterMode: boolean = true;

  @Output() selectedIdsChange = new EventEmitter<number[]>();
  @Output() filterModeChange = new EventEmitter<'OR' | 'AND'>();

  searchText = '';
  filteredItems: T[] = [];
  showOnlySelected = false;

  ngOnInit(): void {
    this.filterItems();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['items'] || changes['selectedIds']) {
      this.filterItems();
    }
  }

  filterItems(): void {
    const searchLower = this.searchText.toLowerCase();
    let filtered = this.items;

    if (this.showOnlySelected) {
      filtered = filtered.filter(item => this.isItemSelected(item.id));
    }

    if (searchLower) {
      filtered = filtered.filter(item => item.name.toLowerCase().includes(searchLower));
    }

    this.filteredItems = filtered.sort((a, b) => a.name.localeCompare(b.name));
  }

  toggleItem(itemId: number): void {
    const currentIds = [...this.selectedIds];
    const index = currentIds.indexOf(itemId);

    if (index > -1) {
      currentIds.splice(index, 1);
    } else {
      currentIds.push(itemId);
    }

    this.selectedIdsChange.emit(currentIds);
  }

  isItemSelected(itemId: number): boolean {
    return this.selectedIds.includes(itemId);
  }

  clearFilters(): void {
    this.selectedIdsChange.emit([]);
    this.filterItems();
  }

  onFilterModeChange(mode: 'OR' | 'AND'): void {
    this.filterModeChange.emit(mode);
  }
}
