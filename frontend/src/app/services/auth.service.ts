import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { API_URL } from '../../config';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  constructor(private http: HttpClient) {}

  public login(email: string, password: string): Observable<any> {
    return this.http.post(`${API_URL}auth/login`, {
      email,
      password,
    });
  }
}
