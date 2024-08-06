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
import { TranslateService } from '@ngx-translate/core';
import * as Handlebars from 'handlebars';
import { CmsContextService } from '../cms-context.service';
import { DialogSize, DocumentEditorMode } from '@bloomreach/ui-extension';
import { PickerConfig } from '../picker-config';
import { Item } from '../item';

@Component({
  selector: 'app-field-view',
  templateUrl: './field-view.component.html',
  styleUrls: ['./field-view.component.scss']
})
export class FieldViewComponent implements OnInit {

  pickerConfig: PickerConfig;
  mode: DocumentEditorMode;

  itemId: string;
  item: Item;
  compareItemId: string;
  compareItem: Item;

  constructor(
    private cmsContextService: CmsContextService,
    private translateService: TranslateService,
  ) { }

  async ngOnInit(): Promise<void> {
    const ui = await this.cmsContextService.getUiScope();
    if (ui) {
      this.pickerConfig = this.cmsContextService.getPickerConfig(ui);
      const docProps = await ui.document.get();
      this.mode = docProps.mode;

      this.itemId = await this.cmsContextService.getFieldValue();

      if (this.itemId) {
        this.item = await this.findItemById(this.pickerConfig, this.itemId);
      }

      if (DocumentEditorMode.Compare === this.mode) {
        this.compareItemId = await this.cmsContextService.getFieldCompareValue();
        if (this.compareItemId) {
          this.compareItem = await this.findItemById(this.pickerConfig, this.compareItemId);
        }
      }
    }
  }

  async onBrowse(): Promise<void> {
    try {
      const ui = await this.cmsContextService.getUiScope();
      const title = await this.translateService.get('ITEMS_DIALOG_TITLE').toPromise();
      const url = this.pickerConfig.treeViewMode ? './index.html?id=field-tree-dialog' : './index.html?id=field-list-dialog';
      const dialogOptions = {
        title,
        url,
        size: DialogSize.Medium,
        value: JSON.stringify({ pickerConfig: this.pickerConfig, id: this.itemId }),
      };
      const res: unknown = await ui.dialog.open(dialogOptions);
      const newItemId = res as string;
      if (newItemId && newItemId !== this.itemId) {
        ui.document.field.setValue(newItemId);
        this.itemId = newItemId;
        this.item = await this.findItemById(this.pickerConfig, newItemId);
      }
    } catch (error) {
      if (error.code === 'DialogCanceled') {
        return;
      }
      console.error('Error after open dialog: ', error.code, error.message);
    }
  }

  private async findItemById(pickerConfig: PickerConfig, itemId: string): Promise<Item> {
    try {
      const ui = await this.cmsContextService.getUiScope();
      const template = Handlebars.compile(pickerConfig.findOneUrl);
      const url = template({ id: itemId });
      return await this.cmsContextService.getHttpResource(url);
    } catch (e) {
      console.log('Failed to get http resource: ' + e);
    }
    return Promise.resolve(undefined);
  }
}
