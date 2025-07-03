import {Component, inject, OnInit} from '@angular/core';
import {TodoStore} from '../state/todo.store';
import {Todo} from '../models/todo';
import {NgClass} from '@angular/common';

@Component({
  selector: 'app-todo',
  imports: [
    NgClass
  ],
  templateUrl: './todo.component.html',
  styleUrl: './todo.component.scss'
})
export class TodoComponent implements OnInit {

  private readonly todoStore = inject(TodoStore);
  todos$ = this.todoStore.entities;
  selectedTodoId: string | null = null;

  constructor() {
  }

  ngOnInit() {
    this.todoStore.loadTodos();
  }

  selectTodo(todo: Todo): void {
    console.log('Selected Todo:', todo);
    this.selectedTodoId = todo.id;
  }
}
