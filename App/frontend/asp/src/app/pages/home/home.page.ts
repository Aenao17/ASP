import { Component } from '@angular/core';
import { AuthService } from "../../services/auth.service";
import { Router } from "@angular/router";

@Component({
  selector: 'app-home',
  templateUrl: 'home.page.html',
  styleUrls: ['home.page.scss'],
  standalone: false,
})
export class HomePage {
  shrinkLogo = false;
  showNavBar = false;
  username = '';
  isAdmin = false;
  isCD = false;


  constructor(private auth: AuthService, private router: Router) {}

  ngOnInit() {
    this.getUsername();
    // Only animate the logo shrinking, don’t hide anything else
    setTimeout(() => {
      this.shrinkLogo = true;
      this.showNavBar = true;
    }, 3000);
    this.getUserRole();
  }

  async getUsername() {
    this.username =  await this.auth.getUsername();
  }

  openVolunteersPage() {
    this.router.navigate(['/volunteers']);
  }

  openTaskPage() {
    this.router.navigate(['/task']);
  }

  openOfficePage() {
    this.router.navigate(['/office']);
  }

  openAdminPage() {
    if (this.isAdmin) {
      this.router.navigate(['/admin']);
    } else {
      console.error('Access denied: User is not an administrator.');
    }
  }


  private async getUserRole(){
    const role = await this.auth.getUserRole();
    if (role=="ADMINISTRATOR"){
      this.isAdmin=true;
    }
    if (role=="CD" || role=="ADMINISTRATOR"){
      this.isCD=true;
    }
  }

  logout() {
    this.auth.logout().then(() => {
      this.router.navigate(['/login']);
    }).catch(err => {
      console.error('Logout failed:', err);
    });
  }
}
