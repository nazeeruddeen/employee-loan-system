import { Component, OnDestroy } from '@angular/core';
import { Subscription } from 'rxjs';
import { Router, RouterModule, RouterOutlet } from '@angular/router';
import { AddEmployeeComponent } from './add-employee/add-employee.component';
import { EmployeeListRefreshService } from './employee-list-refresh.service';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { EmployeeDataService } from './employee-data.service';
import { SharedModule } from './shared/shared.module';
import { CommonModule, DatePipe } from '@angular/common';
import { IpThemeService } from './ip-theme.service';
import { ThemeService } from './theme.service';


@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, SharedModule,RouterModule,CommonModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent implements OnDestroy {
  title = 'employee';
  showTaskboard: boolean = false;
  showLoanboard: boolean = false;
  employeeData: any;
  employeeForm: any;
  displayedColumns: string[] | undefined;
  createNew: boolean = false;
  reverse = true;
  showFilterData: boolean = false;
  id: string | null = '';
  isHomeRoute = false;

  constructor(
    private employeeDataService: EmployeeDataService,
    private formBuilder: FormBuilder,
    private dialog: MatDialog,
    private themeService: ThemeService,
    private router: Router,
    private refreshService: EmployeeListRefreshService
  ) {}
  isDarkTheme = false;
  private themeSub?: Subscription;

  ngOnInit() {
    this.isDarkTheme = this.themeService.isDarkTheme.value;
    this.themeService.setDarkThemeClass(this.isDarkTheme);
    this.themeSub = this.themeService.isDarkTheme$.subscribe((v) => {
      this.isDarkTheme = v;
      this.themeService.setDarkThemeClass(v);
    });
    this.themeService.ensureOverlayTheme();
    this.id = new URLSearchParams(window.location.search).get('id');
    this.updateHomeRoute();
    this.router.events.subscribe(() => this.updateHomeRoute());

    this.displayedColumns = ['id',
      'fullname',
      'dept',
      'age',
      'salary', 'operation'];
    if (!this.id) {
      this.employeeDataService.getAllEmployeeData().subscribe((response: HttpResponse<any>) => {
        this.employeeData = response.body;
      });
    }

  }
 
  private updateHomeRoute() {
    const url = this.router.url;
    this.isHomeRoute = url === '' || url === '/' || url === '/employee';
  }

  openAddEmployeeDialog() {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.disableClose = true;
    dialogConfig.autoFocus = true;
    dialogConfig.data = { id: '' };
    if (this.themeService.isDarkTheme.value) {
      dialogConfig.panelClass = ['app-dark-dialog'];
    }
    const ref = this.dialog.open(AddEmployeeComponent, dialogConfig);
    this.themeService.ensureOverlayTheme();
    ref.afterClosed().subscribe((result) => {
      if (result?.success) {
        this.refreshService.requestRefresh();
      }
    });
  }

  toggleTheme() {
    this.themeService.toggleTheme();
    this.isDarkTheme = this.themeService.isDarkTheme.value;
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
    if (data) {
      dialogConfig.data = { data };
    } else {
      dialogConfig.data = { id: '' };
    }
    if (this.themeService.isDarkTheme.value) {
      dialogConfig.panelClass = ['app-dark-dialog'];
    }
    this.dialog.open(AddEmployeeComponent, dialogConfig);
  }

  deleteEmployee(id: any) {
    this.employeeDataService.deleteEmployeeById(id).subscribe((response: HttpResponse<any>) => {
    });
  }
  sortData(property: any, orderType: string) {

    this.employeeDataService.sortingEmployee(property, orderType).subscribe((response: HttpResponse<any>) => {
      this.employeeData = response.body;
    })

  }
  ngOnDestroy() {
    this.themeSub?.unsubscribe();
  }

 
}

