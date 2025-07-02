import {Component, inject, OnInit} from '@angular/core';
import {TodoService} from '../service/todo.service';
import {TodoStore} from '../state/todo.store';
import {JsonPipe} from '@angular/common';

@Component({
  selector: 'app-todo',
  imports: [],
  templateUrl: './todo.html',
  styleUrl: './todo.scss'
})
export class Todo implements OnInit {

  private readonly todoStore = inject(TodoStore);
  todos$ = this.todoStore.entities;

  constructor(private todoService: TodoService) {
  }

  ngOnInit() {
    this.todoStore.loadTodos();
  }
}
