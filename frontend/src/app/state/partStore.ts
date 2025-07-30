import {patchState, signalStore, withMethods} from '@ngrx/signals';
import {removeEntity, setAllEntities, setEntity, withEntities} from '@ngrx/signals/entities';
import {Part, PartCreationRequest} from '../models/part';
import {PartService} from '../service/part.service';
import {inject} from '@angular/core';
import {rxMethod} from '@ngrx/signals/rxjs-interop';
import {catchError, EMPTY, pipe, switchMap, tap} from 'rxjs';

export const PartStore = signalStore(
  {providedIn: 'root',},
  withEntities<Part>(),
  withMethods((
    store,
    partService = inject(PartService),
  ) => ({
      loadParts: rxMethod<void>(
        pipe(
          switchMap(_ => partService.fetchParts()),
          tap(parts => patchState(store, setAllEntities(parts))),
        )
      ),
      addPart: rxMethod<PartCreationRequest>(
        pipe(
          switchMap(part => partService.addPart(part)),
          tap(part => {
            patchState(store, setEntity(part))
          })
        )
      ),
      deletePart: rxMethod<Part>(
        pipe(
          switchMap(todo => partService.deletePart(todo)),
          tap(part => {
            patchState(store, removeEntity(part.id))
          })
        )
      ),
      deleteAllParts: rxMethod<void>(
        pipe(
          switchMap(_ => partService.deleteAllParts().pipe(
            catchError(error => {
              console.error('Fehler beim Löschen aller Teile:', error);
              return EMPTY;
            })
          )),
          tap({
            next: () => patchState(store, setAllEntities<Part>([])),
            error: (error) => {
              console.error('Fehler beim Aktualisieren des Stores:', error);
            }
          })
        )
      ),
      getAllPartsByMsn: rxMethod<{ msn: string }>(
        pipe(
          switchMap(({ msn }) => partService.getAllPartsByMsn(msn)),
          tap(parts => patchState(store, setAllEntities(parts)))
        )
      ),
      uploadExcel: rxMethod<{ file: File; msnId: string }>(
        pipe(
          switchMap(({ file, msnId }) =>
            partService.uploadExcel(file, msnId).pipe(
              catchError(error => {
                console.error('Fehler beim Excel-Upload:', error);
                return EMPTY;
              })
            )
          )
        )
      )
    }
  ))
)
