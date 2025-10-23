/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import LoanDetails from './loan-details.vue';
import LoanService from './loan.service';
import AlertService from '@/shared/alert/alert.service';

type LoanDetailsComponentType = InstanceType<typeof LoanDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const loanSample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('Loan Management Detail Component', () => {
    let loanServiceStub: SinonStubbedInstance<LoanService>;
    let mountOptions: MountingOptions<LoanDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      loanServiceStub = sinon.createStubInstance<LoanService>(LoanService);

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
          loanService: () => loanServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        loanServiceStub.find.resolves(loanSample);
        route = {
          params: {
            loanId: `${123}`,
          },
        };
        const wrapper = shallowMount(LoanDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.loan).toMatchObject(loanSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        loanServiceStub.find.resolves(loanSample);
        const wrapper = shallowMount(LoanDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
