import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { PartnershipService } from '../services/partnership.service';
import { Partnership } from '../models/partnership';
import { NgxSpinnerService } from 'ngx-spinner';

@Component({
  selector: 'app-partnership',
  templateUrl: './partnership.component.html',
  styleUrls: ['./partnership.component.scss']
})
export class PartnershipComponent implements OnInit {
  partnerships: Partnership[] = [];
  loading = false;
  error: string | null = null;
  deletingPartnerships: Set<number> = new Set(); // Track which partnerships are being deleted

  constructor(
    private partnershipService: PartnershipService,
    private router: Router,
    private spinner: NgxSpinnerService
  ) {}

  ngOnInit(): void {
    this.fetchPartnerships();
  }

  fetchPartnerships(): void {
    this.loading = true;
    this.spinner.show();
    
    this.partnershipService.getPartnerships().subscribe({
      next: (data) => {
        this.partnerships = data;
        this.loading = false;
        this.spinner.hide();
      },
      error: (error) => {
        this.error = 'Error fetching partnerships';
        this.loading = false;
        this.spinner.hide();
        console.error(error);
      }
    });
  }

  navigateToEdit(id: number): void {
    this.router.navigate(['/partnership/edit', id]);
  }

  deletePartnership(id: number): void {
    if (!confirm('Are you sure you want to delete this partnership?')) {
      return;
    }

    // Mark this partnership as being deleted
    this.deletingPartnerships.add(id);

    this.partnershipService.deletePartnership(id).subscribe({
      next: () => {
        // Remove from the list immediately
        this.partnerships = this.partnerships.filter(p => p.idPartnership !== id);
        this.deletingPartnerships.delete(id);
      },
      error: (error) => {
        this.error = 'Error deleting partnership';
        this.deletingPartnerships.delete(id);
        console.error(error);
      }
    });
  }

//  navigateToAdd(): void {
    //this.router.navigate(['/partnership/add']);
  //}

  // Helper method to check if a partnership is being deleted
  isDeleting(id: number): boolean {
    return this.deletingPartnerships.has(id);
  }
}