import {Component, inject, OnInit} from '@angular/core';
import {PartStore} from '../state/partStore';
import {Part} from '../models/part';
import {NgClass, NgIf, NgTemplateOutlet} from '@angular/common';
import {FormBuilder, ReactiveFormsModule} from '@angular/forms';

@Component({
  selector: 'app-todo',
  imports: [
    NgClass,
    ReactiveFormsModule,
    NgTemplateOutlet,
    NgIf

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
  expandedPartIds = new Set<string>();



partForm = this.formBuilder.group({
    part: '',
  })

  constructor() {}

  ngOnInit() {
    this.partStore.loadParts();
  }

  selectPart(part: Part): void {
    console.log('Selected Part:', part);
    this.selectedPartId = part.id;
    this.selectedPart = part;
  }

  isExpanded(partId: string): boolean {
    return this.expandedPartIds.has(partId);
  }

  toggleExpand(partId: string, event: Event): void {
    event.stopPropagation(); // Verhindert, dass die Zeile selektiert wird
    if (this.expandedPartIds.has(partId)) {
      this.expandedPartIds.delete(partId);
    } else {
      this.expandedPartIds.add(partId);
    }
  }

  addPart(): void {
    const partNumber= this.partForm.get('part')?.value;
    if (partNumber) {
      this.partStore.addPart({partNumber: partNumber});
      this.partForm.reset();
    }
  }

  deletePart(): void {
    if (this.selectedPart) {
      this.partStore.deletePart(this.selectedPart);
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
    this.partStore.loadParts();
  }

  deleteAllParts(): void {
    this.partStore.deleteAllParts()
  }
}
