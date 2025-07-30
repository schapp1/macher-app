import {Component, inject, OnInit} from '@angular/core';
import {PartStore} from '../state/partStore';
import {Part} from '../models/part';
import {NgClass, NgIf, NgTemplateOutlet} from '@angular/common';
import {FormBuilder, FormsModule, ReactiveFormsModule} from '@angular/forms';
import {MsnStore} from '../state/msnStore';

@Component({
  selector: 'app-todo',
  imports: [
    NgClass,
    ReactiveFormsModule,
    NgTemplateOutlet,
    NgIf,
    FormsModule

  ],
  templateUrl: './part.component.html',
  styleUrl: './part.component.scss'
})
export class PartComponent implements OnInit{

  private readonly partStore = inject(PartStore);
  private readonly msnStore = inject(MsnStore);
  private readonly formBuilder = inject(FormBuilder);
  readonly parts$ = this.partStore.entities;
  readonly msns$ = this.msnStore.entities;
  selectedPartId: string | null = null;
  selectedPart: Part | null = null;
  expandedPartIds = new Set<string>();
  msnList: string[] = [];
  selectedMsn: string = '';


  partForm = this.formBuilder.group({
    part: '',
  })

  msnForm = this.formBuilder.group({
    msn: '',
  })

  constructor() {}

  ngOnInit() {
    this.partStore.loadParts();
    this.msnStore.loadMsn();
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

  addMsn(): void {
    const msn= this.msnForm.get('msn')?.value;
    if (msn) {
      this.msnStore.addMsn({msnId: msn});
      this.partForm.reset();
    }
  }

  deleteMsn() {
    if (this.selectedMsn) {
      this.msnStore.deleteMsn({ id: this.selectedMsn, msnId: this.selectedMsn });
      this.selectedMsn = '';
    }
  }

  deletePart(): void {
    if (this.selectedPart) {
      this.partStore.deletePart(this.selectedPart);
      this.selectedPart = null;
      this.selectedPartId = null;
    }
  }

  onMsnChange($event: any) {
    if (this.selectedMsn) {
      this.partStore.getAllPartsByMsn({ msn: this.selectedMsn });
    }
  }

  uploadExcel(event: Event): void {
    const target = event.target as HTMLInputElement;
    const files = target.files;
    if (files && files.length > 0) {
      this.partStore.uploadExcel({ file: files[0], msnId: this.selectedMsn })
      target.value = '';
      console.log(this.selectedMsn)
    }
    this.partStore.loadParts();
  }

  deleteAllParts(): void {
    this.partStore.deleteAllParts()
  }

/*  loadParts() {
    if (this.selectedMsn) {
      this.partStore.getAllPartsByMsn(this.selectedMsn)
    }
  }*/
}
