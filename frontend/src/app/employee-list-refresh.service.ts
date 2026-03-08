import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class EmployeeListRefreshService {
  private refresh$ = new Subject<void>();

  requestRefresh() {
    this.refresh$.next();
  }

  onRefresh() {
    return this.refresh$.asObservable();
  }
}
