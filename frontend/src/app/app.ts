import {Component, OnInit} from '@angular/core';
import {PartComponent} from './todo/part.component';

@Component({
  selector: 'app-root',
  imports: [
    PartComponent,
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
