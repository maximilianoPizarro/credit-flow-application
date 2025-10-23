import { type PlanType } from '@/shared/model/enumerations/plan-type.model';
export interface ICreditPlan {
  id?: number;
  name?: string;
  description?: string | null;
  type?: keyof typeof PlanType;
  interestRate?: number;
  maxTermYears?: number | null;
}

export class CreditPlan implements ICreditPlan {
  constructor(
    public id?: number,
    public name?: string,
    public description?: string | null,
    public type?: keyof typeof PlanType,
    public interestRate?: number,
    public maxTermYears?: number | null,
  ) {}
}
