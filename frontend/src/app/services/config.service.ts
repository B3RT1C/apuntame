import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ConfigService {
  private readonly BACKEND_PORT = 8080;

  get apiUrl(): string {
    if (environment.useProxy) {
      return '/api';
    } else {
      const hostname = window.location.hostname;
      return `http://${hostname}:${this.BACKEND_PORT}/api`;
    }
  }

  get wsUrl(): string {
    if (environment.useProxy) {
      return '/ws';
    } else {
      const hostname = window.location.hostname;
      return `ws://${hostname}:${this.BACKEND_PORT}/ws`;
    }
  }

  get backendUrl(): string {
    if (environment.useProxy) {
      return 'http://localhost:8080';
    } else {
      const hostname = window.location.hostname;
      return `http://${hostname}:${this.BACKEND_PORT}`;
    }
  }
}
