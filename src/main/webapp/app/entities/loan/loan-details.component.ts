import { type Ref, defineComponent, inject, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';

import LoanService from './loan.service';
import { useDateFormat } from '@/shared/composables';
import { type ILoan } from '@/shared/model/loan.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'LoanDetails',
  setup() {
    const dateFormat = useDateFormat();
    const loanService = inject('loanService', () => new LoanService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const loan: Ref<ILoan> = ref({});

    const retrieveLoan = async loanId => {
      try {
        const res = await loanService().find(loanId);
        loan.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.loanId) {
      retrieveLoan(route.params.loanId);
    }

    return {
      ...dateFormat,
      alertService,
      loan,

      previousState,
      t$: useI18n().t,
    };
  },
});
