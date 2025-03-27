import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class UniversiteService {
  private baseUrl = 'http://localhost:8089/universite';

  constructor(private _http: HttpClient) {}

  getUniversites(): Observable<any> {
    return this._http.get(`${this.baseUrl}/retrieve-all-universites`);
  }

  getUniversiteById(id: number): Observable<any> {
    return this._http.get(`${this.baseUrl}/retrieve-universite/${id}`);
  }

  addUniversite(data: any): Observable<any> {
    return this._http.post(`${this.baseUrl}/add-universite`, data);
  }

  updateUniversite(data: any): Observable<any> {
    return this._http.put(`${this.baseUrl}/update-universite`, data, { observe: 'response' });
  }
  

  deleteUniversite(id: number): Observable<any> {
    return this._http.delete(`${this.baseUrl}/remove-universite/${id}`);
  }

}
