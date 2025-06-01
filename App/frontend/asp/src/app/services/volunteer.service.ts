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
export class VolunteerService {
  private apiUrl = `${environment.apiUrl}/volunteers`;

  constructor(
    private http: HttpClient,
    private storage: StorageService,
    private router: Router,
    private navCtrl: NavController
  ) {}

  async getVolunteers(): Promise<any> {
    try {
      return await lastValueFrom(this.http.get(`${this.apiUrl}`));
    } catch (error: any) {
      if(error.status !=200) {
        console.error('Error fetching volunteers:', error);
        throw error;
      }
    }
  }

  async addVolunteer(volunteerData: any): Promise<any> {
    try {
      //format birthday to DD-MM-YYYY
      const date = new Date(volunteerData.birthday);
      const day = String(date.getDate()).padStart(2, '0');
      const month = String(date.getMonth() + 1).padStart(2, '0'); // Months are zero-based
      const year = date.getFullYear();
      volunteerData.birthday = `${day}-${month}-${year}`.toString();
      console.log('Formatted birthday:', volunteerData.birthday);
      const body = {
        usernameLinked: volunteerData.usernameLinked,
        points: volunteerData.points.toString(),
        birthday: volunteerData.birthday,
        departament: volunteerData.departament.toString()
      };
      return await lastValueFrom(this.http.post(`${this.apiUrl}`, body));
    } catch (error: any) {
      if(error.status !=200) {
        console.error('Error adding volunteer:', error);
        throw error;
      }
    }
  }

  async updateVolunteer(username: string, volunteerData: any): Promise<any> {
    try {
      const body = {
        usernameLinked: volunteerData.username,
        points: volunteerData.points.toString(),
        birthday: volunteerData.birthday,
        departament: volunteerData.departament.toString()
      }
      return await lastValueFrom(this.http.put(`${this.apiUrl}/${username}`, body));
    } catch (error: any) {
      if(error.status !=200) {
        console.error(`Error updating volunteer ${username}:`, error);
        throw error;
      }
    }
  }

  async deleteVolunteer(username: string): Promise<any> {
    try {
      return await lastValueFrom(this.http.delete(`${this.apiUrl}/${username}`));
    } catch (error: any) {
      if(error.status !=200) {
        console.error(`Error deleting volunteer ${username}:`, error);
        throw error;
      }
    }
  }

}
