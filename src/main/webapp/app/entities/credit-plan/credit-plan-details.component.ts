import { type Ref, defineComponent, inject, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';

import CreditPlanService from './credit-plan.service';
import useDataUtils from '@/shared/data/data-utils.service';
import { type ICreditPlan } from '@/shared/model/credit-plan.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'CreditPlanDetails',
  setup() {
    const creditPlanService = inject('creditPlanService', () => new CreditPlanService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const dataUtils = useDataUtils();

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const creditPlan: Ref<ICreditPlan> = ref({});

    const retrieveCreditPlan = async creditPlanId => {
      try {
        const res = await creditPlanService().find(creditPlanId);
        creditPlan.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.creditPlanId) {
      retrieveCreditPlan(route.params.creditPlanId);
    }

    return {
      alertService,
      creditPlan,

      ...dataUtils,

      previousState,
      t$: useI18n().t,
    };
  },
});
