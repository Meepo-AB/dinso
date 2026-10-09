<script setup lang="ts">
defineProps<{ title: string; description?: string }>()
</script>

<template>
  <div class="page-header">
    <div class="page-header__text">
      <h1>{{ title }}</h1>
      <p v-if="description">{{ description }}</p>
    </div>
    <div v-if="$slots.action" class="page-header__action">
      <slot name="action" />
    </div>
  </div>
</template>

<style scoped>
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  gap: 24px;
  margin-bottom: var(--space-page-header-gap, 32px);
}

.page-header__text h1 {
  font-family: var(--font-display, var(--font-sans));
  font-size: clamp(1.75rem, 3.2vw, 2.5rem);
  font-weight: var(--heading-weight, 700);
  letter-spacing: var(--heading-tracking, -0.02em);
  margin: 0;
  color: var(--ink);
}

.page-header__text p {
  margin: 10px 0 0;
  color: var(--muted);
  line-height: 1.55;
  max-width: 56ch;
}

.page-header__action {
  flex: none;
}

@media (max-width: 760px) {
  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }

  .page-header__action,
  .page-header__action :deep(.button) {
    width: 100%;
  }
}
</style>
