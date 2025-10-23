<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="client">
        <h2 class="jh-entity-heading" data-cy="clientDetailsHeading">
          <span v-text="t$('creditFlowAppicationApp.client.detail.title')"></span> {{ client.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="t$('creditFlowAppicationApp.client.clientNumber')"></span>
          </dt>
          <dd>
            <span>{{ client.clientNumber }}</span>
          </dd>
          <dt>
            <span v-text="t$('creditFlowAppicationApp.client.firstName')"></span>
          </dt>
          <dd>
            <span>{{ client.firstName }}</span>
          </dd>
          <dt>
            <span v-text="t$('creditFlowAppicationApp.client.lastName')"></span>
          </dt>
          <dd>
            <span>{{ client.lastName }}</span>
          </dd>
          <dt>
            <span v-text="t$('creditFlowAppicationApp.client.dateOfBirth')"></span>
          </dt>
          <dd>
            <span>{{ client.dateOfBirth }}</span>
          </dd>
          <dt>
            <span v-text="t$('creditFlowAppicationApp.client.documentType')"></span>
          </dt>
          <dd>
            <span v-text="t$('creditFlowAppicationApp.DocumentType.' + client.documentType)"></span>
          </dd>
          <dt>
            <span v-text="t$('creditFlowAppicationApp.client.documentNumber')"></span>
          </dt>
          <dd>
            <span>{{ client.documentNumber }}</span>
          </dd>
          <dt>
            <span v-text="t$('creditFlowAppicationApp.client.email')"></span>
          </dt>
          <dd>
            <span>{{ client.email }}</span>
          </dd>
          <dt>
            <span v-text="t$('creditFlowAppicationApp.client.phone')"></span>
          </dt>
          <dd>
            <span>{{ client.phone }}</span>
          </dd>
        </dl>
        <button type="submit" @click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.back')"></span>
        </button>
        <router-link v-if="client.id" :to="{ name: 'ClientEdit', params: { clientId: client.id } }" custom v-slot="{ navigate }">
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.edit')"></span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./client-details.component.ts"></script>
