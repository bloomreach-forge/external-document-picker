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
import { Injectable } from '@angular/core';
import { HttpClient, HttpParams, HttpHeaders } from '@angular/common/http';
import UiExtension, { UiScope } from '@bloomreach/ui-extension';
import { PickerConfig } from './picker-config';

@Injectable({
  providedIn: 'root'
})
export class CmsContextService {

  uiScope: UiScope;

  constructor(
    private http: HttpClient,
  ) {}

  async getUiScope(): Promise<UiScope> {
    if (!this.uiScope) {
      this.uiScope = await this.registerUi();
    }

    return this.uiScope;
  }

  getPickerConfig(ui: UiScope): PickerConfig | undefined {
    const config = ui.extension.config;
    if (config) {
      try {
        return JSON.parse(config);
      } catch (e) {
        console.log('Error parsing UI Extension config: ' + e);
      }
    }
    return { findOneUrl: undefined, findAllUrl: undefined };
  }

  async getFieldValue(): Promise<any> {
    if (!this.uiScope) {
      this.uiScope = await this.registerUi();
    }

    return await this.uiScope.document.field.getValue();
  }

  async getFieldCompareValue(): Promise<any> {
    if (!this.uiScope) {
      this.uiScope = await this.registerUi();
    }

    return await this.uiScope.document.field.getCompareValue();
  }

  private async registerUi(): Promise<UiScope> {
    try {
      const uiScope = await UiExtension.register();
      this.addCmsCssToDocument(uiScope);
      return uiScope;
    } catch (e) {
      console.log('Open UI registration failed: ' + e);
    }
  }

  async getHttpResource(url: string, params?: HttpParams, headers?: HttpHeaders): Promise<any> {
    return await this.http.get(
      url,
      {
        headers,
        params,
      }
    ).toPromise();
  }

  private addCmsCssToDocument(uiScope: UiScope) {
    const head = document.getElementsByTagName('head')[0];
    const style = document.createElement('link');
    style.href = `${uiScope.baseUrl}skin/hippo-cms/css/hippo-cms-theme.min.css`;
    style.type = 'text/css';
    style.rel = 'stylesheet';
    head.append(style);
  }
}
