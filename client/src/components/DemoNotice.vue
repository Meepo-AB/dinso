<script setup lang="ts">
defineProps<{ role?: 'status' | 'alert' }>()
</script>

<template>
  <p class="demo-notice" :role="role ?? 'status'">
    <span class="demo-notice__mark" aria-hidden="true" />
    <span><slot /></span>
  </p>
</template>

<style scoped>
.demo-notice {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  margin: 24px 0 0;
  padding: 14px 18px;
  background: var(--notice-bg);
  border: var(--notice-border, 1px solid var(--notice-border-color));
  border-radius: var(--radius-control);
  color: var(--notice-ink, var(--ink));
  line-height: 1.55;
  font-size: 0.92rem;
}

.demo-notice__mark {
  flex: none;
  width: 8px;
  height: 8px;
  margin-top: 6px;
  border-radius: 50%;
  background: var(--notice-mark, var(--primary));
}
</style>
