import { Injectable } from "@angular/core";
import { Storage } from "@ionic/storage-angular";
import {environment} from "../../environments/environment";
import {HttpClient} from "@angular/common/http";
import {StorageService} from "./storage.service";
import {Router} from "@angular/router";
import {NavController} from "@ionic/angular";

@Injectable({
  providedIn: 'root'
})

export class OfficeService {
  private apiUrl = `${environment.apiUrl}/office`;

  constructor(
    private http: HttpClient,
    private storage: StorageService,
    private router: Router,
    private navCtrl: NavController
  ) {}

  async getStorageUnitById(id: number): Promise<any> {
    try {
      return await this.http.get(`${this.apiUrl}/storage-unit/${id}`).toPromise();
    } catch (error: any) {
      console.error(`Error fetching storage unit with ID ${id}:`, error);
      throw error;
    }
  }

  async getRoot(): Promise<any> {
    try {
      return await this.http.get(`${this.apiUrl}/root`).toPromise();
    } catch (error: any) {
      console.error('Error fetching root storage:', error);
      throw error;
    }
  }

  async getItemByName(name: string): Promise<any> {
    try {
      return await this.http.get(`${this.apiUrl}/items/search/${name}`).toPromise();
    } catch (error: any) {
      console.error(`Error fetching item with name ${name}:`, error);
      throw error;
    }
  }

  async createStorageUnit(body: {name: string; parentId: number }) {
    return await this.http.post(`${this.apiUrl}/storage-unit`, body).toPromise();
  }

  async addItemToStorageUnit(unitId: number, name: string, quantity: number) {
    const body = { name, quantity };
    try {
      return await this.http.post(`${this.apiUrl}/storage-unit/${unitId}/item`, body).toPromise();
    } catch (error: any) {
      console.error(`Error adding item to storage unit ${unitId}:`, error);
      throw error;
    }
  }

  async deleteStorageUnit(unitId: number) {
    try {
      return await this.http.delete(`${this.apiUrl}/storage-unit/${unitId}`).toPromise();
    } catch (error: any) {
      console.error(`Error deleting storage unit ${unitId}:`, error);
      throw error;
    }
  }

  async deleteItem(id:number){
    try {
      return await this.http.delete(`${this.apiUrl}/item/${id}`).toPromise();
    } catch (error: any) {
      console.error(`Error deleting item with ID ${id}:`, error);
      throw error;
    }
  }

  async updateItemQuantity(id: number, quantity: number) {
    const body =  quantity;
    try {
      return await this.http.put(`${this.apiUrl}/item/${id}/quantity`,body).toPromise();
    } catch (error: any) {
      console.error(`Error updating item quantity for ID ${id}:`, error);
      throw error;
    }
  }

  async renameStorageUnit(id: number, newName: string) {
    const body = newName;
    try {
      return await this.http.put(`${this.apiUrl}/storage-unit/${id}/name`, body).toPromise();
    } catch (error: any) {
      console.error(`Error renaming storage unit with ID ${id}:`, error);
      throw error;
    }
  }
}
