import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {StorageService} from "./storage.service";
import {Router} from "@angular/router";
import {NavController} from "@ionic/angular";
import {lastValueFrom} from "rxjs";
import {environment} from "../../environments/environment";

@Injectable({
  providedIn: 'root'
})

export class TaskService {
  private apiUrl = `${environment.apiUrl}/tasks`;

  constructor(
    private http: HttpClient,
    private storage: StorageService,
    private router: Router,
    private navCtrl: NavController
  ) {}

  async getTasks(): Promise<any> {
    try {
      return await lastValueFrom(this.http.get(`${this.apiUrl}`));
    } catch (error: any) {
      console.error('Error fetching users:', error);
      throw error;
    }
  }

  async createTask(taskData: any): Promise<any> {
    try {
      return await lastValueFrom(this.http.post(`${this.apiUrl}`, taskData));
    } catch (error: any) {
      if(error.status ==401) {
        console.error('Error creating task:', error);
        throw error;
      }
    }
  }

  async assignTask(taskId: number, volunteerId: string): Promise<any> {
    try{
      await lastValueFrom(this.http.put(`${this.apiUrl}`, { taskId, volunteerId }));
    }catch (error: any) {
      if(error.status ==401) {
        console.error('Error assigning task:', error);
        throw error;
      }
    }
  }
}
