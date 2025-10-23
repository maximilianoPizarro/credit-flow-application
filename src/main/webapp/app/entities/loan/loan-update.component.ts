import { type Ref, computed, defineComponent, inject, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import LoanService from './loan.service';
import { useDateFormat, useValidation } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import ClientService from '@/entities/client/client.service';
import { type IClient } from '@/shared/model/client.model';
import CreditPlanService from '@/entities/credit-plan/credit-plan.service';
import { type ICreditPlan } from '@/shared/model/credit-plan.model';
import { type ILoan, Loan } from '@/shared/model/loan.model';
import { LoanStatus } from '@/shared/model/enumerations/loan-status.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'LoanUpdate',
  setup() {
    const loanService = inject('loanService', () => new LoanService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const loan: Ref<ILoan> = ref(new Loan());

    const clientService = inject('clientService', () => new ClientService());

    const clients: Ref<IClient[]> = ref([]);

    const creditPlanService = inject('creditPlanService', () => new CreditPlanService());

    const creditPlans: Ref<ICreditPlan[]> = ref([]);
    const loanStatusValues: Ref<string[]> = ref(Object.keys(LoanStatus));
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'en'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveLoan = async loanId => {
      try {
        const res = await loanService().find(loanId);
        res.openingDate = new Date(res.openingDate);
        res.closingDate = new Date(res.closingDate);
        loan.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.loanId) {
      retrieveLoan(route.params.loanId);
    }

    const initRelationships = () => {
      clientService()
        .retrieve()
        .then(res => {
          clients.value = res.data;
        });
      creditPlanService()
        .retrieve()
        .then(res => {
          creditPlans.value = res.data;
        });
    };

    initRelationships();

    const { t: t$ } = useI18n();
    const validations = useValidation();
    const validationRules = {
      loanNumber: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      openingDate: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      principalAmount: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      outstandingBalance: {},
      status: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      closingDate: {},
      movements: {},
      client: {},
      plan: {},
    };
    const v$ = useVuelidate(validationRules, loan as any);
    v$.value.$validate();

    return {
      loanService,
      alertService,
      loan,
      previousState,
      loanStatusValues,
      isSaving,
      currentLanguage,
      clients,
      creditPlans,
      v$,
      ...useDateFormat({ entityRef: loan }),
      t$,
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.loan.id) {
        this.loanService()
          .update(this.loan)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(this.t$('creditFlowAppicationApp.loan.updated', { param: param.id }));
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.loanService()
          .create(this.loan)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(this.t$('creditFlowAppicationApp.loan.created', { param: param.id }).toString());
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
