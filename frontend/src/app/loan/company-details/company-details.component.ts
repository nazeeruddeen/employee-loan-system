import { DatePipe } from '@angular/common';
import { HttpResponse } from '@angular/common/http';
import { Component } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { EmployeeDataService } from '../../employee-data.service';
import { SharedModule } from '../../shared/shared.module';
import { Router } from '@angular/router';

@Component({
  selector: 'app-company-details',
  standalone: true,
  imports: [SharedModule],
  templateUrl: './company-details.component.html',
  styleUrl: './company-details.component.css'
})
export class CompanyDetailsComponent {
  companyDetailsForm: any;
  createNew: boolean = true;
  id: string | null = '';
  data: any = null;
  isLoading = false;
  loadError: string | null = null;

  constructor(
    private employeeDataService: EmployeeDataService,
    private formBuilder: FormBuilder,
    private datepipe: DatePipe,
    private router: Router
  ) {}

  ngOnInit() {
    this.createFromGroup();
    this.id = new URLSearchParams(window.location.search).get('id');
    if (!this.id) {
      this.loadError = 'Application ID not found';
      return;
    }
    this.isLoading = true;
    this.loadError = null;
    this.employeeDataService.getCompanyDetails(this.id).subscribe({
      next: (response) => {
        this.data = response.body;
        if (this.data) {
          this.companyDetailsForm.patchValue(this.data);
          if (this.data.dateOfEstablish) {
            const dateVal = new Date(this.data.dateOfEstablish);
            if (!isNaN(dateVal.getTime())) {
              this.companyDetailsForm.get('dateOfEstablish')?.setValue(dateVal);
            }
          }
          this.companyDetailsForm.disable();
        }
      },
      error: (err) => {
        this.loadError = err?.error?.message || 'Failed to load company details';
      },
      complete: () => {
        this.isLoading = false;
      }
    });
  }
  saveDetails() {
    const saveObj = { ...this.companyDetailsForm.value };
    if (saveObj.dateOfEstablish) {
      saveObj.dateOfEstablish = this.datepipe.transform(saveObj.dateOfEstablish, 'MM/dd/yyyy') ?? saveObj.dateOfEstablish;
    }
    this.id = new URLSearchParams(window.location.search).get('id');
    if (!this.id) return;
    this.employeeDataService.saveCompanyDetails(this.id, saveObj).subscribe({
      next: () => {
        this.companyDetailsForm.disable();
      },
      error: (err) => {
        console.error('Failed to save company details', err);
      }
    });
  }
 // Teja added edit form function
 editForm(){
  this.companyDetailsForm.enable();
 }


  createFromGroup() {
    this.companyDetailsForm = this.formBuilder.group({
      companyName: [''],
      dateOfEstablish: [''],
      gstin: [''],
      companyPan: [''],
      industryType: [''],
      turnover: [''],
    });
  }

  ngOnDestroy() {

  }
}
