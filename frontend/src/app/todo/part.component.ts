import {Component, inject, OnInit} from '@angular/core';
import {PartStore} from '../state/partStore';
import {Part} from '../models/part';
import {NgClass} from '@angular/common';
import {FormBuilder, ReactiveFormsModule} from '@angular/forms';

@Component({
  selector: 'app-todo',
  imports: [
    NgClass,
    ReactiveFormsModule
  ],
  templateUrl: './part.component.html',
  styleUrl: './part.component.scss'
})
export class PartComponent implements OnInit{

  private readonly partStore = inject(PartStore);
  private readonly formBuilder = inject(FormBuilder);
  readonly parts$ = this.partStore.entities;
  selectedPartId: string | null = null;
  selectedPart: Part | null = null;

  todoForm = this.formBuilder.group({
    todo: '',
  })

  constructor() {}

  ngOnInit() {
    this.partStore.loadTodos();
  }

  selectPart(part: Part): void {
    console.log('Selected Part:', part);
    this.selectedPartId = part.id;
    this.selectedPart = part;
  }

  addPart(): void {
    const todoValue = this.todoForm.get('todo')?.value;
    if (todoValue) {
      this.partStore.addTodo({title: todoValue});
      this.todoForm.reset();
    }
  }

  deletePart(): void {
    if (this.selectedPart) {
      this.partStore.deleteTodo(this.selectedPart);
      this.selectedPart = null;
      this.selectedPartId = null;
    }
  }

  uploadExcel(event: Event): void {
    const target = event.target as HTMLInputElement;
    const files = target.files;
    if (files && files.length > 0) {
      this.partStore.uploadExcel(files[0]);
      // Datei-Input zurücksetzen
      target.value = '';
    }
    this.partStore.loadTodos();
  }

  deleteAllParts(): void {
    this.partStore.deleteAllParts()
  }
}
