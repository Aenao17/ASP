import {Injectable} from "@angular/core";
import {Router} from "@angular/router";
import {NavController} from "@ionic/angular";
import {StorageService} from "./storage.service";
import {lastValueFrom} from "rxjs";
import {HttpClient} from "@angular/common/http";
import {environment} from "../../environments/environment";

@Injectable({
  providedIn: 'root'
})
export class AuthService{
  private apiUrl = `${environment.apiUrl}/auth`;
  private username:string = '';
  private password:string = '';

  constructor(private http: HttpClient,
              private storage: StorageService,
              private router: Router,
              private navCtrl: NavController
  ) {
    setTimeout(()=>{
      this.storage.get('username').then((username) => {
        if (username) {
          this.username = username;
        }
      });

      this.storage.get('password').then((password) => {
        if (password) {
          this.password = password;
        }
      });
  }, 2000);
  }

  async signup(username: string, password: string, email: string, institutionalEmail: string, firstName: string, lastName: string, phoneNumber: string): Promise<any> {
    const body = {username, password, email, institutionalEmail, firstName, lastName, phoneNumber, role: 'ADMINISTRATOR'};
    const response = await lastValueFrom(this.http.post(`${this.apiUrl}/register`, body));
    this.username = username;
    this.password = password;
    this.storage.set('username', username);
    this.storage.set('password', password);
    return response;
  }

  login(username: string, password: string): Promise<any> {
    const body = {
      username,
      password
    };
    this.storage.set('username', username);
    return lastValueFrom(this.http.post(`${this.apiUrl}/login`, body));
  }

  async logout(): Promise<void> {
    await this.storage.remove('_token');
    await this.storage.remove('username');
    await this.storage.remove('password');
    lastValueFrom(this.http.post(`${this.apiUrl}/logout`, {})).catch(err => {
      console.error('Logout failed:', err);
    });
    this.navCtrl.navigateRoot('/login');
  }
}
