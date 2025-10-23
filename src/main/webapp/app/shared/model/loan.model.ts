import { type IClient } from '@/shared/model/client.model';
import { type ICreditPlan } from '@/shared/model/credit-plan.model';

import { type LoanStatus } from '@/shared/model/enumerations/loan-status.model';
export interface ILoan {
  id?: number;
  loanNumber?: string;
  openingDate?: Date;
  principalAmount?: number;
  outstandingBalance?: number | null;
  status?: keyof typeof LoanStatus;
  closingDate?: Date | null;
  client?: IClient | null;
  plan?: ICreditPlan | null;
}

export class Loan implements ILoan {
  constructor(
    public id?: number,
    public loanNumber?: string,
    public openingDate?: Date,
    public principalAmount?: number,
    public outstandingBalance?: number | null,
    public status?: keyof typeof LoanStatus,
    public closingDate?: Date | null,
    public client?: IClient | null,
    public plan?: ICreditPlan | null,
  ) {}
}
