import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {FinalResult} from "./model/final-result";
import {environment} from "../../environments/environment";

const baseUrl = environment.url + '/api/v1';

@Injectable({
  providedIn: 'root'
})
export class BenchmarkService {

  constructor(private HTTP_CLIENT: HttpClient) {
  }

  uploadFile(file: File): Observable<FinalResult> {

    const formData: FormData = new FormData();
    formData.append('file', file);
    return this.HTTP_CLIENT.post<FinalResult>(baseUrl + '/benchmark', formData);
  }

  isAwake(): Observable<boolean> {
    return this.HTTP_CLIENT.get<boolean>(baseUrl + '/awake');
  }
}
