import { Component } from '@angular/core';
import {AuthService} from "../../services/auth.service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-home',
  templateUrl: 'home.page.html',
  styleUrls: ['home.page.scss'],
  standalone: false,
})
export class HomePage {

  shrinkLogo = false;
  showNavBar = false;

  constructor(private auth: AuthService, private router: Router) {
    // Constructor logic if needed
  }

  ngOnInit() {
    setTimeout(() => {
      this.shrinkLogo = true;
      setTimeout(() => {
        this.showNavBar = true;
      }, 1000); // așteptăm ca shrink-ul să se termine
    }, 5000); // 5 secunde inițiale pe ecran
  }

  logout() {
    this.auth.logout().then(() => {
      this.router.navigate(['/login']);
    }).catch(err => {
      console.error('Logout failed:', err);
    });
  }
}
