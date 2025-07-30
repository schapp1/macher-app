import {patchState, signalStore, withMethods} from '@ngrx/signals';
import {removeEntity, setAllEntities, setEntity, withEntities} from '@ngrx/signals/entities';
import {inject} from '@angular/core';
import {rxMethod} from '@ngrx/signals/rxjs-interop';
import {pipe, switchMap, tap} from 'rxjs';
import {Msn, MsnCreationRequest} from '../models/msn';
import {MsnService} from '../service/msn.service';

export const MsnStore = signalStore(
  {providedIn: 'root',},
  withEntities<Msn>(),
  withMethods((
    store,
    msnService = inject(MsnService),
  ) => ({
      loadMsn: rxMethod<void>(
        pipe(
          switchMap(_ => msnService.fetchMsns()),
          tap(msn => patchState(store, setAllEntities(msn))),
        )
      ),
      addMsn: rxMethod<MsnCreationRequest>(
        pipe(
          switchMap(msn => msnService.addMsn(msn)),
          tap(msn => {
            patchState(store, setEntity(msn))
          })
        )
      ),
      deleteMsn: rxMethod<Msn>(
        pipe(
          switchMap(msn => msnService.deleteMsn(msn)),
          tap(msn => {
            patchState(store, removeEntity(msn.id))
          })
        )
      ),
    }
  ))
)
