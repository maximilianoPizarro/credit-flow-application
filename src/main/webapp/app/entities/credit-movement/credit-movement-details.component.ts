import { type Ref, defineComponent, inject, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';

import CreditMovementService from './credit-movement.service';
import { useDateFormat } from '@/shared/composables';
import { type ICreditMovement } from '@/shared/model/credit-movement.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'CreditMovementDetails',
  setup() {
    const dateFormat = useDateFormat();
    const creditMovementService = inject('creditMovementService', () => new CreditMovementService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const creditMovement: Ref<ICreditMovement> = ref({});

    const retrieveCreditMovement = async creditMovementId => {
      try {
        const res = await creditMovementService().find(creditMovementId);
        creditMovement.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.creditMovementId) {
      retrieveCreditMovement(route.params.creditMovementId);
    }

    return {
      ...dateFormat,
      alertService,
      creditMovement,

      previousState,
      t$: useI18n().t,
    };
  },
});
