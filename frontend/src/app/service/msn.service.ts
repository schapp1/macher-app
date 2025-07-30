import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Msn, MsnCreationRequest} from '../models/msn';
import {map, Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class MsnService {

  private readonly API = 'http://localhost:8080/api/msn';

  constructor(
    private http: HttpClient
  ) {
  }

  fetchMsns(): Observable<Msn[]> {
    return this.http.get<Msn[]>(this.API);
  }

  addMsn(msnCreationRequest: MsnCreationRequest): Observable<Msn> {
    return this.http.post<Msn>(this.API, msnCreationRequest);
  }

  deleteMsn(msn: Msn): Observable<Msn> {
    return this.http.delete<void>(`${this.API}/${msn.id}`).pipe(
      map(() => msn)
    );
  }
}
