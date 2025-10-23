/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';

import CreditPlan from './credit-plan.vue';
import CreditPlanService from './credit-plan.service';
import AlertService from '@/shared/alert/alert.service';

type CreditPlanComponentType = InstanceType<typeof CreditPlan>;

const bModalStub = {
  render: () => {},
  methods: {
    hide: () => {},
    show: () => {},
  },
};

describe('Component Tests', () => {
  let alertService: AlertService;

  describe('CreditPlan Management Component', () => {
    let creditPlanServiceStub: SinonStubbedInstance<CreditPlanService>;
    let mountOptions: MountingOptions<CreditPlanComponentType>['global'];

    beforeEach(() => {
      creditPlanServiceStub = sinon.createStubInstance<CreditPlanService>(CreditPlanService);
      creditPlanServiceStub.retrieve.resolves({ headers: {} });

      alertService = new AlertService({
        i18n: { t: vitest.fn() } as any,
        bvToast: {
          toast: vitest.fn(),
        } as any,
      });

      mountOptions = {
        stubs: {
          bModal: bModalStub as any,
          'font-awesome-icon': true,
          'b-badge': true,
          'b-button': true,
          'router-link': true,
        },
        directives: {
          'b-modal': {},
        },
        provide: {
          alertService,
          creditPlanService: () => creditPlanServiceStub,
        },
      };
    });

    describe('Mount', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        creditPlanServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

        // WHEN
        const wrapper = shallowMount(CreditPlan, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(creditPlanServiceStub.retrieve.calledOnce).toBeTruthy();
        expect(comp.creditPlans[0]).toEqual(expect.objectContaining({ id: 123 }));
      });
    });
    describe('Handles', () => {
      let comp: CreditPlanComponentType;

      beforeEach(async () => {
        const wrapper = shallowMount(CreditPlan, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();
        creditPlanServiceStub.retrieve.reset();
        creditPlanServiceStub.retrieve.resolves({ headers: {}, data: [] });
      });

      it('Should call delete service on confirmDelete', async () => {
        // GIVEN
        creditPlanServiceStub.delete.resolves({});

        // WHEN
        comp.prepareRemove({ id: 123 });

        comp.removeCreditPlan();
        await comp.$nextTick(); // clear components

        // THEN
        expect(creditPlanServiceStub.delete.called).toBeTruthy();

        // THEN
        await comp.$nextTick(); // handle component clear watch
        expect(creditPlanServiceStub.retrieve.callCount).toEqual(1);
      });
    });
  });
});
