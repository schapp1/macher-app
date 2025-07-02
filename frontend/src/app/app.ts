import {Component, OnInit} from '@angular/core';
import {Todo} from './todo/todo.component';

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
  ) {

  }

  ngOnInit() {

  }
}
