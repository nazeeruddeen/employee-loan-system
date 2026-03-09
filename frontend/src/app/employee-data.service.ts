import { HttpClient, HttpResponse, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../environments/environment';
import { Transaction } from './loan/transacation-statement/transaction.model';

@Injectable({
  providedIn: 'root'
})
export class EmployeeDataService {
  hostUrl: string = environment.hostname?.trim?.() ? environment.hostname.trim() : 'http://localhost:8080';

  constructor(private httpClient: HttpClient) {}
  getAllEmployeeData() {

    return this.httpClient.get(this.hostUrl + `/employee/getAll`, { observe: 'response' });

  }
  saveEmployee(empData: any) {
    return this.httpClient.post(this.hostUrl + `/employee/saveEmp`, empData, { observe: 'response' });
  }
  updateEmployee(empData: any) {
    return this.httpClient.put(this.hostUrl + `/employee/updateEmp`, empData, { observe: 'response' });
  }

  searchEmployee(searchTerm: string) {
    return this.httpClient.get(this.hostUrl + `/employee/search/${searchTerm}`, { observe: 'response' });
  }

  sortingEmployee(property: string, orderType: string) {
 // return this.httpClient.get(this.hostUrl + `/employee/dataSorting?property=${property}?orderType=${orderType}`, { observe: 'response' });
  return this.httpClient.get(this.hostUrl + `/employee/dataSorting?property=${property}&orderType=${orderType}`, { observe: 'response' }); 
}
  filterRecords(data: any) {
   // return this.httpClient.get(this.hostUrl + `/employee/dataSorting?property=${property}&orderType=${orderType}`, { observe: 'response' }); 
    return this.httpClient.get(this.hostUrl + `/employee/searchFilters?filterType=${data.type}&empCode=${data.empCode}`, { observe: 'response' });

  }
  getEmployeeById(id: number) {
    return this.httpClient.get(this.hostUrl + `/employee/getByEmpId/${id}`, { observe: 'response' });
  }

  deleteEmployeeById(id: number) {
    return this.httpClient.delete(this.hostUrl + `/employee/deleteEmp/${id}`, { observe: 'response' });
  }
  // ExportEmployees(type: string) {
  //   return this.httpClient.get(this.hostUrl + `/employee/factoryDesign/${type}`, { observe: 'response' });
  // }

  ExportEmployees(type: string): Observable<HttpResponse<Blob>> {
    return this.httpClient.get<Blob>(this.hostUrl + `/employee/factoryDesign/${type}`, { 
      responseType: 'blob' as 'json',
      observe: 'response'
    });
  }
  
  //Loan module
  getLoanTaskboardData() {
    return this.httpClient.get(this.hostUrl + `/loans/loanTaskboard`, { observe: 'response' });
  }
  saveLoanApplication(data: any) {
    return this.httpClient.post(this.hostUrl + `/loans/applyLoan`, data, { observe: 'response' });
  }
  getOverviewDetails(appId: any) {
    return this.httpClient.get(this.hostUrl + `/loans/getOverviewDeatils/${appId}`, { observe: 'response' });
  }
  getProductDetails(appId: any) {
    return this.httpClient.get(this.hostUrl + `/loans/getProductDetails/${appId}`, { observe: 'response' });
  }
  getCompanyDetails(appId: any) {
    return this.httpClient.get(this.hostUrl + `/loans/getCompanyDetails/${appId}`, { observe: 'response' });
  }
  getCompanyAddress(appId: any) {
    return this.httpClient.get(this.hostUrl + `/loans/getCompanyAddress/${appId}`, { observe: 'response' });
  }


  editProductDetails(appId: any, data: any) {
    return this.httpClient.post(this.hostUrl + `/loans/saveProductDetails/${appId}`, data, { observe: 'response' });
  }

  saveCompanyDetails(appId: any, data: any) {
    return this.httpClient.post(this.hostUrl + `/loans/saveCompanyDetails/${appId}`, data, { observe: 'response' });
  }
  saveCompanyAddress(appId: any, data: any) {
    return this.httpClient.post(this.hostUrl + `/loans/saveCompanyAddress/${appId}`, data, { observe: 'response' });
  }
  saveAssuranceDetails(appId: any, data: any) {
    return this.httpClient.post(this.hostUrl + `/loans/saveJsonfileData/${appId}`, data, { observe: 'response' });
  }

  getFilteredTransactions(appid: number, statusList: string[], instrumentList: string[]): Observable<any> {
    let params = new HttpParams();

    statusList.forEach((status) => {
      params = params.append('statusList', status);
    });

    instrumentList.forEach((instrument) => {
      params = params.append('instrumentList', instrument);
    });

    // Backward compatibility for older backend contracts.
    const legacyValues = [...statusList, ...instrumentList];
    if (legacyValues.length > 0) {
      params = params.append('statusOrInstrument', 'all');
      legacyValues.forEach((value) => {
        params = params.append('statusOrInstrumentTypesList', value);
      });
    }

    const endpoint = `${this.hostUrl}/loans/filtertransactions/${appid}`;
    return this.httpClient.get(endpoint, { params });
  }

  getTransactions(appId: any, duration?: string, startDate?: string, endDate?: string): Observable<Transaction[]> {
    let params = new HttpParams();
  
    // Only add the duration to params if it is provided
    if (duration) {
      params = params.set('duration', duration);
    }
  
    // Add startDate and endDate to params only if they are provided
    if (startDate) {
      params = params.set('startDate', startDate);
    }
    if (endDate) {
      params = params.set('endDate', endDate);
    }
  
    // The URL with the appId as a path variable
    const url = `${this.hostUrl}/loans/fetchtransactions/${appId}`;
    
    // Make an HTTP GET request with the query parameters
    return this.httpClient.get<Transaction[]>(url, { params: params });
  }

  updateTransaction(transactionData: any): Observable<any> {
    return this.httpClient.put(this.hostUrl + `/loans/updateTransaction`, transactionData, { observe: 'response' });
  }
}

