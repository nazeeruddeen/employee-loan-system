import { Component } from '@angular/core';
import { SharedModule } from '../shared/shared.module';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { AddEmployeeComponent } from '../add-employee/add-employee.component';
import { EmployeeDataService } from '../employee-data.service';
import { ThemeService } from '../theme.service';
import { EmployeeListRefreshService } from '../employee-list-refresh.service';

@Component({
  selector: 'app-view-employees',
  standalone: true,
  imports: [SharedModule],
  templateUrl: './view-employees.component.html',
  styleUrl: './view-employees.component.css'
})
export class ViewEmployeesComponent {
  showTaskboard = false;
  showLoanboard = false;
  employeeData: any;
  employeeForm: any;
  displayedColumns: string[] | undefined;
  createNew = false;
  reverse = true;
  showFilterData = false;
  id: string | null = '';
  isLoading = false;
  searchTerm = '';

  constructor(
    private employeeDataService: EmployeeDataService,
    private formBuilder: FormBuilder,
    private dialog: MatDialog,
    private snackBar: MatSnackBar,
    private themeService: ThemeService,
    private refreshService: EmployeeListRefreshService
  ) {}

  ngOnInit() {
    this.id = new URLSearchParams(window.location.search).get('id');
    this.refreshService.onRefresh().subscribe(() => this.loadEmployees());
    this.displayedColumns = ['id',
      'fullname',
      'dept',
      'age',
      'salary', 'operation'];
    if (!this.id) {
      this.loadEmployees();
    }
  }

  searchEmployees() {
    if (!this.searchTerm?.trim()) {
      this.loadEmployees();
      return;
    }
    this.isLoading = true;
    this.employeeDataService.searchEmployee(this.searchTerm.trim()).subscribe({
      next: (response: HttpResponse<any>) => {
        this.employeeData = response.body ?? [];
      },
      error: (err) => {
        this.snackBar.open(err?.error?.message || 'Search failed', 'Close', {
          duration: 3000,
          horizontalPosition: 'end',
          verticalPosition: 'top',
          panelClass: ['error-snackbar']
        });
      },
      complete: () => {
        this.isLoading = false;
      }
    });
  }

  loadEmployees() {
    this.isLoading = true;
    this.employeeDataService.getAllEmployeeData().subscribe({
      next: (response: HttpResponse<any>) => {
        this.employeeData = response.body ?? [];
      },
      error: (err) => {
        this.snackBar.open(err?.error?.message || 'Failed to load employees', 'Close', {
          duration: 5000,
          horizontalPosition: 'end',
          verticalPosition: 'top',
          panelClass: ['error-snackbar']
        });
      },
      complete: () => {
        this.isLoading = false;
      }
    });
  }

  showFilterdataScreen() {
    this.showFilterData = true;
  }
  showTaskboardData() {
    this.showTaskboard = true;
  }
  showloanData() {
    this.showLoanboard = true;
    this.showTaskboard = false;
    this.showFilterData = false;
  }
  showEmployeeData() {
    this.showLoanboard = false;
    this.showTaskboard = true;
    this.showFilterData = true;
  }
  back() {
    this.showTaskboard = false;
    this.showFilterData = false;
    this.showLoanboard = false;
  }
  addNew() {
    this.employeeForm.reset();
    this.createNew = true;
  }
  saveNewEmployee() {

  }
  updateEmployee(employee: any) {
    this.createNew = true;
    this.employeeForm.patchValue(employee);
  }
  openDialog(data?: any) {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.disableClose = true;
    dialogConfig.autoFocus = true;
    dialogConfig.data = data ? { data } : { id: '' };
    if (this.themeService.isDarkTheme.value) {
      dialogConfig.panelClass = ['app-dark-dialog'];
    }
    const dialogRef = this.dialog.open(AddEmployeeComponent, dialogConfig);
    dialogRef.afterClosed().subscribe((result) => {
      if (result?.success) {
        this.loadEmployees();
      }
    });
  }

  deleteEmployee(id: any) {
    if (!confirm('Are you sure you want to delete this employee?')) return;
    this.employeeDataService.deleteEmployeeById(id).subscribe({
      next: (response: HttpResponse<any>) => {
        if (response?.status === 200 || response?.status === 204) {
          this.snackBar.open('Employee deleted successfully', 'Close', {
            duration: 3000,
            horizontalPosition: 'end',
            verticalPosition: 'top'
          });
          this.loadEmployees();
        }
      },
      error: (err) => {
        this.snackBar.open(err?.error?.message || 'Failed to delete employee', 'Close', {
          duration: 5000,
          horizontalPosition: 'end',
          verticalPosition: 'top',
          panelClass: ['error-snackbar']
        });
      }
    });
  }
  sortData(property: any, orderType: string) {

    this.employeeDataService.sortingEmployee(property, orderType).subscribe((response: HttpResponse<any>) => {
      this.employeeData = response.body;
    })

  }
  ngOnDestroy() {

  }
}

