import { Component, OnInit } from '@angular/core';
import { StorageService } from '../../services/storage.service';
import { Router } from '@angular/router';
import { NavController } from '@ionic/angular';
import { UsersService } from '../../services/users.service';

@Component({
  selector: 'app-admin',
  templateUrl: './admin.page.html',
  styleUrls: ['./admin.page.scss'],
  standalone: false
})
export class AdminPage implements OnInit {
  users: any[] = [];
  selectedUser: any = null;
  isEditModalOpen = false;
  roles: any[] = [];

  constructor(
    private usersS: UsersService,
    private storage: StorageService,
    private router: Router,
    private navCtrl: NavController
  ) {}

  ngOnInit() {
    this.getUsers();
    this.getRoles();
  }

  async getRoles() {
    try {
      const response = await this.usersS.getRoles();
      if (response) {
        this.roles = response;
      } else {
        console.error('No roles found or invalid response format:', response);
      }
    } catch (error) {
      console.error('Error fetching roles:', error);
    }
  }

  async getUsers() {
    try {
      const response = await this.usersS.getUsers();
      if (response) {
        this.users = response;
      } else {
        console.error('No users found or invalid response format:', response);
      }
    } catch (error) {
      console.error('Error fetching users:', error);
    }
  }

  editUser(user: any) {
    this.selectedUser = { ...user };
    this.isEditModalOpen = true;
  }

  async saveUser() {
    try {
      await this.usersS.updateUser(this.selectedUser.username, this.selectedUser);
      this.isEditModalOpen = false;
      this.getUsers();
    } catch (error) {
      console.error('Error updating user:', error);
    }
  }

  async deleteUser(username: string) {
    if (confirm('Are you sure you want to delete this user?')) {
      try {
        console.log(username);
        await this.usersS.deleteUser(username);
        this.getUsers();
      } catch (error) {
        console.error('Error deleting user:', error);
      }
    }
  }

  closeEditModal() {
    this.isEditModalOpen = false;
    this.selectedUser = null;
  }
}
