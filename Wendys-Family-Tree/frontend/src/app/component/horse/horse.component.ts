import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { map, of } from 'rxjs';
import { ViewChild } from '@angular/core';
import { AutocompleteComponent } from 'src/app/component/autocomplete/autocomplete.component';
import { HorseService } from 'src/app/service/horse.service';
import { Horse, HorseSearch } from 'src/app/dto/horse';
import { Owner } from 'src/app/dto/owner';
import { OwnerService } from 'src/app/service/owner.service';
import { ConfirmDeleteDialogComponent } from 'src/app/component/confirm-delete-dialog/confirm-delete-dialog.component';



@Component({
  selector: 'app-horse',
  templateUrl: './horse.component.html',
  imports: [
    RouterLink,
    FormsModule,
    AutocompleteComponent,
    ConfirmDeleteDialogComponent
],
  styleUrls: ['./horse.component.scss']
})
export class HorseComponent implements OnInit {
  @ViewChild('ownerAutocomplete') ownerAutocomplete?: AutocompleteComponent<Owner>;
  horses: Horse[] = [];
  bannerError: string | null = null;
  horseForDeletion: Horse | undefined;
  searchParams: HorseSearch = {};

  constructor(
    private service: HorseService,
    private ownerService: OwnerService,
    private notification: ToastrService,
  ) { }

  ngOnInit(): void {
    this.reloadHorses();
  }

  resetAndReload() {
    this.searchParams = {};
    if (this.ownerAutocomplete) {
      this.ownerAutocomplete.inputText = '';
      this.ownerAutocomplete.value = null;
    }
    this.reloadHorses();
  }

  reloadHorses() {
    this.service.search(this.searchParams)
      .subscribe({
        next: data => {
          this.horses = data;
        },
        error: error => {
          console.error('Error fetching horses', error);
          this.bannerError = 'Could not fetch horses: ' + error.message;
          const errorMessage = error.status === 0
            ? 'Is the backend up?'
            : error.message.message;
          this.notification.error(errorMessage, 'Could Not Fetch Horses');
        }
      });
  }

  ownerSuggestions = (input: string) => (input === '')
    ? of([])
    : this.ownerService.searchByName(input, 5).pipe(
        map(owners => owners.map(o => `${o.firstName} ${o.lastName}`))
      );

  ownerName(owner: Owner | null | undefined): string {
    return owner
      ? `${owner.firstName} ${owner.lastName}`
      : '';
  }

  dateOfBirthAsLocaleDate(horse: Horse): string {
    return new Date(horse.dateOfBirth).toLocaleDateString();
  }

  deleteHorse(horse: Horse | undefined) {
    if (!horse?.id) {
      this.notification.error('Cannot delete horse without an ID.', 'Delete failed');
      return;
    }

    this.service.delete(horse.id).subscribe({
      next: () => {
        this.notification.success(`Horse ${horse.name} successfully deleted.`);
        this.horseForDeletion = undefined;
        this.reloadHorses();
      },
      error: (error: any) => {
        console.error('Error deleting horse', error);
        const backendMessage = error?.error?.message;
        const details = error?.error?.errors;
        if (Array.isArray(details) && details.length > 0) {
          this.notification.error(details.join('; '), 'Delete failed');
          return;
        }

        this.notification.error(
          backendMessage ?? 'Deleting horse failed due to an unexpected backend error.',
          'Delete failed'
        );
      }
    });
  }
}
