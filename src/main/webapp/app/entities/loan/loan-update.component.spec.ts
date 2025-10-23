/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import dayjs from 'dayjs';
import LoanUpdate from './loan-update.vue';
import LoanService from './loan.service';
import { DATE_TIME_LONG_FORMAT } from '@/shared/composables/date-format';
import AlertService from '@/shared/alert/alert.service';

import ClientService from '@/entities/client/client.service';
import CreditPlanService from '@/entities/credit-plan/credit-plan.service';

type LoanUpdateComponentType = InstanceType<typeof LoanUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const loanSample = { id: 123 };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<LoanUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('Loan Management Update Component', () => {
    let comp: LoanUpdateComponentType;
    let loanServiceStub: SinonStubbedInstance<LoanService>;

    beforeEach(() => {
      route = {};
      loanServiceStub = sinon.createStubInstance<LoanService>(LoanService);
      loanServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

      alertService = new AlertService({
        i18n: { t: vitest.fn() } as any,
        bvToast: {
          toast: vitest.fn(),
        } as any,
      });

      mountOptions = {
        stubs: {
          'font-awesome-icon': true,
          'b-input-group': true,
          'b-input-group-prepend': true,
          'b-form-datepicker': true,
          'b-form-input': true,
        },
        provide: {
          alertService,
          loanService: () => loanServiceStub,
          clientService: () =>
            sinon.createStubInstance<ClientService>(ClientService, {
              retrieve: sinon.stub().resolves({}),
            } as any),
          creditPlanService: () =>
            sinon.createStubInstance<CreditPlanService>(CreditPlanService, {
              retrieve: sinon.stub().resolves({}),
            } as any),
        },
      };
    });

    afterEach(() => {
      vitest.resetAllMocks();
    });

    describe('load', () => {
      beforeEach(() => {
        const wrapper = shallowMount(LoanUpdate, { global: mountOptions });
        comp = wrapper.vm;
      });
      it('Should convert date from string', () => {
        // GIVEN
        const date = new Date('2019-10-15T11:42:02Z');

        // WHEN
        const convertedDate = comp.convertDateTimeFromServer(date);

        // THEN
        expect(convertedDate).toEqual(dayjs(date).format(DATE_TIME_LONG_FORMAT));
      });

      it('Should not convert date if date is not present', () => {
        expect(comp.convertDateTimeFromServer(null)).toBeNull();
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', async () => {
        // GIVEN
        const wrapper = shallowMount(LoanUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.loan = loanSample;
        loanServiceStub.update.resolves(loanSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(loanServiceStub.update.calledWith(loanSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        loanServiceStub.create.resolves(entity);
        const wrapper = shallowMount(LoanUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.loan = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(loanServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        loanServiceStub.find.resolves(loanSample);
        loanServiceStub.retrieve.resolves([loanSample]);

        // WHEN
        route = {
          params: {
            loanId: `${loanSample.id}`,
          },
        };
        const wrapper = shallowMount(LoanUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.loan).toMatchObject(loanSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        loanServiceStub.find.resolves(loanSample);
        const wrapper = shallowMount(LoanUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
