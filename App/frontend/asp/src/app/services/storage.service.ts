import { Injectable } from "@angular/core";
import { Storage } from "@ionic/storage-angular";

@Injectable({
  providedIn: 'root'
})
export class StorageService {
  private _storage: Storage | null = null;

  constructor(private storage: Storage) {
    this.init();
  }

  async init() {
    if (!this._storage) {
      this._storage = await this.storage.create();
    }
  }

  private async ensureInitialized() {
    if (!this._storage) {
      await this.init();
    }
  }

  public async set(key: string, value: any) {
    await this.ensureInitialized();
    return this._storage!.set(key, value);
  }

  public async get(key: string) {
    await this.ensureInitialized();
    return this._storage!.get(key);
  }

  public async remove(key: string) {
    await this.ensureInitialized();
    return this._storage!.remove(key);
  }
}
