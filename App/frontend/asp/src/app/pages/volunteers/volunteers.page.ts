import { Component, OnInit } from '@angular/core';
import { VolunteerService } from '../../services/volunteer.service';
import { NavController, AlertController } from '@ionic/angular';
import { Router } from "@angular/router";
import { AuthService } from "../../services/auth.service";

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
  userRole: string | null = null;
  displayOnlyPoints = false;
  points = 0;

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

  sortOption: string = 'name';

  constructor(
    private volunteerS: VolunteerService,
    private navCtrl: NavController,
    private alertCtrl: AlertController,
    private router: Router,
    private auth: AuthService
  ) {}

  async ngOnInit() {
    this.getRole().then(role => {
      this.userRole = role;
      if (this.userRole === 'USER' || this.userRole === 'VOLUNTEER' || this.userRole === 'PM') {
        this.displayOnlyPoints = true;
        this.getPoints();
      } else {
        this.getVolunteers();
      }
    });
    await this.sync();
  }

  async sync() {
    await this.volunteerS.syncVolunteers();
    await this.getVolunteers();
  }

  async getPoints() {
    try {
      const volunteer = await this.volunteerS.getVolunteerByUsername(await this.auth.getUsername());
      this.points = volunteer ? volunteer.points : 0;
    } catch (error) {
      console.error('Error fetching volunteer points:', error);
      this.showAlert('Error', 'Failed to fetch your points. Please try again later.');
    }
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
    let result = this.volunteers.filter(v => {
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

    switch (this.sortOption) {
      case 'name':
        result.sort((a, b) =>
          (a.firstName + a.lastName).localeCompare(b.firstName + b.lastName)
        );
        break;
      case 'username':
        result.sort((a, b) => a.username.localeCompare(b.username));
        break;
      case 'pointsAsc':
        result.sort((a, b) => a.points - b.points);
        break;
      case 'pointsDesc':
        result.sort((a, b) => b.points - a.points);
        break;
    }

    return result;
  }

  goHome() {
    this.router.navigate(['/home']);
  }

  async getRole() {
    try {
      const role = await this.auth.getUserRole();
      return role;
    } catch (error) {
      console.error('Error fetching user role:', error);
      return null;
    }
  }

  toggleAddForm() {
    this.isAddFormOpen = !this.isAddFormOpen;
    this.usernameError = false;
  }

  async addPointsToVolunteer(username: string) {
    const alert = await this.alertCtrl.create({
      header: 'Add Points',
      inputs: [
        {
          name: 'points',
          type: 'number',
          placeholder: 'Enter points to add'
        }
      ],
      buttons: [
        {
          text: 'Cancel',
          role: 'cancel'
        },
        {
          text: 'Add',
          handler: async (data) => {
            if (data.points && !isNaN(data.points)) {
              try {
                await this.volunteerS.addPointsToVolunteer(username, data.points);
                await this.getVolunteers();
              } catch (error) {
                console.error('Error adding points:', error);
                this.showAlert('Error', 'Failed to add points. Please try again.');
              }
            } else {
              this.showAlert('Invalid Input', 'Please enter a valid number of points.');
            }
          }
        }
      ]
    });
    await alert.present();
  }

  async addVolunteer() {
    this.usernameError = false;
    const { usernameLinked, points, birthday, departament } = this.newVolunteer;

    if (!usernameLinked || usernameLinked.trim() === '') {
      this.usernameError = true;
      this.showAlert('Input Error', 'Username is required.');
      return;
    }

    if (points == null || isNaN(points) || points < 0) {
      this.showAlert('Input Error', 'Please enter a valid non-negative number of points.');
      return;
    }

    if (!birthday || birthday.trim() === '') {
      this.showAlert('Input Error', 'Please select a birthday.');
      return;
    }

    if (!departament || departament.trim() === '') {
      this.showAlert('Input Error', 'Please choose a department.');
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
      this.showAlert('Add Error', 'Failed to add volunteer. Please try again.');
    } finally {
      this.isLoading = false;
    }
  }

  editVolunteer(volunteer: any) {
    this.selectedVolunteer = { ...volunteer };
    this.isEditModalOpen = true;
  }

  async saveVolunteer() {
    const { points, birthday, departament } = this.selectedVolunteer;

    if (points == null || isNaN(points) || points < 0) {
      this.showAlert('Input Error', 'Please enter a valid non-negative number of points.');
      return;
    }

    if (!birthday || birthday.trim() === '') {
      this.showAlert('Input Error', 'Please select a birthday.');
      return;
    }

    if (!departament || departament.trim() === '') {
      this.showAlert('Input Error', 'Please choose a department.');
      return;
    }

    try {
      this.selectedVolunteer.birthday = birthday.substring(0, 10).split("-").reverse().join("-");
      await this.volunteerS.updateVolunteer(this.selectedVolunteer.username, this.selectedVolunteer);
      this.isEditModalOpen = false;
      await this.getVolunteers();
    } catch (error) {
      console.error('Error updating volunteer:', error);
      this.showAlert('Update Error', 'Failed to update volunteer. Please try again.');
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
      this.showAlert('Delete Error', 'Failed to delete volunteer. Please try again.');
    }
  }

  closeEditModal() {
    this.isEditModalOpen = false;
    this.selectedVolunteer = null;
  }

  private async showAlert(header: string, message: string) {
    const alert = await this.alertCtrl.create({
      header,
      message,
      buttons: ['OK']
    });
    await alert.present();
  }
}
