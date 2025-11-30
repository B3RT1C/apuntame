import { Component, Inject } from '@angular/core';
import { MatDialogModule, MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { FilterableItem } from '../../models/filterable-item.model';
import { GenericFilterDialogData, GenericFilterDialogResult } from '../../models/generic-filter.model';
import { GenericFilterComponent } from '../generic-filter/generic-filter.component';

@Component({
  selector: 'app-generic-filter-dialog',
  standalone: true,
  imports: [
    MatDialogModule,
    MatButtonModule,
    GenericFilterComponent
  ],
  templateUrl: './generic-filter-dialog.component.html',
  styleUrl: './generic-filter-dialog.component.scss'
})
export class GenericFilterDialogComponent<T extends FilterableItem> {
  selectedIds: number[];
  filterMode: 'OR' | 'AND';

  constructor(
    public dialogRef: MatDialogRef<GenericFilterDialogComponent<T>>,
    @Inject(MAT_DIALOG_DATA) public data: GenericFilterDialogData<T>
  ) {
    this.selectedIds = [...data.filterConfig.selectedIds];
    this.filterMode = data.filterConfig.filterMode;
  }

  onSelectedIdsChange(ids: number[]): void {
    this.selectedIds = ids;
  }

  onFilterModeChange(mode: 'OR' | 'AND'): void {
    this.filterMode = mode;
  }

  onCancel(): void {
    this.dialogRef.close();
  }

  onApply(): void {
    const result: GenericFilterDialogResult = {
      filterConfig: {
        selectedIds: this.selectedIds,
        filterMode: this.filterMode
      }
    };
    this.dialogRef.close(result);
  }
}
