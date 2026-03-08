import { HttpResponse } from '@angular/common/http';
import { Component } from '@angular/core';
import { EmployeeDataService } from '../employee-data.service';
import { SharedModule } from '../shared/shared.module';

@Component({
  selector: 'app-task-board',
  standalone: true,
  imports: [SharedModule],
  templateUrl: './task-board.component.html',
  styleUrl: './task-board.component.css'
})
export class TaskBoardComponent {
title = "write task board list here";
  displayedColumns: string[] | undefined;
  taskBoardData: any;
  types: any;
  docType: string[] = [];
  searchTerm : any;

  constructor(private employeeDataService: EmployeeDataService) {}

  typeList: any[] = [{ 'value': 'employee.pdf', 'key': 'pdf' }, { 'value': 'employee.docx', 'key': 'docx' }, { 'value': 'employee.txt', 'key': 'txt' }, { 'value': 'employee.xlsx', 'key': 'xlsx' }];
  ngOnInit() {
    this.displayedColumns = ['sno', 'fullname',   'dept', 'salary', 'age','empCode'];
    this.employeeDataService.getAllEmployeeData().subscribe((response: HttpResponse<any>) => {
      this.taskBoardData = response.body;
    })
  }
  filter(event: any) {
    this.docType.push(event.source.value);
  }
  // downloadData() {
  //   this.employeeDataService.ExportEmployees(this.docType.toString()).subscribe((response: HttpResponse<any>) => {
  //   })

  // }
  downloadData() {
    const types = Array.isArray(this.docType) ? this.docType : [];
    if (types.length === 0) return;
    types.forEach((type, index) => {
      this.employeeDataService.ExportEmployees(type).subscribe((response: HttpResponse<any>) => {
        const contentType = response.headers.get('Content-Type') || 'application/octet-stream';
        const blob = new Blob([response.body], { type: contentType });
  
        let fileName = 'Employee';
        const contentDisposition = response.headers.get('Content-Disposition');
        if (contentDisposition) {
          const filenameRegex = /filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/;
          const matches = filenameRegex.exec(contentDisposition);
          if (matches != null && matches[1]) {
            fileName = matches[1].replace(/['"]/g, '');
          }
        }
  
        setTimeout(() => {
          // Use different methods to save the file based on the browser
          if ((navigator as any).msSaveOrOpenBlob) { // For Internet Explorer
            (navigator as any).msSaveOrOpenBlob(blob, fileName);
          } else {
            const url = window.URL.createObjectURL(blob);
            const anchor = document.createElement('a');
            anchor.href = url;
            anchor.download = fileName;
            document.body.appendChild(anchor);
            anchor.click();
            document.body.removeChild(anchor);
            window.URL.revokeObjectURL(url);  // Clean up the URL object
          }
        }, 1000 * index);  // This will space out downloads by 1 second
      });
    });
  }
  
  
 
  

  searchEmployee(data: string){
    if (data == '') {
      this.employeeDataService.getAllEmployeeData().subscribe((response: HttpResponse<any>) => {
        this.taskBoardData = response.body;
      })
    } else {
      this.employeeDataService.searchEmployee(data).subscribe((response: HttpResponse<any>) => {
        this.taskBoardData = response.body;
      })
    }
  }
  filterEmployee(data: string) {
    if (data == '') {
      this.employeeDataService.getAllEmployeeData().subscribe((response: HttpResponse<any>) => {
        this.taskBoardData = response.body;
      })
    } else {
      this.employeeDataService.searchEmployee(data).subscribe((response: HttpResponse<any>) => {
        this.taskBoardData = response.body;
      })
    }

  }
  ngOnDestroy() {

  }

  
}
