import { Component, OnInit } from '@angular/core';
import { ToastrService } from 'ngx-toastr';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { Horse } from 'src/app/dto/horse';
import { HorseService } from 'src/app/service/horse.service';
import { ConfirmDeleteDialogComponent } from 'src/app/component/confirm-delete-dialog/confirm-delete-dialog.component';
import { Location } from '@angular/common';

@Component({
  selector: 'app-horse-detail',
  imports: [
    RouterLink,
    ConfirmDeleteDialogComponent],
  templateUrl: './horse-detail.component.html',
  styleUrl: './horse-detail.component.scss',
})
export class HorseDetailComponent implements OnInit {
  horse: Horse | null = null;
  horseForDeletion: Horse | undefined;

  constructor(
    private service: HorseService,
    private route: ActivatedRoute,
    private router: Router,
    private notification: ToastrService,
    private location: Location,
  ) { }

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const idParam = params.get('id');
      const id = Number(idParam);

      if (!idParam || Number.isNaN(id)) {
        this.notification.error('Invalid horse ID.', 'Error');
        void this.router.navigate(['/horses']);
        return;
      }

      this.service.getById(id).subscribe({
        next: (horse: Horse) => {
          this.horse = horse;
        },
        error: (error) => {
          const backendMessage = error?.error?.message;
          this.notification.error(
            backendMessage || 'Failed to load horse details.',
            'Error'
          );
          void this.router.navigate(['/horses']);
        }
      });
    });
  }
  

  public delete(): void {
    if (this.horse?.id == null) return;

    this.service.delete(this.horse.id).subscribe({
      next: () => {
        this.notification.success(`Horse ${this.horse?.name} deleted successfully.`, 'Deleted');
        this.horseForDeletion = undefined;
        void this.router.navigate(['/horses']);
      }, 
      error: (error: any) => {
        const backendMessage = error?.error?.message;
        const details = error?.error?.details;
        this.notification.error(
          Array.isArray(details) && details.length > 0
          ? details.join('; ')
          : (backendMessage ?? 'Deleting horse failed'),
        'Delete failed'
        );
      }
    });
  }

  public dateofBirthAsLocalDate(): string {
    return this.horse?.dateOfBirth ? new Date(this.horse.dateOfBirth).toLocaleDateString() : '';
  }

  public ownerName(): string {
    return this.horse?.owner
    ? `${this.horse?.owner?.firstName} ${this.horse?.owner?.lastName}`
    : 'No owner';
  }

  public goBack(): void {
    this.location.back();
  }
}
