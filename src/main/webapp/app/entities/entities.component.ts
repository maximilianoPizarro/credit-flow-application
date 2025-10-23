import { defineComponent, provide } from 'vue';

import ClientService from './client/client.service';
import CreditPlanService from './credit-plan/credit-plan.service';
import LoanService from './loan/loan.service';
import CreditMovementService from './credit-movement/credit-movement.service';
import UserService from '@/entities/user/user.service';
// jhipster-needle-add-entity-service-to-entities-component-import - JHipster will import entities services here

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'Entities',
  setup() {
    provide('userService', () => new UserService());
    provide('clientService', () => new ClientService());
    provide('creditPlanService', () => new CreditPlanService());
    provide('loanService', () => new LoanService());
    provide('creditMovementService', () => new CreditMovementService());
    // jhipster-needle-add-entity-service-to-entities-component - JHipster will import entities services here
  },
});
