import { Component } from '@angular/core';
import { EmployeeDataService } from '../../employee-data.service';
import { SharedModule } from '../../shared/shared.module';

interface TransactionFilters {
  success: boolean;
  pending: boolean;
  failed: boolean;
  cancelled: boolean;
  creditCard: boolean;
  debitCard: boolean;
  upi: boolean;
  wallet: boolean;
}

@Component({
  selector: 'app-transactionfilters',
  standalone: true,
  imports: [SharedModule],
  templateUrl: './transactionfilters.component.html',
  styleUrl: './transactionfilters.component.css'
})
export class TransactionfiltersComponent {
  transactions: any[] = [];
  filters: TransactionFilters = {
    success: false,
    pending: false,
    failed: false,
    cancelled: false,
    creditCard: false,
    debitCard: false,
    upi: false,
    wallet: false
  };
  showStatusOptions = false;
  showInstrumentOptions = false;
  isLoading = false;
  loadError: string | null = null;
  private appId: string | null = null;

  constructor(private employeeDataService: EmployeeDataService) {}

  ngOnInit(): void {
    this.appId = new URLSearchParams(window.location.search).get('id');
  }

  fetchFilteredTransactions(): void {
    if (!this.appId) {
      this.loadError = 'Application ID not found';
      return;
    }

    const selectedStatuses: string[] = [];
    const selectedInstruments: string[] = [];

    if (this.filters.success) selectedStatuses.push('SUCCESS');
    if (this.filters.pending) selectedStatuses.push('PENDING');
    if (this.filters.failed) selectedStatuses.push('FAILED');
    if (this.filters.cancelled) selectedStatuses.push('CANCELLED');

    if (this.filters.creditCard) selectedInstruments.push('CREDITCARD');
    if (this.filters.debitCard) selectedInstruments.push('DEBITCARD');
    if (this.filters.upi) selectedInstruments.push('UPI');
    if (this.filters.wallet) selectedInstruments.push('WALLET');

    if (selectedStatuses.length === 0 && selectedInstruments.length === 0) {
      this.transactions = [];
      this.loadError = 'Please select at least one status or instrument filter';
      return;
    }

    this.isLoading = true;
    this.loadError = null;

    this.employeeDataService.getFilteredTransactions(
      Number(this.appId),
      selectedStatuses,
      selectedInstruments
    ).subscribe({
      next: (data) => {
        this.transactions = Array.isArray(data) ? data : (data?.body ?? []);
      },
      error: (err) => {
        this.loadError = err?.error?.message || 'Failed to fetch filtered transactions';
      },
      complete: () => {
        this.isLoading = false;
      }
    });
  }
  clearFilters(): void {
    for (const key in this.filters) {
      if (Object.prototype.hasOwnProperty.call(this.filters, key)) {
        this.filters[key as keyof TransactionFilters] = false;
      }
    }
    this.transactions = [];
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

