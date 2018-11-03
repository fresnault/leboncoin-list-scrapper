import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { IProperty } from 'app/shared/model/leboncoinListScrapper/property.model';
import { PropertyService } from './property.service';

@Component({
    selector: 'jhi-property-update',
    templateUrl: './property-update.component.html'
})
export class PropertyUpdateComponent implements OnInit {

    property: IProperty;
    isSaving: boolean;

    constructor(
        private propertyService: PropertyService,
        private activatedRoute: ActivatedRoute
    ) {
    }

    ngOnInit() {
        this.isSaving = false;
        this.activatedRoute.data.subscribe(({property}) => {
            this.property = property;
        });
    }

    previousState() {
        window.history.back();
    }

    save() {
        this.isSaving = true;
        if (this.property.id !== undefined) {
            this.subscribeToSaveResponse(
                this.propertyService.update(this.property));
        } else {
            this.subscribeToSaveResponse(
                this.propertyService.create(this.property));
        }
    }

    private subscribeToSaveResponse(result: Observable<HttpResponse<IProperty>>) {
        result.subscribe((res: HttpResponse<IProperty>) =>
            this.onSaveSuccess(), (res: HttpErrorResponse) => this.onSaveError());
    }

    private onSaveSuccess() {
        this.isSaving = false;
        this.previousState();
    }

    private onSaveError() {
        this.isSaving = false;
    }
}
