import {Component, OnInit} from '@angular/core';
import { RouterOutlet } from '@angular/router';
import {UserService} from './services/user';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App implements OnInit{
  protected title = 'macher';

  message: string = '';
  constructor(private userService: UserService) {}
  ngOnInit() {
    this.userService.getUsers().subscribe(data => this.message = data);
  }
}
