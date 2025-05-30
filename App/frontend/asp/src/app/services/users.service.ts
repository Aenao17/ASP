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

  async updateUser(userId: number, userData: any): Promise<any> {
    try {
      return await lastValueFrom(this.http.put(`${this.apiUrl}/${userId}`, userData));
    } catch (error: any) {
      console.error(`Error updating user ${userId}:`, error);
      throw error;
    }
  }

  async deleteUser(userId: number): Promise<any> {
    try {
      return await lastValueFrom(this.http.delete(`${this.apiUrl}/${userId}`));
    } catch (error: any) {
      console.error(`Error deleting user ${userId}:`, error);
      throw error;
    }
  }
}
