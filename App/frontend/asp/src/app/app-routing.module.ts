import { NgModule } from '@angular/core';
import { PreloadAllModules, RouterModule, Routes } from '@angular/router';
import {AuthGuard} from "./guards/auth.guard";
import {LoginGuard} from "./guards/login.guard";

const routes: Routes = [
  {
    path: 'home',
    loadChildren: () => import('./pages/home/home.module').then(m => m.HomePageModule),
    canActivate: [AuthGuard]
  },
  {
    path: '',
    redirectTo: 'home',
    pathMatch: 'full'
  },
  {
    path: 'signup',
    loadChildren: () => import('./pages/signup/signup.module').then( m => m.SignupPageModule),
    canActivate: [LoginGuard]
  },
  {
    path: 'login',
    loadChildren: () => import('./pages/login/login.module').then( m => m.LoginPageModule),
    canActivate: [LoginGuard]
  },
  {
    path: 'admin',
    loadChildren: () => import('./pages/admin/admin.module').then( m => m.AdminPageModule),
    canActivate: [AuthGuard]
  },
  {
    path: 'volunteers',
    loadChildren: () => import('./pages/volunteers/volunteers.module').then( m => m.VolunteersPageModule),
    canActivate: [AuthGuard]
  },
  {
    path: 'task',
    loadChildren: () => import('./pages/task/task.module').then( m => m.TaskPageModule),
    canActivate: [AuthGuard]
  },
  {
    path: 'office',
    loadChildren: () => import('./pages/office/office.module').then( m => m.OfficePageModule),
    canActivate: [AuthGuard]
  },
];

@NgModule({
  imports: [
    RouterModule.forRoot(routes, { preloadingStrategy: PreloadAllModules })
  ],
  exports: [RouterModule]
})
export class AppRoutingModule { }
