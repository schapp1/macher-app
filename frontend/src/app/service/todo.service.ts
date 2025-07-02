import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Todo} from '../models/todo';

@Injectable({
  providedIn: 'root'
})
export class TodoService {

  private readonly API = 'http://localhost:8080/api/todos';

  constructor(
    private http: HttpClient
  ) {
  }


  fetchTodos(): Observable<Todo[]> {
    return this.http.get<Todo[]>(this.API);
  }
}
