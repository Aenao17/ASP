import { ComponentFixture, TestBed } from '@angular/core/testing';
import { VolunteersPage } from './volunteers.page';

describe('VolunteersPage', () => {
  let component: VolunteersPage;
  let fixture: ComponentFixture<VolunteersPage>;

  beforeEach(() => {
    fixture = TestBed.createComponent(VolunteersPage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
