import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import { Partnership } from '../models/partnership';

@Injectable({
  providedIn: 'root'
})
export class PartnershipService {
  private baseUrl = 'http://localhost:8088/Partnership';

  constructor(private http: HttpClient) {}

  private handleError(error: HttpErrorResponse) {
    console.error('API Error:', error);
    let errorMessage = 'An error occurred';
    if (error.error instanceof ErrorEvent) {
      errorMessage = error.error.message;
    } else {
      errorMessage = `Backend returned code ${error.status}, body was: ${JSON.stringify(error.error)}`;
    }
    console.error(errorMessage);
    return throwError(() => errorMessage);
  }

  getPartnerships(): Observable<Partnership[]> {
    const url = `${this.baseUrl}/partnerships/all`;
    return this.http.get<Partnership[]>(url).pipe(
      tap(data => console.log('Received partnerships:', data)),
      catchError(this.handleError)
    );
  }

  getPartnership(id: number): Observable<Partnership> {
    const url = `${this.baseUrl}/partnerships/${id}`;
    return this.http.get<Partnership>(url).pipe(
      tap(data => console.log('Received partnership:', data)),
      catchError(this.handleError)
    );
  }

  createPartnership(partnership: Partnership): Observable<Partnership> {
    const url = `${this.baseUrl}/partnerships/add`;
    return this.http.post<Partnership>(url, partnership).pipe(
      tap(data => console.log('Created partnership:', data)),
      catchError(this.handleError)
    );
  }
  
  updatePartnership(id: number, partnership: Partnership): Observable<Partnership> {
    const url = `${this.baseUrl}/partnerships/update/${id}`;
    return this.http.put<Partnership>(url, partnership).pipe(
      tap(data => console.log('Updated partnership:', data)),
      catchError(this.handleError)
    );
  }

  deletePartnership(id: number): Observable<void> {
    const url = `${this.baseUrl}/partnerships/delete/${id}`;
    return this.http.delete<void>(url).pipe(
      tap(() => console.log('Deleted partnership:', id)),
      catchError(this.handleError)
    );
  }
}
