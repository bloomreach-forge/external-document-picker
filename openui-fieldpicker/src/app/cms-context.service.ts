/*
 * Copyright 2020 BloomReach Inc. (https://www.bloomreach.com/)
 */
import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import UiExtension, { UiScope } from '@bloomreach/ui-extension';

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

  async getConnectorIdFieldValue(): Promise<string> {
    // CMS-12058: later you can uncomment and use the following instead.
    //return this.getDocumentFieldValue('starterstore:connectorid');

    if (!this.uiScope) {
      this.uiScope = await this.registerUi();
    }
    const docProps = await this.uiScope.document.get();
    const documentData = await this.getConnectorResourceDocument(docProps.id, docProps.mode);
    const connectorId = documentData?.props['starterstore:connectorid']; 
    return Promise.resolve(connectorId);
  }

  async getExternalResourceIdFieldValues(live: boolean = false): Promise<string[]> {
    // CMS-12058: later you can uncomment and use the following instead.
    //return this.getDocumentFieldValue('starterstoreboot:relatedexdocids');

    if (!this.uiScope) {
      this.uiScope = await this.registerUi();
    }
    const docProps = await this.uiScope.document.get();
    const documentData = await this.getConnectorResourceDocument(docProps.id, docProps.mode, live);
    const connectorId = documentData?.props['starterstoreboot:relatedexdocids']; 
    return Promise.resolve(connectorId);
  }

  async getDocumentFieldValue(): Promise<any> {
    if (!this.uiScope) {
      this.uiScope = await this.registerUi();
    }

    return await this.uiScope.document.field.getValue();
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

  private addCmsCssToDocument(uiScope: UiScope) {
    const head = document.getElementsByTagName('head')[0];
    const style = document.createElement('link');
    style.href = `${uiScope.baseUrl}skin/hippo-cms/css/hippo-cms-theme.min.css`;
    style.type = 'text/css';
    style.rel = 'stylesheet';
    head.append(style);
  }

  private async getConnectorResourceDocument(documentId: string, mode: string, live: boolean = false): Promise<any> {
    try {
      const data = await this.getRestResourceData(`ws/starterstore/connector-resource-document/${documentId}`);
      const liveVariant = data['published'];
      const previewVariant = data['unpublished'] ?? liveVariant;
      const draftVariant = data['draft'];

      if (live) {
        return Promise.resolve(liveVariant);
      }

      return Promise.resolve(mode === 'edit' ? draftVariant : previewVariant);
    } catch (e) {
      const { error } = e;
      console.log('Failed to get connector resource document: ' + JSON.stringify(error));
    }
    return Promise.resolve(undefined);
  }

  async getRestResourceData(relPath: string, params?: HttpParams): Promise<any> {
    const url = `${this.uiScope.baseUrl}${relPath}`;
    return await this.http.get(
      url,
      {
        headers: {
          'Content-Type': 'application/json',
        },
        params,
      }
    ).toPromise();
  }
}
