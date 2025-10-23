import { type Ref, defineComponent, inject, onMounted, ref } from 'vue';
import { useI18n } from 'vue-i18n';

import ClientService from './client.service';
import { type IClient } from '@/shared/model/client.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'Client',
  setup() {
    const { t: t$ } = useI18n();
    const clientService = inject('clientService', () => new ClientService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const clients: Ref<IClient[]> = ref([]);

    const isFetching = ref(false);

    const clear = () => {};

    const retrieveClients = async () => {
      isFetching.value = true;
      try {
        const res = await clientService().retrieve();
        clients.value = res.data;
      } catch (err) {
        alertService.showHttpError(err.response);
      } finally {
        isFetching.value = false;
      }
    };

    const handleSyncList = () => {
      retrieveClients();
    };

    onMounted(async () => {
      await retrieveClients();
    });

    const removeId: Ref<number> = ref(null);
    const removeEntity = ref<any>(null);
    const prepareRemove = (instance: IClient) => {
      removeId.value = instance.id;
      removeEntity.value.show();
    };
    const closeDialog = () => {
      removeEntity.value.hide();
    };
    const removeClient = async () => {
      try {
        await clientService().delete(removeId.value);
        const message = t$('creditFlowAppicationApp.client.deleted', { param: removeId.value }).toString();
        alertService.showInfo(message, { variant: 'danger' });
        removeId.value = null;
        retrieveClients();
        closeDialog();
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    return {
      clients,
      handleSyncList,
      isFetching,
      retrieveClients,
      clear,
      removeId,
      removeEntity,
      prepareRemove,
      closeDialog,
      removeClient,
      t$,
    };
  },
});
