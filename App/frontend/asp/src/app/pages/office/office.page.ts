import { Component, OnInit } from '@angular/core';
import { OfficeService } from '../../services/office.service';

@Component({
  selector: 'app-office',
  templateUrl: './office.page.html',
  styleUrls: ['./office.page.scss'],
  standalone: false
})
export class OfficePage implements OnInit{
  rootStorageUnit: any = null;
  currentStorageUnit: any = null;
  private history: any[] = [];

  constructor(private officeS: OfficeService) {}

  ngOnInit() {
    this.loadRootList();
  }

  private async loadRootList() {
    try {
      this.rootStorageUnit = await this.officeS.getRoot();
      this.currentStorageUnit = null;
      this.history = [];
    } catch (err) {
      console.error('Failed loading storage units', err);
    }
  }

  async displayStorageUnitDetails(unitId: number) {
    try {
      const details = await this.officeS.getStorageUnitById(unitId);
      if (!details) {
        console.error(`No data for unit ${unitId}`);
        return;
      }
      this.history.push(this.currentStorageUnit);
      this.currentStorageUnit = details;

    } catch (err) {
      console.error('Error fetching details for', unitId, err);
    }
  }

  goBack() {
    if (this.history.length > 0) {
      this.currentStorageUnit = this.history.pop()!;
    } else {
      this.loadRootList();
    }
  }

  get isRootView(): boolean {
    return this.currentStorageUnit === null;
  }
  get hasSubUnits(): boolean {
    return this.currentStorageUnit?.subUnits?.length > 0;
  }
  get hasItems(): boolean {
    return this.currentStorageUnit?.items?.length > 0;
  }
}
