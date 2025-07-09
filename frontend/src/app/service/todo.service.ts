import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {map, Observable} from 'rxjs';
import {Todo, TodoCreationRequest} from '../models/todo';

@Injectable({
  providedIn: 'root'
})
export class TodoService {

  private readonly API = 'http://localhost:8080/api/todos';

  constructor(
    private http: HttpClient
  ) { }

  fetchTodos(): Observable<Todo[]> {
    return this.http.get<Todo[]>(this.API);
  }

  addTodo(todoCreationRequest: TodoCreationRequest): Observable<Todo> {
    return this.http.post<Todo>(this.API, todoCreationRequest);
  }

  deleteTodo(todo: Todo): Observable<Todo> {
    return this.http.delete<void>(`${this.API}/${todo.id}`).pipe(
      map(() => todo)
    )
  }

  uploadExcel(file: File): Observable<any> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<any>(`${this.API}/upload-excel`, formData);
  }
}
