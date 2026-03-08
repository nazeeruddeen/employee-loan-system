import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TransacationStatementComponent } from './transacation-statement.component';

describe('TransacationStatementComponent', () => {
  let component: TransacationStatementComponent;
  let fixture: ComponentFixture<TransacationStatementComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TransacationStatementComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(TransacationStatementComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
