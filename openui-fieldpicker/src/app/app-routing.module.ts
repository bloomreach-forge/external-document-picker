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
import { NgModule } from '@angular/core';
import { Routes, RouterModule, UrlSegment } from '@angular/router';
import { FieldViewComponent } from './field-view/field-view.component';
import { FieldListDialogComponent } from './field-list-dialog/field-list-dialog.component';
import { FieldTreeDialogComponent } from './field-tree-dialog/field-tree-dialog.component';

const getComponentId = (url: UrlSegment[]) => {
  const [, id] = /^.*[\?&]id=([\w-]+)&?.*$/.exec(location?.href) ?? [ undefined, undefined ];
  return id ?? (url.length ? url[0].path : undefined);
};

const routes: Routes = [
  {
    matcher: (url) => ('field-list-dialog' === getComponentId(url) ? { consumed: url } : null),
    component: FieldListDialogComponent,
  },
  {
    matcher: (url) => ('field-tree-dialog' === getComponentId(url) ? { consumed: url } : null),
    component: FieldTreeDialogComponent,
  },
  {
    path: '**',
    component: FieldViewComponent,
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes, { relativeLinkResolution: 'legacy' })],
  exports: [RouterModule]
})
export class AppRoutingModule { }
