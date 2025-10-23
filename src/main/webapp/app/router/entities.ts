import { Authority } from '@/shared/security/authority';
/* tslint:disable */
// prettier-ignore
const Entities = () => import('@/entities/entities.vue');

const Client = () => import('@/entities/client/client.vue');
const ClientUpdate = () => import('@/entities/client/client-update.vue');
const ClientDetails = () => import('@/entities/client/client-details.vue');

const CreditPlan = () => import('@/entities/credit-plan/credit-plan.vue');
const CreditPlanUpdate = () => import('@/entities/credit-plan/credit-plan-update.vue');
const CreditPlanDetails = () => import('@/entities/credit-plan/credit-plan-details.vue');

const Loan = () => import('@/entities/loan/loan.vue');
const LoanUpdate = () => import('@/entities/loan/loan-update.vue');
const LoanDetails = () => import('@/entities/loan/loan-details.vue');

const CreditMovement = () => import('@/entities/credit-movement/credit-movement.vue');
const CreditMovementUpdate = () => import('@/entities/credit-movement/credit-movement-update.vue');
const CreditMovementDetails = () => import('@/entities/credit-movement/credit-movement-details.vue');

// jhipster-needle-add-entity-to-router-import - JHipster will import entities to the router here

export default {
  path: '/',
  component: Entities,
  children: [
    {
      path: 'client',
      name: 'Client',
      component: Client,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'client/new',
      name: 'ClientCreate',
      component: ClientUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'client/:clientId/edit',
      name: 'ClientEdit',
      component: ClientUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'client/:clientId/view',
      name: 'ClientView',
      component: ClientDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'credit-plan',
      name: 'CreditPlan',
      component: CreditPlan,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'credit-plan/new',
      name: 'CreditPlanCreate',
      component: CreditPlanUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'credit-plan/:creditPlanId/edit',
      name: 'CreditPlanEdit',
      component: CreditPlanUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'credit-plan/:creditPlanId/view',
      name: 'CreditPlanView',
      component: CreditPlanDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'loan',
      name: 'Loan',
      component: Loan,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'loan/new',
      name: 'LoanCreate',
      component: LoanUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'loan/:loanId/edit',
      name: 'LoanEdit',
      component: LoanUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'loan/:loanId/view',
      name: 'LoanView',
      component: LoanDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'credit-movement',
      name: 'CreditMovement',
      component: CreditMovement,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'credit-movement/new',
      name: 'CreditMovementCreate',
      component: CreditMovementUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'credit-movement/:creditMovementId/edit',
      name: 'CreditMovementEdit',
      component: CreditMovementUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'credit-movement/:creditMovementId/view',
      name: 'CreditMovementView',
      component: CreditMovementDetails,
      meta: { authorities: [Authority.USER] },
    },
    // jhipster-needle-add-entity-to-router - JHipster will add entities to the router here
  ],
};
