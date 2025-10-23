/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import CreditMovementDetails from './credit-movement-details.vue';
import CreditMovementService from './credit-movement.service';
import AlertService from '@/shared/alert/alert.service';

type CreditMovementDetailsComponentType = InstanceType<typeof CreditMovementDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const creditMovementSample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('CreditMovement Management Detail Component', () => {
    let creditMovementServiceStub: SinonStubbedInstance<CreditMovementService>;
    let mountOptions: MountingOptions<CreditMovementDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      creditMovementServiceStub = sinon.createStubInstance<CreditMovementService>(CreditMovementService);

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
          creditMovementService: () => creditMovementServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        creditMovementServiceStub.find.resolves(creditMovementSample);
        route = {
          params: {
            creditMovementId: `${123}`,
          },
        };
        const wrapper = shallowMount(CreditMovementDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.creditMovement).toMatchObject(creditMovementSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        creditMovementServiceStub.find.resolves(creditMovementSample);
        const wrapper = shallowMount(CreditMovementDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
