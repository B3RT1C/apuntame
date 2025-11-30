import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ConfigService } from './config.service';
import { Item } from '../models/item.model';

@Injectable({
  providedIn: 'root'
})
export class ItemService {
  private apiUrl: string;

  constructor(
    private http: HttpClient,
    private configService: ConfigService
  ) {
    this.apiUrl = `${this.configService.apiUrl}/items`;
  }

  getAllItems(): Observable<Item[]> {
    return this.http.get<Item[]>(this.apiUrl);
  }

  getItemsByCategories(categoryIds: number[], filterMode: string = 'OR'): Observable<Item[]> {
    const params = categoryIds.length > 0
      ? `?categories=${categoryIds.join(',')}&filterMode=${filterMode}`
      : '';
    return this.http.get<Item[]>(`${this.apiUrl}${params}`);
  }

  getItemsBySections(sectionIds: number[], filterMode: string = 'OR'): Observable<Item[]> {
    const params = sectionIds.length > 0
      ? `?sections=${sectionIds.join(',')}&filterMode=${filterMode}`
      : '';
    return this.http.get<Item[]>(`${this.apiUrl}${params}`);
  }

  getItemById(id: number): Observable<Item> {
    return this.http.get<Item>(`${this.apiUrl}/${id}`);
  }

  createItem(item: Item): Observable<Item> {
    return this.http.post<Item>(this.apiUrl, item);
  }

  updateItem(id: number, item: Item): Observable<Item> {
    return this.http.put<Item>(`${this.apiUrl}/${id}`, item);
  }

  deleteItem(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
