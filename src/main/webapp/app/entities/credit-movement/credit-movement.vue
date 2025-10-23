<template>
  <div>
    <h2 id="page-heading" data-cy="CreditMovementHeading">
      <span v-text="t$('creditFlowAppicationApp.creditMovement.home.title')" id="credit-movement-heading"></span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" @click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon>
          <span v-text="t$('creditFlowAppicationApp.creditMovement.home.refreshListLabel')"></span>
        </button>
        <router-link :to="{ name: 'CreditMovementCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-credit-movement"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span v-text="t$('creditFlowAppicationApp.creditMovement.home.createLabel')"></span>
          </button>
        </router-link>
      </div>
    </h2>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && creditMovements && creditMovements.length === 0">
      <span v-text="t$('creditFlowAppicationApp.creditMovement.home.notFound')"></span>
    </div>
    <div class="table-responsive" v-if="creditMovements && creditMovements.length > 0">
      <table class="table table-striped" aria-describedby="creditMovements">
        <thead>
          <tr>
            <th scope="row" @click="changeOrder('id')">
              <span v-text="t$('global.field.id')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'id'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('movementDate')">
              <span v-text="t$('creditFlowAppicationApp.creditMovement.movementDate')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'movementDate'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('type')">
              <span v-text="t$('creditFlowAppicationApp.creditMovement.type')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'type'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('amount')">
              <span v-text="t$('creditFlowAppicationApp.creditMovement.amount')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'amount'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('description')">
              <span v-text="t$('creditFlowAppicationApp.creditMovement.description')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'description'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('externalReference')">
              <span v-text="t$('creditFlowAppicationApp.creditMovement.externalReference')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'externalReference'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('loan.loanNumber')">
              <span v-text="t$('creditFlowAppicationApp.creditMovement.loan')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'loan.loanNumber'"></jhi-sort-indicator>
            </th>
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="creditMovement in creditMovements" :key="creditMovement.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'CreditMovementView', params: { creditMovementId: creditMovement.id } }">{{
                creditMovement.id
              }}</router-link>
            </td>
            <td>{{ formatDateShort(creditMovement.movementDate) || '' }}</td>
            <td v-text="t$('creditFlowAppicationApp.MovementType.' + creditMovement.type)"></td>
            <td>{{ creditMovement.amount }}</td>
            <td>{{ creditMovement.description }}</td>
            <td>{{ creditMovement.externalReference }}</td>
            <td>
              <div v-if="creditMovement.loan">
                <router-link :to="{ name: 'LoanView', params: { loanId: creditMovement.loan.id } }">{{
                  creditMovement.loan.loanNumber
                }}</router-link>
              </div>
            </td>
            <td class="text-right">
              <div class="btn-group">
                <router-link
                  :to="{ name: 'CreditMovementView', params: { creditMovementId: creditMovement.id } }"
                  custom
                  v-slot="{ navigate }"
                >
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.view')"></span>
                  </button>
                </router-link>
                <router-link
                  :to="{ name: 'CreditMovementEdit', params: { creditMovementId: creditMovement.id } }"
                  custom
                  v-slot="{ navigate }"
                >
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.edit')"></span>
                  </button>
                </router-link>
                <b-button
                  @click="prepareRemove(creditMovement)"
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
          id="creditFlowAppicationApp.creditMovement.delete.question"
          data-cy="creditMovementDeleteDialogHeading"
          v-text="t$('entity.delete.title')"
        ></span>
      </template>
      <div class="modal-body">
        <p
          id="jhi-delete-creditMovement-heading"
          v-text="t$('creditFlowAppicationApp.creditMovement.delete.question', { id: removeId })"
        ></p>
      </div>
      <template #modal-footer>
        <div>
          <button type="button" class="btn btn-secondary" v-text="t$('entity.action.cancel')" @click="closeDialog()"></button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-creditMovement"
            data-cy="entityConfirmDeleteButton"
            v-text="t$('entity.action.delete')"
            @click="removeCreditMovement()"
          ></button>
        </div>
      </template>
    </b-modal>
    <div v-show="creditMovements && creditMovements.length > 0">
      <div class="row justify-content-center">
        <jhi-item-count :page="page" :total="queryCount" :itemsPerPage="itemsPerPage"></jhi-item-count>
      </div>
      <div class="row justify-content-center">
        <b-pagination size="md" :total-rows="totalItems" v-model="page" :per-page="itemsPerPage"></b-pagination>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./credit-movement.component.ts"></script>
