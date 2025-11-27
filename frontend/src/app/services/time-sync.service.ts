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
    return this.formatDateTime(serverTime);
  }

  formatElapsedTime(startTimestamp: string, endTimestamp?: string): string {
    const startDate = new Date(startTimestamp.replace(' ', 'T'));
    const endDate = endTimestamp
      ? new Date(endTimestamp.replace(' ', 'T'))
      : this.getServerTime();

    const diffMs = endDate.getTime() - startDate.getTime();
    const totalSeconds = Math.floor(diffMs / 1000);
    const totalMinutes = Math.floor(totalSeconds / 60);
    const seconds = totalSeconds % 60;

    if (totalMinutes >= 60) {
      const hours = Math.floor(totalMinutes / 60);
      const minutes = totalMinutes % 60;
      return `${this.padZero(hours)}:${this.padZero(minutes)}:${this.padZero(seconds)}`;
    }

    return `${this.padZero(totalMinutes)}:${this.padZero(seconds)}`;
  }

  parseTimestamp(timestamp: string): Date {
    return new Date(timestamp.replace(' ', 'T'));
  }

  private formatDateTime(date: Date): string {
    const datePart = this.formatDate(date);
    const timePart = this.formatTime(date);
    return `${datePart} ${timePart}`;
  }

  private formatDate(date: Date): string {
    const year = date.getFullYear();
    const month = this.padZero(date.getMonth() + 1);
    const day = this.padZero(date.getDate());
    return `${year}-${month}-${day}`;
  }

  private formatTime(date: Date): string {
    const hours = this.padZero(date.getHours());
    const minutes = this.padZero(date.getMinutes());
    const seconds = this.padZero(date.getSeconds());
    return `${hours}:${minutes}:${seconds}`;
  }

  private padZero(num: number): string {
    return String(num).padStart(2, '0');
  }

  reset(): void {
    this.timeDifference = 0;
    this.isSynced = false;
  }
}
