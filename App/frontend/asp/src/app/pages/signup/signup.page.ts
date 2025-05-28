import {Component, OnInit} from '@angular/core';
import {NavController} from "@ionic/angular";
import {Router} from "@angular/router";
import {HttpClient} from "@angular/common/http";
import {AuthService} from "../../services/auth.service";

@Component({
  selector: 'app-signup',
  templateUrl: './signup.page.html',
  standalone: false,
  styleUrls: ['./signup.page.scss']
})

export class SignupPage implements OnInit{
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
  ) { }

  ngOnInit() {
  }

  signUp():void{
    this.auth.signup(this.username, this.password,this.email, this.institutionalEmail, this.firstName, this.lastName, this.phoneNumber).then(() => {
      this.navCtrl.navigateRoot("/login");
    }).catch(() => {
      this.errorMessage = 'Signup failed. Please try again.';
    });
  }
}
