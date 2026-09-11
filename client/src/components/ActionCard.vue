<script setup lang="ts">
defineProps<{ title: string; description: string; actionLabel: string }>()
defineEmits<{ action: [] }>()
</script>

<template>
  <aside class="action-card">
    <h2>{{ title }}</h2>
    <p>{{ description }}</p>
    <button class="button" type="button" @click="$emit('action')">{{ actionLabel }}</button>
  </aside>
</template>

<style scoped>
.action-card {
  background: var(--accent-panel-bg, var(--surface));
  border: var(--panel-border, 1px solid var(--border));
  border-radius: var(--radius-panel);
  box-shadow: var(--shadow-panel, none);
  padding: var(--space-panel-padding, 24px);
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.action-card h2 {
  font-family: var(--font-display, var(--font-sans));
  font-size: 1.15rem;
  margin: 0;
  color: var(--ink);
}

.action-card p {
  margin: 0;
  color: var(--muted);
  line-height: 1.55;
  flex: 1;
}

.action-card .button {
  align-self: flex-start;
}

@media (max-width: 640px) {
  .action-card .button {
    align-self: stretch;
    width: 100%;
  }
}
</style>
