import { Component, inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { Section } from '../../../../models/section.model';

export interface SectionFormDialogData {
  section?: Section;
}

@Component({
  selector: 'app-section-form-dialog',
  standalone: true,
  imports: [
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    ReactiveFormsModule
  ],
  templateUrl: './section-form-dialog.component.html',
  styleUrl: './section-form-dialog.component.scss'
})
export class SectionFormDialogComponent implements OnInit {
  private fb = inject(FormBuilder);
  data = inject<SectionFormDialogData>(MAT_DIALOG_DATA);
  dialogRef = inject(MatDialogRef<SectionFormDialogComponent>);

  sectionForm!: FormGroup;
  isEditMode = false;

  ngOnInit(): void {
    this.isEditMode = !!this.data.section;
    this.sectionForm = this.fb.group({
      name: [this.data.section?.name || '', [Validators.required, Validators.minLength(2)]]
    });
  }

  get title(): string {
    return this.isEditMode ? 'Editar Sección' : 'Crear Sección';
  }

  onSubmit(): void {
    if (this.sectionForm.valid) {
      const section: Partial<Section> & { name: string } = {
        name: this.sectionForm.value.name
      };

      if (this.isEditMode && this.data.section?.id) {
        section.id = this.data.section.id;
      }

      this.dialogRef.close(section);
    }
  }

  onCancel(): void {
    this.dialogRef.close();
  }
}
