import { HttpResponse } from '@angular/common/http';
import { Component } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { EmployeeDataService } from '../../employee-data.service';
import { SharedModule } from '../../shared/shared.module';
import { Router } from '@angular/router';

@Component({
  selector: 'app-business-product',
  standalone: true,
  imports: [SharedModule],
  templateUrl: './business-product.component.html',
  styleUrl: './business-product.component.css'
})
export class BusinessProductComponent {
  businessProductForm: any;
  createNew: boolean = true;
  id: string | null = '';
  data: Object | null = {};
  loanType: string[];
  isLoading = false;
  loadError: string | null = null;

  constructor(
    private employeeDataService: EmployeeDataService,
    private formBuilder: FormBuilder,
    private router: Router
  ) {
    this.loanType = ['Adopting Business', 'Business Expansion', 'Company Maintenance', 'Salaries'];
  }

  ngOnInit() {
    this.createFromGroup();
    this.id = new URLSearchParams(window.location.search).get('id');
    if (!this.id) {
      this.loadError = 'Application ID not found';
      return;
    }
    this.isLoading = true;
    this.loadError = null;
    this.employeeDataService.getProductDetails(this.id).subscribe({
      next: (response) => {
        const data = response.body;
        if (data) {
          this.data = data;
          this.businessProductForm.patchValue(data);
          this.businessProductForm.disable();
        }
      },
      error: (err) => {
        this.loadError = err?.error?.message || 'Failed to load product details';
      },
      complete: () => {
        this.isLoading = false;
      }
    });
  }
  saveDetails() {
    //this.employeeDataService.saveLoanApplication(this.businessProductForm.value).subscribe((response: HttpResponse<any>) => {
    //  editProductDetails(appId: any, data: any) {
      this.id = new URLSearchParams(window.location.search).get('id');
      this.employeeDataService.editProductDetails(this.id,this.businessProductForm.value).subscribe((response: HttpResponse<any>) => {
    });
    this.businessProductForm.disable();

  }

 editForm(){
  this.businessProductForm.enable();
 }
  createFromGroup() {
    this.businessProductForm = this.formBuilder.group({
      purposeOfLoan: [''],
      natureOfBusiness: [''],
      tenure: [''],
      loanAmount: ['']
    });
  }
  ngOnDestroy() {

  }
}
