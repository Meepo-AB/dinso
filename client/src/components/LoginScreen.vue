<script setup lang="ts">
import { computed } from 'vue'
import type { Portal, Profile } from '../data/customer'

const props = defineProps<{
  customerName: string
  customerIconUrl: string
  portals: {
    id: Portal
    title: string
    description: string
    available: boolean
  }[]
  selectedPortal: Portal | null
  profiles: Profile[]
  selectedProfileId: string
  loginError: string
  roleLabel: (profile: Profile) => string
  labels: {
    heading: string
    intro: string
    switchHint: string
    portalQuestion: string
    portalLabel: string
    profileQuestion: string
    profileLabel: string
    chooseProfileFirst: string
    chooseProfile: string
    hint: string
    open: string
  }
}>()

const emit = defineEmits<{
  'select-portal': [Portal]
  'select-profile': [string]
  submit: []
}>()

const selectedProfile = computed(
  () => props.profiles.find((item) => item.id === props.selectedProfileId) ?? null,
)
const availablePortals = computed(() =>
  props.portals.filter((portal) => portal.available),
)
</script>

<template>
  <main class="login-screen">
    <div class="login-screen__frame">
      <header class="login-screen__intro">
        <p class="login-screen__brand">
          <img
            class="login-screen__icon"
            :src="customerIconUrl"
            alt=""
            aria-hidden="true"
          />
          {{ customerName }}
        </p>
        <h1>{{ labels.heading }}</h1>
        <p class="login-screen__lede">{{ labels.intro }}</p>
      </header>

      <section class="login-screen__profiles" aria-labelledby="profile-heading">
        <h2 id="profile-heading">{{ labels.profileQuestion }}</h2>

        <div
          class="profile-cards"
          role="radiogroup"
          :aria-label="labels.profileLabel"
        >
          <button
            v-for="item in profiles"
            :key="item.id"
            type="button"
            class="profile-card"
            :class="{ 'is-selected': selectedProfileId === item.id }"
            role="radio"
            :aria-checked="selectedProfileId === item.id"
            @click="emit('select-profile', item.id)"
          >
            <span class="profile-card__top">
              <span class="profile-card__name">{{ item.name }}</span>
              <span class="profile-card__role">{{ roleLabel(item) }}</span>
            </span>
            <span class="profile-card__description">
              {{ item.description }}
            </span>
            <span class="profile-card__preview">{{ item.preview }}</span>
          </button>
        </div>
      </section>

      <section class="login-screen__portals" aria-labelledby="portal-heading">
        <h2 id="portal-heading">{{ labels.portalQuestion }}</h2>

        <div v-if="!selectedProfile" class="profile-placeholder">
          <p>{{ labels.chooseProfileFirst }}</p>
        </div>

        <div
          v-else
          class="portal-cards"
          role="radiogroup"
          :aria-label="labels.portalLabel"
        >
          <button
            v-for="portal in availablePortals"
            :key="portal.id"
            type="button"
            class="portal-card"
            :class="{ 'is-selected': selectedPortal === portal.id }"
            role="radio"
            :aria-checked="selectedPortal === portal.id"
            @click="emit('select-portal', portal.id)"
          >
            <span class="portal-card__marker" aria-hidden="true" />
            <span class="portal-card__body">
              <span class="portal-card__title">{{ portal.title }}</span>
              <span class="portal-card__description">
                {{ portal.description }}
              </span>
            </span>
          </button>
        </div>

        <p v-if="loginError" class="login-screen__error" role="alert">
          {{ loginError }}
        </p>

        <div class="login-screen__submit">
          <p class="login-screen__hint">
            {{ selectedPortal ? labels.switchHint : labels.hint }}
          </p>
          <button
            class="button"
            type="button"
            :disabled="!selectedProfile || !selectedPortal"
            @click="emit('submit')"
          >
            {{ labels.open }}
          </button>
        </div>
      </section>
    </div>
  </main>
</template>

<style scoped>
.login-screen {
  min-height: 100vh;
  background: var(--login-bg, var(--canvas));
  display: flex;
  justify-content: center;
  padding: var(--space-login-padding, 64px 24px 80px);
}

.login-screen__frame {
  width: 100%;
  max-width: var(--login-max-width, 880px);
}

.login-screen__intro {
  padding-bottom: var(--space-login-intro-gap, 40px);
  border-bottom: var(--login-intro-border, 1px solid var(--border));
  margin-bottom: var(--space-login-section-gap, 40px);
  position: relative;
}

.login-screen__intro::after {
  content: '';
  position: absolute;
  left: 0;
  bottom: var(--login-intro-accent-offset, -1px);
  width: var(--login-intro-accent-width, 0);
  height: var(--login-intro-accent-height, 0);
  background: var(--login-intro-accent-color, transparent);
}

.login-screen__brand {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  margin: 0 0 12px;
  font-weight: 700;
  letter-spacing: var(--brand-tracking, -0.01em);
  color: var(--primary);
  text-transform: var(--brand-case, none);
}

.login-screen__icon {
  width: 32px;
  height: 32px;
  flex: none;
  border-radius: var(--radius-control);
}

