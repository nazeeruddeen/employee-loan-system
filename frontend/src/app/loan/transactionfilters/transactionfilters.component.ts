import { HttpParams } from '@angular/common/http';
import { Component } from '@angular/core';
import { EmployeeDataService } from '../../employee-data.service';
import { SharedModule } from '../../shared/shared.module';

interface TransactionFilters {
  success: boolean;
  pending: boolean;
  failed: boolean;
  cancelled: boolean;
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
  filters = {
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
      let params = new HttpParams();
    
      // Define sets for status and instrument
      const statusSet = new Set(['SUCCESS', 'PENDING', 'FAILED', 'CANCELLED']);
      const instrumentSet = new Set(['CREDITCARD', 'DEBITCARD', 'UPI', 'WALLET']);
    
      // Loop over the filters object and add the selected filters to the params
      let selectedFilters: string[] = [];
      for (const key in this.filters) {
        if (this.filters.hasOwnProperty(key) && this.filters[key as keyof TransactionFilters] === true) {
          // Convert the filter key to the corresponding value expected by the backend
          const filterValue = key.toUpperCase();
          selectedFilters.push(filterValue);
        }
      }
    
      // Determine if the selected filters are related to status or instrument
      let isStatus = selectedFilters.some(filter => statusSet.has(filter));
      let isInstrument = selectedFilters.some(filter => instrumentSet.has(filter));
    
      // Set the 'statusOrInstrument' parameter accordingly
      if (isStatus) {
        params = params.set('statusOrInstrument', 'status');
      } else if (isInstrument) {
        params = params.set('statusOrInstrument', 'instrument');
      }
    
    selectedFilters.forEach(filter => {
      params = params.append('statusOrInstrumentTypesList', filter);
    });

    this.isLoading = true;
    this.loadError = null;
    const statusOrInstrument = isStatus ? 'status' : (isInstrument ? 'instrument' : 'status');
    this.employeeDataService.getFilteredTransactions(
      Number(this.appId),
      statusOrInstrument,
      selectedFilters
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
        if (this.filters.hasOwnProperty(key)) {
            // Use the keyof keyword and type assertion here
            this.filters[key as keyof typeof this.filters] = false;
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

