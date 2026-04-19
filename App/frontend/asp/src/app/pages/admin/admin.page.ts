import { Component, OnInit } from '@angular/core';
import { StorageService } from '../../services/storage.service';
import { Router } from '@angular/router';
import { NavController, AlertController } from '@ionic/angular';
import { UsersService } from '../../services/users.service';
import { AuthService } from '../../services/auth.service';

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
  isAdmin: boolean = false;

  searchTerm: string = '';

  constructor(
    private usersS: UsersService,
    private storage: StorageService,
    private router: Router,
    private navCtrl: NavController,
    private auth: AuthService,
    private alertCtrl: AlertController
  ) {}

  ngOnInit() {
    this.getRole();
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

  goHome() {
    this.navCtrl.navigateRoot('/home');
  }

  async getRole() {
    try {
      const role = await this.auth.getUserRole();
      this.isAdmin = role === 'ADMINISTRATOR';
    } catch (error) {
      console.error('Error fetching user role:', error);
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

  get filteredUsers() {
    if (!this.searchTerm.trim()) return this.users;

    return this.users.filter(user => {
      const fullName = `${user.firstName} ${user.lastName}`.toLowerCase();
      return (
        user.username?.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
        fullName.includes(this.searchTerm.toLowerCase())
      );
    });
  }

  editUser(user: any) {
    this.selectedUser = { ...user };
    this.isEditModalOpen = true;
  }

  async saveUser() {
    const { firstName, lastName, email, institutionalEmail, phoneNumber, role } = this.selectedUser;

    if (!firstName || firstName.trim() === '') {
      this.showAlert('Input Error', 'First name is required.');
      return;
    }

    if (!lastName || lastName.trim() === '') {
      this.showAlert('Input Error', 'Last name is required.');
      return;
    }

    if (!email || !this.validateEmail(email)) {
      this.showAlert('Input Error', 'A valid personal email is required.');
      return;
    }

    if (!institutionalEmail || !this.validateEmail(institutionalEmail)) {
      this.showAlert('Input Error', 'A valid institutional email is required.');
      return;
    }

    if (!phoneNumber || phoneNumber.trim().length < 6) {
      this.showAlert('Input Error', 'A valid phone number is required.');
      return;
    }

    if (!role || role.trim() === '') {
      this.showAlert('Input Error', 'User role must be selected.');
      return;
    }

    try {
      await this.usersS.updateUser(this.selectedUser.username, this.selectedUser);
      this.isEditModalOpen = false;
      this.getUsers();
    } catch (error) {
      console.error('Error updating user:', error);
      this.showAlert('Update Error', 'Failed to update user. Please try again.');
    }
  }

  async deleteUser(username: string) {
    if (confirm('Are you sure you want to delete this user?')) {
      try {
        await this.usersS.deleteUser(username);
        this.getUsers();
      } catch (error) {
        console.error('Error deleting user:', error);
        this.showAlert('Delete Error', 'Failed to delete user. Please try again.');
      }
    }
  }

  closeEditModal() {
    this.isEditModalOpen = false;
    this.selectedUser = null;
  }

  private async showAlert(header: string, message: string) {
    const alert = await this.alertCtrl.create({
      header,
      message,
      buttons: ['OK']
    });
    await alert.present();
  }

  private validateEmail(email: string): boolean {
    const pattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return pattern.test(email);
  }
}
