<script setup lang="ts"></script>

<template>
  <div class="app-dark app-root">
    <RouterView />
  </div>
</template>

<style>
html,
body,
#app {
  margin: 0;
  min-height: 100%;
}

.app-root {
  min-height: 100vh;
}

.app-dark .p-card {
  color: #e2e8f0;
}

.app-dark .p-card .p-card-title,
.app-dark .p-card .p-card-caption .p-card-title {
  color: #f8fafc !important;
}

.app-dark .p-card-body,
.app-dark .p-card-content {
  color: #e2e8f0;
}
</style>
