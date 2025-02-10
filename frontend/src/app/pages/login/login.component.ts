import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { User } from '../../models/user';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css',
})
export class LoginComponent {
  protected user: User = {} as User;
  protected error: string = '';

  constructor(protected authService: AuthService, protected router: Router) {}

  protected login(email: string, password: string) {
    this.authService.login(email, password).subscribe({
      next: (res: any) => {
        this.error = '';
        localStorage.setItem('access_token', res.token);
        localStorage.setItem('userId', res.userId);
      },
      error: (err: any) => {
        this.error = err.error.message;
      },
    });
  }

  protected cleanErrors() {
    this.error = '';
  }
}
