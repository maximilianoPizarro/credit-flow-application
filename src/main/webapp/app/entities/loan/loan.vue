<template>
  <div>
    <h2 id="page-heading" data-cy="LoanHeading">
      <span v-text="t$('creditFlowAppicationApp.loan.home.title')" id="loan-heading"></span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" @click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon>
          <span v-text="t$('creditFlowAppicationApp.loan.home.refreshListLabel')"></span>
        </button>
        <router-link :to="{ name: 'LoanCreate' }" custom v-slot="{ navigate }">
          <button @click="navigate" id="jh-create-entity" data-cy="entityCreateButton" class="btn btn-primary jh-create-entity create-loan">
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span v-text="t$('creditFlowAppicationApp.loan.home.createLabel')"></span>
          </button>
        </router-link>
      </div>
    </h2>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && loans && loans.length === 0">
      <span v-text="t$('creditFlowAppicationApp.loan.home.notFound')"></span>
    </div>
    <div class="table-responsive" v-if="loans && loans.length > 0">
      <table class="table table-striped" aria-describedby="loans">
        <thead>
          <tr>
            <th scope="row" @click="changeOrder('id')">
              <span v-text="t$('global.field.id')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'id'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('loanNumber')">
              <span v-text="t$('creditFlowAppicationApp.loan.loanNumber')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'loanNumber'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('openingDate')">
              <span v-text="t$('creditFlowAppicationApp.loan.openingDate')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'openingDate'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('principalAmount')">
              <span v-text="t$('creditFlowAppicationApp.loan.principalAmount')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'principalAmount'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('outstandingBalance')">
              <span v-text="t$('creditFlowAppicationApp.loan.outstandingBalance')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'outstandingBalance'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('status')">
              <span v-text="t$('creditFlowAppicationApp.loan.status')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'status'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('closingDate')">
              <span v-text="t$('creditFlowAppicationApp.loan.closingDate')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'closingDate'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('client.documentNumber')">
              <span v-text="t$('creditFlowAppicationApp.loan.client')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'client.documentNumber'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('plan.name')">
              <span v-text="t$('creditFlowAppicationApp.loan.plan')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'plan.name'"></jhi-sort-indicator>
            </th>
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="loan in loans" :key="loan.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'LoanView', params: { loanId: loan.id } }">{{ loan.id }}</router-link>
            </td>
            <td>{{ loan.loanNumber }}</td>
            <td>{{ formatDateShort(loan.openingDate) || '' }}</td>
            <td>{{ loan.principalAmount }}</td>
            <td>{{ loan.outstandingBalance }}</td>
            <td v-text="t$('creditFlowAppicationApp.LoanStatus.' + loan.status)"></td>
            <td>{{ formatDateShort(loan.closingDate) || '' }}</td>
            <td>
              <div v-if="loan.client">
                <router-link :to="{ name: 'ClientView', params: { clientId: loan.client.id } }">{{
                  loan.client.documentNumber
                }}</router-link>
              </div>
            </td>
            <td>
              <div v-if="loan.plan">
                <router-link :to="{ name: 'CreditPlanView', params: { creditPlanId: loan.plan.id } }">{{ loan.plan.name }}</router-link>
              </div>
            </td>
            <td class="text-right">
              <div class="btn-group">
                <router-link :to="{ name: 'LoanView', params: { loanId: loan.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.view')"></span>
                  </button>
                </router-link>
                <router-link :to="{ name: 'LoanEdit', params: { loanId: loan.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.edit')"></span>
                  </button>
                </router-link>
                <b-button
                  @click="prepareRemove(loan)"
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
        <span id="creditFlowAppicationApp.loan.delete.question" data-cy="loanDeleteDialogHeading" v-text="t$('entity.delete.title')"></span>
      </template>
      <div class="modal-body">
        <p id="jhi-delete-loan-heading" v-text="t$('creditFlowAppicationApp.loan.delete.question', { id: removeId })"></p>
      </div>
      <template #modal-footer>
        <div>
          <button type="button" class="btn btn-secondary" v-text="t$('entity.action.cancel')" @click="closeDialog()"></button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-loan"
            data-cy="entityConfirmDeleteButton"
            v-text="t$('entity.action.delete')"
            @click="removeLoan()"
          ></button>
        </div>
      </template>
    </b-modal>
    <div v-show="loans && loans.length > 0">
      <div class="row justify-content-center">
        <jhi-item-count :page="page" :total="queryCount" :itemsPerPage="itemsPerPage"></jhi-item-count>
      </div>
      <div class="row justify-content-center">
        <b-pagination size="md" :total-rows="totalItems" v-model="page" :per-page="itemsPerPage"></b-pagination>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./loan.component.ts"></script>
