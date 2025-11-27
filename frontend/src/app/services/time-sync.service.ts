import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class TimeSyncService {
  private timeDifference: number = 0; // Time difference between server and client in ms
  private isSynced: boolean = false;

  /**
   * Server time in ‘yyyy-MM-dd HH:mm:ss’ format.
   */
  syncWithServer(serverTime: string): void {
    if (this.isSynced) { 
      return;
    }

    const serverDate = new Date(serverTime.replace(' ', 'T'));
    const clientDate = new Date();

    this.timeDifference = serverDate.getTime() - clientDate.getTime();
    this.isSynced = true;
  }

  getServerTime(): Date {
    const clientNow = new Date();
    return new Date(clientNow.getTime() + this.timeDifference);
  }

  getServerTimeString(): string {
    const serverTime = this.getServerTime();

    const year = serverTime.getFullYear();
    const month = String(serverTime.getMonth() + 1).padStart(2, '0');
    const day = String(serverTime.getDate()).padStart(2, '0');
    const hours = String(serverTime.getHours()).padStart(2, '0');
    const minutes = String(serverTime.getMinutes()).padStart(2, '0');
    const seconds = String(serverTime.getSeconds()).padStart(2, '0');

    return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
  }

  reset(): void {
    this.timeDifference = 0;
    this.isSynced = false;
  }
}
