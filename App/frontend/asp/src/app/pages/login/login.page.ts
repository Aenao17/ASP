import { Component, OnInit } from '@angular/core';
import {StorageService} from "../../services/storage.service";
import {AuthService} from "../../services/auth.service";
import {Router} from "@angular/router";
import {NavController} from "@ionic/angular";

@Component({
  selector: 'app-login',
  templateUrl: './login.page.html',
  styleUrls: ['./login.page.scss'],
  standalone:false
})
export class LoginPage implements OnInit {
  username: string = '';
  password: string = '';
  errorMessage: string = '';

  constructor(
    private auth: AuthService,
    private storage: StorageService,
    private router: Router,
    private navCtrl: NavController
  ) { }

  ngOnInit() {
    console.log(this.username);
  }

  async login() {
    try {
      const response = await this.auth.login(this.username, this.password) as any;
      await this.storage.set("_token", response.token);
      console
      this.navCtrl.navigateRoot("/home");
    } catch (err) {
      console.error(err);
      this.errorMessage = 'Login failed. Please check your credentials and try again.';
    }
  }
}
