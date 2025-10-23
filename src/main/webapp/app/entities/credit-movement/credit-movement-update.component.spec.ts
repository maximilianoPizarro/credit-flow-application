/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import dayjs from 'dayjs';
import CreditMovementUpdate from './credit-movement-update.vue';
import CreditMovementService from './credit-movement.service';
import { DATE_TIME_LONG_FORMAT } from '@/shared/composables/date-format';
import AlertService from '@/shared/alert/alert.service';

import LoanService from '@/entities/loan/loan.service';

type CreditMovementUpdateComponentType = InstanceType<typeof CreditMovementUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const creditMovementSample = { id: 123 };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<CreditMovementUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('CreditMovement Management Update Component', () => {
    let comp: CreditMovementUpdateComponentType;
    let creditMovementServiceStub: SinonStubbedInstance<CreditMovementService>;

    beforeEach(() => {
      route = {};
      creditMovementServiceStub = sinon.createStubInstance<CreditMovementService>(CreditMovementService);
      creditMovementServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

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
          creditMovementService: () => creditMovementServiceStub,
          loanService: () =>
            sinon.createStubInstance<LoanService>(LoanService, {
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
        const wrapper = shallowMount(CreditMovementUpdate, { global: mountOptions });
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
        const wrapper = shallowMount(CreditMovementUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.creditMovement = creditMovementSample;
        creditMovementServiceStub.update.resolves(creditMovementSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(creditMovementServiceStub.update.calledWith(creditMovementSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        creditMovementServiceStub.create.resolves(entity);
        const wrapper = shallowMount(CreditMovementUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.creditMovement = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(creditMovementServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        creditMovementServiceStub.find.resolves(creditMovementSample);
        creditMovementServiceStub.retrieve.resolves([creditMovementSample]);

        // WHEN
        route = {
          params: {
            creditMovementId: `${creditMovementSample.id}`,
          },
        };
        const wrapper = shallowMount(CreditMovementUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.creditMovement).toMatchObject(creditMovementSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        creditMovementServiceStub.find.resolves(creditMovementSample);
        const wrapper = shallowMount(CreditMovementUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
