<script setup lang="ts">
defineProps<{ title?: string }>()
</script>

<template>
  <article class="panel">
    <h2 v-if="title">{{ title }}</h2>
    <slot />
  </article>
</template>

<style scoped>
.panel {
  background: var(--surface);
  border: var(--panel-border, 1px solid var(--border));
  border-radius: var(--radius-panel);
  box-shadow: var(--shadow-panel, none);
  padding: var(--space-panel-padding, 24px);
}

.panel h2 {
  font-family: var(--font-display, var(--font-sans));
  font-size: 1.15rem;
  font-weight: 700;
  margin: 0 0 16px;
  padding-bottom: var(--panel-heading-gap, 0);
  border-bottom: var(--panel-heading-border, none);
  color: var(--ink);
}
</style>
