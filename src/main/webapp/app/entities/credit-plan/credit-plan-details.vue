<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="creditPlan">
        <h2 class="jh-entity-heading" data-cy="creditPlanDetailsHeading">
          <span v-text="t$('creditFlowAppicationApp.creditPlan.detail.title')"></span> {{ creditPlan.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="t$('creditFlowAppicationApp.creditPlan.name')"></span>
          </dt>
          <dd>
            <span>{{ creditPlan.name }}</span>
          </dd>
          <dt>
            <span v-text="t$('creditFlowAppicationApp.creditPlan.description')"></span>
          </dt>
          <dd>
            <span>{{ creditPlan.description }}</span>
          </dd>
          <dt>
            <span v-text="t$('creditFlowAppicationApp.creditPlan.type')"></span>
          </dt>
          <dd>
            <span v-text="t$('creditFlowAppicationApp.PlanType.' + creditPlan.type)"></span>
          </dd>
          <dt>
            <span v-text="t$('creditFlowAppicationApp.creditPlan.interestRate')"></span>
          </dt>
          <dd>
            <span>{{ creditPlan.interestRate }}</span>
          </dd>
          <dt>
            <span v-text="t$('creditFlowAppicationApp.creditPlan.maxTermYears')"></span>
          </dt>
          <dd>
            <span>{{ creditPlan.maxTermYears }}</span>
          </dd>
        </dl>
        <button type="submit" @click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.back')"></span>
        </button>
        <router-link
          v-if="creditPlan.id"
          :to="{ name: 'CreditPlanEdit', params: { creditPlanId: creditPlan.id } }"
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

<script lang="ts" src="./credit-plan-details.component.ts"></script>
