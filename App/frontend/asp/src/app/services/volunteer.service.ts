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
      console.error('Error fetching volunteers:', error);
      throw error;
    }
  }

  async addVolunteer(volunteerData: any): Promise<any> {
    try {

      //format birthday to timestamp
      if (volunteerData.birthday) {
        const date = new Date(volunteerData.birthday);
        volunteerData.birthday = date.getTime();
      }
      console.log(volunteerData.birthday);
      const body = {
        usernameLinked: volunteerData.usernameLinked,
        points: volunteerData.points,
        birthday: volunteerData.birthday,
        departament: volunteerData.departament
      };
      return await lastValueFrom(this.http.post(`${this.apiUrl}`, body));
    } catch (error: any) {
      console.error('Error adding volunteer:', error);
      throw error;
    }
  }
}
