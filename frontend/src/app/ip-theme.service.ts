import { Injectable } from '@angular/core';
import { environment } from '../environments/environment.dev'; 
@Injectable({
  providedIn: 'root'
})
export class IpThemeService {
  private darkModeEnabled = environment.production == true ? true : false;
 //private darkModeEnabled =true;
  constructor() { }

  setTheme() {
    const themeLink = document.querySelector('#theme-link');
    const toggleBtn = document.querySelector("#toggle-btn");

    console.log(toggleBtn?.textContent)
    if (themeLink) {
      themeLink.remove();
    }

    const linkElement = document.createElement('link');
    linkElement.id = 'theme-link';
    linkElement.rel = 'stylesheet';

    if (window.location.hostname === environment.hostname) {
      console.log("1")
      if (this.darkModeEnabled) {
        console.log("2")
        linkElement.href = 'assets/dark-theme.css';

      } else {
        console.log("3")
        linkElement.href = 'assets/light-theme.css';
      }

    } else {
      if (this.darkModeEnabled) {
        console.log("4")
        linkElement.href = 'assets/dark-theme.css';

      } else {
        console.log("5")
        linkElement.href = 'assets/light-theme.css';
      }
    }

    document.head.appendChild(linkElement);
  }
  toggleTheme() {
    this.darkModeEnabled = !this.darkModeEnabled;
    this.setTheme();
  }
  isDarkModeEnabled(): boolean {
    return this.darkModeEnabled;
  }
}
