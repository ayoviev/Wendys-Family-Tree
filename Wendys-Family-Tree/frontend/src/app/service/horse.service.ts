import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { Horse, HorseCreate, HorseSearch, HorseTreeNode } from '../dto/horse';
import { Sex } from '../dto/sex';

const baseUri = environment.backendUrl + '/horses';

@Injectable({
  providedIn: 'root'
})
export class HorseService {

  constructor(
    private http: HttpClient
  ) { }

  /**
   * Get all horses stored in the system
   *
   * @return observable list of found horses.
   */
  getAll(): Observable<Horse[]> {
    return this.http.get<Horse[]>(baseUri);
  }


  /**
   * Create a new horse in the system.
   *
   * @param horse the data for the horse that should be created
   * @return an Observable for the created horse
   */
  create(horse: HorseCreate
  ): Observable<Horse> {
    return this.http.post<Horse>(
      baseUri,
      horse
    );
  }

  /**
   * Get a horse by its id.
   * 
   * @param id the id of the horse that should be retrieved
   * @returns an Observable for the retrieved horse
   */
  getById(id: number): Observable<Horse> {
    return this.http.get<Horse>(baseUri + '/' + id);
  }

  /**
   * Update an existing horse by its id.
   * 
   * @param id the id of the horse that should be updated
   * @param horse the data for the horse that should be updated
   * @returns an Observable for the updated horse
   */
  update(id: number, horse: HorseCreate): Observable<Horse> {
    return this.http.put<Horse>(baseUri + '/' + id, horse);
  }

  /**
   * Delete a horse by its id.
   * 
   * @param id the id of the horse that should be deleted
   * @returns an Observable for the deletion operation
   */
  delete(id: number) : Observable<void> {
    return this.http.delete<void>(baseUri + '/' + id);
  }
  
  /**
  * Search horses, optionally filtered by sex.
  *
  * @param name substring to filter by name
  * @param sex optional sex filter
  * @param limit max number of results
  * @returns an Observable list of matching horses
  */
  searchByName(name: string, sex?: Sex, limit?: number): Observable<Horse[]> {
    let params = new HttpParams().set('name', name);
    if (sex != null) {
      params = params.set('sex', sex);
    }
    if (limit != null) {
      params = params.set('limit', limit);
    }
    return this.http.get<Horse[]>(baseUri, { params });
  }

  /**
   * Search horses by owner's name.
   *
   * @param ownerName substring to filter by owner's name
   * @param limit max number of results
   * @returns an Observable list of matching horses
   */
  searchByOwnerName(ownerName: string, limit?: number): Observable<Horse[]> {
    let params = new HttpParams().set('ownerName', ownerName);
    if (limit != null) {
      params = params.set('limit', limit);
    }
    return this.http.get<Horse[]>(baseUri, { params });
  }

  /**
   * Search for horses using optional filter criteria.
   * All parameters are optional; omitting a parameter means that criterion is not applied.
   * An empty search (no criteria) returns all horses.
   *
   * @param searchParams the search criteria to filter horses by
   * @param searchParams.name substring to match against the horse's name (case-insensitive)
   * @param searchParams.description substring to match against the horse's description (case-insensitive)
   * @param searchParams.bornBefore only return horses born strictly before this date (format: yyyy-MM-dd)
   * @param searchParams.sex filter by biological sex ('MALE' or 'FEMALE')
   * @param searchParams.ownerName substring to match against the owner's full name (case-insensitive)
   * @param searchParams.limit maximum number of results to return
   * @returns an Observable list of horses matching all provided criteria
   */
  search(searchParams: HorseSearch): Observable<Horse[]> {
    let params = new HttpParams();
    if (searchParams.name) {
      params = params.set('name', searchParams.name);
    }
    if (searchParams.description) {
      params = params.set('description', searchParams.description);
    }
    if (searchParams.bornBefore) {
      params = params.set('bornBefore', searchParams.bornBefore);
    }
    if (searchParams.sex) {
      params = params.set('sex', searchParams.sex);
    }
    if (searchParams.ownerName) {
      params = params.set('ownerName', searchParams.ownerName);
    }
    if (searchParams.limit) {
      params = params.set('limit', searchParams.limit);
    }
    return this.http.get<Horse[]>(baseUri, { params });
  }

  getFamilyTree(id: number, generations: number): Observable<HorseTreeNode> {
    return this.http.get<HorseTreeNode>(baseUri + '/' + id + '/familytree', {
      params: new HttpParams().set('generations', generations)
    });
  }
}
