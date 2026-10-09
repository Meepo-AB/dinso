<script setup lang="ts">
import { computed, nextTick, ref } from 'vue'
import { RouterLink, type RouteLocationRaw } from 'vue-router'
import type { Portal, Profile } from '../data/customer'
import DemoProfileModal from './DemoProfileModal.vue'

const props = defineProps<{
  customerName: string
  customerIconUrl: string
  portals: {
    id: Portal
    title: string
    description: string
    to: RouteLocationRaw
  }[]
  activePortal: Portal | null
  profiles: (Profile & {
    companies: { id: string; name: string; detail: string }[]
  })[]
  selectedProfileId: string
  loginError: string
  roleLabel: (profile: Profile) => string
  labels: {
    heading: string
    intro: string
    changePortal: string
    profileLabel: string
    chooseProfile: string
    modalIntro: string
    close: string
    login: string
  }
}>()

const emit = defineEmits<{
  'select-profile': [string]
  submit: []
}>()

const modalOpen = ref(false)
const profileTrigger = ref<HTMLButtonElement | null>(null)

const portal = computed(
  () => props.portals.find((item) => item.id === props.activePortal) ?? null,
)
const selectedProfile = computed(
  () => props.profiles.find((item) => item.id === props.selectedProfileId) ?? null,
)

const closeModal = async (): Promise<void> => {
  modalOpen.value = false
  await nextTick()
  profileTrigger.value?.focus()
}
const selectProfile = (id: string): void => {
  emit('select-profile', id)
  void closeModal()
}
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
        <RouterLink v-if="portal" class="login-screen__back" :to="{ name: 'login' }">
          <span aria-hidden="true">←</span> {{ labels.changePortal }}
        </RouterLink>
        <h1>{{ portal?.title ?? labels.heading }}</h1>
        <p class="login-screen__lede">{{ portal?.description ?? labels.intro }}</p>
      </header>

      <nav v-if="!portal" class="portal-cards" :aria-label="labels.heading">
        <RouterLink
          v-for="item in portals"
          :key="item.id"
          class="portal-card"
          :to="item.to"
        >
          <span class="portal-card__body">
            <span class="portal-card__title">{{ item.title }}</span>
            <span class="portal-card__description">{{ item.description }}</span>
          </span>
          <span class="portal-card__arrow" aria-hidden="true">→</span>
        </RouterLink>
      </nav>

      <form v-else class="login-form" @submit.prevent="emit('submit')">
        <div class="profile-field">
          <span id="profile-field-label" class="profile-field__label">
            {{ labels.profileLabel }}
          </span>
          <button
            ref="profileTrigger"
            type="button"
            class="profile-field__input"
            aria-haspopup="dialog"
            aria-labelledby="profile-field-label profile-field-value"
            @click="modalOpen = true"
          >
            <span
              id="profile-field-value"
              class="profile-field__value"
              :class="{ 'is-empty': !selectedProfile }"
            >
              {{ selectedProfile?.name ?? labels.chooseProfile }}
            </span>
            <span class="profile-field__chevron" aria-hidden="true">▾</span>
          </button>
        </div>

        <p v-if="loginError" class="login-screen__error" role="alert">
          {{ loginError }}
        </p>

        <div class="login-screen__submit">
          <button class="button" type="submit" :disabled="!selectedProfile">
            {{ labels.login }}
          </button>
        </div>
      </form>
    </div>

    <DemoProfileModal
      v-if="modalOpen"
      :profiles="profiles"
      :selected-profile-id="selectedProfileId"
      :role-label="roleLabel"
      :labels="{
        title: labels.chooseProfile,
        intro: labels.modalIntro,
        close: labels.close,
      }"
      @select="selectProfile"
      @close="closeModal"
    />
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
  display: flex;
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

.login-screen__back {
  display: inline-flex;
  gap: 6px;
  margin: 8px 0 16px;
  color: var(--primary);
  font-weight: 600;
  text-decoration: none;
}

.login-screen__back:hover {
  text-decoration: underline;
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

.portal-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 16px;
}

.portal-card {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  gap: 24px;
  padding: var(--space-panel-padding, 22px);
  background: var(--surface);
  border: var(--card-border, 1px solid var(--border));
  border-radius: var(--radius-panel);
  color: var(--ink);
  text-decoration: none;
  box-shadow: var(--shadow-panel, none);
  transition:
    border-color 0.16s ease,
    transform 0.16s ease,
    box-shadow 0.16s ease;
}

.portal-card:hover {
  border-color: var(--primary);
  transform: var(--card-hover-lift, none);
  box-shadow: var(--shadow-panel-hover, var(--shadow-panel, none));
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

.portal-card__arrow {
  flex: none;
  color: var(--primary);
  font-size: 1.3rem;
}

.login-form {
  max-width: 480px;
}

.profile-field {
  display: grid;
  gap: 8px;
}

.profile-field__label {
  color: var(--ink);
  font-size: 0.92rem;
  font-weight: 700;
}

.profile-field__input {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  width: 100%;
  min-height: 46px;
  padding: 10px 12px;
  border: 1px solid var(--border);
  border-radius: var(--radius-control);
  background: var(--surface);
  color: var(--ink);
  text-align: left;
  cursor: pointer;
}

.profile-field__input:hover {
  border-color: var(--primary);
}

.profile-field__value.is-empty {
  color: var(--muted);
}

.profile-field__chevron {
  flex: none;
  color: var(--muted);
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
  justify-content: flex-end;
  margin-top: 24px;
}

@media (max-width: 640px) {
  .login-screen {
    padding: 32px 16px max(40px, env(safe-area-inset-bottom));
  }

  .login-screen__submit {
    flex-direction: column;
    align-items: stretch;
  }
}

@media (prefers-reduced-motion: reduce) {
  .portal-card {
    transition: none;
  }
}
</style>
