<script setup lang="ts">
defineProps<{ label: string; tone?: 'positive' | 'pending' | 'attention' | 'neutral' }>()
</script>

<template>
  <span class="status-badge" :class="`is-${tone ?? 'neutral'}`">
    <span class="status-badge__dot" aria-hidden="true" />
    {{ label }}
  </span>
</template>

<style scoped>
.status-badge {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: var(--badge-padding, 4px 10px 4px 8px);
  border-radius: var(--radius-badge, 999px);
  font-size: 0.8rem;
  font-weight: 600;
  line-height: 1.4;
  white-space: nowrap;
  border: var(--badge-border, none);
}

.status-badge__dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  flex: none;
}

.status-badge.is-positive {
  background: var(--status-positive-bg);
  color: var(--status-positive-ink);
}
.status-badge.is-positive .status-badge__dot {
  background: var(--status-positive-ink);
}

.status-badge.is-pending {
  background: var(--status-pending-bg);
  color: var(--status-pending-ink);
}
.status-badge.is-pending .status-badge__dot {
  background: var(--status-pending-ink);
}

.status-badge.is-attention {
  background: var(--status-attention-bg);
  color: var(--status-attention-ink);
}
.status-badge.is-attention .status-badge__dot {
  background: var(--status-attention-ink);
}

.status-badge.is-neutral {
  background: var(--status-neutral-bg);
  color: var(--status-neutral-ink);
}
.status-badge.is-neutral .status-badge__dot {
  background: var(--status-neutral-ink);
}
</style>
