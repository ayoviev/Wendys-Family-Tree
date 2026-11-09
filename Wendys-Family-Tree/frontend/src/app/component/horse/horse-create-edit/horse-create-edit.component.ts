import { Component, OnInit, ViewChild } from '@angular/core';
import { FormsModule, NgForm, NgModel } from '@angular/forms';
import { ActivatedRoute, Data, Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { Observable, of } from 'rxjs';
import { AutocompleteComponent } from 'src/app/component/autocomplete/autocomplete.component';
import { Horse, convertFromHorseToCreate } from 'src/app/dto/horse';
import { Owner } from 'src/app/dto/owner';
import { Sex } from 'src/app/dto/sex';
import { HorseService } from 'src/app/service/horse.service';
import { OwnerService } from 'src/app/service/owner.service';
import { ConfirmDeleteDialogComponent } from 'src/app/component/confirm-delete-dialog/confirm-delete-dialog.component';
import { Location } from '@angular/common';

export enum HorseCreateEditMode {
  create,
  edit,
};

@Component({
  selector: 'app-horse-create-edit',
  templateUrl: './horse-create-edit.component.html',
  imports: [
    FormsModule,
    AutocompleteComponent,
    ConfirmDeleteDialogComponent,
],
  styleUrls: ['./horse-create-edit.component.scss']
})
export class HorseCreateEditComponent implements OnInit {
  showDeleteImage = false;

  horseForDeletion: Horse | undefined;

  @ViewChild('motherAutocomplete') motherAutocomplete?: AutocompleteComponent<Horse>;
  @ViewChild('fatherAutocomplete') fatherAutocomplete?: AutocompleteComponent<Horse>;
  @ViewChild('ownerAutocomplete') ownerAutocomplete?: AutocompleteComponent<Owner>;

  mode: HorseCreateEditMode = HorseCreateEditMode.create;
  horse: Horse = {
    name: '',
    description: '',
    dateOfBirth: '',
    sex: '' as any,
  };


  constructor(
    private service: HorseService,
    private ownerService: OwnerService,
    private router: Router,
    private route: ActivatedRoute,
    private notification: ToastrService,
    private location: Location,
  ) { }

  public get heading(): string {
    switch (this.mode) {
      case HorseCreateEditMode.create:
        return 'Create New Horse';
      default:
        return 'Edit Horse';
    }
  }

  public get submitButtonText(): string {
    switch (this.mode) {
      case HorseCreateEditMode.create:
        return 'Create';
      default:
        return 'Update';
    }
  }

  get modeIsCreate(): boolean {
    return this.mode === HorseCreateEditMode.create;
  }


  private get modeActionFinished(): string {
    switch (this.mode) {
      case HorseCreateEditMode.create:
        return 'created';
      default:
        return 'updated';
    }
  }

  ownerSuggestions = (input: string) => (input === '')
    ? of([])
    : this.ownerService.searchByName(input, 5);

  ngOnInit(): void {
    this.route.data.subscribe((data: Data) => {
      this.mode = this.route.snapshot.data['mode'] as HorseCreateEditMode;
      if (this.mode === HorseCreateEditMode.edit) {
        const idParam = this.route.snapshot.paramMap.get('id');
        const id = Number(idParam);

        if (!idParam || Number.isNaN(id)) {
          this.notification.error('Invalid horse ID for edit page.', 'Edit failed');
          void this.router.navigate(['/horses']);
          return;
        }

        this.service.getById(id).subscribe({
          next: (horse: Horse) => {
            this.horse = {
              ...horse,
              id: horse.id ?? id,
              owner: horse.owner ?? undefined, // ensure it's Owner object, not null
              mother: horse.mother ?? undefined,
              father: horse.father ?? undefined,
            };

          },
          error: (error: any) => {
            console.error('Error fetching horse', error);
            const backendMessage = error?.error?.message;
            this.notification.error(
              backendMessage ?? 'Failed to load horse data.',
              'Edit failed'
            );
          }
        });
      }
    });
  }

  motherSuggestions = (input: string) => (input === '')
    ? of([])
    : this.service.searchByName(input, Sex.female, 5);

  fatherSuggestions = (input: string) => (input === '')
    ? of([])
    : this.service.searchByName(input, Sex.male, 5);

public formatHorseName(horse: Horse | null | undefined): string {
  return (horse == null) ? '' : horse.name;
}

  public dynamicCssClassesForInput(input: NgModel): any {
    return {
      'is-invalid': !input.valid && !input.pristine,
    };
  }

  public formatOwnerName(owner: Owner | null | undefined): string {
    return (owner == null)
      ? ''
      : `${owner.firstName} ${owner.lastName}`;
  }

  /**
   * Handles the file input change event when the user selects an image.
   * Reads the selected file using FileReader and converts it to a Base64
   * data URL string, which is then stored in the horse's image field.
   *
   * @param event the DOM change event fired by the file input element
   */
  public onImageSelected(event: Event): void {
    const file = (event.target as HTMLInputElement).files?.[0];
    if(file){
      const maxSizeInBytes = 5 * 1024 * 1024; // 5 MB
      const selectedSizeMb = (file.size / (1024 * 1024)).toFixed(1);
      if(file.size > maxSizeInBytes){
        this.notification.error('Image is too large. Selected: ' + selectedSizeMb + ' MB.' + 
          ' Max Size: ' + maxSizeInBytes / (1024 * 1024) + ' MB');
        (event.target as HTMLInputElement).value = ''; // Clearing the file input
        return;
      } else {
          const reader = new FileReader();
          reader.onload = () => {
          this.horse.image = reader.result as string;
          };
          reader.readAsDataURL(file);
      }
    }
  }

  public delete(){
    if (this.mode !== HorseCreateEditMode.edit) {
      this.notification.error('Delete is only available in edit mode.', 'Delete failed');
      return;
    }

    if (this.horse.id == null) {
      this.notification.error('Horse ID is missing.', 'Delete failed');
      return;
    }

    this.service.delete(this.horse.id).subscribe({
      next: () => {
        this.notification.success(`Horse ${this.horse.name} successfully deleted.`);
        this.horseForDeletion = undefined;
        this.router.navigate(['/horses']);
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


  public onSubmit(form: NgForm): void {
    if (!this.horse.name || !this.horse.dateOfBirth || !this.horse.sex) {
      this.notification.error('Name, Birth Date and Sex are required.', 'Validation error');
      return;
    }
    if (form.valid) {
      const nameRegex = /^[A-Z][a-z]*$/;
      if (!nameRegex.test(this.horse.name)) {
        this.notification.error(
        'Horse name must start with an uppercase letter, contains only lowercase letters afterwards and contains no spaces.',
        'Validation error'
      );
      return;
      }

      if (this.ownerAutocomplete?.hasUnmatchedInput) {
        this.notification.error(
        'Please select a valid owner from the suggestions or clear the input.',
        'Invalid owner'
      );
      return;
    }

      if (this.motherAutocomplete?.hasUnmatchedInput) {
        this.notification.error('Please select a valid mother or clear the input.', 'Invalid mother');
        return;
      }
      if (this.fatherAutocomplete?.hasUnmatchedInput) {
        this.notification.error('Please select a valid father or clear the input.', 'Invalid father');
        return;
      }

      if (this.horse.description === '') {
        delete this.horse.description;
      }
      // This will hold either create or update HTTP request, but doens't execute it yet
      let observable: Observable<Horse>;
      switch (this.mode) {
        case HorseCreateEditMode.create:
          // Assign POST to observable
          observable = this.service.create(
            convertFromHorseToCreate(this.horse)
          );
          break;
        case HorseCreateEditMode.edit:
          if (this.horse.id == null) {
            this.notification.error('Cannot update horse because the ID is missing.', 'Update failed');
            return;
          }
          const payload = convertFromHorseToCreate(this.horse);
          // Assign PUT to observable
          observable = this.service.update(
            this.horse.id!,
            payload
          );
          break;
        default:
          console.error('Unknown HorseCreateEditMode', this.mode);
          return;
      }
      // Executes the HTTP request
      observable.subscribe({
        next: data => {
          this.notification.success(`Horse ${this.horse.name} successfully ${this.modeActionFinished}.`);
          this.location.back();
        },
        error: (error: any) => {
          const backendMessage = error?.error?.message;
          const details = error?.error?.errors;
          const detailText = Array.isArray(details) && details.length > 0
            ? details.join('; ')
            : null;
          const genericText = this.mode === HorseCreateEditMode.edit
            ? 'Updating horse failed due to an unexpected backend error.'
            : 'Creating horse failed due to an unexpected backend error.';
          this.notification.error(
            detailText
              ? detailText
              : (backendMessage ?? `${genericText} (HTTP ${error?.status ?? 'unknown'})`),
            this.mode === HorseCreateEditMode.edit ? 'Update failed' : 'Create failed'
          );
        }
      });
    }
  }

  public goBack(): void {
    this.location.back();
  }

  public removeImage(): void {
    this.horse.image = undefined;
    // reset the file input
    const fileInput = document.getElementById('horseImage') as HTMLInputElement;
    if (fileInput) {
      fileInput.value = '';
    }
  }
}
