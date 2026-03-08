import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ThemeService {
  public isDarkTheme = new BehaviorSubject<boolean>(false);
  isDarkTheme$ = this.isDarkTheme.asObservable();

  toggleTheme() {
    this.isDarkTheme.next(!this.isDarkTheme.value);
  }

  /** Apply dark theme to body and overlay container (dialogs are in overlay) */
  setDarkThemeClass(isDark: boolean) {
    if (typeof document !== 'undefined') {
      document.body.classList.toggle('app-dark-theme', isDark);
      this.syncOverlayTheme(isDark);
    }
  }

  /** Sync theme to overlay container (created lazily when first dialog opens) */
  ensureOverlayTheme() {
    this.syncOverlayTheme(this.isDarkTheme.value);
    const check = () => {
      this.syncOverlayTheme(this.isDarkTheme.value);
    };
    setTimeout(check, 100);
    setTimeout(check, 500);
  }

  private syncOverlayTheme(isDark: boolean) {
    if (typeof document !== 'undefined') {
      const overlay = document.querySelector('.cdk-overlay-container');
      if (overlay) {
        overlay.classList.toggle('app-dark-theme', isDark);
      }
    }
  }
}