import { Component } from '@angular/core';
import { RouterOutlet, Router } from '@angular/router';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatListModule } from '@angular/material/list';
import { MatIconModule } from '@angular/material/icon';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { AuthService } from '../../services/auth.service';
import { AppRoutes } from '../../constants/app-routes.constants';
import { ListItem } from '../../models/list-item.model';
import { AppToolbarComponent } from '../toolbar/app-toolbar/app-toolbar.component';
import { BottomNavComponent } from '../navigation/bottom-nav/bottom-nav.component';

@Component({
  selector: 'app-main-layout',
  imports: [
    RouterOutlet,
    MatSidenavModule,
    MatListModule,
    MatIconModule,
    AppToolbarComponent,
    BottomNavComponent
  ],
  templateUrl: './main-layout.component.html',
  styleUrl: './main-layout.component.scss'
})
export class MainLayoutComponent {
  username: string = '';
  isMobile: boolean = false;
  sidenavOpened: boolean = false;

  menuItems: ListItem[] = [
    { label: 'Inicio', route: AppRoutes.ROUTE.HOME, icon: 'home' },
    { label: 'Tomar pedidos', route: AppRoutes.ROUTE.TAKE_ORDER, icon: 'add_shopping_cart' },
    { label: 'Editar pedido', route: AppRoutes.ROUTE.CHARGE_ORDER, icon: 'edit' },
    { label: 'Vista pedidos', route: AppRoutes.ROUTE.VIEW_ORDERS, icon: 'list_alt' },
    { label: 'Gestión', route: AppRoutes.ROUTE.MANAGEMENT, icon: 'settings' }
  ];

  constructor(
    private authService: AuthService,
    private router: Router,
    private breakpointObserver: BreakpointObserver
  ) {
    const user = this.authService.getCurrentUser();
    this.username = user?.username || '';

    this.breakpointObserver.observe([
      Breakpoints.XSmall,
      Breakpoints.Small
    ]).subscribe(result => {
      this.isMobile = result.matches;
    });
  }

  toggleSidenav(): void {
    this.sidenavOpened = !this.sidenavOpened;
  }

  navigateTo(route: string): void {
    this.router.navigate([route]);
    this.sidenavOpened = false;
  }

  logout(): void {
    this.authService.logout();
  }
}
