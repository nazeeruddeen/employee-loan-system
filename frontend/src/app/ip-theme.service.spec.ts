import { TestBed } from '@angular/core/testing';

import { IpThemeService } from './ip-theme.service';

describe('IpThemeService', () => {
  let service: IpThemeService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(IpThemeService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
