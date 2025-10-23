<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="creditMovement">
        <h2 class="jh-entity-heading" data-cy="creditMovementDetailsHeading">
          <span v-text="t$('creditFlowAppicationApp.creditMovement.detail.title')"></span> {{ creditMovement.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="t$('creditFlowAppicationApp.creditMovement.movementDate')"></span>
          </dt>
          <dd>
            <span v-if="creditMovement.movementDate">{{ formatDateLong(creditMovement.movementDate) }}</span>
          </dd>
          <dt>
            <span v-text="t$('creditFlowAppicationApp.creditMovement.type')"></span>
          </dt>
          <dd>
            <span v-text="t$('creditFlowAppicationApp.MovementType.' + creditMovement.type)"></span>
          </dd>
          <dt>
            <span v-text="t$('creditFlowAppicationApp.creditMovement.amount')"></span>
          </dt>
          <dd>
            <span>{{ creditMovement.amount }}</span>
          </dd>
          <dt>
            <span v-text="t$('creditFlowAppicationApp.creditMovement.description')"></span>
          </dt>
          <dd>
            <span>{{ creditMovement.description }}</span>
          </dd>
          <dt>
            <span v-text="t$('creditFlowAppicationApp.creditMovement.externalReference')"></span>
          </dt>
          <dd>
            <span>{{ creditMovement.externalReference }}</span>
          </dd>
          <dt>
            <span v-text="t$('creditFlowAppicationApp.creditMovement.loan')"></span>
          </dt>
          <dd>
            <div v-if="creditMovement.loan">
              <router-link :to="{ name: 'LoanView', params: { loanId: creditMovement.loan.id } }">{{
                creditMovement.loan.loanNumber
              }}</router-link>
            </div>
          </dd>
        </dl>
        <button type="submit" @click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.back')"></span>
        </button>
        <router-link
          v-if="creditMovement.id"
          :to="{ name: 'CreditMovementEdit', params: { creditMovementId: creditMovement.id } }"
          custom
          v-slot="{ navigate }"
        >
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.edit')"></span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./credit-movement-details.component.ts"></script>
