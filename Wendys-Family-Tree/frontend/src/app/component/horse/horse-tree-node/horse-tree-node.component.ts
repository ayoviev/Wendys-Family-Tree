import { Component, EventEmitter, Input, Output } from '@angular/core';
import { RouterLink } from '@angular/router';
import { HorseTreeNode } from 'src/app/dto/horse';

@Component({
  selector: 'app-horse-tree-node',
  imports: [
    RouterLink,
    HorseTreeNodeComponent
  ],
  templateUrl: './horse-tree-node.component.html',
  styleUrl: './horse-tree-node.component.scss',
})
export class HorseTreeNodeComponent {
  @Input() node!: HorseTreeNode;
  @Input() collapsedNodes!: Set<number>;
  @Output() deleteRequest = new EventEmitter<HorseTreeNode>();

  isCollapsed(): boolean {
    return this.collapsedNodes.has(this.node.id);
  }

  toggleCollapse(): void {
    if (this.collapsedNodes.has(this.node.id)) {
      this.collapsedNodes.delete(this.node.id);
    } else {
      this.collapsedNodes.add(this.node.id);
    }
  }

  hasParents(): boolean {
    return this.node.mother != null || this.node.father != null;
  }

  dateAsLocalDate(): string {
    return new Date(this.node.dateOfBirth).toLocaleDateString();
  }

  onDeleteRequest(node: HorseTreeNode): void {
    this.deleteRequest.emit(node);
  }
}