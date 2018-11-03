/* tslint:disable max-line-length */
import { TestBed, getTestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { of } from 'rxjs';
import { take, map } from 'rxjs/operators';
import { PropertyService } from 'app/entities/leboncoinListScrapper/property/property.service';
import { IProperty, Property,} from 'app/shared/model/leboncoinListScrapper/property.model';

describe('Service Tests', () => {

    describe('Property Service', () => {
        let injector: TestBed;
        let service: PropertyService;
        let httpMock: HttpTestingController;
        let elemDefault: IProperty;
        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [
                    HttpClientTestingModule
                ]
            });
            injector = getTestBed();
            service = injector.get(PropertyService);
            httpMock = injector.get(HttpTestingController);

            elemDefault = new Property(
                'ID',
            )
        });

        describe('Service methods', async () => {
            it('should find an element', async () => {
                const returnedFromService = Object.assign({
                }, elemDefault);
                service
                    .find('123')
                    .pipe(take(1)).subscribe(resp => expect(resp).toMatchObject({body: elemDefault}));

                const req  = httpMock.expectOne({ method: 'GET' });
                req.flush(JSON.stringify(returnedFromService));
            });

            it('should create a Property', async () => {
                const returnedFromService = Object.assign({
                    id: 'ID',
                }, elemDefault);
                const expected = Object.assign({
                }, returnedFromService);
                service
                    .create(new Property(null))
                    .pipe(take(1)).subscribe(resp => expect(resp).toMatchObject({body: expected}));
                const req  = httpMock.expectOne({ method: 'POST' });
                req.flush(JSON.stringify(returnedFromService));
            });

            it('should update a Property', async () => {
                const returnedFromService = Object.assign({
                }, elemDefault);

                const expected = Object.assign({
                }, returnedFromService);
                service
                    .update(expected)
                    .pipe(take(1)).subscribe(resp => expect(resp).toMatchObject({body: expected}));
                const req  = httpMock.expectOne({ method: 'PUT' });
                req.flush(JSON.stringify(returnedFromService));
            });

            it('should return a list of Property', async () => {
                const returnedFromService = Object.assign({
                }, elemDefault);
                const expected = Object.assign({
                }, returnedFromService);
                service
                    .query(expected)
                    .pipe(
                        take(1),
                        map(resp => resp.body)
                    ).subscribe(body  => expect(body).toContainEqual(expected));
                const req  = httpMock.expectOne({ method: 'GET' });
                req.flush(JSON.stringify([returnedFromService]));
                httpMock.verify();

            });

            it('should delete a Property', async () => {
                const rxPromise = service
                    .delete('123').subscribe(resp => expect(resp.ok));

                const req  = httpMock.expectOne({ method: 'DELETE' });
                req.flush({ status: 200 });
            });
        });

        afterEach(() => {
            httpMock.verify();
        });
    });
});
