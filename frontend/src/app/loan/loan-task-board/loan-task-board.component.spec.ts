import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LoanTaskBoardComponent } from './loan-task-board.component';

describe('LoanTaskBoardComponent', () => {
  let component: LoanTaskBoardComponent;
  let fixture: ComponentFixture<LoanTaskBoardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LoanTaskBoardComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(LoanTaskBoardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
