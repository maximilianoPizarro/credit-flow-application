<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate @submit.prevent="save()">
        <h2
          id="creditFlowAppicationApp.creditPlan.home.createOrEditLabel"
          data-cy="CreditPlanCreateUpdateHeading"
          v-text="t$('creditFlowAppicationApp.creditPlan.home.createOrEditLabel')"
        ></h2>
        <div>
          <div class="form-group" v-if="creditPlan.id">
            <label for="id" v-text="t$('global.field.id')"></label>
            <input type="text" class="form-control" id="id" name="id" v-model="creditPlan.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('creditFlowAppicationApp.creditPlan.name')" for="credit-plan-name"></label>
            <input
              type="text"
              class="form-control"
              name="name"
              id="credit-plan-name"
              data-cy="name"
              :class="{ valid: !v$.name.$invalid, invalid: v$.name.$invalid }"
              v-model="v$.name.$model"
              required
            />
            <div v-if="v$.name.$anyDirty && v$.name.$invalid">
              <small class="form-text text-danger" v-for="error of v$.name.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label
              class="form-control-label"
              v-text="t$('creditFlowAppicationApp.creditPlan.description')"
              for="credit-plan-description"
            ></label>
            <textarea
              class="form-control"
              name="description"
              id="credit-plan-description"
              data-cy="description"
              :class="{ valid: !v$.description.$invalid, invalid: v$.description.$invalid }"
              v-model="v$.description.$model"
            ></textarea>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('creditFlowAppicationApp.creditPlan.type')" for="credit-plan-type"></label>
            <select
              class="form-control"
              name="type"
              :class="{ valid: !v$.type.$invalid, invalid: v$.type.$invalid }"
              v-model="v$.type.$model"
              id="credit-plan-type"
              data-cy="type"
              required
            >
              <option
                v-for="planType in planTypeValues"
                :key="planType"
                :value="planType"
                :label="t$('creditFlowAppicationApp.PlanType.' + planType)"
              >
                {{ planType }}
              </option>
            </select>
            <div v-if="v$.type.$anyDirty && v$.type.$invalid">
              <small class="form-text text-danger" v-for="error of v$.type.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label
              class="form-control-label"
              v-text="t$('creditFlowAppicationApp.creditPlan.interestRate')"
              for="credit-plan-interestRate"
            ></label>
            <input
              type="number"
              class="form-control"
              name="interestRate"
              id="credit-plan-interestRate"
              data-cy="interestRate"
              :class="{ valid: !v$.interestRate.$invalid, invalid: v$.interestRate.$invalid }"
              v-model.number="v$.interestRate.$model"
              required
            />
            <div v-if="v$.interestRate.$anyDirty && v$.interestRate.$invalid">
              <small class="form-text text-danger" v-for="error of v$.interestRate.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label
              class="form-control-label"
              v-text="t$('creditFlowAppicationApp.creditPlan.maxTermYears')"
              for="credit-plan-maxTermYears"
            ></label>
            <input
              type="number"
              class="form-control"
              name="maxTermYears"
              id="credit-plan-maxTermYears"
              data-cy="maxTermYears"
              :class="{ valid: !v$.maxTermYears.$invalid, invalid: v$.maxTermYears.$invalid }"
              v-model.number="v$.maxTermYears.$model"
            />
          </div>
        </div>
        <div>
          <button type="button" id="cancel-save" data-cy="entityCreateCancelButton" class="btn btn-secondary" @click="previousState()">
            <font-awesome-icon icon="ban"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.cancel')"></span>
          </button>
          <button
            type="submit"
            id="save-entity"
            data-cy="entityCreateSaveButton"
            :disabled="v$.$invalid || isSaving"
            class="btn btn-primary"
          >
            <font-awesome-icon icon="save"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.save')"></span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>
<script lang="ts" src="./credit-plan-update.component.ts"></script>
