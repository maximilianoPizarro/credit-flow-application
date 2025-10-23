<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate @submit.prevent="save()">
        <h2
          id="creditFlowAppicationApp.creditMovement.home.createOrEditLabel"
          data-cy="CreditMovementCreateUpdateHeading"
          v-text="t$('creditFlowAppicationApp.creditMovement.home.createOrEditLabel')"
        ></h2>
        <div>
          <div class="form-group" v-if="creditMovement.id">
            <label for="id" v-text="t$('global.field.id')"></label>
            <input type="text" class="form-control" id="id" name="id" v-model="creditMovement.id" readonly />
          </div>
          <div class="form-group">
            <label
              class="form-control-label"
              v-text="t$('creditFlowAppicationApp.creditMovement.movementDate')"
              for="credit-movement-movementDate"
            ></label>
            <div class="d-flex">
              <input
                id="credit-movement-movementDate"
                data-cy="movementDate"
                type="datetime-local"
                class="form-control"
                name="movementDate"
                :class="{ valid: !v$.movementDate.$invalid, invalid: v$.movementDate.$invalid }"
                required
                :value="convertDateTimeFromServer(v$.movementDate.$model)"
                @change="updateZonedDateTimeField('movementDate', $event)"
              />
            </div>
            <div v-if="v$.movementDate.$anyDirty && v$.movementDate.$invalid">
              <small class="form-text text-danger" v-for="error of v$.movementDate.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('creditFlowAppicationApp.creditMovement.type')" for="credit-movement-type"></label>
            <select
              class="form-control"
              name="type"
              :class="{ valid: !v$.type.$invalid, invalid: v$.type.$invalid }"
              v-model="v$.type.$model"
              id="credit-movement-type"
              data-cy="type"
              required
            >
              <option
                v-for="movementType in movementTypeValues"
                :key="movementType"
                :value="movementType"
                :label="t$('creditFlowAppicationApp.MovementType.' + movementType)"
              >
                {{ movementType }}
              </option>
            </select>
            <div v-if="v$.type.$anyDirty && v$.type.$invalid">
              <small class="form-text text-danger" v-for="error of v$.type.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label
              class="form-control-label"
              v-text="t$('creditFlowAppicationApp.creditMovement.amount')"
              for="credit-movement-amount"
            ></label>
            <input
              type="number"
              class="form-control"
              name="amount"
              id="credit-movement-amount"
              data-cy="amount"
              :class="{ valid: !v$.amount.$invalid, invalid: v$.amount.$invalid }"
              v-model.number="v$.amount.$model"
              required
            />
            <div v-if="v$.amount.$anyDirty && v$.amount.$invalid">
              <small class="form-text text-danger" v-for="error of v$.amount.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label
              class="form-control-label"
              v-text="t$('creditFlowAppicationApp.creditMovement.description')"
              for="credit-movement-description"
            ></label>
            <input
              type="text"
              class="form-control"
              name="description"
              id="credit-movement-description"
              data-cy="description"
              :class="{ valid: !v$.description.$invalid, invalid: v$.description.$invalid }"
              v-model="v$.description.$model"
            />
          </div>
          <div class="form-group">
            <label
              class="form-control-label"
              v-text="t$('creditFlowAppicationApp.creditMovement.externalReference')"
              for="credit-movement-externalReference"
            ></label>
            <input
              type="text"
              class="form-control"
              name="externalReference"
              id="credit-movement-externalReference"
              data-cy="externalReference"
              :class="{ valid: !v$.externalReference.$invalid, invalid: v$.externalReference.$invalid }"
              v-model="v$.externalReference.$model"
            />
            <div v-if="v$.externalReference.$anyDirty && v$.externalReference.$invalid">
              <small class="form-text text-danger" v-for="error of v$.externalReference.$errors" :key="error.$uid">{{
                error.$message
              }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('creditFlowAppicationApp.creditMovement.loan')" for="credit-movement-loan"></label>
            <select class="form-control" id="credit-movement-loan" data-cy="loan" name="loan" v-model="creditMovement.loan">
              <option :value="null"></option>
              <option
                :value="creditMovement.loan && loanOption.id === creditMovement.loan.id ? creditMovement.loan : loanOption"
                v-for="loanOption in loans"
                :key="loanOption.id"
              >
                {{ loanOption.loanNumber }}
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
<script lang="ts" src="./credit-movement-update.component.ts"></script>
