import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TransactionfiltersComponent } from './transactionfilters.component';

describe('TransactionfiltersComponent', () => {
  let component: TransactionfiltersComponent;
  let fixture: ComponentFixture<TransactionfiltersComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TransactionfiltersComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(TransactionfiltersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
