import { Component, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { Owner } from 'src/app/dto/owner';
import { OwnerService } from 'src/app/service/owner.service';

@Component({
  selector: 'app-owner-list',
  imports: [
    RouterLink
  ],
  templateUrl: './owner-list.component.html',
  styleUrl: './owner-list.component.scss',
})
export class OwnerListComponent {

  owners: Owner[] = [];

  constructor( 
    private ownerService: OwnerService,
    private notification: ToastrService,
  ) {
  }

  ngOnInit(): void {
    this.loadOwners();
  }

  loadOwners(): void {
    this.ownerService.searchByName('', 1000).subscribe({
      next: (owners: Owner[]) => {
        this.owners = owners;
      },
      error: (errors: any) => {
        const errorMessage = errors.status === 0
          ? 'Is the backend running?'
          : errors.message;
        this.notification.error(errorMessage, 'Could not fetch owners'); 
      }
    });
  }
}
