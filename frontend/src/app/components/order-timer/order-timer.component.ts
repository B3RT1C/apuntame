import { Component, Input, Output, EventEmitter, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TimeSyncService } from '../../services/time-sync.service';

@Component({
  selector: 'app-order-timer',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './order-timer.component.html',
  styleUrls: ['./order-timer.component.scss']
})
export class OrderTimerComponent implements OnInit, OnDestroy {
  @Input() creationDate?: string;
  @Input() paidAt?: string;
  @Input() preparedAt?: string;
  @Input() deliveredAt?: string;
  @Output() colorChange = new EventEmitter<string>();

  elapsedTime: string = '00:00';
  timerClass: string = 'green';
  private intervalId: any;
  private isStopped: boolean = false;

  constructor(private timeSyncService: TimeSyncService) {}

  ngOnInit(): void {
    this.updateTimer();
    this.intervalId = setInterval(() => {
      this.updateTimer();
    }, 1000);
  }

  ngOnDestroy(): void {
    if (this.intervalId) {
      clearInterval(this.intervalId);
    }
  }

  private updateTimer(): void {
    if (!this.creationDate) {
      this.resetTimer();
      return;
    }

    const isFullyCompleted = this.isOrderFullyCompleted();
    this.handleCompletedOrder(isFullyCompleted);

    const createdDate = this.parseDate(this.creationDate);
    if (!createdDate) {
      this.resetTimer();
      return;
    }

    const diffMs = this.calculateTimeDifference(createdDate, isFullyCompleted);
    if (diffMs < 0) {
      this.resetTimer();
      return;
    }

    this.updateTimerDisplay(diffMs);
  }

  private isOrderFullyCompleted(): boolean {
    return !!(this.paidAt && this.preparedAt && this.deliveredAt);
  }

  private handleCompletedOrder(isFullyCompleted: boolean): void {
    if (isFullyCompleted && !this.isStopped) {
      this.isStopped = true;
      this.setTimerClass('red');

      if (this.intervalId) {
        clearInterval(this.intervalId);
      }
    }
  }

  private parseDate(dateString: string): Date | null {
    const dateStr = dateString.includes('T') ? dateString : dateString.replace(' ', 'T');
    const date = new Date(dateStr);
    return isNaN(date.getTime()) ? null : date;
  }

  private calculateTimeDifference(createdDate: Date, isFullyCompleted: boolean): number {
    if (isFullyCompleted) {
      const mostRecentDate = this.getMostRecentCompletionDate();
      return mostRecentDate.getTime() - createdDate.getTime();
    } else {
      const currentTime = this.timeSyncService.getServerTime();
      return currentTime.getTime() - createdDate.getTime();
    }
  }

  private getMostRecentCompletionDate(): Date {
    const dates: Date[] = [
      this.paidAt,
      this.preparedAt,
      this.deliveredAt
    ]
      .filter((dateStr): dateStr is string => !!dateStr)
      .map(dateStr => this.parseDate(dateStr)!)
      .filter(date => date !== null);

    return new Date(Math.max(...dates.map(d => d.getTime())));
  }

  private updateTimerDisplay(diffMs: number): void {
    const diffMinutes = Math.floor(diffMs / 60000);
    const diffSeconds = Math.floor((diffMs % 60000) / 1000);

    this.updateColorClass(diffMinutes);
    this.formatElapsedTime(diffMinutes, diffSeconds);
  }

  private updateColorClass(diffMinutes: number): void {
    if (this.isStopped) {
      return;
    }

    if (diffMinutes < 10) {
      this.setTimerClass('green');
    } else if (diffMinutes < 20) {
      this.setTimerClass('yellow');
    } else {
      this.setTimerClass('red');
    }
  }

  private formatElapsedTime(diffMinutes: number, diffSeconds: number): void {
    if (diffMinutes >= 60) {
      const hours = Math.floor(diffMinutes / 60);
      const minutes = diffMinutes % 60;
      this.elapsedTime = `${this.padZero(hours)}:${this.padZero(minutes)}:${this.padZero(diffSeconds)}`;
    } else {
      this.elapsedTime = `${this.padZero(diffMinutes)}:${this.padZero(diffSeconds)}`;
    }
  }

  private resetTimer(): void {
    this.elapsedTime = '00:00';
    this.setTimerClass('green');
  }

  private setTimerClass(color: string): void {
    if (this.timerClass !== color) {
      this.timerClass = color;
      setTimeout(() => this.colorChange.emit(color), 0);
    }
  }

  private padZero(num: number): string {
    return num.toString().padStart(2, '0');
  }
}
