/**
 * Copyright 2020 BloomReach Inc. (https://www.bloomreach.com/)
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
import { FlatTreeControl} from '@angular/cdk/tree';
import { MatTreeFlatDataSource, MatTreeFlattener } from '@angular/material/tree';
import { CmsContextService } from '../cms-context.service';
import { PickerConfig } from '../picker-config';
import { Item } from '../item';

class TreeNode {
  id: string;
  displayName: string;
  props?: object;
  selectable?: boolean;
  children?: TreeNode[];
}

class TreeFlatNode {
  constructor(
    public readonly id: string,
    public readonly displayName: string,
    public readonly selectable: boolean,
    public readonly level: number,
    public readonly expandable: boolean,
  ) {}
}

@Component({
  selector: 'app-field-tree-dialog',
  templateUrl: './field-tree-dialog.component.html',
  styleUrls: ['./field-tree-dialog.component.scss']
})
export class FieldTreeDialogComponent implements OnInit {

  pickerConfig: PickerConfig;
  curItemId: string;
  curParentItemId: string;
  curParentTreeFlatNode: TreeFlatNode;
  newItemId: string;

  items: Item[];

  treeControl: FlatTreeControl<TreeFlatNode>;
  treeFlattener: MatTreeFlattener<TreeNode, TreeFlatNode>;
  dataSource: MatTreeFlatDataSource<TreeNode, TreeFlatNode>;

  initTreeExpandLevel = 2;

  constructor(
    private cmsContextService: CmsContextService,
  ) {
    this.treeFlattener = new MatTreeFlattener(
      (node: TreeNode, level: number) => {
        const flatNode = new TreeFlatNode(node.id, node.displayName, node.selectable ?? true, level, !!node.children);
        if (this.curParentItemId === node.id) {
          this.curParentTreeFlatNode = flatNode;
        }
        return flatNode;
      },
      (node: TreeFlatNode) => node.level,
      (node: TreeFlatNode) => node.expandable,
      (node: TreeNode): TreeNode[] => node.children,
    );
    this.treeControl = new FlatTreeControl<TreeFlatNode>(
      (node: TreeFlatNode) => node.level,
      (node: TreeFlatNode) => node.expandable,
    );
    this.dataSource = new MatTreeFlatDataSource(this.treeControl, this.treeFlattener);
  }

  async ngOnInit(): Promise<void> {
    const ui = await this.cmsContextService.getUiScope();
    if (ui) {
      const options = await ui.dialog.options();
      const { pickerConfig, id } = JSON.parse(options.value);
      this.pickerConfig = pickerConfig;
      this.initTreeExpandLevel = this.pickerConfig.initTreeExpandLevel ?? 2;
      this.curItemId = id;
    }

    await this.searchItems();
    this.curParentTreeFlatNode = null;
    this.dataSource.data = this.buildTreeNodes();
    this.expandInitialTree();
  }

  async onOK(): Promise<void> {
    const ui = await this.cmsContextService.getUiScope();
    if (ui) {
      if (this.newItemId) {
        ui.dialog.close(this.newItemId); 
      } else {
        ui.dialog.cancel();
      }
    } else {
      console.log('OpenUI UiScope unavailable. Selected item ID:', this.newItemId);
    }
  }

  async onCancel(): Promise<void> {
    const ui = await this.cmsContextService.getUiScope();
    if (ui) {
      ui.dialog.cancel();
    }
  }

  hasChild = (_: number, _nodeData: TreeFlatNode) => _nodeData.expandable;

  onNewItemSelected(node: TreeFlatNode): void {
    this.newItemId = node.id;
  }

  private async searchItems(): Promise<void> {
    this.items = [];
    try {
      const url = this.pickerConfig.findAllUrl;
      this.items = await this.cmsContextService.getHttpResource(url);
    } catch (e) {
      console.log('Failed to get http resource: ' + e);
    }
  }

  private buildTreeNodes(): TreeNode[] {
    const treeNodes: TreeNode[] = [];
    const treeNodeMapById = new Map<string, TreeNode>();
    const treeNodeMapByPath = new Map<string, TreeNode>();

    this.items.forEach((item) => {
      const treeNode = {
        id: item.id,
        displayName: item.title,
        props: {
          parentId: item.parentId,
        },
      };

      treeNodeMapById.set(treeNode.id, treeNode);

      if (!item.parentId) {
        treeNodes.push(treeNode);
      }
    });

    treeNodeMapById.forEach((treeNode: TreeNode, id: string) => {
      const parentId = treeNode.props['parentId'];
      const parent = treeNodeMapById.get(parentId);

      if (parent) {
        parent.children = parent.children ?? [];
        parent.children.push(treeNode);
      }

      if (id === this.curItemId) {
        this.curParentItemId = parentId;
      }
    });

    return treeNodes;
  }

  private expandInitialTree(): void {
    if (this.curParentTreeFlatNode) {
      this.treeControl.expand(this.curParentTreeFlatNode);
    }

    if (this.initTreeExpandLevel > 0) {
      this.treeControl.dataNodes.forEach((flatNode) => {
        if (flatNode.level < this.initTreeExpandLevel) {
          this.treeControl.expand(flatNode);
        }
      });
    }
  }
}
