import {patchState, signalStore, withMethods} from '@ngrx/signals';
import {removeEntity, setAllEntities, setEntity, withEntities} from '@ngrx/signals/entities';
import {Part, PartCreationRequest} from '../models/part';
import {PartService} from '../service/part.service';
import {inject} from '@angular/core';
import {rxMethod} from '@ngrx/signals/rxjs-interop';
import {pipe, switchMap, tap} from 'rxjs';

export const PartStore = signalStore(
  {providedIn: 'root',},
  withEntities<Part>(),
  withMethods((
    store,
    partService = inject(PartService),
  ) => ({
      loadTodos: rxMethod<void>(
        pipe(
          switchMap(_ => partService.fetchParts()),
          tap(parts => patchState(store, setAllEntities(parts))),
        )
      ),
      addTodo: rxMethod<PartCreationRequest>(
        pipe(
          switchMap(part => partService.addPart(part)),
          tap(todo => {
            patchState(store, setEntity(todo))
          })
        )
      ),
      deleteTodo: rxMethod<Part>(
        pipe(
          switchMap(todo => partService.deletePart(todo)),
          tap(todo => {
            patchState(store, removeEntity(todo.id))
          })
        )
      ),
      uploadExcel: rxMethod<File>(
        pipe(
          switchMap(file => partService.uploadExcel(file)),
        )
      )
    }
  ))
)
