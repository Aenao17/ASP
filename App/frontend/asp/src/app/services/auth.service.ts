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
    const body = {username, password, email, institutionalEmail, firstName, lastName, phoneNumber, role: 'USER'};
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

  async getUsername(): Promise<string> {
    try {
      const username = this.storage.get('username');
      return await username;
    } catch (error: any) {
      return 'USER';
    }
  }

  clear(): void {
    this.storage.clear();
  }

  async getUserRole(): Promise<string> {
    try{
      const response = await lastValueFrom(this.http.get(`${this.apiUrl}/role`));
      return response as string;
    }catch (error: any) {
      if (error.status === 200) {
        return error.error.text;
      } else {
        console.error('Error fetching user role:', error);
        throw error;
      }
    }

  }

  async logout(): Promise<void> {
    try {
      await lastValueFrom(this.http.post(`${this.apiUrl}/logout`, {}));
    } catch (err) {
      console.warn('Backend logout failed, proceeding anyway.');
    }

    // Always clear local tokens
    await this.storage.remove('_token');
    await this.storage.remove('username');
    await this.storage.remove('password');

    this.navCtrl.navigateRoot('/login');
  }
}
