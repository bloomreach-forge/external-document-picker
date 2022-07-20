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
import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { CmsContextService } from './cms-context.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  title = 'exdocpickerbase-openui-field';

  constructor(
    private cmsContextService: CmsContextService,
    private translateService: TranslateService,
  ) {
  }

  async ngOnInit(): Promise<void> {
    const ui = await this.cmsContextService.getUiScope();
    const locale = ui?.locale;
    if (locale) {
      this.translateService.setDefaultLang(locale);
    }
  }
}
