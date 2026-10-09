<script setup lang="ts">
import { onBeforeUnmount, onMounted, ref } from 'vue'

export interface CaseDetails {
  name: string
  status: string
  value: string
  detail: string
}

const props = defineProps<{
  item: CaseDetails
  canApprove: boolean
  labels: {
    eyebrow: string
    close: string
    status: string
    due: string
    details: string
    approve: string
    approved: string
    readOnly: string
  }
}>()

const emit = defineEmits<{ close: []; approve: [] }>()
const dialog = ref<HTMLElement | null>(null)

function onKeydown(event: KeyboardEvent) {
  if (event.key === 'Escape') emit('close')
}

onMounted(() => {
  document.addEventListener('keydown', onKeydown)
  dialog.value?.focus()
})

onBeforeUnmount(() => document.removeEventListener('keydown', onKeydown))
</script>

<template>
  <div class="modal-backdrop" @click.self="emit('close')">
    <section
      ref="dialog"
      class="case-modal"
      role="dialog"
      aria-modal="true"
      :aria-labelledby="`case-title-${item.name}`"
      tabindex="-1"
    >
      <div class="case-modal__header">
        <div>
          <p class="case-modal__eyebrow">{{ labels.eyebrow }}</p>
          <h2 :id="`case-title-${item.name}`">{{ item.name }}</h2>
        </div>
        <button
          class="icon-button"
          type="button"
          :aria-label="labels.close"
          @click="emit('close')"
        >
          ×
        </button>
      </div>

      <dl class="case-modal__summary">
        <div>
          <dt>{{ labels.status }}</dt>
          <dd>{{ item.status }}</dd>
        </div>
        <div>
          <dt>{{ labels.due }}</dt>
          <dd>{{ item.value }}</dd>
        </div>
      </dl>

      <div class="case-modal__details">
        <h3>{{ labels.details }}</h3>
        <p>{{ item.detail }}</p>
      </div>

      <p
        v-if="!canApprove && item.status !== labels.approved"
        class="case-modal__readonly"
      >
        {{ labels.readOnly }}
      </p>
      <div class="case-modal__actions">
        <button class="button secondary" type="button" @click="emit('close')">
          {{ labels.close }}
        </button>
        <button
          v-if="canApprove && item.status !== labels.approved"
          class="button"
          type="button"
          @click="emit('approve')"
        >
          {{ labels.approve }}
        </button>
      </div>
    </section>
  </div>
</template>

<style scoped>
.modal-backdrop {
  position: fixed;
  inset: 0;
  z-index: 20;
  display: grid;
  place-items: center;
  padding: 24px;
  background: rgb(16 42 67 / 42%);
}

.case-modal {
  width: min(100%, 560px);
  max-height: min(700px, calc(100vh - 48px));
  overflow: auto;
  padding: 28px;
  background: var(--surface);
  border: var(--panel-border, 1px solid var(--border));
  border-radius: var(--radius-panel);
  box-shadow: 0 20px 60px rgb(16 42 67 / 20%);
  outline: none;
}

.case-modal__header {
  display: flex;
  justify-content: space-between;
  gap: 24px;
  align-items: flex-start;
}

.case-modal__eyebrow {
  margin: 0 0 8px;
  color: var(--muted);
  font-size: 0.78rem;
  font-weight: 700;
  letter-spacing: var(--label-tracking, 0.02em);
  text-transform: var(--label-case, uppercase);
}

.case-modal h2,
.case-modal h3 {
  margin: 0;
  font-family: var(--font-display, var(--font-sans));
}

.case-modal h2 {
  font-size: 1.55rem;
}
.case-modal h3 {
  font-size: 1rem;
}

.icon-button {
  width: 44px;
  min-height: 44px;
  padding: 0;
  border: 1px solid var(--border);
  border-radius: var(--radius-control);
  background: transparent;
  color: var(--ink);
  font-size: 1.6rem;
  line-height: 1;
}

.case-modal__summary {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
  margin: 28px 0;
  padding: 16px 0;
  border-top: 1px solid var(--border);
  border-bottom: 1px solid var(--border);
}

.case-modal__summary div {
  display: grid;
  gap: 4px;
}
.case-modal dt {
  color: var(--muted);
  font-size: 0.8rem;
}
.case-modal dd {
  margin: 0;
  font-weight: 700;
}
.case-modal__details {
  line-height: 1.55;
}
.case-modal__details p {
  margin: 8px 0 0;
  color: var(--muted);
}
.case-modal__readonly {
  color: var(--muted);
  font-size: 0.9rem;
}
.case-modal__actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 28px;
}

@media (max-width: 520px) {
  .modal-backdrop {
    padding: 12px;
  }
  .case-modal {
    padding: 22px 18px;
  }
  .case-modal__actions {
    flex-direction: column-reverse;
  }
  .case-modal__actions .button {
    width: 100%;
  }
}
</style>
