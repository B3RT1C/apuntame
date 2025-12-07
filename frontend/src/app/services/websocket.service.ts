import { Injectable } from '@angular/core';
import { Client, StompConfig } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { Subject, Observable } from 'rxjs';
import { ApiEndpoints } from '../constants/api-endpoints.constants';
import { WEBSOCKET_CONFIG } from '../constants/websocket.constants';
import { AuthService } from './auth.service';
import { OrderEventDTO } from '../models/order-event.model';

@Injectable({
  providedIn: 'root'
})
export class WebsocketService {
  private client: Client | null = null;
  private messagesSubject = new Subject<OrderEventDTO>();
  public messages$: Observable<OrderEventDTO> = this.messagesSubject.asObservable();

  constructor(private authService: AuthService) {}

  connect(): void {
    if (this.client?.active) {
      return;
    }

    const token = this.authService.getToken();
    if (!token) {
      console.error('No hay token de autenticación disponible');
      return;
    }

    const config: StompConfig = {
      webSocketFactory: () => new SockJS(ApiEndpoints.WEBSOCKET.URL),
      connectHeaders: {
        Authorization: `Bearer ${token}`
      },
      reconnectDelay: WEBSOCKET_CONFIG.RECONNECT_DELAY,
      heartbeatIncoming: WEBSOCKET_CONFIG.HEARTBEAT_INCOMING,
      heartbeatOutgoing: WEBSOCKET_CONFIG.HEARTBEAT_OUTGOING,
      onConnect: () => {
        this.subscribeToOrders();
      },
      onStompError: (frame) => {
        console.error('Error STOMP:', frame);
      },
      onWebSocketError: (error) => {
        console.error('Error WebSocket:', error);
      }
    };

    this.client = new Client(config);
    this.client.activate();
  }

  private subscribeToOrders(): void {
    if (!this.client) {
      return;
    }

    this.client.subscribe('/topic/orders', (message) => {
      try {
        const orderEvent: OrderEventDTO = JSON.parse(message.body);
        this.messagesSubject.next(orderEvent);
      } catch (error) {
        console.error('Error al parsear mensaje WebSocket:', error);
      }
    });
  }

  disconnect(): void {
    if (this.client) {
      this.client.deactivate();
      this.client = null;
    }
  }
}
