<script setup lang="ts">
import type { PrivateOverviewMetricLayout } from '../data/customer'

withDefaults(
  defineProps<{
    metrics: { label: string; value: string; hint?: string }[]
    layout?: PrivateOverviewMetricLayout
  }>(),
  {
    layout: 'cards',
  },
)
</script>

<template>
  <div class="metric-grid" :class="`metric-grid--${layout}`">
    <article v-for="metric in metrics" :key="metric.label" class="metric-card">
      <span class="metric-card__label">{{ metric.label }}</span>
      <strong class="metric-card__value">{{ metric.value }}</strong>
      <span v-if="metric.hint" class="metric-card__hint">
        {{ metric.hint }}
      </span>
    </article>
  </div>
</template>

<style scoped>
.metric-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: var(--space-metric-gap, 16px);
}

.metric-card {
  background: var(--metric-bg, var(--surface));
  border: var(--panel-border, 1px solid var(--border));
  border-radius: var(--radius-panel);
  box-shadow: var(--shadow-panel, none);
  padding: var(--space-panel-padding, 24px);
  display: flex;
  flex-direction: column;
  gap: 8px;
  position: relative;
  overflow: hidden;
}

.metric-card__label {
  font-size: 0.85rem;
  font-weight: 600;
  color: var(--muted);
  letter-spacing: var(--label-tracking, 0);
  text-transform: var(--label-case, none);
}

.metric-card__value {
  font-family: var(--font-display, var(--font-sans));
  font-size: 1.75rem;
  font-weight: 700;
  color: var(--ink);
  font-variant-numeric: tabular-nums;
  letter-spacing: var(--heading-tracking, -0.01em);
}

.metric-card__hint {
  font-size: 0.85rem;
  color: var(--muted-soft, var(--muted));
}

.metric-grid--bottom-bar {
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 0;
  margin-top: 32px;
  background: var(--metric-bar-bg, var(--surface));
  border: var(--panel-border, 1px solid var(--border));
  border-radius: var(--radius-panel);
  box-shadow: var(--shadow-panel, none);
  overflow: hidden;
}

.metric-grid--bottom-bar .metric-card {
  min-width: 0;
  padding: 16px 24px;
  background: transparent;
  border: 0;
  border-radius: 0;
  box-shadow: none;
}

.metric-grid--bottom-bar .metric-card + .metric-card {
  border-left: 1px solid var(--border);
}

.metric-grid--bottom-bar .metric-card__value {
  font-size: 1.5rem;
}

@media (max-width: 760px) {
  .metric-grid--bottom-bar {
    grid-template-columns: 1fr;
  }

  .metric-grid--bottom-bar .metric-card {
    padding: 16px;
  }

  .metric-grid--bottom-bar .metric-card + .metric-card {
    border-top: 1px solid var(--border);
    border-left: 0;
  }
}
</style>
