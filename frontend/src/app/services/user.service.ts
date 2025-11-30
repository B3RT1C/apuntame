import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { User } from '../models/user.model';
import { ConfigService } from './config.service';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private http = inject(HttpClient);
  private config = inject(ConfigService);
  private apiUrl = `${this.config.apiUrl}/users`;

  getAllUsers(limit?: number): Observable<User[]> {
    const params = limit ? `?limit=${limit}` : '';
    return this.http.get<User[]>(`${this.apiUrl}${params}`);
  }

  getUserByUsername(username: string): Observable<User> {
    return this.http.get<User>(`${this.apiUrl}/${username}`);
  }

  createUser(user: User): Observable<User> {
    return this.http.post<User>(this.apiUrl, user);
  }

  updateUser(username: string, user: User): Observable<User> {
    return this.http.put<User>(`${this.apiUrl}/${username}`, user);
  }

  deleteUser(username: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${username}`);
  }
}
