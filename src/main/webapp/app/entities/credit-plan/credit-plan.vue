<template>
  <div>
    <h2 id="page-heading" data-cy="CreditPlanHeading">
      <span v-text="t$('creditFlowAppicationApp.creditPlan.home.title')" id="credit-plan-heading"></span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" @click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon>
          <span v-text="t$('creditFlowAppicationApp.creditPlan.home.refreshListLabel')"></span>
        </button>
        <router-link :to="{ name: 'CreditPlanCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-credit-plan"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span v-text="t$('creditFlowAppicationApp.creditPlan.home.createLabel')"></span>
          </button>
        </router-link>
      </div>
    </h2>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && creditPlans && creditPlans.length === 0">
      <span v-text="t$('creditFlowAppicationApp.creditPlan.home.notFound')"></span>
    </div>
    <div class="table-responsive" v-if="creditPlans && creditPlans.length > 0">
      <table class="table table-striped" aria-describedby="creditPlans">
        <thead>
          <tr>
            <th scope="row"><span v-text="t$('global.field.id')"></span></th>
            <th scope="row"><span v-text="t$('creditFlowAppicationApp.creditPlan.name')"></span></th>
            <th scope="row"><span v-text="t$('creditFlowAppicationApp.creditPlan.description')"></span></th>
            <th scope="row"><span v-text="t$('creditFlowAppicationApp.creditPlan.type')"></span></th>
            <th scope="row"><span v-text="t$('creditFlowAppicationApp.creditPlan.interestRate')"></span></th>
            <th scope="row"><span v-text="t$('creditFlowAppicationApp.creditPlan.maxTermYears')"></span></th>
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="creditPlan in creditPlans" :key="creditPlan.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'CreditPlanView', params: { creditPlanId: creditPlan.id } }">{{ creditPlan.id }}</router-link>
            </td>
            <td>{{ creditPlan.name }}</td>
            <td>{{ creditPlan.description }}</td>
            <td v-text="t$('creditFlowAppicationApp.PlanType.' + creditPlan.type)"></td>
            <td>{{ creditPlan.interestRate }}</td>
            <td>{{ creditPlan.maxTermYears }}</td>
            <td class="text-right">
              <div class="btn-group">
                <router-link :to="{ name: 'CreditPlanView', params: { creditPlanId: creditPlan.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.view')"></span>
                  </button>
                </router-link>
                <router-link :to="{ name: 'CreditPlanEdit', params: { creditPlanId: creditPlan.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.edit')"></span>
                  </button>
                </router-link>
                <b-button
                  @click="prepareRemove(creditPlan)"
                  variant="danger"
                  class="btn btn-sm"
                  data-cy="entityDeleteButton"
                  v-b-modal.removeEntity
                >
                  <font-awesome-icon icon="times"></font-awesome-icon>
                  <span class="d-none d-md-inline" v-text="t$('entity.action.delete')"></span>
                </b-button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
    <b-modal ref="removeEntity" id="removeEntity">
      <template #modal-title>
        <span
          id="creditFlowAppicationApp.creditPlan.delete.question"
          data-cy="creditPlanDeleteDialogHeading"
          v-text="t$('entity.delete.title')"
        ></span>
      </template>
      <div class="modal-body">
        <p id="jhi-delete-creditPlan-heading" v-text="t$('creditFlowAppicationApp.creditPlan.delete.question', { id: removeId })"></p>
      </div>
      <template #modal-footer>
        <div>
          <button type="button" class="btn btn-secondary" v-text="t$('entity.action.cancel')" @click="closeDialog()"></button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-creditPlan"
            data-cy="entityConfirmDeleteButton"
            v-text="t$('entity.action.delete')"
            @click="removeCreditPlan()"
          ></button>
        </div>
      </template>
    </b-modal>
  </div>
</template>

<script lang="ts" src="./credit-plan.component.ts"></script>
