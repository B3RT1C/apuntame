import { Component, inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatDividerModule } from '@angular/material/divider';
import { Item } from '../../../../models/item.model';
import { Category } from '../../../../models/category.model';
import { Section } from '../../../../models/section.model';
import { GenericFilterComponent } from '../../../../components/generic-filter/generic-filter.component';

export interface ItemFormDialogData {
  item?: Item;
  allCategories: Category[];
  allSections: Section[];
}

@Component({
  selector: 'app-item-form-dialog',
  standalone: true,
  imports: [
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatDividerModule,
    ReactiveFormsModule,
    GenericFilterComponent
  ],
  templateUrl: './item-form-dialog.component.html',
  styleUrl: './item-form-dialog.component.scss'
})
export class ItemFormDialogComponent implements OnInit {
  private fb = inject(FormBuilder);
  data = inject<ItemFormDialogData>(MAT_DIALOG_DATA);
  dialogRef = inject(MatDialogRef<ItemFormDialogComponent>);

  itemForm!: FormGroup;
  isEditMode = false;
  selectedCategoryIds: number[] = [];
  selectedSectionIds: number[] = [];

  ngOnInit(): void {
    this.isEditMode = !!this.data.item;

    if (this.isEditMode && this.data.item) {
      this.selectedCategoryIds = this.data.item.categories?.map(c => c.id) || [];
      this.selectedSectionIds = this.data.item.sections?.map(s => s.id) || [];
    }

    this.itemForm = this.fb.group({
      name: [this.data.item?.name || '', [Validators.required, Validators.minLength(2)]],
      price: [this.data.item?.price || null, [Validators.required, Validators.min(0.01)]]
    });
  }

  get title(): string {
    return this.isEditMode ? 'Editar Artículo' : 'Crear Artículo';
  }

  onCategoryIdsChange(ids: number[]): void {
    this.selectedCategoryIds = ids;
  }

  onSectionIdsChange(ids: number[]): void {
    this.selectedSectionIds = ids;
  }

  onSubmit(): void {
    if (this.itemForm.valid) {
      const item: Partial<Item> & { name: string; price: number } = {
        name: this.itemForm.value.name,
        price: this.itemForm.value.price,
        categories: this.data.allCategories.filter(c => this.selectedCategoryIds.includes(c.id)),
        sections: this.data.allSections.filter(s => this.selectedSectionIds.includes(s.id))
      };

      if (this.isEditMode && this.data.item?.id) {
        item.id = this.data.item.id;
      }

      this.dialogRef.close(item);
    }
  }

  onCancel(): void {
    this.dialogRef.close();
  }
}
