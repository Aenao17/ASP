import { Component, OnInit } from '@angular/core';
import { AlertController, ActionSheetController } from '@ionic/angular';
import { OfficeService } from '../../services/office.service';
import { Router } from "@angular/router";
import { AuthService } from "../../services/auth.service";

@Component({
  selector: 'app-office',
  templateUrl: './office.page.html',
  styleUrls: ['./office.page.scss'],
  standalone: false
})
export class OfficePage implements OnInit {
  rootStorageUnit: any = null;
  currentStorageUnit: any = null;
  private history: (number | null)[] = [];
  isUser: boolean = false;

  searchQuery: string = '';
  searchResults: any[] = [];
  searchInProgress = false;

  constructor(
    private officeS: OfficeService,
    private alertCtrl: AlertController,
    private actionSheetCtrl: ActionSheetController,
    private router: Router,
    private auth: AuthService
  ) {}

  ngOnInit() {
    this.loadRootList();
    this.getRole();
  }

  private async getRole() {
    try {
      const role = await this.auth.getUserRole();
      this.isUser = ['USER', 'VOLUNTEER', 'PM'].includes(role);
    } catch (err) {
      console.error('Error fetching user role', err);
      this.isUser = true;
    }
  }

  private async loadRootList() {
    try {
      this.rootStorageUnit = await this.officeS.getRoot();
      this.currentStorageUnit = null;
      this.history = [];
    } catch (err) {
      this.showAlert('Error', 'Failed to load root storage unit.');
    }
  }

  async displayStorageUnitDetails(unitId: number) {
    try {
      const details = await this.officeS.getStorageUnitById(unitId);
      if (!details) return;
      this.history.push(this.currentStorageUnit ? this.currentStorageUnit.id : null);
      this.currentStorageUnit = details;
    } catch (err) {
      this.showAlert('Error', `Could not load details for unit ID ${unitId}.`);
    }
  }

