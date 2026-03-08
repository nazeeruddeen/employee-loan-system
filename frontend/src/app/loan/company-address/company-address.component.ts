import { HttpResponse } from '@angular/common/http';
import { Component } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { EmployeeDataService } from '../../employee-data.service';
import { SharedModule } from '../../shared/shared.module';
import { Router } from '@angular/router';

@Component({
  selector: 'app-company-address',
  standalone: true,
  imports: [SharedModule],
  templateUrl: './company-address.component.html',
  styleUrl: './company-address.component.css'
})
export class CompanyAddressComponent {
 companyAddressForm: any;
  createNew: boolean = true;
  id: string | null = '';
  data: Object | null = '';
  constructor(private employeeDataService: EmployeeDataService,
    private formBuilder: FormBuilder, private router: Router) {
  }
  ngOnInit() {
    this.createFromGroup();
    this.id = new URLSearchParams(window.location.search).get('id');
    this.employeeDataService.getCompanyAddress(this.id).subscribe((response) => {
      this.data = response.body;
      this.companyAddressForm.patchValue(this.data);
      this.companyAddressForm.disable();
    })
  }
  saveDetails() {
   // this.employeeDataService.saveLoanApplication(this.companyAddressForm.value).subscribe((response: HttpResponse<any>) => {
   //modifeid by Teja..
   this.id = new URLSearchParams(window.location.search).get('id');
    this.employeeDataService.saveCompanyAddress(this.id,this.companyAddressForm.value).subscribe((response: HttpResponse<any>) => { 
  });
    this.companyAddressForm.disable();
  }
  // Teja added edit form function
  editForm(){
    this.companyAddressForm.enable();
   }
  createFromGroup() {
    this.companyAddressForm = this.formBuilder.group({
      building: [''],
      line: [''],
      state: [''],
      city: [''],
      pincode: [''],
      landmark: [''],
      flatnum: [''],
      area: ['']
    });
  }
  ngOnDestroy() {

  }
}
