/**
 * Copyright 2020-2022 BloomReach Inc. (https://www.bloomreach.com/)
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
import { NgModule, Injector, APP_INITIALIZER } from '@angular/core';
import { APP_BASE_HREF, LOCATION_INITIALIZED } from '@angular/common';
import { HttpClientModule, HttpClient } from '@angular/common/http';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatRadioModule } from '@angular/material/radio';
import { MatTreeModule } from '@angular/material/tree';
import { TranslateModule, TranslateLoader, TranslateService } from '@ngx-translate/core';
import { TranslateHttpLoader } from '@ngx-translate/http-loader';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { FieldViewComponent } from './field-view/field-view.component';
import { FieldListDialogComponent } from './field-list-dialog/field-list-dialog.component';
import { FieldTreeDialogComponent } from './field-tree-dialog/field-tree-dialog.component';

export function HttpLoaderFactory(http: HttpClient) {
  const [, antiCache] = /[?&]antiCache=([^&#]*)/.exec(location.href) ?? [undefined, '' + new Date().getTime()];
  return new TranslateHttpLoader(http, './i18n/', `.json?antiCache=${antiCache}`);
}

export function appInitializerFactory(
  translateService: TranslateService,
  injector: Injector,
) {
  return () => new Promise<any>((resolve: any) => {
    const locationInitialized = injector.get(LOCATION_INITIALIZED, Promise.resolve(null));
    locationInitialized.then(() => {
      const defaultLang = 'en';
      translateService.addLangs([
        'en',
        'nl',
        'fr',
        'de',
        'es',
        'zh',
      ]);
      translateService.setDefaultLang(defaultLang);
      translateService.use(defaultLang).subscribe(() => {
        // default language resources successfully initialized ...
      }, err => {
        console.error(`Problem with '${defaultLang}' language initialization.'`);
      }, () => {
        resolve(null);
      });
    });
  });
}

@NgModule({
  declarations: [
    AppComponent,
    FieldViewComponent,
    FieldListDialogComponent,
    FieldTreeDialogComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    HttpClientModule,
    TranslateModule.forRoot({
      loader: {
        provide: TranslateLoader,
        useFactory: HttpLoaderFactory,
        deps: [ HttpClient ]
      }
    }),
    MatIconModule,
    MatButtonModule,
    MatRadioModule,
    MatTreeModule,
  ],
  providers: [
    {
      provide: APP_BASE_HREF,
      useValue: window['base-href']
    },
    {
      provide: APP_INITIALIZER,
      useFactory: appInitializerFactory,
      deps: [ TranslateService, Injector ],
      multi: true
    },
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
