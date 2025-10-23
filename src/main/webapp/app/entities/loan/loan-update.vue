<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate @submit.prevent="save()">
        <h2
          id="creditFlowAppicationApp.loan.home.createOrEditLabel"
          data-cy="LoanCreateUpdateHeading"
          v-text="t$('creditFlowAppicationApp.loan.home.createOrEditLabel')"
        ></h2>
        <div>
          <div class="form-group" v-if="loan.id">
            <label for="id" v-text="t$('global.field.id')"></label>
            <input type="text" class="form-control" id="id" name="id" v-model="loan.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('creditFlowAppicationApp.loan.loanNumber')" for="loan-loanNumber"></label>
            <input
              type="text"
              class="form-control"
              name="loanNumber"
              id="loan-loanNumber"
              data-cy="loanNumber"
              :class="{ valid: !v$.loanNumber.$invalid, invalid: v$.loanNumber.$invalid }"
              v-model="v$.loanNumber.$model"
              required
            />
            <div v-if="v$.loanNumber.$anyDirty && v$.loanNumber.$invalid">
              <small class="form-text text-danger" v-for="error of v$.loanNumber.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('creditFlowAppicationApp.loan.openingDate')" for="loan-openingDate"></label>
            <div class="d-flex">
              <input
                id="loan-openingDate"
                data-cy="openingDate"
                type="datetime-local"
                class="form-control"
                name="openingDate"
                :class="{ valid: !v$.openingDate.$invalid, invalid: v$.openingDate.$invalid }"
                required
                :value="convertDateTimeFromServer(v$.openingDate.$model)"
                @change="updateZonedDateTimeField('openingDate', $event)"
              />
            </div>
            <div v-if="v$.openingDate.$anyDirty && v$.openingDate.$invalid">
              <small class="form-text text-danger" v-for="error of v$.openingDate.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label
              class="form-control-label"
              v-text="t$('creditFlowAppicationApp.loan.principalAmount')"
              for="loan-principalAmount"
            ></label>
            <input
              type="number"
              class="form-control"
              name="principalAmount"
              id="loan-principalAmount"
              data-cy="principalAmount"
              :class="{ valid: !v$.principalAmount.$invalid, invalid: v$.principalAmount.$invalid }"
              v-model.number="v$.principalAmount.$model"
              required
            />
            <div v-if="v$.principalAmount.$anyDirty && v$.principalAmount.$invalid">
              <small class="form-text text-danger" v-for="error of v$.principalAmount.$errors" :key="error.$uid">{{
                error.$message
              }}</small>
            </div>
          </div>
          <div class="form-group">
            <label
              class="form-control-label"
              v-text="t$('creditFlowAppicationApp.loan.outstandingBalance')"
              for="loan-outstandingBalance"
            ></label>
            <input
              type="number"
              class="form-control"
              name="outstandingBalance"
              id="loan-outstandingBalance"
              data-cy="outstandingBalance"
              :class="{ valid: !v$.outstandingBalance.$invalid, invalid: v$.outstandingBalance.$invalid }"
              v-model.number="v$.outstandingBalance.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('creditFlowAppicationApp.loan.status')" for="loan-status"></label>
            <select
              class="form-control"
              name="status"
              :class="{ valid: !v$.status.$invalid, invalid: v$.status.$invalid }"
              v-model="v$.status.$model"
              id="loan-status"
              data-cy="status"
              required
            >
              <option
                v-for="loanStatus in loanStatusValues"
                :key="loanStatus"
                :value="loanStatus"
                :label="t$('creditFlowAppicationApp.LoanStatus.' + loanStatus)"
              >
                {{ loanStatus }}
              </option>
            </select>
            <div v-if="v$.status.$anyDirty && v$.status.$invalid">
              <small class="form-text text-danger" v-for="error of v$.status.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('creditFlowAppicationApp.loan.closingDate')" for="loan-closingDate"></label>
            <div class="d-flex">
              <input
                id="loan-closingDate"
                data-cy="closingDate"
                type="datetime-local"
                class="form-control"
                name="closingDate"
                :class="{ valid: !v$.closingDate.$invalid, invalid: v$.closingDate.$invalid }"
                :value="convertDateTimeFromServer(v$.closingDate.$model)"
                @change="updateZonedDateTimeField('closingDate', $event)"
              />
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('creditFlowAppicationApp.loan.client')" for="loan-client"></label>
            <select class="form-control" id="loan-client" data-cy="client" name="client" v-model="loan.client">
              <option :value="null"></option>
              <option
                :value="loan.client && clientOption.id === loan.client.id ? loan.client : clientOption"
                v-for="clientOption in clients"
                :key="clientOption.id"
              >
                {{ clientOption.documentNumber }}
              </option>
            </select>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('creditFlowAppicationApp.loan.plan')" for="loan-plan"></label>
            <select class="form-control" id="loan-plan" data-cy="plan" name="plan" v-model="loan.plan">
              <option :value="null"></option>
              <option
                :value="loan.plan && creditPlanOption.id === loan.plan.id ? loan.plan : creditPlanOption"
                v-for="creditPlanOption in creditPlans"
                :key="creditPlanOption.id"
              >
                {{ creditPlanOption.name }}
              </option>
            </select>
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
<script lang="ts" src="./loan-update.component.ts"></script>
