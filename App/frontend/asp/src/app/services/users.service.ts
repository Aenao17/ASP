import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { HttpClient } from '@angular/common/http';
import { StorageService } from './storage.service';
import { Router } from '@angular/router';
import { NavController } from '@ionic/angular';
import { lastValueFrom } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class UsersService {
  private apiUrl = `${environment.apiUrl}/users`;

  constructor(
    private http: HttpClient,
    private storage: StorageService,
    private router: Router,
    private navCtrl: NavController
  ) {}

  async getUsers(): Promise<any> {
    try {
      return await lastValueFrom(this.http.get(`${this.apiUrl}`));
    } catch (error: any) {
      console.error('Error fetching users:', error);
      throw error;
    }
  }

  async updateUser(username: string, userData: any): Promise<any> {
    try {
      const body={
        username: userData.username,
        email: userData.email,
        institutionalEmail: userData.institutionalEmail,
        firstName: userData.firstName,
        lastName: userData.lastName,
        phoneNumber: userData.phoneNumber,
        role: userData.role
      }
      return await lastValueFrom(this.http.put(`${this.apiUrl}/${username}`, body));
    } catch (error: any) {
      console.error(`Error updating user ${username}:`, error);
      throw error;
    }
  }

  async deleteUser(username: string): Promise<any> {
    try {
      return await lastValueFrom(this.http.delete(`${this.apiUrl}/${username}`));
    } catch (error: any) {
      console.error(`Error deleting user ${username}:`, error);
      throw error;
    }
  }

  async getRoles(): Promise<any> {
    try {
      return await lastValueFrom(this.http.get(`${this.apiUrl}/roles`));
    } catch (error: any) {
      console.error('Error fetching roles:', error);
      throw error;
    }
  }
}
