import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatTabsModule } from '@angular/material/tabs';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { User } from '../../models/user.model';
import { Item } from '../../models/item.model';
import { Category } from '../../models/category.model';
import { Section } from '../../models/section.model';
import { UserService } from '../../services/user.service';
import { ItemService } from '../../services/item.service';
import { CategoryService } from '../../services/category.service';
import { SectionService } from '../../services/section.service';
import { NotificationService } from '../../services/notification.service';
import { ErrorHandlerService } from '../../services/error-handler.service';
import { FORM_DIALOG_CONFIG, CONFIRM_DIALOG_CONFIG, FORM_DIALOG_SCROLLABLE_CONFIG } from '../../constants/dialog-config.constants';
import { UI_MESSAGES } from '../../constants/ui-messages.constants';
import { ConfirmDialogComponent, ConfirmDialogData } from './components/confirm-dialog/confirm-dialog.component';
import { UserFormDialogComponent, UserFormDialogData } from './components/user-form-dialog/user-form-dialog.component';
import { ItemFormDialogComponent, ItemFormDialogData } from './components/item-form-dialog/item-form-dialog.component';
import { CategoryFormDialogComponent, CategoryFormDialogData } from './components/category-form-dialog/category-form-dialog.component';
import { SectionFormDialogComponent, SectionFormDialogData } from './components/section-form-dialog/section-form-dialog.component';

@Component({
  selector: 'app-management',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatTabsModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatChipsModule,
    MatDialogModule,
    MatSnackBarModule,
    MatProgressSpinnerModule,
    MatFormFieldModule,
    MatInputModule
  ],
  templateUrl: './management.component.html',
  styleUrl: './management.component.scss'
})
export class ManagementComponent implements OnInit {
  private userService = inject(UserService);
  private itemService = inject(ItemService);
  private categoryService = inject(CategoryService);
  private sectionService = inject(SectionService);
  private dialog = inject(MatDialog);
  private notificationService = inject(NotificationService);
  private errorHandlerService = inject(ErrorHandlerService);

  categories: Category[] = [];
  sections: Section[] = [];
  items: Item[] = [];
  users: User[] = [];

  filteredCategories: Category[] = [];
  filteredSections: Section[] = [];
  filteredItems: Item[] = [];
  filteredUsers: User[] = [];

  categorySearchText = '';
  sectionSearchText = '';
  itemSearchText = '';
  userSearchText = '';

  usersLoading = true;
  itemsLoading = true;
  categoriesLoading = true;
  sectionsLoading = true;

  userColumns = ['username', 'role', 'actions'];
  itemColumns = ['name', 'price', 'categories', 'sections', 'actions'];
  categoryColumns = ['name', 'itemCount', 'actions'];
  sectionColumns = ['name', 'itemCount', 'actions'];

  ngOnInit(): void {
    this.loadUsers();
    this.loadItems();
    this.loadCategories();
    this.loadSections();
  }

  loadUsers(): void {
    this.usersLoading = true;
    this.userService.getAllUsers().subscribe({
      next: (users) => {
        this.users = users;
        this.filteredUsers = users;
        this.usersLoading = false;
      },
      error: (err) => {
        this.errorHandlerService.handleHttpError(err, 'Error al cargar usuarios');
        this.usersLoading = false;
      }
    });
  }

  loadItems(): void {
    this.itemsLoading = true;
    this.itemService.getAllItems().subscribe({
      next: (items) => {
        this.items = items;
        this.filteredItems = items;
        this.itemsLoading = false;
      },
      error: (err) => {
        this.errorHandlerService.handleHttpError(err, 'Error al cargar productos');
        this.itemsLoading = false;
      }
    });
  }

  loadCategories(): void {
    this.categoriesLoading = true;
    this.categoryService.getAllCategories().subscribe({
      next: (categories) => {
        this.categories = categories;
        this.filteredCategories = categories;
        this.categoriesLoading = false;
      },
      error: (err) => {
        this.errorHandlerService.handleHttpError(err, 'Error al cargar categorías');
        this.categoriesLoading = false;
      }
    });
  }

  loadSections(): void {
    this.sectionsLoading = true;
    this.sectionService.getAllSections().subscribe({
      next: (sections) => {
        this.sections = sections;
        this.filteredSections = sections;
        this.sectionsLoading = false;
      },
      error: (err) => {
        this.errorHandlerService.handleHttpError(err, 'Error al cargar secciones');
        this.sectionsLoading = false;
      }
    });
  }

