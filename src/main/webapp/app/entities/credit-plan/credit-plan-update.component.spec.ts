/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import CreditPlanUpdate from './credit-plan-update.vue';
import CreditPlanService from './credit-plan.service';
import AlertService from '@/shared/alert/alert.service';

type CreditPlanUpdateComponentType = InstanceType<typeof CreditPlanUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const creditPlanSample = { id: 123 };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<CreditPlanUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('CreditPlan Management Update Component', () => {
    let comp: CreditPlanUpdateComponentType;
    let creditPlanServiceStub: SinonStubbedInstance<CreditPlanService>;

    beforeEach(() => {
      route = {};
      creditPlanServiceStub = sinon.createStubInstance<CreditPlanService>(CreditPlanService);
      creditPlanServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

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
          creditPlanService: () => creditPlanServiceStub,
        },
      };
    });

    afterEach(() => {
      vitest.resetAllMocks();
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', async () => {
        // GIVEN
        const wrapper = shallowMount(CreditPlanUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.creditPlan = creditPlanSample;
        creditPlanServiceStub.update.resolves(creditPlanSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(creditPlanServiceStub.update.calledWith(creditPlanSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        creditPlanServiceStub.create.resolves(entity);
        const wrapper = shallowMount(CreditPlanUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.creditPlan = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(creditPlanServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        creditPlanServiceStub.find.resolves(creditPlanSample);
        creditPlanServiceStub.retrieve.resolves([creditPlanSample]);

        // WHEN
        route = {
          params: {
            creditPlanId: `${creditPlanSample.id}`,
          },
        };
        const wrapper = shallowMount(CreditPlanUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.creditPlan).toMatchObject(creditPlanSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        creditPlanServiceStub.find.resolves(creditPlanSample);
        const wrapper = shallowMount(CreditPlanUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
