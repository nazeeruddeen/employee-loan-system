import { Routes,RouterModule } from '@angular/router';
import { FilterDataComponent } from './filter-data/filter-data.component';
import { TaskBoardComponent } from './task-board/task-board.component';
import { EmployeeDataComponent } from './employee-data/employee-data.component';
import { LoanTaskBoardComponent } from './loan/loan-task-board/loan-task-board.component';
import { OverviewComponent } from './loan/overview/overview.component';
import { ViewEmployeesComponent } from './view-employees/view-employees.component';

export const routes: Routes = [
  { path: '', component: ViewEmployeesComponent },
  { path: 'employee', component: EmployeeDataComponent },
  { path: 'taskboard', component: TaskBoardComponent },
  { path: 'filterdata', component: FilterDataComponent },
  {
    path: 'loan', component: LoanTaskBoardComponent,
    children: [
      { path: '', pathMatch: 'full', redirectTo: 'overview' },
      { path: 'overview', component: OverviewComponent },
      { path: 'business-product', component: OverviewComponent },
      { path: 'company-details', component: OverviewComponent },
      { path: 'company-address', component: OverviewComponent },
      { path: 'assurancedetails', component: OverviewComponent },
      { path: 'salesreport', component: OverviewComponent },
      { path: 'transactions', component: OverviewComponent },
      { path: 'txn-filters', component: OverviewComponent },
      { path: 'txn-statement', component: OverviewComponent }
    ]
  },
];