  applyFilterCategories(): void {
    const searchLower = this.categorySearchText.toLowerCase();
    this.filteredCategories = this.categories.filter(category =>
      category.name.toLowerCase().includes(searchLower)
    );
  }

  applyFilterSections(): void {
    const searchLower = this.sectionSearchText.toLowerCase();
    this.filteredSections = this.sections.filter(section =>
      section.name.toLowerCase().includes(searchLower)
    );
  }

  applyFilterItems(): void {
    const searchLower = this.itemSearchText.toLowerCase();
    this.filteredItems = this.items.filter(item =>
      item.name.toLowerCase().includes(searchLower)
    );
  }

  applyFilterUsers(): void {
    const searchLower = this.userSearchText.toLowerCase();
    this.filteredUsers = this.users.filter(user =>
      user.username.toLowerCase().includes(searchLower) ||
      user.role.toLowerCase().includes(searchLower)
    );
  }

  createUser(): void {
    const dialogRef = this.dialog.open(UserFormDialogComponent, {
      ...FORM_DIALOG_CONFIG,
      data: {} as UserFormDialogData
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.userService.createUser(result).subscribe({
          next: () => {
            this.loadUsers();
            this.notificationService.success(UI_MESSAGES.USER_CREATED);
          },
          error: (err) => this.errorHandlerService.handleHttpError(err, 'Error al crear usuario')
        });
      }
    });
  }

  editUser(user: User): void {
    const dialogRef = this.dialog.open(UserFormDialogComponent, {
      ...FORM_DIALOG_CONFIG,
      data: { user } as UserFormDialogData
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.userService.updateUser(user.username, result).subscribe({
          next: () => {
            this.loadUsers();
            this.notificationService.success(UI_MESSAGES.USER_UPDATED);
          },
          error: (err) => this.errorHandlerService.handleHttpError(err, 'Error al actualizar usuario')
        });
      }
    });
  }

  deleteUser(user: User): void {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      ...CONFIRM_DIALOG_CONFIG,
      data: {
        title: UI_MESSAGES.DELETE_USER_TITLE,
        message: UI_MESSAGES.confirmDeleteUser(user.username),
        confirmText: UI_MESSAGES.DELETE,
        cancelText: UI_MESSAGES.CANCEL
      } as ConfirmDialogData
    });

    dialogRef.afterClosed().subscribe(confirmed => {
      if (confirmed) {
        this.userService.deleteUser(user.username).subscribe({
          next: () => {
            this.loadUsers();
            this.notificationService.success(UI_MESSAGES.USER_DELETED);
          },
          error: (err) => this.errorHandlerService.handleHttpError(err, 'Error al eliminar usuario', () => this.loadUsers())
        });
      }
    });
  }

  createItem(): void {
    const dialogRef = this.dialog.open(ItemFormDialogComponent, {
      ...FORM_DIALOG_SCROLLABLE_CONFIG,
      data: {
        allCategories: this.categories,
        allSections: this.sections
      } as ItemFormDialogData
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.itemService.createItem(result).subscribe({
          next: () => {
            this.loadItems();
            this.notificationService.success(UI_MESSAGES.ITEM_CREATED);
          },
          error: (err) => this.errorHandlerService.handleHttpError(err, 'Error al crear producto')
        });
      }
    });
  }

  editItem(item: Item): void {
    const dialogRef = this.dialog.open(ItemFormDialogComponent, {
      ...FORM_DIALOG_SCROLLABLE_CONFIG,
      data: {
        item,
        allCategories: this.categories,
        allSections: this.sections
      } as ItemFormDialogData
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.itemService.updateItem(item.id, result).subscribe({
          next: () => {
            this.loadItems();
            this.notificationService.success(UI_MESSAGES.ITEM_UPDATED);
          },
          error: (err) => this.errorHandlerService.handleHttpError(err, 'Error al actualizar producto')
        });
      }
    });
  }

  deleteItem(item: Item): void {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      ...CONFIRM_DIALOG_CONFIG,
      data: {
        title: UI_MESSAGES.DELETE_ITEM_TITLE,
        message: UI_MESSAGES.confirmDeleteItem(item.name),
        confirmText: UI_MESSAGES.DELETE,
        cancelText: UI_MESSAGES.CANCEL
      } as ConfirmDialogData
    });

    dialogRef.afterClosed().subscribe(confirmed => {
      if (confirmed) {
        this.itemService.deleteItem(item.id).subscribe({
          next: () => {
            this.loadItems();
            this.notificationService.success(UI_MESSAGES.ITEM_DELETED);
          },
          error: (err) => this.errorHandlerService.handleHttpError(err, 'Error al eliminar producto', () => this.loadItems())
        });
      }
    });
  }

  createCategory(): void {
    const dialogRef = this.dialog.open(CategoryFormDialogComponent, {
      ...FORM_DIALOG_CONFIG,
      data: {} as CategoryFormDialogData
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.categoryService.createCategory(result).subscribe({
          next: () => {
            this.loadCategories();
            this.notificationService.success(UI_MESSAGES.CATEGORY_CREATED);
          },
          error: (err) => this.errorHandlerService.handleHttpError(err, 'Error al crear categoría')
        });
      }
    });
  }

  editCategory(category: Category): void {
    const dialogRef = this.dialog.open(CategoryFormDialogComponent, {
      ...FORM_DIALOG_CONFIG,
      data: { category } as CategoryFormDialogData
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.categoryService.updateCategory(category.id, result).subscribe({
          next: () => {
            this.loadCategories();
            this.loadItems();
            this.notificationService.success(UI_MESSAGES.CATEGORY_UPDATED);
          },
          error: (err) => this.errorHandlerService.handleHttpError(err, 'Error al actualizar categoría')
        });
      }
    });
  }

  deleteCategory(category: Category): void {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      ...CONFIRM_DIALOG_CONFIG,
      data: {
        title: UI_MESSAGES.DELETE_CATEGORY_TITLE,
        message: UI_MESSAGES.confirmDeleteCategory(category.name),
        confirmText: UI_MESSAGES.DELETE,
        cancelText: UI_MESSAGES.CANCEL
      } as ConfirmDialogData
    });

    dialogRef.afterClosed().subscribe(confirmed => {
      if (confirmed) {
        this.categoryService.deleteCategory(category.id).subscribe({
          next: () => {
            this.loadCategories();
            this.loadItems();
            this.notificationService.success(UI_MESSAGES.CATEGORY_DELETED);
          },
          error: (err) => this.errorHandlerService.handleHttpError(err, 'Error al eliminar categoría', () => this.loadCategories())
        });
      }
    });
  }

  createSection(): void {
    const dialogRef = this.dialog.open(SectionFormDialogComponent, {
      ...FORM_DIALOG_CONFIG,
      data: {} as SectionFormDialogData
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.sectionService.createSection(result).subscribe({
          next: () => {
            this.loadSections();
            this.notificationService.success(UI_MESSAGES.SECTION_CREATED);
          },
          error: (err) => this.errorHandlerService.handleHttpError(err, 'Error al crear sección')
        });
      }
    });
  }

  editSection(section: Section): void {
    const dialogRef = this.dialog.open(SectionFormDialogComponent, {
      ...FORM_DIALOG_CONFIG,
      data: { section } as SectionFormDialogData
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.sectionService.updateSection(section.id, result).subscribe({
          next: () => {
            this.loadSections();
            this.loadItems();
            this.notificationService.success(UI_MESSAGES.SECTION_UPDATED);
          },
          error: (err) => this.errorHandlerService.handleHttpError(err, 'Error al actualizar sección')
        });
      }
    });
  }

  deleteSection(section: Section): void {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      ...CONFIRM_DIALOG_CONFIG,
      data: {
        title: UI_MESSAGES.DELETE_SECTION_TITLE,
        message: UI_MESSAGES.confirmDeleteSection(section.name),
        confirmText: UI_MESSAGES.DELETE,
        cancelText: UI_MESSAGES.CANCEL
      } as ConfirmDialogData
    });

    dialogRef.afterClosed().subscribe(confirmed => {
      if (confirmed) {
        this.sectionService.deleteSection(section.id).subscribe({
          next: () => {
            this.loadSections();
            this.loadItems();
            this.notificationService.success(UI_MESSAGES.SECTION_DELETED);
          },
          error: (err) => this.errorHandlerService.handleHttpError(err, 'Error al eliminar sección', () => this.loadSections())
        });
      }
    });
  }

  getItemCountForCategory(categoryId: number): number {
    return this.items.filter(item =>
      item.categories?.some(c => c.id === categoryId)
    ).length;
  }

  getItemCountForSection(sectionId: number): number {
    return this.items.filter(item =>
      item.sections?.some(s => s.id === sectionId)
    ).length;
  }
}
