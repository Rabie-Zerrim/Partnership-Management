// src/app/app-routing.module.ts
import { NgModule } from '@angular/core';
import { RouterModule, Routes, PreloadAllModules } from '@angular/router';

import { FullLayoutComponent } from './layouts/full/full-layout.component';
import { ContentLayoutComponent } from './layouts/content/content-layout.component';

import { Full_ROUTES } from './shared/routes/full-layout.routes';
import { CONTENT_ROUTES } from './shared/routes/content-layout.routes';

import { AuthGuard } from './shared/auth/auth-guard.service';

const appRoutes: Routes = [
  {
    path: '',
    component: FullLayoutComponent,
    data: { title: 'Full Views' },
    children: [
      ...Full_ROUTES,
      
      {
        path: 'proposal',
        loadChildren: () => import('./proposal/proposal.module').then(m => m.ProposalModule),
      },
      
      {
        path: 'entreprise',
        loadChildren: () => import('./entreprise/entreprise/entreprise.module').then(m => m.EntrepriseModule),
      },
      
      {
        path: 'partnership',
        loadChildren: () => import('./partnership/partnership.module').then(m => m.PartnershipModule),
      },
      {
        path: 'data-tables',
        loadChildren: () => import('./data-tables/data-tables.module').then(m => m.DataTablesModule),
      },
      
    ],
    canActivate: [AuthGuard], // Protect these routes using AuthGuard
  },
  {
    path: '',
    component: ContentLayoutComponent,
    data: { title: 'Content Views' },
    children: CONTENT_ROUTES, // Add your content layout-specific routes here
  },
  {
    path: '**', // Wildcard route for unmatched paths (404 page)
    redirectTo: 'pages/error',
  },
];

@NgModule({
  imports: [RouterModule.forRoot(appRoutes, { preloadingStrategy: PreloadAllModules })],
  exports: [RouterModule],
})
export class AppRoutingModule {}