  async goBack() {
    if (this.history.length > 0) {
      const prevId = this.history.pop()!;
      if (prevId === null) {
        await this.loadRootList();
      } else {
        try {
          this.currentStorageUnit = await this.officeS.getStorageUnitById(prevId);
        } catch (err) {
          this.showAlert('Error', 'Could not go back to previous storage unit.');
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

  async presentActionSheet() {
    const sheet = await this.actionSheetCtrl.create({
      header: 'Actions',
      buttons: [
        {
          text: '+ Add Item',
          icon: 'add',
          handler: () => this.promptNewItem()
        },
        {
          text: '+ Add Storage Unit',
          icon: 'folder-open',
          handler: () => this.promptNewStorageUnit()
        },
        {
          text: '↺ Rename Current Unit',
          icon: 'pencil',
          handler: () => this.promptRenameStorageUnit()
        },
        {
          text: '- Delete Current Unit',
          icon: 'trash',
          role: 'destructive',
          handler: () => this.confirmDelete()
        },
        { text: 'Cancel', icon: 'close', role: 'cancel' }
      ]
    });
    await sheet.present();
  }

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
      this.showAlert('Search Error', 'Could not perform search.');
    } finally {
      this.searchInProgress = false;
    }
  }

  onSearchCancel() {
    this.searchQuery = '';
    this.searchResults = [];
  }

  goHome() {
    this.router.navigate(['/home']);
  }

  async promptNewStorageUnit() {
    if (!this.currentStorageUnit) return;
    const alert = await this.alertCtrl.create({
      header: 'New Storage Unit',
      inputs: [{ name: 'name', type: 'text', placeholder: 'e.g. Cutie' }],
      buttons: [
        { text: 'Cancel', role: 'cancel' },
        {
          text: 'Create',
          handler: async data => {
            const name = (data.name || '').trim();
            if (!name) {
              this.showAlert('Validation Error', 'Unit name cannot be empty.');
              return false;
            }
            await this.createStorageUnit(name, this.currentStorageUnit.id);
            return true;
          }
        }
      ]
    });
    await alert.present();
  }

  private async createStorageUnit(name: string, parentId: number) {
    try {
      await this.officeS.createStorageUnit({ name, parentId });
      await this.displayStorageUnitDetails(parentId);
      this.history.pop();
    } catch (err) {
      this.showAlert('Error', 'Failed to create storage unit.');
    }
  }

  async promptNewItem() {
    if (!this.currentStorageUnit) return;
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
          handler: async data => {
            const name = (data.name || '').trim();
            const qty = Number(data.quantity);
            if (!name || isNaN(qty) || qty <= 0) {
              this.showAlert('Validation Error', 'Provide a valid name and quantity (> 0).');
              return false;
            }
            await this.addItem(name, qty, this.currentStorageUnit.id);
            return true;
          }
        }
      ]
    });
    await alert.present();
  }

  private async addItem(name: string, quantity: number, unitId: number) {
    try {
      await this.officeS.addItemToStorageUnit(unitId, name, quantity);
      await this.displayStorageUnitDetails(unitId);
      this.history.pop();
    } catch (err) {
      this.showAlert('Error', 'Failed to add item.');
    }
  }

  async promptEditItem(item: any) {
    const alert = await this.alertCtrl.create({
      header: 'Edit Quantity',
      inputs: [
        {
          name: 'quantity',
          type: 'number',
          min: 1,
          value: item.quantity,
          placeholder: 'New quantity'
        }
      ],
      buttons: [
        { text: 'Cancel', role: 'cancel' },
        {
          text: 'Save',
          handler: async data => {
            const newQty = Number(data.quantity);
            if (isNaN(newQty) || newQty <= 0) {
              this.showAlert('Validation Error', 'Quantity must be a number > 0.');
              return false;
            }
            if (newQty !== item.quantity) {
              await this.updateItemQuantity(item.id, newQty);
            }
            return true;
          }
        }
      ]
    });
    await alert.present();
  }

  private async updateItemQuantity(id: number, quantity: number) {
    try {
      await this.officeS.updateItemQuantity(id, quantity);
      await this.displayStorageUnitDetails(this.currentStorageUnit!.id);
      this.history.pop();
    } catch (error) {
      this.showAlert('Error', 'Failed to update item.');
    }
  }

  async confirmDeleteItem(item: any) {
    const alert = await this.alertCtrl.create({
      header: 'Delete Item',
      message: `Delete “${item.name}”? This cannot be undone.`,
      buttons: [
        { text: 'Cancel', role: 'cancel' },
        {
          text: 'Delete',
          role: 'destructive',
          handler: () => this.deleteItem(item.id)
        }
      ]
    });
    await alert.present();
  }

  private async deleteItem(id: number) {
    try {
      await this.officeS.deleteItem(id);
      await this.displayStorageUnitDetails(this.currentStorageUnit!.id);
      this.history.pop();
    } catch (error) {
      this.showAlert('Error', 'Failed to delete item.');
    }
  }

  async confirmDelete() {
    const alert = await this.alertCtrl.create({
      header: 'Delete Storage Unit',
      message: `Are you sure you want to delete "${this.currentStorageUnit?.name}"? This cannot be undone.`,
      buttons: [
        { text: 'Cancel', role: 'cancel' },
        {
          text: 'Delete',
          role: 'destructive',
          handler: () => this.deleteCurrentUnit()
        }
      ]
    });
    await alert.present();
  }

  private async deleteCurrentUnit() {
    try {
      await this.officeS.deleteStorageUnit(this.currentStorageUnit!.id);
      this.goBack();
    } catch (error) {
      this.showAlert('Error', 'Failed to delete storage unit.');
    }
  }

  async promptRenameStorageUnit() {
    if (!this.currentStorageUnit) return;

    const alert = await this.alertCtrl.create({
      header: 'Rename Storage Unit',
      inputs: [
        {
          name: 'name',
          type: 'text',
          value: this.currentStorageUnit.name,
          placeholder: 'New storage unit name'
        }
      ],
      buttons: [
        { text: 'Cancel', role: 'cancel' },
        {
          text: 'Save',
          handler: async data => {
            const newName = (data.name || '').trim();
            if (!newName || newName === this.currentStorageUnit.name) {
              this.showAlert('Validation Error', 'Please enter a different, non-empty name.');
              return false;
            }
            await this.renameStorageUnit(this.currentStorageUnit.id, newName);
            return true;
          }
        }
      ]
    });

    await alert.present();
  }

  private async renameStorageUnit(id: number, newName: string) {
    try {
      await this.officeS.renameStorageUnit(id, newName);
      await this.displayStorageUnitDetails(id);
      this.history.pop();
    } catch (error) {
      this.showAlert('Error', 'Failed to rename storage unit.');
    }
  }

  private async showAlert(header: string, message: string) {
    const alert = await this.alertCtrl.create({
      header,
      message,
      buttons: ['OK']
    });
    await alert.present();
  }
}
