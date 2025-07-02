import {patchState, signalStore, withMethods} from '@ngrx/signals';
import {setAllEntities, withEntities} from '@ngrx/signals/entities';
import {Todo} from '../models/todo';
import {TodoService} from '../service/todo.service';
import {inject} from '@angular/core';
import {rxMethod} from '@ngrx/signals/rxjs-interop';
import {pipe, switchMap, tap} from 'rxjs';

export const TodoStore = signalStore(
  {providedIn: 'root',},
  withEntities<Todo>(),
  withMethods((
    store,
    todoService = inject(TodoService),
  ) => ({
    loadTodos: rxMethod<void>(
      pipe(
        switchMap(_ => todoService.fetchTodos()),
        tap(todos =>  patchState(store, setAllEntities(todos))),
      )
    ),
  }))
)
