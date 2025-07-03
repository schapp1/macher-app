import {Component, inject, OnInit} from '@angular/core';
import {TodoStore} from '../state/todo.store';
import {Todo} from '../models/todo';
import {NgClass} from '@angular/common';
import {FormBuilder, ReactiveFormsModule} from '@angular/forms';

@Component({
  selector: 'app-todo',
  imports: [
    NgClass,
    ReactiveFormsModule
  ],
  templateUrl: './todo.component.html',
  styleUrl: './todo.component.scss'
})
export class TodoComponent implements OnInit {

  private readonly todoStore = inject(TodoStore);
  private readonly formBuilder = inject(FormBuilder);
  todos$ = this.todoStore.entities;
  selectedTodoId: string | null = null;
  todoForm = this.formBuilder.group({
    todo: '',
  })

  constructor() {}

  ngOnInit() {
    this.todoStore.loadTodos();
  }

  selectTodo(todo: Todo): void {
    console.log('Selected Todo:', todo);
    this.selectedTodoId = todo.id;
  }

  addTodo(): void {
    const todoValue = this.todoForm.get('todo')?.value;
    if (todoValue) {
      this.todoStore.addTodo({title: todoValue});
      this.todoForm.reset();
    }
  }
}
