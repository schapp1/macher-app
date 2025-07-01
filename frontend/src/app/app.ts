import {Component, OnInit} from '@angular/core';
import {UserService} from './services/user';

@Component({
  selector: 'app-root',
  imports: [],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App implements OnInit{

  message: string = ''

  constructor(
    private userService: UserService
  ) {

  }

  ngOnInit() {
    this.userService.getUsers().subscribe(data => this.message = data);
  }
}
