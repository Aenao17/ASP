import { Component, OnInit } from '@angular/core';
import {StorageService} from "../../services/storage.service";
import {Router} from "@angular/router";
import {NavController} from "@ionic/angular";
import {UsersService} from "../../services/users.service";

@Component({
  selector: 'app-admin',
  templateUrl: './admin.page.html',
  styleUrls: ['./admin.page.scss'],
  standalone: false
})

export class AdminPage implements OnInit {
  users: any[] = [];

  constructor(
    private usersS: UsersService,
    private storage: StorageService,
    private router: Router,
    private navCtrl: NavController
  ) {
  }

  ngOnInit() {
    this.getUsers();
  }

  async getUsers() {
    const response = await this.usersS.getUsers();
    if (response && response.users) {
      this.users = response.users;
    } else {
      console.error('No users found or invalid response format:', response);
    }
  }

}
