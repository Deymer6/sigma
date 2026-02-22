import { Component, OnInit, inject, signal } from '@angular/core';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService, JwtPayload } from '../../../core/services/auth';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { 
  faHouse, 
  faCalendarDays, 
  faRightFromBracket,
  faUserPlus,
  faClockRotateLeft,
  faFileMedical,
  faBars,
  faXmark,
  faUsers,       
  faUserDoctor,  
  faClipboardList,
  faChartLine 
} from '@fortawesome/free-solid-svg-icons';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [
    RouterLink,
    RouterLinkActive,
    FontAwesomeModule
  ],
  templateUrl: './sidebar.html',
  styleUrl: './sidebar.scss'
})
export class Sidebar implements OnInit {

  private authService = inject(AuthService);
  private router = inject(Router);
  
  faHouse = faHouse;
  faCalendarDays = faCalendarDays;
  faRightFromBracket = faRightFromBracket;
  faUserPlus = faUserPlus;
  faClockRotateLeft = faClockRotateLeft;
  faFileMedical = faFileMedical;
  faBars = faBars;
  faXmark = faXmark;
  faUsers = faUsers;
  faUserDoctor = faUserDoctor;
  faClipboardList = faClipboardList;
  faChartLine =faChartLine;

  public userRole = signal<string | null>(null);
  public userEmail = signal<string | null>(null);
  public isMobileMenuOpen = signal<boolean>(false);


  ngOnInit(): void {
    const payload: JwtPayload | null = this.authService.getUserPayload();
    if (payload) {
      this.userRole.set(payload.authorities[0]); 
      this.userEmail.set(payload.sub); 
    }
  }
  
  logout(): void {
    this.authService.logout();
    this.router.navigate(['/home']);
  }

  toggleMenu() {
    this.isMobileMenuOpen.update(value => !value);
  }

  closeMenu() {
    this.isMobileMenuOpen.set(false);
  }
}