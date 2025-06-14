import { Component, OnInit } from '@angular/core';
import { VolunteerService } from '../../services/volunteer.service';
import { NavController, AlertController } from '@ionic/angular';

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
  usernameError = false;
  isLoading = false;

  searchTerm = '';
  selectedDepartment: string | null = null;
  pointsFilter = {
    operator: '>=',
    value: null as number | null
  };

  newVolunteer = {
    usernameLinked: '',
    points: 0,
    birthday: '',
    departament: 'HR'
  };

  constructor(
    private volunteerS: VolunteerService,
    private navCtrl: NavController,
    private alertCtrl: AlertController
  ) {}

  ngOnInit() {
    this.getVolunteers();
  }

  async sync() {
    await this.volunteerS.syncVolunteers();
    await this.getVolunteers();
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

  get filteredVolunteers() {
    return this.volunteers.filter(v => {
      const matchesSearch =
        this.searchTerm === '' ||
        v.firstName?.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
        v.lastName?.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
        v.username?.toLowerCase().includes(this.searchTerm.toLowerCase());

      const matchesDepartment =
        !this.selectedDepartment || v.departament === this.selectedDepartment;

      const matchesPoints =
        this.pointsFilter.value === null ||
        (this.pointsFilter.operator === '>=' && v.points >= this.pointsFilter.value) ||
        (this.pointsFilter.operator === '<=' && v.points <= this.pointsFilter.value) ||
        (this.pointsFilter.operator === '=' && v.points === this.pointsFilter.value);

      return matchesSearch && matchesDepartment && matchesPoints;
    });
  }

  toggleAddForm() {
    this.isAddFormOpen = !this.isAddFormOpen;
    this.usernameError = false;
  }

  async addVolunteer() {
    this.usernameError = false;

    if (!this.newVolunteer.usernameLinked || this.newVolunteer.usernameLinked.trim() === '') {
      this.usernameError = true;
      return;
    }

    this.isLoading = true;
    try {
      await this.volunteerS.addVolunteer(this.newVolunteer);
      this.newVolunteer = {
        usernameLinked: '',
        points: 0,
        birthday: '',
        departament: 'HR'
      };
      this.isAddFormOpen = false;
      await this.getVolunteers();
    } catch (error) {
      console.error('Error adding volunteer:', error);
      const alert = await this.alertCtrl.create({
        header: 'Add Error',
        message: 'Failed to add volunteer. Please try again.',
        buttons: ['OK']
      });
      await alert.present();
    } finally {
      this.isLoading = false;
    }
  }

  editVolunteer(volunteer: any) {
    this.selectedVolunteer = { ...volunteer };
    this.isEditModalOpen = true;
  }

  async saveVolunteer() {
    try {
      this.selectedVolunteer.birthday = this.selectedVolunteer.birthday?.substring(0, 10);
      await this.volunteerS.updateVolunteer(this.selectedVolunteer.username, this.selectedVolunteer);
      this.isEditModalOpen = false;
      await this.getVolunteers();
    } catch (error) {
      console.error('Error updating volunteer:', error);
      const alert = await this.alertCtrl.create({
        header: 'Update Error',
        message: 'Failed to update volunteer. Please try again.',
        buttons: ['OK']
      });
      await alert.present();
    }
  }

  async deleteVolunteer(username: string) {
    const confirmDelete = confirm('Are you sure you want to delete this volunteer?');
    if (!confirmDelete) return;

    try {
      await this.volunteerS.deleteVolunteer(username);
      await this.getVolunteers();
    } catch (error) {
      console.error('Error deleting volunteer:', error);
      const alert = await this.alertCtrl.create({
        header: 'Delete Error',
        message: 'Failed to delete volunteer. Please try again.',
        buttons: ['OK']
      });
      await alert.present();
    }
  }

  closeEditModal() {
    this.isEditModalOpen = false;
    this.selectedVolunteer = null;
  }
}
