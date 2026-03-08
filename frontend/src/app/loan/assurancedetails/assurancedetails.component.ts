import { HttpClient } from '@angular/common/http';
import { ChangeDetectorRef, Component } from '@angular/core';
import { FormBuilder, FormGroup, FormArray, Validators, FormControl } from '@angular/forms';
import { SharedModule } from '../../shared/shared.module';

@Component({
  selector: 'app-assurancedetails',
  standalone: true,
  imports: [SharedModule],
  templateUrl: './assurancedetails.component.html',
  styleUrl: './assurancedetails.component.css'
})
export class AssurancedetailsComponent {
  data: any[] = [];
  file!: File;
  id: string | null = '';
  isEditing = false;
  isLoading = false;
  errorMessage = '';
  successMessage = '';
  
  personForm: FormGroup;

  constructor(
    private http: HttpClient, 
    private cdr: ChangeDetectorRef,
    private fb: FormBuilder
  ) {
    this.personForm = this.fb.group({
      persons: this.fb.array([])
    });
  }

  get personsFormArray(): FormArray {
    return this.personForm.get('persons') as FormArray;
  }

  getPersonGroup(index: number): FormGroup {
    const group = this.personsFormArray.at(index) as FormGroup;
    return group || new FormGroup({});
  }

  onFileChange(event: Event) {
    const target = event.target as HTMLInputElement;
    if (target.files && target.files.length > 0) {
      this.file = target.files[0];
      this.clearMessages();
    }
  }

  submit() {
    if (!this.file) {
      this.errorMessage = 'Please select a file first';
      return;
    }

    this.isLoading = true;
    this.clearMessages();
    
    const formData = new FormData();
    formData.append('file', this.file, this.file.name);
    
    this.http.post<any[]>('http://localhost:8080/loans/readJson', formData).subscribe(
      response => {
        this.data = response;
        this.populateForm(response);
        this.isLoading = false;
        this.successMessage = 'File processed successfully!';
        this.cdr.detectChanges();
      },
      error => {
        this.isLoading = false;
        this.errorMessage = error.error?.message || 'Error occurred while processing file';
        this.cdr.detectChanges();
      }
    );
  }

  populateForm(persons: any[]) {
    this.personsFormArray.clear();
    persons.forEach(person => {
      this.personsFormArray.push(this.createPersonGroup(person));
    });
  }

  createPersonGroup(person: any = {}): FormGroup {
    return this.fb.group({
      id: [person.id || null],
      ename: [person.ename || '', [Validators.required, Validators.maxLength(100)]],
      nationality: [person.nationality || '', [Validators.maxLength(50)]],
      age: [person.age || null, [Validators.min(18), Validators.max(100)]],
      mail: [person.mail || '', [Validators.email, Validators.maxLength(100)]],
      gender: [person.gender || '', [Validators.pattern(/^(male|female|others)$/)]]
    });
  }

  saveData() {
    if (this.personsFormArray.invalid) {
      this.errorMessage = 'Please fix validation errors before saving';
      this.markFormGroupTouched(this.personForm);
      return;
    }

    this.id = new URLSearchParams(window.location.search).get('id');
    if (!this.id) {
      this.errorMessage = 'Application ID not found in URL';
      return;
    }

    this.isLoading = true;
    this.clearMessages();

    const formData = this.personsFormArray.value;
    
    this.http.post<any[]>(`http://localhost:8080/loans/saveJsonfileData/${this.id}`, formData).subscribe(    
      response => {
        this.isLoading = false;
        this.successMessage = 'Data saved successfully!';
        this.data = response;
        this.isEditing = false;
        this.cdr.detectChanges();
      },
      error => {
        this.isLoading = false;
        this.errorMessage = error.error?.message || 'Error occurred while saving data';
        this.cdr.detectChanges();
      }
    );
  }

  fetchData(appId: number) {
    this.isLoading = true;
    this.clearMessages();
    
    this.http.get<any[]>(`http://localhost:8080/loans/getPersonDetails/${appId}`).subscribe(
      response => {
        this.data = response;
        this.populateForm(response);
        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error => {
        this.isLoading = false;
        this.errorMessage = 'Error occurred while fetching data';
        this.cdr.detectChanges();
      }
    );
  }

  editForm() {
    this.isEditing = !this.isEditing;
    if (this.isEditing && this.data.length > 0) {
      this.populateForm(this.data);
    }
  }

  addNewPerson() {
    this.personsFormArray.push(this.createPersonGroup());
    this.isEditing = true;
  }

  removePerson(index: number) {
    this.personsFormArray.removeAt(index);
  }

  cancelEdit() {
    this.isEditing = false;
    if (this.data.length > 0) {
      this.populateForm(this.data);
    }
  }

  clearMessages() {
    this.errorMessage = '';
    this.successMessage = '';
  }

  markFormGroupTouched(formGroup: FormGroup) {
    Object.values(formGroup.controls).forEach(control => {
      control.markAsTouched();
      if (control instanceof FormGroup) {
        this.markFormGroupTouched(control);
      } else if (control instanceof FormArray) {
        control.controls.forEach(arrayControl => {
          if (arrayControl instanceof FormGroup) {
            this.markFormGroupTouched(arrayControl);
          }
        });
      }
    });
  }

  ngOnInit(): void {
    this.id = new URLSearchParams(window.location.search).get('id');
    if (this.id) {
      this.fetchData(Number(this.id));
    }
  }
}
