import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { Entreprise } from 'app/models/entreprise';
import { EntrepriseService } from 'app/services/entreprise.service';
import { NgxSpinnerService } from 'ngx-spinner';

@Component({
  selector: 'app-entreprise',
  templateUrl: './entreprise.component.html',
  styleUrls: ['./entreprise.component.scss'],
})
export class EntrepriseComponent implements OnInit {
  entreprises: Entreprise[] = [];
  loading = false;
  error: string | null = null;
  deletingId: number | null = null; // Track the ID of the entreprise being deleted
  editingId: number | null = null; // Track the ID of the entreprise being edited
  editForm: FormGroup; // Form for editing an entreprise

  constructor(
    private entrepriseService: EntrepriseService,
    private spinner: NgxSpinnerService,
    private fb: FormBuilder,
    private router: Router // Inject the Router service
  ) {}

  ngOnInit(): void {
    this.editForm = this.fb.group({
      nameEntreprise: ['', Validators.required],
      addressEntreprise: ['', Validators.required],
      phoneEntreprise: ['', Validators.required],
      emailEntreprise: ['', [Validators.required, Validators.email]],
      descriptionEntreprise: [''],
    });

    this.fetchEntreprises();
  }

  // Fetch all entreprises
  fetchEntreprises(): void {
    this.loading = true;
    this.spinner.show();

    this.entrepriseService.getEntreprises().subscribe({
      next: (data) => {
        this.entreprises = data;
        this.loading = false;
        this.spinner.hide();
      },
      error: (error) => {
        this.error = 'Error fetching entreprises';
        this.loading = false;
        this.spinner.hide();
        console.error(error);
      },
    });
  }

  // Delete an entreprise
  deleteEntreprise(id: number): void {
    if (!confirm('Are you sure you want to delete this entreprise?')) {
      return;
    }

    this.deletingId = id;

    this.entrepriseService.deleteEntreprise(id).subscribe({
      next: () => {
        this.entreprises = this.entreprises.filter((e) => e.idEntreprise !== id);
        this.deletingId = null;
      },
      error: (error) => {
        this.error = 'Error deleting entreprise';
        this.deletingId = null;
        console.error(error);
      },
    });
  }

  // Enter edit mode for an entreprise
  enterEditMode(entreprise: Entreprise): void {
    this.editingId = entreprise.idEntreprise!;
    this.editForm.patchValue(entreprise);
  }

  // Cancel edit mode
  cancelEditMode(): void {
    this.editingId = null;
    this.editForm.reset();
  }

  // Submit the edit form
  onSubmit(): void {
    if (this.editForm.invalid) {
      return;
    }

    this.loading = true;
    this.spinner.show();

    const updatedEntreprise: Entreprise = {
      ...this.editForm.value,
      idEntreprise: this.editingId!,
    };

    this.entrepriseService.updateEntreprise(updatedEntreprise).subscribe({
      next: () => {
        this.loading = false;
        this.spinner.hide();
        this.cancelEditMode();
        this.fetchEntreprises();
      },
      error: (error) => {
        this.error = 'Error updating entreprise';
        this.loading = false;
        this.spinner.hide();
        console.error(error);
      },
    });
  }

  // Navigate to the "Add Entreprise" page
  navigateToAdd(): void {
    this.router.navigate(['/add-entreprise']);
  }
}