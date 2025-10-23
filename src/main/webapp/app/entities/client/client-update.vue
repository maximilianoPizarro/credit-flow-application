<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate @submit.prevent="save()">
        <h2
          id="creditFlowAppicationApp.client.home.createOrEditLabel"
          data-cy="ClientCreateUpdateHeading"
          v-text="t$('creditFlowAppicationApp.client.home.createOrEditLabel')"
        ></h2>
        <div>
          <div class="form-group" v-if="client.id">
            <label for="id" v-text="t$('global.field.id')"></label>
            <input type="text" class="form-control" id="id" name="id" v-model="client.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('creditFlowAppicationApp.client.clientNumber')" for="client-clientNumber"></label>
            <input
              type="number"
              class="form-control"
              name="clientNumber"
              id="client-clientNumber"
              data-cy="clientNumber"
              :class="{ valid: !v$.clientNumber.$invalid, invalid: v$.clientNumber.$invalid }"
              v-model.number="v$.clientNumber.$model"
              required
            />
            <div v-if="v$.clientNumber.$anyDirty && v$.clientNumber.$invalid">
              <small class="form-text text-danger" v-for="error of v$.clientNumber.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('creditFlowAppicationApp.client.firstName')" for="client-firstName"></label>
            <input
              type="text"
              class="form-control"
              name="firstName"
              id="client-firstName"
              data-cy="firstName"
              :class="{ valid: !v$.firstName.$invalid, invalid: v$.firstName.$invalid }"
              v-model="v$.firstName.$model"
              required
            />
            <div v-if="v$.firstName.$anyDirty && v$.firstName.$invalid">
              <small class="form-text text-danger" v-for="error of v$.firstName.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('creditFlowAppicationApp.client.lastName')" for="client-lastName"></label>
            <input
              type="text"
              class="form-control"
              name="lastName"
              id="client-lastName"
              data-cy="lastName"
              :class="{ valid: !v$.lastName.$invalid, invalid: v$.lastName.$invalid }"
              v-model="v$.lastName.$model"
              required
            />
            <div v-if="v$.lastName.$anyDirty && v$.lastName.$invalid">
              <small class="form-text text-danger" v-for="error of v$.lastName.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('creditFlowAppicationApp.client.dateOfBirth')" for="client-dateOfBirth"></label>
            <b-input-group class="mb-3">
              <b-input-group-prepend>
                <b-form-datepicker
                  aria-controls="client-dateOfBirth"
                  v-model="v$.dateOfBirth.$model"
                  name="dateOfBirth"
                  class="form-control"
                  :locale="currentLanguage"
                  button-only
                  today-button
                  reset-button
                  close-button
                >
                </b-form-datepicker>
              </b-input-group-prepend>
              <b-form-input
                id="client-dateOfBirth"
                data-cy="dateOfBirth"
                type="text"
                class="form-control"
                name="dateOfBirth"
                :class="{ valid: !v$.dateOfBirth.$invalid, invalid: v$.dateOfBirth.$invalid }"
                v-model="v$.dateOfBirth.$model"
                required
              />
            </b-input-group>
            <div v-if="v$.dateOfBirth.$anyDirty && v$.dateOfBirth.$invalid">
              <small class="form-text text-danger" v-for="error of v$.dateOfBirth.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('creditFlowAppicationApp.client.documentType')" for="client-documentType"></label>
            <select
              class="form-control"
              name="documentType"
              :class="{ valid: !v$.documentType.$invalid, invalid: v$.documentType.$invalid }"
              v-model="v$.documentType.$model"
              id="client-documentType"
              data-cy="documentType"
              required
            >
              <option
                v-for="documentType in documentTypeValues"
                :key="documentType"
                :value="documentType"
                :label="t$('creditFlowAppicationApp.DocumentType.' + documentType)"
              >
                {{ documentType }}
              </option>
            </select>
            <div v-if="v$.documentType.$anyDirty && v$.documentType.$invalid">
              <small class="form-text text-danger" v-for="error of v$.documentType.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label
              class="form-control-label"
              v-text="t$('creditFlowAppicationApp.client.documentNumber')"
              for="client-documentNumber"
            ></label>
            <input
              type="text"
              class="form-control"
              name="documentNumber"
              id="client-documentNumber"
              data-cy="documentNumber"
              :class="{ valid: !v$.documentNumber.$invalid, invalid: v$.documentNumber.$invalid }"
              v-model="v$.documentNumber.$model"
              required
            />
            <div v-if="v$.documentNumber.$anyDirty && v$.documentNumber.$invalid">
              <small class="form-text text-danger" v-for="error of v$.documentNumber.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('creditFlowAppicationApp.client.email')" for="client-email"></label>
            <input
              type="text"
              class="form-control"
              name="email"
              id="client-email"
              data-cy="email"
              :class="{ valid: !v$.email.$invalid, invalid: v$.email.$invalid }"
              v-model="v$.email.$model"
            />
            <div v-if="v$.email.$anyDirty && v$.email.$invalid">
              <small class="form-text text-danger" v-for="error of v$.email.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('creditFlowAppicationApp.client.phone')" for="client-phone"></label>
            <input
              type="text"
              class="form-control"
              name="phone"
              id="client-phone"
              data-cy="phone"
              :class="{ valid: !v$.phone.$invalid, invalid: v$.phone.$invalid }"
              v-model="v$.phone.$model"
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
<script lang="ts" src="./client-update.component.ts"></script>
