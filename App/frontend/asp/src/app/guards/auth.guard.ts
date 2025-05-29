import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot, UrlTree } from '@angular/router';
import { StorageService } from '../services/storage.service';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard  {
  constructor(
    private storage: StorageService,
    private router: Router
  ) { }

  async canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Promise<boolean | UrlTree> {
    let isAuthenticated = false;

    const token = await this.storage.get('_token');

    if (token != null) {
      isAuthenticated = true;
    }

    if (!isAuthenticated) {
      return this.router.parseUrl('/login');
    }
    return true;
  }
}
