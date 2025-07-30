import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {map, Observable} from 'rxjs';
import {Part, PartCreationRequest} from '../models/part';

@Injectable({
  providedIn: 'root'
})
export class PartService {

  private readonly API = 'http://localhost:8080/api/parts';

  constructor(
    private http: HttpClient
  ) { }

  fetchParts(): Observable<Part[]> {
    return this.http.get<Part[]>(this.API);
  }

  addPart(partCreationRequest: PartCreationRequest): Observable<Part> {
    return this.http.post<Part>(this.API, partCreationRequest);
  }

  deletePart(part: Part): Observable<Part> {
    return this.http.delete<void>(`${this.API}/${part.id}`).pipe(
      map(() => part)
    )
  }

  deleteAllParts(): Observable<void> {
    return this.http.delete<void>(this.API);
  }

  uploadExcel(file: File, msnId: string): Observable<any> {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('msnId', msnId);
    return this.http.post<any>(`${this.API}/upload-excel`, formData);
  }

  getAllPartsByMsn(msn: string): Observable<Part[]> {
    return this.http.get<Part[]>(`${this.API}/msn/${msn}`);
  }
}
