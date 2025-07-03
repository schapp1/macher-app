import {Component, inject, OnInit} from '@angular/core';
import {TodoStore} from '../state/todo.store';
import {TodoCreationRequest} from '../models/todo';

@Component({
  selector: 'app-todo',
  imports: [],
  templateUrl: './todo.component.html',
  styleUrl: './todo.component.scss'
})
export class Todo implements OnInit {

  private readonly todoStore = inject(TodoStore);
  todos$ = this.todoStore.entities;

  constructor() {
  }

  ngOnInit() {
    this.todoStore.loadTodos();
  }

  selectTodo(todo: TodoCreationRequest): void {
    console.log('Selected Todo:', todo);
  }
}
