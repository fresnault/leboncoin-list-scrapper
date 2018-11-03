import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpErrorResponse,HttpResponse } from '@angular/common/http';
import { Subscription } from 'rxjs';
import { JhiEventManager, JhiAlertService } from 'ng-jhipster';

import { IProperty } from 'app/shared/model/leboncoinListScrapper/property.model';
import { Principal } from 'app/core';
import { PropertyService } from './property.service';

@Component({
    selector: 'jhi-property',
    templateUrl: './property.component.html'
})
export class PropertyComponent implements OnInit, OnDestroy {
    properties: IProperty[];
    currentAccount: any;
    eventSubscriber: Subscription;

    constructor(
        private propertyService: PropertyService,
        private jhiAlertService: JhiAlertService,
        private eventManager: JhiEventManager,
        private principal: Principal
    ) {
    }

    loadAll() {
        this.propertyService.query().subscribe(
            (res: HttpResponse<IProperty[]>) => {
                this.properties = res.body;
            },
            (res: HttpErrorResponse) => this.onError(res.message)
        );
    }

    ngOnInit() {
        this.loadAll();
        this.principal.identity().then((account) => {
            this.currentAccount = account;
        });
        this.registerChangeInProperties();
    }

    ngOnDestroy() {
        this.eventManager.destroy(this.eventSubscriber);
    }

    trackId(index: number, item: IProperty) {
        return item.id;
    }

    registerChangeInProperties() {
        this.eventSubscriber = this.eventManager.subscribe('propertyListModification', (response) => this.loadAll());
    }

    private onError(errorMessage: string) {
        this.jhiAlertService.error(errorMessage, null, null);
    }
}
