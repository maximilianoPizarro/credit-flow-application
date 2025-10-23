import { type DocumentType } from '@/shared/model/enumerations/document-type.model';
export interface IClient {
  id?: number;
  clientNumber?: number;
  firstName?: string;
  lastName?: string;
  dateOfBirth?: Date;
  documentType?: keyof typeof DocumentType;
  documentNumber?: string;
  email?: string | null;
  phone?: string | null;
}

export class Client implements IClient {
  constructor(
    public id?: number,
    public clientNumber?: number,
    public firstName?: string,
    public lastName?: string,
    public dateOfBirth?: Date,
    public documentType?: keyof typeof DocumentType,
    public documentNumber?: string,
    public email?: string | null,
    public phone?: string | null,
  ) {}
}
