import { Component, inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { Category } from '../../../../models/category.model';

export interface CategoryFormDialogData {
  category?: Category;
}

@Component({
  selector: 'app-category-form-dialog',
  standalone: true,
  imports: [
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    ReactiveFormsModule
  ],
  templateUrl: './category-form-dialog.component.html',
  styleUrl: './category-form-dialog.component.scss'
})
export class CategoryFormDialogComponent implements OnInit {
  private fb = inject(FormBuilder);
  data = inject<CategoryFormDialogData>(MAT_DIALOG_DATA);
  dialogRef = inject(MatDialogRef<CategoryFormDialogComponent>);

  categoryForm!: FormGroup;
  isEditMode = false;

  ngOnInit(): void {
    this.isEditMode = !!this.data.category;
    this.categoryForm = this.fb.group({
      name: [this.data.category?.name || '', [Validators.required, Validators.minLength(2)]]
    });
  }

  get title(): string {
    return this.isEditMode ? 'Editar Categoría' : 'Crear Categoría';
  }

  onSubmit(): void {
    if (this.categoryForm.valid) {
      const category: Partial<Category> & { name: string } = {
        name: this.categoryForm.value.name
      };

      if (this.isEditMode && this.data.category?.id) {
        category.id = this.data.category.id;
      }

      this.dialogRef.close(category);
    }
  }

  onCancel(): void {
    this.dialogRef.close();
  }
}
