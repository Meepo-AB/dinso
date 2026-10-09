<script setup lang="ts">
import { onBeforeUnmount, onMounted, ref } from 'vue'
import type { Profile } from '../data/customer'

defineProps<{
  profiles: (Profile & {
    companies: { id: string; name: string; detail: string }[]
  })[]
  selectedProfileId: string
  roleLabel: (profile: Profile) => string
  labels: {
    title: string
    intro: string
    close: string
  }
}>()

const emit = defineEmits<{ select: [string]; close: [] }>()
const dialog = ref<HTMLElement | null>(null)

function onKeydown(event: KeyboardEvent) {
  if (event.key === 'Escape') {
    emit('close')
    return
  }
  if (event.key !== 'Tab' || !dialog.value) return
  const focusable = dialog.value.querySelectorAll<HTMLElement>('button')
  const first = focusable[0]
  const last = focusable[focusable.length - 1]
  if (!first || !last) return
  if (event.shiftKey && document.activeElement === first) {
    event.preventDefault()
    last.focus()
  } else if (!event.shiftKey && document.activeElement === last) {
    event.preventDefault()
    first.focus()
  }
}

onMounted(() => {
  document.addEventListener('keydown', onKeydown)
  const selected = dialog.value?.querySelector<HTMLElement>('.is-selected')
  ;(selected ?? dialog.value)?.focus()
})

onBeforeUnmount(() => document.removeEventListener('keydown', onKeydown))
</script>

<template>
  <div class="modal-backdrop" @click.self="emit('close')">
    <section
      ref="dialog"
      class="profile-modal"
      role="dialog"
      aria-modal="true"
      aria-labelledby="profile-modal-title"
      aria-describedby="profile-modal-intro"
      tabindex="-1"
    >
      <div class="profile-modal__header">
        <h2 id="profile-modal-title">{{ labels.title }}</h2>
        <button
          class="icon-button"
          type="button"
          :aria-label="labels.close"
          @click="emit('close')"
        >
          ×
        </button>
      </div>
      <p id="profile-modal-intro" class="profile-modal__intro">
        {{ labels.intro }}
      </p>

      <div class="profile-cards" role="radiogroup" :aria-label="labels.title">
        <button
          v-for="item in profiles"
          :key="item.id"
          type="button"
          class="profile-card"
          :class="{ 'is-selected': selectedProfileId === item.id }"
          role="radio"
          :aria-checked="selectedProfileId === item.id"
          @click="emit('select', item.id)"
        >
          <span class="profile-card__name">{{ item.name }}</span>
          <span class="profile-card__role">{{ roleLabel(item) }}</span>
          <span v-if="item.companies.length" class="profile-card__companies">
            <span
              v-for="company in item.companies"
              :key="company.id"
              class="profile-card__company"
            >
              <span class="profile-card__company-name">{{ company.name }}</span>
              <span class="profile-card__company-detail">
                {{ company.detail }}
              </span>
            </span>
          </span>
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

.profile-modal {
  width: min(100%, 640px);
  max-height: min(760px, calc(100vh - 48px));
  overflow: auto;
  padding: 28px;
  background: var(--surface);
  border: var(--panel-border, 1px solid var(--border));
  border-radius: var(--radius-panel);
  box-shadow: 0 20px 60px rgb(16 42 67 / 20%);
  outline: none;
}

.profile-modal__header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 24px;
}

.profile-modal h2 {
  margin: 0;
  font-family: var(--font-display, var(--font-sans));
  font-size: 1.55rem;
  color: var(--ink);
}

.profile-modal__intro {
  margin: 8px 0 24px;
  color: var(--muted);
  line-height: 1.5;
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

.profile-cards {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 12px;
}

.profile-card {
  display: flex;
  flex-direction: column;
  gap: 4px;
  text-align: left;
  padding: var(--space-panel-padding, 20px);
  background: var(--surface);
  border: var(--card-border, 1px solid var(--border));
  border-radius: var(--radius-panel);
  color: var(--ink);
  transition:
    border-color 0.16s ease,
    background-color 0.16s ease;
}

.profile-card:hover {
  border-color: var(--primary);
}

.profile-card.is-selected {
  border-color: var(--primary);
  background: var(--selection-bg);
  border-left: var(--card-selected-accent, 1px solid var(--primary));
}

.profile-card__name {
  font-weight: 700;
  line-height: 1.35;
}

.profile-card__role {
  font-size: 0.82rem;
  font-weight: 600;
  line-height: 1.4;
  color: var(--primary);
}

.profile-card__companies {
  display: grid;
  gap: 4px;
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid var(--border);
  font-size: 0.82rem;
  line-height: 1.4;
}

.profile-card__company {
  display: flex;
  justify-content: space-between;
  align-items: baseline;
  gap: 16px;
}

.profile-card__company-name {
  min-width: 0;
  overflow-wrap: anywhere;
}

.profile-card__company-detail {
  flex-shrink: 0;
  color: var(--muted-soft, var(--muted));
  font-variant-numeric: tabular-nums;
}

@media (max-width: 520px) {
  .modal-backdrop {
    padding: 12px;
  }

  .profile-modal {
    padding: 22px 18px;
  }
}

@media (prefers-reduced-motion: reduce) {
  .profile-card {
    transition: none;
  }
}
</style>
