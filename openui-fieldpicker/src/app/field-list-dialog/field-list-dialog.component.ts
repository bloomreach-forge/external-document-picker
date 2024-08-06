/**
 * Copyright 2020-2024 BloomReach Inc. (https://www.bloomreach.com/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import { Component, OnInit } from '@angular/core';
import * as Handlebars from 'handlebars';
import { CmsContextService } from '../cms-context.service';
import { PickerConfig } from '../picker-config';
import { Item } from '../item';

@Component({
  selector: 'app-field-list-dialog',
  templateUrl: './field-list-dialog.component.html',
  styleUrls: ['./field-list-dialog.component.scss']
})
export class FieldListDialogComponent implements OnInit {

  pickerConfig: PickerConfig;
  items: Item[];
  curItemId: string;
  newItemId: string;
  searchTerm = '';
  showSearchInput = true;
  searchOnInit = false;

  constructor(
    private cmsContextService: CmsContextService,
  ) { }

  async ngOnInit(): Promise<void> {
    const ui = await this.cmsContextService.getUiScope();
    if (ui) {
      const options = await ui.dialog.options();
      const { pickerConfig, id } = JSON.parse(options.value);
      this.pickerConfig = pickerConfig;
      this.showSearchInput = this.pickerConfig.showSearchInput ?? true;
      this.searchOnInit = this.pickerConfig.searchOnInit ?? false;
      this.searchTerm = this.pickerConfig.initSearchTerm ?? '';
      this.curItemId = id;

      if (this.searchOnInit) {
        await this.onSearch();
      }
    }
  }

  async onSearch(): Promise<void> {
    this.items = [];
    try {
      const template = Handlebars.compile(this.pickerConfig.findAllUrl);
      const url = template({ q: this.searchTerm });
      this.items = await this.cmsContextService.getHttpResource(url);
    } catch (e) {
      console.log('Failed to get http resource: ' + e);
    }
  }

  async onOK(): Promise<void> {
    const newItemIdValue = this.newItemId;
    const ui = await this.cmsContextService.getUiScope();
    if (ui) {
      if (newItemIdValue) {
        ui.dialog.close(newItemIdValue);
      } else {
        ui.dialog.cancel();
      }
    } else {
      console.log('OpenUI UiScope unavailable. Selected item ID:', newItemIdValue);
    }
  }

  async onCancel(): Promise<void> {
    const ui = await this.cmsContextService.getUiScope();
    if (ui) {
      ui.dialog.cancel();
    }
  }

  isCurrentSelectedItem(item: Item) {
    if (this.curItemId) {
      return this.curItemId === item.id;
    }
    return false;
  }

  onNewItemSelected(itemId: any) {
    this.newItemId = itemId;
  }
}
