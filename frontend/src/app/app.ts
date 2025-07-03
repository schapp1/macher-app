import {Component, OnInit} from '@angular/core';
import {TodoComponent} from './todo/todo.component';

@Component({
  selector: 'app-root',
  imports: [
    TodoComponent,
  ],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App implements OnInit{

  message: string = ''

  constructor(
  ) {

  }

  ngOnInit() {

  }
}
