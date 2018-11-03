import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { NgbActiveModal, NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { IProperty } from 'app/shared/model/leboncoinListScrapper/property.model';
import { PropertyService } from './property.service';

@Component({
    selector: 'jhi-property-delete-dialog',
    templateUrl: './property-delete-dialog.component.html'
})
export class PropertyDeleteDialogComponent {

    property: IProperty;

    constructor(
        private propertyService: PropertyService,
        public activeModal: NgbActiveModal,
        private eventManager: JhiEventManager
    ) {
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    confirmDelete(id: string) {
        this.propertyService.delete(id).subscribe((response) => {
            this.eventManager.broadcast({
                name: 'propertyListModification',
                content: 'Deleted an property'
            });
            this.activeModal.dismiss(true);
        });
    }
}

@Component({
    selector: 'jhi-property-delete-popup',
    template: ''
})
export class PropertyDeletePopupComponent implements OnInit, OnDestroy {

    private ngbModalRef: NgbModalRef;

    constructor(
        private activatedRoute: ActivatedRoute,
        private router: Router,
        private modalService: NgbModal
    ) {}

    ngOnInit() {
        this.activatedRoute.data.subscribe(({property}) => {
            setTimeout(() => {
                this.ngbModalRef = this.modalService.open(PropertyDeleteDialogComponent as Component, { size: 'lg', backdrop: 'static'});
                this.ngbModalRef.componentInstance.property = property;
                this.ngbModalRef.result.then((result) => {
                    this.router.navigate([{ outlets: { popup: null }}], { replaceUrl: true, queryParamsHandling: 'merge' });
                    this.ngbModalRef = null;
                }, (reason) => {
                    this.router.navigate([{ outlets: { popup: null }}], { replaceUrl: true, queryParamsHandling: 'merge' });
                    this.ngbModalRef = null;
                });
            }, 0);
        });
    }

    ngOnDestroy() {
        this.ngbModalRef = null;
    }
}
