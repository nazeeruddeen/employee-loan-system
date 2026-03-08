import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AssurancedetailsComponent } from './assurancedetails.component';

describe('AssurancedetailsComponent', () => {
  let component: AssurancedetailsComponent;
  let fixture: ComponentFixture<AssurancedetailsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AssurancedetailsComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(AssurancedetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