.login-screen__intro h1 {
  font-family: var(--font-display, var(--font-sans));
  font-size: clamp(2rem, 5vw, 3.1rem);
  letter-spacing: var(--heading-tracking, -0.02em);
  line-height: 1.08;
  margin: 0;
  color: var(--ink);
}

.login-screen__lede {
  max-width: 58ch;
  margin: 16px 0 0;
  color: var(--muted);
  line-height: 1.6;
}

.login-screen__portals,
.login-screen__profiles {
  margin-bottom: var(--space-login-section-gap, 40px);
}

.login-screen h2 {
  font-family: var(--font-display, var(--font-sans));
  font-size: 1.15rem;
  font-weight: 700;
  margin: 0 0 16px;
  color: var(--ink);
}

.portal-cards {
  display: grid;
  gap: 12px;
}

.portal-card {
  display: flex;
  align-items: flex-start;
  gap: 14px;
  width: 100%;
  text-align: left;
  padding: var(--space-panel-padding, 22px);
  background: var(--surface);
  border: var(--card-border, 1px solid var(--border));
  border-radius: var(--radius-panel);
  color: var(--ink);
  box-shadow: var(--shadow-panel, none);
  transition:
    border-color 0.16s ease,
    background-color 0.16s ease,
    transform 0.16s ease,
    box-shadow 0.16s ease;
}

.portal-card:hover {
  border-color: var(--primary);
  transform: var(--card-hover-lift, none);
  box-shadow: var(--shadow-panel-hover, var(--shadow-panel, none));
}

.portal-card.is-selected {
  border-color: var(--primary);
  background: var(--selection-bg);
  border-left: var(--card-selected-accent, 1px solid var(--primary));
}

.portal-card__marker {
  flex: none;
  width: var(--marker-size, 18px);
  height: var(--marker-size, 18px);
  margin-top: 3px;
  border-radius: var(--marker-radius, 50%);
  border: 2px solid var(--border);
  background: var(--surface);
  position: relative;
  transition:
    border-color 0.16s ease,
    background-color 0.16s ease;
}

.portal-card.is-selected .portal-card__marker {
  border-color: var(--primary);
  background: var(--primary);
}

.portal-card.is-selected .portal-card__marker::after {
  content: '';
  position: absolute;
  inset: var(--marker-inset, 4px);
  border-radius: var(--marker-radius, 50%);
  background: var(--on-primary, white);
}

.portal-card__body {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.portal-card__title {
  font-weight: 700;
  font-size: 1.05rem;
}

.portal-card__description {
  color: var(--muted);
  line-height: 1.5;
}

.profile-placeholder {
  padding: 24px;
  border: var(--empty-border, 1px dashed var(--border));
  border-radius: var(--radius-panel);
  color: var(--muted);
}

.profile-placeholder p {
  margin: 0;
}

.profile-cards {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 12px;
}

.profile-card {
  display: flex;
  flex-direction: column;
  gap: 8px;
  text-align: left;
  padding: var(--space-panel-padding, 20px);
  background: var(--surface);
  border: var(--card-border, 1px solid var(--border));
  border-radius: var(--radius-panel);
  color: var(--ink);
  box-shadow: var(--shadow-panel, none);
  transition:
    border-color 0.16s ease,
    background-color 0.16s ease;
}

.profile-card:hover {
  border-color: var(--primary);
  transform: var(--card-hover-lift, none);
  box-shadow: var(--shadow-panel-hover, var(--shadow-panel, none));
}

.profile-card.is-selected {
  border-color: var(--primary);
  background: var(--selection-bg);
  border-left: var(--card-selected-accent, 1px solid var(--primary));
}

.profile-card__top {
  display: flex;
  justify-content: space-between;
  align-items: baseline;
  gap: 8px;
}

.profile-card__name {
  font-weight: 700;
}

.profile-card__role {
  font-size: 0.78rem;
  font-weight: 600;
  color: var(--primary);
  white-space: nowrap;
}

.profile-card__description {
  color: var(--muted);
  line-height: 1.45;
  font-size: 0.92rem;
}

.profile-card__preview {
  font-size: 0.82rem;
  color: var(--muted-soft, var(--muted));
  font-variant-numeric: tabular-nums;
}

.login-screen__error {
  margin: 16px 0 0;
  padding: 12px 16px;
  background: var(--status-attention-bg);
  color: var(--status-attention-ink);
  border-radius: var(--radius-control);
}

.login-screen__submit {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 24px;
  margin-top: 24px;
  padding-top: 24px;
  border-top: 1px solid var(--border);
}

.login-screen__hint {
  margin: 0;
  color: var(--muted);
  line-height: 1.5;
  max-width: 42ch;
}

@media (max-width: 640px) {
  .login-screen {
    padding: 32px 16px max(40px, env(safe-area-inset-bottom));
  }

  .profile-card__top {
    align-items: flex-start;
    flex-direction: column;
  }

  .login-screen__submit {
    flex-direction: column;
    align-items: stretch;
    gap: 16px;
  }
}
</style>
