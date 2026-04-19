import { Component, OnInit } from '@angular/core';
import { NavController } from "@ionic/angular";
import { Router } from "@angular/router";
import { HttpClient } from "@angular/common/http";
import { AuthService } from "../../services/auth.service";

@Component({
  selector: 'app-signup',
  templateUrl: './signup.page.html',
  styleUrls: ['./signup.page.scss'],
  standalone: false
})
export class SignupPage implements OnInit {
  username: string = '';
  password: string = '';
  email: string = '';
  institutionalEmail: string = '';
  firstName: string = '';
  lastName: string = '';
  phoneNumber: string = '';
  errorMessage: string = '';

  constructor(
    private auth: AuthService,
    private navCtrl: NavController,
    private route: Router,
    private http: HttpClient
  ) {}

  ngOnInit() {}

  signUp(): void {
    // Trim all inputs
    this.username = this.username.trim();
    this.password = this.password.trim();
    this.email = this.email.trim();
    this.institutionalEmail = this.institutionalEmail.trim();
    this.firstName = this.firstName.trim();
    this.lastName = this.lastName.trim();
    this.phoneNumber = this.phoneNumber.trim();

    // Validate required fields
    if (!this.username || !this.password || !this.email || !this.institutionalEmail || !this.firstName || !this.lastName || !this.phoneNumber) {
      this.errorMessage = 'All fields are required.';
      return;
    }

    // Validate email format
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(this.email)) {
      this.errorMessage = 'Personal email format is invalid.';
      return;
    }

    if (!emailRegex.test(this.institutionalEmail)) {
      this.errorMessage = 'Institutional email format is invalid.';
      return;
    }


    // Validate phone number
    const phoneRegex = /^\+?[0-9]{6,15}$/;
    if (!phoneRegex.test(this.phoneNumber)) {
      this.errorMessage = 'Phone number is invalid. It should contain only digits and optionally start with +.';
      return;
    }

    // All validations passed
    this.errorMessage = '';

    this.auth.signup(
      this.username,
      this.password,
      this.email,
      this.institutionalEmail,
      this.firstName,
      this.lastName,
      this.phoneNumber
    ).then(() => {
      this.navCtrl.navigateRoot("/login");
    }).catch(() => {
      this.errorMessage = 'Signup failed. Please try again.';
    });
  }
}
