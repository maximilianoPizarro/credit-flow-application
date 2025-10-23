import { type Ref, computed, defineComponent, inject, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import CreditPlanService from './credit-plan.service';
import useDataUtils from '@/shared/data/data-utils.service';
import { useValidation } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import { CreditPlan, type ICreditPlan } from '@/shared/model/credit-plan.model';
import { PlanType } from '@/shared/model/enumerations/plan-type.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'CreditPlanUpdate',
  setup() {
    const creditPlanService = inject('creditPlanService', () => new CreditPlanService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const creditPlan: Ref<ICreditPlan> = ref(new CreditPlan());
    const planTypeValues: Ref<string[]> = ref(Object.keys(PlanType));
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'en'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

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

    const initRelationships = () => {};

    initRelationships();

    const dataUtils = useDataUtils();

    const { t: t$ } = useI18n();
    const validations = useValidation();
    const validationRules = {
      name: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      description: {},
      type: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      interestRate: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      maxTermYears: {},
      loans: {},
    };
    const v$ = useVuelidate(validationRules, creditPlan as any);
    v$.value.$validate();

    return {
      creditPlanService,
      alertService,
      creditPlan,
      previousState,
      planTypeValues,
      isSaving,
      currentLanguage,
      ...dataUtils,
      v$,
      t$,
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.creditPlan.id) {
        this.creditPlanService()
          .update(this.creditPlan)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(this.t$('creditFlowAppicationApp.creditPlan.updated', { param: param.id }));
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.creditPlanService()
          .create(this.creditPlan)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(this.t$('creditFlowAppicationApp.creditPlan.created', { param: param.id }).toString());
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
