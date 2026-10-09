<script setup lang="ts">
defineProps<{
  customerName: string
  customerIconUrl: string
  portalLabel: string

  signedInAsLabel: string
  profileName: string
  signOutLabel: string
  companyLabel?: string
  companies?: { id: string; name: string }[]
  selectedCompanyId?: string
}>()

defineEmits<{ 'sign-out': []; 'select-company': [companyId: string] }>()
</script>

<template>
  <header class="app-header">
    <div class="app-header__identity">
      <span class="app-header__brand">
        <img
          class="app-header__icon"
          :src="customerIconUrl"
          alt=""
          aria-hidden="true"
        />
        {{ customerName }}
        <span class="app-header__portal">/ {{ portalLabel }}</span>
      </span>
    </div>
    <div class="app-header__profile">
      <div class="app-header__profile-info">
        <span class="app-header__meta">{{ signedInAsLabel }}</span>
        <strong>{{ profileName }}</strong>
      </div>
      <label
        v-if="companies && companies.length > 1"
        class="app-header__company"
      >
        <span>{{ companyLabel }}</span>
        <select
          :value="selectedCompanyId"
          @change="
            $emit('select-company', ($event.target as HTMLSelectElement).value)
          "
        >
          <option
            v-for="company in companies"
            :key="company.id"
            :value="company.id"
          >
            {{ company.name }}
          </option>
        </select>
      </label>
      <button
        class="app-header__signout"
        type="button"
        @click="$emit('sign-out')"
      >
        {{ signOutLabel }}
      </button>
    </div>
  </header>
</template>

<style scoped>
.app-header {
  background: var(--header-bg, var(--ink));
  color: var(--header-ink, white);
  padding: var(--space-header-padding, 18px)
    max(24px, calc((100vw - var(--shell-max-width, 1440px)) / 2));
  padding-top: max(var(--space-header-padding, 18px), env(safe-area-inset-top));
  display: flex;
  gap: 24px;
  justify-content: space-between;
  align-items: center;
  border-bottom: var(--header-border, none);
  position: relative;
}

.app-header__identity {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.app-header__brand {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  font-family: var(--font-display, var(--font-sans));
  font-size: 1.25rem;
  font-weight: 700;
  letter-spacing: var(--brand-tracking, -0.02em);
}

.app-header__icon {
  width: 28px;
  height: 28px;
  flex: none;
  border-radius: var(--radius-control);
}

.app-header__portal {
  font-size: 0.82rem;
  font-weight: 500;
  letter-spacing: 0;
  opacity: 0.85;
}

.app-header__meta {
  font-size: 0.8rem;
  opacity: 0.75;
}

.app-header__profile {
  display: flex;
  gap: 16px;
  align-items: center;
}

.app-header__profile-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
  text-align: right;
}

.app-header__profile-info strong {
  font-size: 0.92rem;
}

.app-header__company {
  display: flex;
  flex-direction: column;
  gap: 3px;
  font-size: 0.75rem;
  font-weight: 600;
}

.app-header__company select {
  min-height: 36px;
  max-width: 220px;
  border: 1px solid
    color-mix(in srgb, var(--header-ink, white) 55%, transparent);
  border-radius: var(--radius-control);
  background: var(--header-bg, var(--ink));
  color: var(--header-ink, white);
  font: inherit;
  font-size: 0.88rem;
  padding: 6px 28px 6px 9px;
}

.app-header__signout {
  border: 1px solid
    color-mix(in srgb, var(--header-ink, white) 55%, transparent);
  background: transparent;
  color: var(--header-ink, white);
  padding: 9px 14px;
  border-radius: var(--radius-control);
  font-weight: 600;
  font-size: 0.88rem;
  transition: background-color 0.16s ease;
}

.app-header__signout:hover {
  background: color-mix(in srgb, var(--header-ink, white) 14%, transparent);
}

.app-header__signout:active {
  background: color-mix(in srgb, var(--header-ink, white) 22%, transparent);
}

@media (max-width: 760px) {
  .app-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
    padding-right: 16px;
    padding-left: 16px;
  }

  .app-header__profile {
    width: 100%;
    justify-content: space-between;
  }

  .app-header__profile-info {
    text-align: left;
  }

  .app-header__company {
    flex: 1;
  }

  .app-header__company select {
    max-width: none;
    width: 100%;
  }

  .app-header__signout {
    min-width: 44px;
  }
}
</style>
