import { Routes } from '@angular/router';
import { AppRoutes } from './constants/app-routes.constants';
import { LoginComponent } from './pages/login/login.component';
import { MainLayoutComponent } from './components/main-layout/main-layout.component';
import { authGuard } from './guards/auth.guard';

export const routes: Routes = [
  {
    path: AppRoutes.PATH.LOGIN,
    component: LoginComponent
  },
  {
    path: AppRoutes.PATH.HOME,
    component: MainLayoutComponent,
    canActivate: [authGuard],
    children: [
      {
        path: '',
        redirectTo: AppRoutes.PATH.TAKE_ORDER,
        pathMatch: 'full'
      },
      {
        path: AppRoutes.PATH.TAKE_ORDER,
        loadComponent: () => import('./pages/take-order/take-order.component').then(m => m.TakeOrderComponent)
      },
      {
        path: AppRoutes.PATH.CHARGE_ORDER,
        loadComponent: () => import('./pages/charge-order/charge-order.component').then(m => m.ChargeOrderComponent)
      },
      {
        path: AppRoutes.PATH.VIEW_ORDERS,
        loadComponent: () => import('./pages/view-orders/view-orders.component').then(m => m.ViewOrdersComponent)
      },
      {
        path: AppRoutes.PATH.MANAGEMENT,
        loadComponent: () => import('./pages/management/management.component').then(m => m.ManagementComponent)
      }
    ]
  },
  {
    path: '**',
    redirectTo: AppRoutes.PATH.HOME
  }
];
