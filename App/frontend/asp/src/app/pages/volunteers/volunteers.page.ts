import { Component, OnInit } from '@angular/core';
import { VolunteerService } from '../../services/volunteer.service';
import { NavController } from '@ionic/angular';

@Component({
  selector: 'app-volunteers',
  templateUrl: './volunteers.page.html',
  styleUrls: ['./volunteers.page.scss'],
  standalone: false
})
export class VolunteersPage implements OnInit {
  volunteers: any[] = [];
  isAddFormOpen = false;

  newVolunteer = {
    usernameLinked: '',
    points: 0,
    birthday: '',
    departament: 'HR'
  };

  constructor(
    private volunteerS: VolunteerService,
    private navCtrl: NavController
  ) {}

  ngOnInit() {
    this.getVolunteers();
  }

  async getVolunteers() {
    try {
      const response = await this.volunteerS.getVolunteers();
      console.log(response);
      if (response) {
        this.volunteers = response;
      } else {
        console.error('No volunteers found or invalid response format:', response);
      }
    } catch (error) {
      console.error('Error fetching volunteers:', error);
    }
  }

  toggleAddForm() {
    this.isAddFormOpen = !this.isAddFormOpen;
  }

  async addVolunteer() {
    try {
      await this.volunteerS.addVolunteer(this.newVolunteer);
      this.newVolunteer = {
        usernameLinked: '',
        points: 0,
        birthday: '',
        departament: 'HR'
      };
      this.isAddFormOpen = false;
      this.getVolunteers();
    } catch (error) {
      console.error('Error adding volunteer:', error);
    }
  }
}
