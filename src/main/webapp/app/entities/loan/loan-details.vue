<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="loan">
        <h2 class="jh-entity-heading" data-cy="loanDetailsHeading">
          <span v-text="t$('creditFlowAppicationApp.loan.detail.title')"></span> {{ loan.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="t$('creditFlowAppicationApp.loan.loanNumber')"></span>
          </dt>
          <dd>
            <span>{{ loan.loanNumber }}</span>
          </dd>
          <dt>
            <span v-text="t$('creditFlowAppicationApp.loan.openingDate')"></span>
          </dt>
          <dd>
            <span v-if="loan.openingDate">{{ formatDateLong(loan.openingDate) }}</span>
          </dd>
          <dt>
            <span v-text="t$('creditFlowAppicationApp.loan.principalAmount')"></span>
          </dt>
          <dd>
            <span>{{ loan.principalAmount }}</span>
          </dd>
          <dt>
            <span v-text="t$('creditFlowAppicationApp.loan.outstandingBalance')"></span>
          </dt>
          <dd>
            <span>{{ loan.outstandingBalance }}</span>
          </dd>
          <dt>
            <span v-text="t$('creditFlowAppicationApp.loan.status')"></span>
          </dt>
          <dd>
            <span v-text="t$('creditFlowAppicationApp.LoanStatus.' + loan.status)"></span>
          </dd>
          <dt>
            <span v-text="t$('creditFlowAppicationApp.loan.closingDate')"></span>
          </dt>
          <dd>
            <span v-if="loan.closingDate">{{ formatDateLong(loan.closingDate) }}</span>
          </dd>
          <dt>
            <span v-text="t$('creditFlowAppicationApp.loan.client')"></span>
          </dt>
          <dd>
            <div v-if="loan.client">
              <router-link :to="{ name: 'ClientView', params: { clientId: loan.client.id } }">{{ loan.client.documentNumber }}</router-link>
            </div>
          </dd>
          <dt>
            <span v-text="t$('creditFlowAppicationApp.loan.plan')"></span>
          </dt>
          <dd>
            <div v-if="loan.plan">
              <router-link :to="{ name: 'CreditPlanView', params: { creditPlanId: loan.plan.id } }">{{ loan.plan.name }}</router-link>
            </div>
          </dd>
        </dl>
        <button type="submit" @click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.back')"></span>
        </button>
        <router-link v-if="loan.id" :to="{ name: 'LoanEdit', params: { loanId: loan.id } }" custom v-slot="{ navigate }">
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.edit')"></span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./loan-details.component.ts"></script>
