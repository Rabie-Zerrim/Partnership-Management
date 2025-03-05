import { Component, OnInit, ViewEncapsulation, ViewChild } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { map } from 'rxjs/operators';
import { PartnershipService } from '../services/partnership.service';
import { Partnership } from '../models/partnership';
import { ColumnMode, DatatableComponent, SelectionType } from '@swimlane/ngx-datatable';
import { FormsModule } from '@angular/forms'; // Add this import
import { NgForm } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

interface ContentHeader {
  headerTitle: string;
  actionButton: boolean;
  breadcrumb: {
    type: string;
    links: { name: string; isLink: boolean; link?: string }[];
  };
}

@Component({
  selector: 'app-datatables',
  templateUrl: './data-tables.component.html',
  styleUrls: ['./data-tables.component.scss', '../../assets/sass/libs/datatables.scss'],
  encapsulation: ViewEncapsulation.None
})
export class DataTablesComponent implements OnInit {
  public contentHeader: ContentHeader;
  public partnerships: Partnership[] = [];
  public filteredPartnerships: Partnership[] = [];
  public loading = false;
  public error: string | null = null;

  // For Pagination
  public offset = 0;

  // For "Add Partnership" Form
  public showAddForm = false;
  public newPartnership: Partnership = { partnershipStatus: '' };

  // Columns for the ngx-datatable
  public columns = [
    { name: 'ID', prop: 'idPartnership', width: 100 },
    { name: 'Status', prop: 'partnershipStatus', width: 150 },
    { name: 'Actions', prop: 'actions', width: 100 }
  ];

  public ColumnMode = ColumnMode;
  public SelectionType = SelectionType;
  
  @ViewChild(DatatableComponent) table: DatatableComponent;

  constructor(private partnershipService: PartnershipService,private router: Router,private route: ActivatedRoute  ) {}

  ngOnInit() {
    this.loadPartnerships();

    this.contentHeader = {
      headerTitle: 'Partnerships',
      actionButton: true,
      breadcrumb: {
        type: '',
        links: [
          { name: 'Home', isLink: true, link: '#' },
          { name: 'Partnerships', isLink: false }
        ]
      }
    };
  }

  loadPartnerships() {
    this.loading = true;
    this.error = null;

    this.partnershipService.getPartnerships().subscribe({
      next: (data) => {
        this.partnerships = data;
        this.filteredPartnerships = [...this.partnerships];
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Error fetching partnerships';
        this.loading = false;
        console.error(err);
      }
    });
  }

  filterUpdate(event: Event) {
    const val = (event.target as HTMLInputElement).value.toLowerCase();
    this.filteredPartnerships = this.partnerships.filter(p =>
      p.partnershipStatus.toLowerCase().includes(val)
    );
    this.table.offset = 0;  // Reset pagination on filter update
  }

  // Show the "Add Partnership" Form
  openAddPartnershipForm() {
    this.showAddForm = true;
  }

  // Cancel adding a new partnership
  cancelAddPartnership() {
    this.showAddForm = false;
  }
  addPartnership(): void {
    this.partnershipService.createPartnership(this.newPartnership).subscribe({
      next: (partnership) => {
        this.loadPartnerships(); // Refresh the list after adding
        this.cancelAddPartnership(); // Hide the form
      },
      error: (error) => console.error('Error adding partnership', error)
    });
  }
  
  
  
  
    navigateToAdd() {
      this.router.navigate(['/add']); // Navigate to the "add" route
    }
  

  // Navigate to edit partnership
  navigateToEdit(id: number) {
    // Navigate to the edit page (implement the logic for editing here)
  }

  // Delete partnership
  deletePartnership(id: number) {
    this.partnershipService.deletePartnership(id).subscribe({
      next: () => {
        this.partnerships = this.partnerships.filter(p => p.idPartnership !== id);
        this.filteredPartnerships = this.filteredPartnerships.filter(p => p.idPartnership !== id);
      },
      error: (err) => {
        this.error = 'Error deleting partnership';
        console.error(err);
      }
    });
  }
}
