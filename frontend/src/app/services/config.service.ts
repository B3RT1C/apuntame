import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class ConfigService {
  private readonly BACKEND_PORT = 8080;

  get apiUrl(): string {
    const hostname = window.location.hostname;
    return `http://${hostname}:${this.BACKEND_PORT}/api`;
  }

  get wsUrl(): string {
    const hostname = window.location.hostname;
    return `ws://${hostname}:${this.BACKEND_PORT}/ws`;
  }

  get backendUrl(): string {
    const hostname = window.location.hostname;
    return `http://${hostname}:${this.BACKEND_PORT}`;
  }
}
