import {Component, OnInit} from '@angular/core';
import {UserService} from './services/user';
import {Todo} from './todo/todo';

@Component({
  selector: 'app-root',
  imports: [
    Todo,
  ],
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
