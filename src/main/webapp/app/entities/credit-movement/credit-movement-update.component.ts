import { type Ref, computed, defineComponent, inject, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import CreditMovementService from './credit-movement.service';
import { useDateFormat, useValidation } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import LoanService from '@/entities/loan/loan.service';
import { type ILoan } from '@/shared/model/loan.model';
import { CreditMovement, type ICreditMovement } from '@/shared/model/credit-movement.model';
import { MovementType } from '@/shared/model/enumerations/movement-type.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'CreditMovementUpdate',
  setup() {
    const creditMovementService = inject('creditMovementService', () => new CreditMovementService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const creditMovement: Ref<ICreditMovement> = ref(new CreditMovement());

    const loanService = inject('loanService', () => new LoanService());

    const loans: Ref<ILoan[]> = ref([]);
    const movementTypeValues: Ref<string[]> = ref(Object.keys(MovementType));
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'en'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveCreditMovement = async creditMovementId => {
      try {
        const res = await creditMovementService().find(creditMovementId);
        res.movementDate = new Date(res.movementDate);
        creditMovement.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.creditMovementId) {
      retrieveCreditMovement(route.params.creditMovementId);
    }

    const initRelationships = () => {
      loanService()
        .retrieve()
        .then(res => {
          loans.value = res.data;
        });
    };

    initRelationships();

    const { t: t$ } = useI18n();
    const validations = useValidation();
    const validationRules = {
      movementDate: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      type: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      amount: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      description: {},
      externalReference: {},
      loan: {},
    };
    const v$ = useVuelidate(validationRules, creditMovement as any);
    v$.value.$validate();

    return {
      creditMovementService,
      alertService,
      creditMovement,
      previousState,
      movementTypeValues,
      isSaving,
      currentLanguage,
      loans,
      v$,
      ...useDateFormat({ entityRef: creditMovement }),
      t$,
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.creditMovement.id) {
        this.creditMovementService()
          .update(this.creditMovement)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(this.t$('creditFlowAppicationApp.creditMovement.updated', { param: param.id }));
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.creditMovementService()
          .create(this.creditMovement)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(this.t$('creditFlowAppicationApp.creditMovement.created', { param: param.id }).toString());
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
