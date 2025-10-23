import { type Ref, defineComponent, inject, onMounted, ref } from 'vue';
import { useI18n } from 'vue-i18n';

import CreditPlanService from './credit-plan.service';
import { type ICreditPlan } from '@/shared/model/credit-plan.model';
import useDataUtils from '@/shared/data/data-utils.service';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'CreditPlan',
  setup() {
    const { t: t$ } = useI18n();
    const dataUtils = useDataUtils();
    const creditPlanService = inject('creditPlanService', () => new CreditPlanService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const creditPlans: Ref<ICreditPlan[]> = ref([]);

    const isFetching = ref(false);

    const clear = () => {};

    const retrieveCreditPlans = async () => {
      isFetching.value = true;
      try {
        const res = await creditPlanService().retrieve();
        creditPlans.value = res.data;
      } catch (err) {
        alertService.showHttpError(err.response);
      } finally {
        isFetching.value = false;
      }
    };

    const handleSyncList = () => {
      retrieveCreditPlans();
    };

    onMounted(async () => {
      await retrieveCreditPlans();
    });

    const removeId: Ref<number> = ref(null);
    const removeEntity = ref<any>(null);
    const prepareRemove = (instance: ICreditPlan) => {
      removeId.value = instance.id;
      removeEntity.value.show();
    };
    const closeDialog = () => {
      removeEntity.value.hide();
    };
    const removeCreditPlan = async () => {
      try {
        await creditPlanService().delete(removeId.value);
        const message = t$('creditFlowAppicationApp.creditPlan.deleted', { param: removeId.value }).toString();
        alertService.showInfo(message, { variant: 'danger' });
        removeId.value = null;
        retrieveCreditPlans();
        closeDialog();
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    return {
      creditPlans,
      handleSyncList,
      isFetching,
      retrieveCreditPlans,
      clear,
      removeId,
      removeEntity,
      prepareRemove,
      closeDialog,
      removeCreditPlan,
      t$,
      ...dataUtils,
    };
  },
});
