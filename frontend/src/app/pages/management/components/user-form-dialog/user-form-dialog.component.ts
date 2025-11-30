import { Component, inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { User } from '../../../../models/user.model';

export interface UserFormDialogData {
  user?: User;
}

@Component({
  selector: 'app-user-form-dialog',
  standalone: true,
  imports: [
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    ReactiveFormsModule
  ],
  templateUrl: './user-form-dialog.component.html',
  styleUrl: './user-form-dialog.component.scss'
})
export class UserFormDialogComponent implements OnInit {
  private fb = inject(FormBuilder);
  data = inject<UserFormDialogData>(MAT_DIALOG_DATA);
  dialogRef = inject(MatDialogRef<UserFormDialogComponent>);

  userForm!: FormGroup;
  isEditMode = false;

  ngOnInit(): void {
    this.isEditMode = !!this.data.user;

    if (this.isEditMode) {
      this.userForm = this.fb.group({
        username: [{ value: this.data.user?.username || '', disabled: true }],
        role: [this.data.user?.role || '', Validators.required]
      });
    } else {
      this.userForm = this.fb.group({
        username: ['', [Validators.required, Validators.minLength(3)]],
        password: ['', [Validators.required, Validators.minLength(6)]],
        role: ['', Validators.required]
      });
    }
  }

  get title(): string {
    return this.isEditMode ? 'Editar Usuario' : 'Crear Usuario';
  }

  onSubmit(): void {
    if (this.userForm.valid) {
      const user: User = {
        username: this.isEditMode ? this.data.user!.username : this.userForm.value.username,
        role: this.userForm.value.role
      };

      if (!this.isEditMode) {
        user.password = this.userForm.value.password;
      }

      this.dialogRef.close(user);
    }
  }

  onCancel(): void {
    this.dialogRef.close();
  }
}
