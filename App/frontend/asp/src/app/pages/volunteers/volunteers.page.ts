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
  selectedVolunteer: any = null;
  isEditModalOpen = false;
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
    } catch (error) {
      console.error('Error adding volunteer:', error);
    }
    this.getVolunteers();
  }

  editVolunteer(volunteer: any) {
    this.selectedVolunteer = { ...volunteer };
    this.isEditModalOpen = true;
  }

  async saveVolunteer() {
    try {
      //take only first 10 characters of birthday
      this.selectedVolunteer.birthday = this.selectedVolunteer.birthday.substring(0, 10).split('-').reverse().join('-');

      await this.volunteerS.updateVolunteer(this.selectedVolunteer.username, this.selectedVolunteer);
      this.isEditModalOpen = false;
      this.getVolunteers();
    } catch (error) {
      console.error('Error updating volunteer:', error);
    }
    this.getVolunteers();
  }

  async deleteVolunteer(username: string) {
    if (confirm('Are you sure you want to delete this volunteer?')) {
      try {
        await this.volunteerS.deleteVolunteer(username);
        this.getVolunteers();
      } catch (error) {
        console.error('Error deleting volunteer:', error);
      }
      this.getVolunteers();
    }
  }

  closeEditModal() {
    this.isEditModalOpen = false;
    this.selectedVolunteer = null;
  }
}
