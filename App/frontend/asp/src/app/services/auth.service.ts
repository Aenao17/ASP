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
  ) {}

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
}
