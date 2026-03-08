import { HttpResponse } from '@angular/common/http';
import { Component, Inject } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { EmployeeDataService } from '../employee-data.service';
import { SharedModule } from '../shared/shared.module';

@Component({
  selector: 'app-add-employee',
  standalone: true,
  imports: [SharedModule],
  templateUrl: './add-employee.component.html',
  styleUrl: './add-employee.component.css'
})
export class AddEmployeeComponent {
  employeeForm: any;
  createNew: boolean = true;
  isSubmitting = false;

  constructor(
    private employeeDataService: EmployeeDataService,
    private dialogRef: MatDialogRef<AddEmployeeComponent>,
    private formBuilder: FormBuilder,
    private snackBar: MatSnackBar,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) {}

  ngOnInit() {
    this.createFromGroup();
    if (this.data?.data) {
      this.employeeForm.patchValue(this.data.data);
    }
  }

  get isEditMode(): boolean {
    return !!(this.data?.data?.id);
  }

  onSubmit() {
    if (this.isSubmitting) return;
    this.isSubmitting = true;

    const apiCall = this.isEditMode
      ? this.employeeDataService.updateEmployee(this.employeeForm.value)
      : this.employeeDataService.saveEmployee(this.employeeForm.value);

    apiCall.subscribe({
      next: (response: HttpResponse<any>) => {
        if (response?.status === 200 || response?.status === 201) {
          this.snackBar.open(
            this.isEditMode ? 'Employee updated successfully' : 'Employee added successfully',
            'Close',
            { duration: 3000, horizontalPosition: 'end', verticalPosition: 'top' }
          );
          this.dialogRef.close({ success: true, data: response.body });
        }
      },
      error: (err) => {
        this.isSubmitting = false;
        this.snackBar.open(
          err?.error?.message || 'Failed to save employee. Please try again.',
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
  }

  createFromGroup() {
    this.employeeForm = this.formBuilder.group({
      fname: [''],
      id: [''],
      lname: [''],
      fullname: [''],
      dept: [''],
      age: [''],
     // mobile: [''],
      salary: [''],
      empCode: [''],
    });
  }
  ngOnDestroy() {

  }
}
