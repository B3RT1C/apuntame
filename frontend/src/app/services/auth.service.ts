import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { LoginRequest, LoginResponse, User } from '../models/auth.model';
import { ConfigService } from './config.service';
import { ApiEndpoints } from '../constants/api-endpoints.constants';
import { AppRoutes } from '../constants/app-routes.constants';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly TOKEN_KEY = 'auth_token';
  private readonly USER_KEY = 'auth_user';
  private readonly REMEMBER_KEY = 'auth_remember';
  private currentUserSubject: BehaviorSubject<User | null>;
  public currentUser$: Observable<User | null>;

  constructor(
    private http: HttpClient,
    private router: Router,
    private configService: ConfigService
  ) {
    const storedUser = this.getFromStorage(this.USER_KEY);
    const user = storedUser ? JSON.parse(storedUser) : null;
    this.currentUserSubject = new BehaviorSubject<User | null>(user);
    this.currentUser$ = this.currentUserSubject.asObservable();
  }

  private getStorage(): Storage {
    const remember = localStorage.getItem(this.REMEMBER_KEY);
    return remember === 'true' ? localStorage : sessionStorage;
  }

  private getFromStorage(key: string): string | null {
    return localStorage.getItem(key) || sessionStorage.getItem(key);
  }

  private setInStorage(key: string, value: string): void {
    const storage = this.getStorage();
    storage.setItem(key, value);
  }

  private removeFromStorage(key: string): void {
    localStorage.removeItem(key);
    sessionStorage.removeItem(key);
  }

  login(credentials: LoginRequest, rememberMe: boolean = false): Observable<LoginResponse> {
    localStorage.setItem(this.REMEMBER_KEY, rememberMe.toString());

    return this.http.post<LoginResponse>(
      `${this.configService.apiUrl}${ApiEndpoints.AUTH.LOGIN}`,
      credentials
    ).pipe(
      tap(response => {
        this.setInStorage(this.TOKEN_KEY, response.token);

        const user: User = {
          username: response.username,
          role: response.role
        };
        this.setInStorage(this.USER_KEY, JSON.stringify(user));

        this.currentUserSubject.next(user);
      })
    );
  }

  logout(): void {
    this.clearSession();

    this.router.navigate([AppRoutes.ROUTE.LOGIN]);
  }

  clearSession(): void {
    this.removeFromStorage(this.TOKEN_KEY);
    this.removeFromStorage(this.USER_KEY);
    localStorage.removeItem(this.REMEMBER_KEY);

    this.currentUserSubject.next(null);
  }

  isAuthenticated(): boolean {
    const token = this.getToken();
    if (!token) {
      return false;
    }

    return !this.isTokenExpired(token);
  }

  private isTokenExpired(token: string): boolean {
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));

      if (!payload.exp) {
        return true; // Tokens with no expiration are invalid
      }

      const now = Date.now() / 1000;
      return payload.exp < now;

    } catch {
      return true;
    }
  }

  getToken(): string | null {
    return this.getFromStorage(this.TOKEN_KEY);
  }

  getCurrentUser(): User | null {
    return this.currentUserSubject.value;
  }
}