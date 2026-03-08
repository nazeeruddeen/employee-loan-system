import { formatDate } from '@angular/common';
import { Component } from '@angular/core';
import { EmployeeDataService } from '../../employee-data.service';
import { SharedModule } from '../../shared/shared.module';
import { Transaction } from './transaction.model';

@Component({
  selector: 'app-transacation-statement',
  standalone: true,
  imports: [SharedModule],
  templateUrl: './transacation-statement.component.html',
  styleUrls: ['./transacation-statement.component.css']
})
export class TransacationStatementComponent {
  transactions: Transaction[] = [];
  selectedDuration: string = '';
  startDate?: string;
  endDate?: string;
  isLoading = false;

  ngOnInit() {
    this.loadTransactions();
  }

  constructor(private transactionService: EmployeeDataService) {}

  onDurationChange(duration: string): void {
    this.selectedDuration = duration;
    this.fetchTransactions();
  }

  id: string | null = '';
  
  fetchTransactions(): void {
    const formattedStartDate = this.startDate ? formatDate(this.startDate, 'dd-MM-yyyy', 'en-US') : undefined;
    const formattedEndDate = this.endDate ? formatDate(this.endDate, 'dd-MM-yyyy', 'en-US') : undefined;
  
    if (formattedStartDate && formattedEndDate) {
      this.selectedDuration = '';
    }
  
    this.transactionService.getTransactions(this.id, this.selectedDuration, formattedStartDate, formattedEndDate)
      .subscribe(
        transactions => this.transactions = transactions,
        error => {
          console.error('There was an error retrieving transactions', error);
        }
      );
  }
  
  onCustomDateChange(): void {
    if (this.startDate && this.endDate) {
      this.fetchTransactions();
    }
  }
  
  loadTransactions() {
    this.id = new URLSearchParams(window.location.search).get('id');
    if (!this.id) return;
    const formattedStart = this.startDate ? formatDate(this.startDate, 'dd-MM-yyyy', 'en-US') : undefined;
    const formattedEnd = this.endDate ? formatDate(this.endDate, 'dd-MM-yyyy', 'en-US') : undefined;
    this.transactionService.getTransactions(this.id, this.selectedDuration, formattedStart, formattedEnd)
      .subscribe({
        next: (data) => {
          this.transactions = data;
        },
        error: (err) => {
          console.error('There was an error retrieving transactions', err);
        }
      });
  }

  showAllTransactions(): void {
    this.isLoading = true;
    this.selectedDuration = '';
    this.startDate = undefined;
    this.endDate = undefined;
    
    this.transactionService.getTransactions(this.id, '', undefined, undefined)
      .subscribe({
        next: (data) => {
          this.transactions = data;
          this.isLoading = false;
        },
        error: (err) => {
          console.error('There was an error retrieving all transactions', err);
          this.isLoading = false;
        }
      });
  }

  getStatusClass(status: string): string {
    switch (status?.toLowerCase()) {
      case 'success':
      case 'completed':
        return 'bg-success';
      case 'pending':
      case 'processing':
        return 'bg-warning';
      case 'failed':
      case 'error':
        return 'bg-danger';
      case 'cancelled':
        return 'bg-secondary';
      default:
        return 'bg-info';
    }
  }
}
