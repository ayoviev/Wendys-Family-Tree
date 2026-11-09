import { Component } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { ToastrService } from 'ngx-toastr';
import { Router, RouterLink } from '@angular/router';
import { OwnerService } from 'src/app/service/owner.service';
import { Location } from '@angular/common';

@Component({
  selector: 'app-owner-create',
  imports: [
    FormsModule,
  ],
  templateUrl: './owner-create.component.html',
  styleUrl: './owner-create.component.scss',
})
export class OwnerCreateComponent {
  ownerName = '';
  email = '';

  constructor (
    private ownerService: OwnerService,
    private route: Router,
    private notification: ToastrService,
    private location: Location,
  ) {
  }

  onSubmit(form: NgForm): void {
    if (!this.ownerName.trim()) {
      this.notification.error('First and Last name are required', 'Validation error');
      return;
    }
    if (this.email && !/^[a-zA-Z0-9._%+\-]+@[a-zA-Z0-9.\-]+\.[a-zA-Z]{2,}$/.test(this.email)) {
      this.notification.error('Please enter a valid email address.', 'Validation error');
      return;
    }
    const nameRegex = /^[A-Z][a-z]+ [A-Z][a-z]+$/;
    if (!nameRegex.test(this.ownerName.trim())) {
      this.notification.error(
      'First and last name must each start with a capital letter followed by lowercase letters (e.g. John Doe).',
      'Validation error'
    );
    return;
    } 
    if (form.valid) {
      const parts = this.ownerName.trim().split(' ');
      const firstName = parts[0];
      const lastName = parts.slice(1).join(' ');

      if (!firstName) {
        this.notification.error('Name is required.', 'Validation error');
        return;
      }

      if (!lastName) {
        this.notification.error('Please enter both first and last name.', 'Validation error');
        return;
      }

      this.ownerService.create({
        firstName,
        lastName,
        email: this.email || undefined,
      }).subscribe({
        next: () => {
          this.notification.success(`Owner ${this.ownerName} created successfully.`, 'Success');
          void this.route.navigate(['/owners']);
        },
        error: (error: any) => {
          const backendMessage = error?.error?.message;
          const details = error?.error?.errors;
          this.notification.error(
            Array.isArray(details) && details.length > 0
              ? details.join('; ')
              : (backendMessage ?? 'Creating owner failed'),
            'Create failed'
          );
        }
      });
    }
  }
  goBack(): void {
    this.location.back();
  }

}
