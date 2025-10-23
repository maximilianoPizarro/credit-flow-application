import { type ILoan } from '@/shared/model/loan.model';

import { type MovementType } from '@/shared/model/enumerations/movement-type.model';
export interface ICreditMovement {
  id?: number;
  movementDate?: Date;
  type?: keyof typeof MovementType;
  amount?: number;
  description?: string | null;
  externalReference?: string | null;
  loan?: ILoan | null;
}

export class CreditMovement implements ICreditMovement {
  constructor(
    public id?: number,
    public movementDate?: Date,
    public type?: keyof typeof MovementType,
    public amount?: number,
    public description?: string | null,
    public externalReference?: string | null,
    public loan?: ILoan | null,
  ) {}
}
