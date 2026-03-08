import { HttpResponse } from '@angular/common/http';
import { Component, Inject } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { EmployeeDataService } from '../../employee-data.service';
import { SharedModule } from '../../shared/shared.module';
import { Router } from '@angular/router';

@Component({
  selector: 'app-loan-application',
  standalone: true,
  imports: [SharedModule],
  templateUrl: './loan-application.component.html',
  styleUrl: './loan-application.component.css'
})
export class LoanApplicationComponent {
  loanForm: any;
  createNew: boolean = true;
  isSubmitting = false;

  constructor(
    private employeeDataService: EmployeeDataService,
    private dialogRef: MatDialogRef<LoanApplicationComponent>,
    private formBuilder: FormBuilder,
    private snackBar: MatSnackBar,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private router: Router
  ) {}

  ngOnInit() {
    this.createFromGroup();
  }

  onSubmit() {
    if (this.isSubmitting) return;
    this.isSubmitting = true;

    this.employeeDataService.saveLoanApplication(this.loanForm.value).subscribe({
      next: (response: HttpResponse<any>) => {
        if (response?.status === 200 || response?.status === 201) {
          this.snackBar.open('Loan application submitted successfully', 'Close', {
            duration: 3000,
            horizontalPosition: 'end',
            verticalPosition: 'top'
          });
          this.dialogRef.close({ success: true, data: response.body });
        }
      },
      error: (err) => {
        this.isSubmitting = false;
        this.snackBar.open(
          err?.error?.message || 'Failed to submit loan application. Please try again.',
          'Close',
          { duration: 5000, horizontalPosition: 'end', verticalPosition: 'top', panelClass: ['error-snackbar'] }
        );
      },
      complete: () => {
        this.isSubmitting = false;
      }
    });
  }

  close() {
    this.dialogRef.close();
    this.loanForm.reset();
  }

  createFromGroup() {
    this.loanForm = this.formBuilder.group({
      fname: [''],
      lname: [''],
      mailId: [''],
      mobile: [''],
      city: ['']
    });
  }
  ngOnDestroy() {

  }
}
