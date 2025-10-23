<template>
  <div>
    <b-dropdown-item to="/client">
      <font-awesome-icon icon="asterisk" />
      <span v-text="t$('global.menu.entities.client')"></span>
    </b-dropdown-item>
    <b-dropdown-item to="/credit-plan">
      <font-awesome-icon icon="asterisk" />
      <span v-text="t$('global.menu.entities.creditPlan')"></span>
    </b-dropdown-item>
    <b-dropdown-item to="/loan">
      <font-awesome-icon icon="asterisk" />
      <span v-text="t$('global.menu.entities.loan')"></span>
    </b-dropdown-item>
    <b-dropdown-item to="/credit-movement">
      <font-awesome-icon icon="asterisk" />
      <span v-text="t$('global.menu.entities.creditMovement')"></span>
    </b-dropdown-item>
    <!-- jhipster-needle-add-entity-to-menu - JHipster will add entities to the menu here -->
  </div>
</template>

<script lang="ts" src="./entities-menu.component.ts"></script>
