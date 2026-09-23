<script setup lang="ts">
defineProps<{ title: string; description?: string }>()
</script>

<template>
  <div class="empty-state">
    <p class="empty-state__title">{{ title }}</p>
    <p v-if="description" class="empty-state__description">{{ description }}</p>
    <div v-if="$slots.action" class="empty-state__action">
      <slot name="action" />
    </div>
  </div>
</template>

<style scoped>
.empty-state {
  padding: 40px 24px;
  text-align: center;
  border: var(--empty-border, 1px dashed var(--border));
  border-radius: var(--radius-panel);
  background: var(--empty-bg, transparent);
}

.empty-state__title {
  margin: 0;
  font-weight: 600;
  color: var(--ink);
}

.empty-state__description {
  margin: 8px 0 0;
  color: var(--muted);
  line-height: 1.5;
}

.empty-state__action {
  margin-top: 16px;
}
</style>
