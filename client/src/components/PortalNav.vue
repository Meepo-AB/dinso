<script setup lang="ts">
defineProps<{ items: [string, string][]; active: string }>()
defineEmits<{ select: [string] }>()
</script>

<template>
  <nav class="portal-nav" aria-label="Huvudnavigation">
    <button
      v-for="item in items"
      :key="item[0]"
      type="button"
      class="portal-nav__item"
      :class="{ 'is-active': active === item[0] }"
      @click="$emit('select', item[0])"
    >
      {{ item[1] }}
    </button>
  </nav>
</template>

<style scoped>
.portal-nav {
  display: flex;
  gap: var(--nav-gap, 4px);
  border-bottom: var(--nav-rule, 1px solid var(--border));
  margin-bottom: var(--space-nav-gap, 40px);
  padding: var(--nav-padding, 0);
  background: var(--nav-track-bg, transparent);
  border-radius: var(--nav-track-radius, 0);
  overflow-x: auto;
  overflow-y: hidden;
}

.portal-nav__item {
  border: 0;
  background: var(--nav-item-bg, transparent);
  border-radius: var(--nav-item-radius, 0);
  padding: var(--nav-item-padding, 14px 16px);
  color: var(--muted);
  font-weight: 600;
  font-size: 0.95rem;
  white-space: nowrap;
  border-bottom: var(--nav-underline-width, 3px) solid transparent;
  margin-bottom: var(--nav-item-margin-bottom, -1px);
  transition:
    color 0.16s ease,
    border-color 0.16s ease,
    background-color 0.16s ease;
}

.portal-nav__item:hover {
  color: var(--ink);
}

.portal-nav__item.is-active {
  color: var(--nav-active-ink, var(--ink));
  border-bottom-color: var(--nav-active-underline, var(--primary));
  background: var(--nav-active-bg, transparent);
}

@media (max-width: 760px) {
  .portal-nav {
    margin-right: -16px;
    margin-left: -16px;
    padding-right: 16px;
    padding-left: 16px;
    scroll-padding-inline: 16px;
    scrollbar-width: thin;
    overscroll-behavior-x: contain;
  }

  .portal-nav__item {
    scroll-snap-align: start;
  }
}
</style>
