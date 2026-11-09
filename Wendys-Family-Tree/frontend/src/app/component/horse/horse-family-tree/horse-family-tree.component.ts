import { Component, ElementRef, ViewChild } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { HorseTreeNode } from 'src/app/dto/horse';
import { HorseService } from 'src/app/service/horse.service';
import { HorseTreeNodeComponent } from '../horse-tree-node/horse-tree-node.component';
import { ConfirmDeleteDialogComponent } from '../../confirm-delete-dialog/confirm-delete-dialog.component';
import { Location} from '@angular/common';

@Component({
  selector: 'app-horse-family-tree',
  imports: [
    FormsModule,
    ConfirmDeleteDialogComponent,
    HorseTreeNodeComponent
  ],
  templateUrl: './horse-family-tree.component.html',
  styleUrl: './horse-family-tree.component.scss',
})
export class HorseFamilyTreeComponent {

  @ViewChild('deleteDialog') deleteDialogRef!: ElementRef;

  tree: HorseTreeNode | null = null;
  generations: number = 0;
  horseId: number = 0;
  horseForDeletion: HorseTreeNode | undefined;
  collapsedNodes: Set<number> = new Set<number>();

  constructor(
    private service: HorseService,
    private route: ActivatedRoute,
    private router: Router,
    private notification: ToastrService,
    private location: Location,
  ) { }

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    this.horseId = Number(idParam);
    const genParam = this.route.snapshot.queryParamMap.get('generations');
    if (genParam) {
      this.generations = Number(genParam);
    }
    this.loadTree();
  }

  loadTree(): void {
    this.collapsedNodes.clear();
    this.service.getFamilyTree(this.horseId, this.generations).subscribe({
      next: (data: HorseTreeNode) => {
        this.tree = data;
      },
      error: (error: any) => {
        const backendMessage = error?.error?.message;
        this.notification.error(
          backendMessage ?? 'Failed to load horse family tree.',
          'Error'
        );
        void this.router.navigate(['/horses']);
      }
    });
  }

  isCollapsed(id: number): boolean {
    return this.collapsedNodes.has(id);
  }

  toggleCollapse(id: number): void {
    if (this.collapsedNodes.has(id)) {
      this.collapsedNodes.delete(id);
    } else {
      this.collapsedNodes.add(id);
    }
  }

  hasParents(node: HorseTreeNode): boolean {
    return node.mother != null || node.father != null;
  }

  dateAsLocalDate(dateOfBirth: string): string {
    return new Date(dateOfBirth).toLocaleDateString();
  }

  confirmDelete(node: HorseTreeNode): void {
    this.horseForDeletion = node;
    // manually trigger the modal after setting horseForDeletion
    const modal = new (window as any).bootstrap.Modal(
      document.getElementById('delete-dialog')
    );
    modal.show();
  }

  deleteHorse(node: HorseTreeNode | undefined): void {
    if (!node?.id) return;
    this.service.delete(node.id).subscribe({
      next: () => {
        this.notification.success(`Horse ${node?.name} deleted successfully.`);
        this.horseForDeletion = undefined;
        this.loadTree();
      },
      error: (error: any) => {
        const backendMessage = error?.error?.message;
        const details = error?.error?.error;
        this.notification.error(
          Array.isArray(details) && details.length > 0
            ? details.join('; ')
            : (backendMessage ?? 'Failed to delete horse.'),
          'Delete failed'
        );
      }
   });
  }

  goBack(): void {
    this.location.back();
  }
}
