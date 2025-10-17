import { Routes } from '@angular/router';
import { AppRoutes } from './constants/app-routes.constants';
import { LoginComponent } from './pages/login/login.component';
import { HomeComponent } from './pages/home/home.component';
import { authGuard } from './guards/auth.guard';

export const routes: Routes = [
  {
    path: AppRoutes.PATH.LOGIN,
    component: LoginComponent
  },
  {
    path: AppRoutes.PATH.HOME,
    component: HomeComponent,
    canActivate: [authGuard]
  },
  {
    path: '**',
    redirectTo: AppRoutes.PATH.HOME
  }
];
