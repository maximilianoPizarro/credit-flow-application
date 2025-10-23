/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import CreditPlanDetails from './credit-plan-details.vue';
import CreditPlanService from './credit-plan.service';
import AlertService from '@/shared/alert/alert.service';

type CreditPlanDetailsComponentType = InstanceType<typeof CreditPlanDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const creditPlanSample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('CreditPlan Management Detail Component', () => {
    let creditPlanServiceStub: SinonStubbedInstance<CreditPlanService>;
    let mountOptions: MountingOptions<CreditPlanDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      creditPlanServiceStub = sinon.createStubInstance<CreditPlanService>(CreditPlanService);

      alertService = new AlertService({
        i18n: { t: vitest.fn() } as any,
        bvToast: {
          toast: vitest.fn(),
        } as any,
      });

      mountOptions = {
        stubs: {
          'font-awesome-icon': true,
          'router-link': true,
        },
        provide: {
          alertService,
          creditPlanService: () => creditPlanServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        creditPlanServiceStub.find.resolves(creditPlanSample);
        route = {
          params: {
            creditPlanId: `${123}`,
          },
        };
        const wrapper = shallowMount(CreditPlanDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.creditPlan).toMatchObject(creditPlanSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        creditPlanServiceStub.find.resolves(creditPlanSample);
        const wrapper = shallowMount(CreditPlanDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
