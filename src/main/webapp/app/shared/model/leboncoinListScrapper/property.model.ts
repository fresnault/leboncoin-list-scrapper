

export interface IProperty {
    id?: string;
}

export class Property implements IProperty {
    constructor(
        public id?: string,
    ) {
    }
}
