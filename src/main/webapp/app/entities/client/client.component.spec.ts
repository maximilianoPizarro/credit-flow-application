/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';

import Client from './client.vue';
import ClientService from './client.service';
import AlertService from '@/shared/alert/alert.service';

type ClientComponentType = InstanceType<typeof Client>;

const bModalStub = {
  render: () => {},
  methods: {
    hide: () => {},
    show: () => {},
  },
};

describe('Component Tests', () => {
  let alertService: AlertService;

  describe('Client Management Component', () => {
    let clientServiceStub: SinonStubbedInstance<ClientService>;
    let mountOptions: MountingOptions<ClientComponentType>['global'];

    beforeEach(() => {
      clientServiceStub = sinon.createStubInstance<ClientService>(ClientService);
      clientServiceStub.retrieve.resolves({ headers: {} });

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
          clientService: () => clientServiceStub,
        },
      };
    });

    describe('Mount', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        clientServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

        // WHEN
        const wrapper = shallowMount(Client, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(clientServiceStub.retrieve.calledOnce).toBeTruthy();
        expect(comp.clients[0]).toEqual(expect.objectContaining({ id: 123 }));
      });
    });
    describe('Handles', () => {
      let comp: ClientComponentType;

      beforeEach(async () => {
        const wrapper = shallowMount(Client, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();
        clientServiceStub.retrieve.reset();
        clientServiceStub.retrieve.resolves({ headers: {}, data: [] });
      });

      it('Should call delete service on confirmDelete', async () => {
        // GIVEN
        clientServiceStub.delete.resolves({});

        // WHEN
        comp.prepareRemove({ id: 123 });

        comp.removeClient();
        await comp.$nextTick(); // clear components

        // THEN
        expect(clientServiceStub.delete.called).toBeTruthy();

        // THEN
        await comp.$nextTick(); // handle component clear watch
        expect(clientServiceStub.retrieve.callCount).toEqual(1);
      });
    });
  });
});
