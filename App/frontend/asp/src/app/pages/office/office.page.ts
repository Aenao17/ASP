// src/app/office/office.page.ts
import { Component, OnInit } from '@angular/core';
import { OfficeService } from '../../services/office.service';
import { AlertController } from "@ionic/angular";

@Component({
  selector: 'app-office',
  templateUrl: './office.page.html',
  styleUrls: ['./office.page.scss'],
  standalone: false
})
export class OfficePage implements OnInit {
  rootStorageUnit: any = null;
  currentStorageUnit: any = null;

  // history of IDs (null = root)
  private history: (number | null)[] = [];

  // for search
  searchQuery: string = '';
  searchResults: any[] = [];
  searchInProgress = false;

  constructor(private officeS: OfficeService, private alertCtrl: AlertController) {}

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

  /** Navigate into a unit */
  async displayStorageUnitDetails(unitId: number) {
    try {
      const details = await this.officeS.getStorageUnitById(unitId);
      if (!details) {
        console.error(`No data for unit ${unitId}`);
        return;
      }
      this.history.push(this.currentStorageUnit ? this.currentStorageUnit.id : null);
      this.currentStorageUnit = details;
    } catch (err) {
      console.error('Error fetching details for', unitId, err);
    }
  }

  /** Navigate back one level, reloading from the backend */
  async goBack() {
    if (this.history.length > 0) {
      const prevId = this.history.pop()!;
      if (prevId === null) {
        await this.loadRootList();
      } else {
        try {
          this.currentStorageUnit = await this.officeS.getStorageUnitById(prevId);
        } catch (err) {
          console.error('Error reloading storage unit', prevId, err);
        }
      }
    } else {
      await this.loadRootList();
    }
  }

  get isRootView(): boolean {
    return this.currentStorageUnit === null;
  }
  get hasSubUnits(): boolean {
    return !!this.currentStorageUnit?.subUnits?.length;
  }
  get hasItems(): boolean {
    return !!this.currentStorageUnit?.items?.length;
  }

  // SEARCH
  async onSearchChange(event: CustomEvent) {
    const q = (event.detail.value || '').trim();
    this.searchQuery = q;
    if (!q) {
      this.searchResults = [];
      return;
    }
    this.searchInProgress = true;
    try {
      const results = await this.officeS.getItemByName(q);
      for (let item of results) {
        let location = '';
        let parentUnit = await this.officeS.getStorageUnitById(item.parent);
        location = parentUnit.name + ' / ';
        while (parentUnit.parent != null) {
          parentUnit = await this.officeS.getStorageUnitById(parentUnit.parent.id);
          location = parentUnit.name + ' / ' + location;
        }
        item.location = location.slice(0, -3);
      }
      this.searchResults = results;
    } catch (err) {
      console.error('Search error', err);
      this.searchResults = [];
    } finally {
      this.searchInProgress = false;
    }
  }

  onSearchCancel() {
    this.searchQuery = '';
    this.searchResults = [];
  }

  // ADD NEW UNIT
  async promptNewStorageUnit() {
    const parent = this.currentStorageUnit;
    if (!parent) {
      console.warn('Cannot add a sub-unit at root level');
      return;
    }

    const alert = await this.alertCtrl.create({
      header: 'New Storage Unit',
      inputs: [{ name: 'name', type: 'text', placeholder: 'e.g. Electronics Shelf' }],
      buttons: [
        { text: 'Cancel', role: 'cancel' },
        {
          text: 'Create',
          handler: async (data) => {
            const name = (data.name || '').trim();
            if (name) {
              await this.createStorageUnit(name, parent.id);
            }
          }
        }
      ]
    });
    await alert.present();
  }

  private async createStorageUnit(name: string, parentId: number) {
    try {
      const body = { name, parentId };
      const newUnit = await this.officeS.createStorageUnit(body);
      await this.displayStorageUnitDetails(parentId);
      this.history.pop();
    } catch (err) {
      console.error('Failed to create storage unit', err);
    }
  }

  // ADD NEW ITEM
  async promptNewItem() {
    const parent = this.currentStorageUnit;
    if (!parent) {
      console.warn('Cannot add an item at root level');
      return;
    }

    const alert = await this.alertCtrl.create({
      header: 'New Item',
      inputs: [
        { name: 'name', type: 'text', placeholder: 'Item name' },
        { name: 'quantity', type: 'number', placeholder: 'Quantity', min: 1 }
      ],
      buttons: [
        { text: 'Cancel', role: 'cancel' },
        {
          text: 'Add',
          handler: async (data) => {
            const name = (data.name || '').trim();
            const quantity = Number(data.quantity);
            if (name && quantity > 0) {
              await this.addItem(name, quantity, parent.id);
            }
          }
        }
      ]
    });
    await alert.present();
  }

  private async addItem(name: string, quantity: number, unitId: number) {
    try {
      await this.officeS.addItemToStorageUnit(unitId, name, quantity);
      // reload current unit to show the new item
      await this.displayStorageUnitDetails(unitId);
      this.history.pop();
    } catch (error) {
      console.error(`Error adding item to storage unit ${unitId}:`, error);
    }
  }

  async confirmDelete() {
    const alert = await this.alertCtrl.create({
      header: 'Delete Storage Unit',
      message: `Are you sure you want to delete "${this.currentStorageUnit.name}"? This cannot be undone.`,
      buttons: [
        { text: 'Cancel', role: 'cancel' },
        { text: 'Delete', role: 'destructive', handler: () => this.deleteCurrentUnit() }
      ]
    });
    await alert.present();
  }

  /**
   * Call service and navigate back on success
   */
  async deleteCurrentUnit() {
    try {
      await this.officeS.deleteStorageUnit(this.currentStorageUnit.id);
      this.goBack();
    } catch (error) {
      console.error('Error deleting storage unit', error);
    }
  }
}
