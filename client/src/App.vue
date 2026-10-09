<script setup lang="ts">
import { computed, ref, watch, watchEffect } from 'vue'
import { RouterView, useRoute, useRouter } from 'vue-router'
import customer from '@customer/config'
import faviconUrl from '@customer/favicon.svg'
import '@customer/theme.css'
import { roleLabels, type Portal, type Profile } from './data/customer'
import { activeLocale, translate } from './i18n'
import AppHeader from './components/AppHeader.vue'
import PortalNav from './components/PortalNav.vue'
import LoginScreen from './components/LoginScreen.vue'
import { useDemoSessionStore } from './stores/demoSession'
import { useDemoPortalStore } from './stores/demoPortal'

const route = useRoute()
const router = useRouter()
const session = useDemoSessionStore()
const portal = useDemoPortalStore()
const selectedProfileId = ref('')
const locale = activeLocale
const loginError = ref('')
const useApi = import.meta.env.VITE_USE_API === 'true'
const apiUrl = import.meta.env.VITE_API_URL?.replace(/\/$/, '') ?? ''
const t = (source: string, values?: Record<string, string | number>): string =>
  translate(locale.value, source, values)

const routeNames: Record<Portal, Record<string, string | undefined>> = {
  PRIVATE: {
    overview: 'private-overview',
    insurance: 'private-insurance',
    events: 'private-events',
    documents: 'private-documents',
    payments: 'private-payments',
  },
  COMPANY: {
    overview: 'company-overview',
    employees: 'company-employees',
    plans: 'company-plans',
    cases: 'company-cases',
    documents: 'company-documents',
  },
  SYSTEM: { overview: 'system-admin-overview' },
}

const labels = computed(() => ({
  overview: t('Översikt'),
  insurance: t('Försäkringar'),
  events: t('Händelser'),
  documents: t('Dokument'),
  employees: t('Medarbetare'),
  plans: t('Avtal'),
  cases: t('Ärenden'),
  payments: t('Utbetalningar'),
  open: t('Öppna portal'),
}))
const portalSlugs: Record<Portal, string> = {
  PRIVATE: 'privat',
  COMPANY: 'foretag',
  SYSTEM: 'systemadmin',
}
const canEnter = (item: Profile, portal: Portal): boolean =>
  (item.portals ?? [item.portal]).includes(portal)
const isLoginRoute = computed(
  () => route.name === 'login' || route.name === 'portal-login',
)
const loginPortal = computed<Portal | null>(() => {
  if (route.name !== 'portal-login') return null
  const entry = Object.entries(portalSlugs).find(
    ([, slug]) => slug === route.params.portal,
  )
  return entry ? (entry[0] as Portal) : null
})
const selectedProfile = computed(
  () =>
    customer.profiles.find((item) => item.id === selectedProfileId.value) ??
    null,
)
const portals = computed(() =>
  (
    [
      {
        id: 'PRIVATE' as Portal,
        title: t('Individportalen'),
        description: t(
          'För privatpersoner som vill se och administrera sina pensionsförsäkringar.',
        ),
      },
      {
        id: 'COMPANY' as Portal,
        title: t('Företagsportalen'),
        description: t(
          'För företagsadministratörer som vill administrera företagets pensionsplaner och medarbetare.',
        ),
      },
      {
        id: 'SYSTEM' as Portal,
        title: t('Systemadmin'),
        description: t(
          'För systemadministratör som vill administrera behörigheter för företagsadministratörer.',
        ),
      },
    ] as const
  )
    .filter((item) =>
      customer.profiles.some((profile) => canEnter(profile, item.id)),
    )
    .map((item) => ({
      ...item,
      to: { name: 'portal-login', params: { portal: portalSlugs[item.id] } },
    })),
)
const loginProfiles = computed(() =>
  customer.profiles
    .filter((item) => loginPortal.value && canEnter(item, loginPortal.value))
    .map((item) => ({
    ...item,
      companies: (session.profileCompanies[item.id] ?? []).map((company) => ({
        id: company.id,
        name: company.name,
        detail: t('{count} medarbetare', { count: company.employees }),
      })),
    })),
)
const loginLabels = computed(() => ({
  heading: t('Välj portal'),
  intro: t('Välj vilken portal du vill logga in i.'),
  changePortal: t('Byt portal'),
  profileLabel: t('Person'),
  chooseProfile: t('Välj demoprofil'),
  modalIntro: t('Profilen avgör vilken roll och vilka uppgifter du ser.'),
  close: t('Stäng'),
  login: t('Logga in'),
}))
const portalLabel = computed(() =>
  t(
    session.activePortal === 'SYSTEM'
      ? 'Systemadmin'
      : session.isCompany
        ? 'Företag'
        : 'Privat',
  ),
)
const companies = computed(() =>
  session.isCompany ? session.availableCompanies : [],
)
const nav = computed<[string, string][]>(() =>
  session.activePortal === 'SYSTEM'
    ? [['overview', labels.value.overview]]
    : session.isCompany
      ? [
          ['overview', labels.value.overview],
          ['employees', labels.value.employees],
          ['plans', labels.value.plans],
          ['cases', labels.value.cases],
          ['documents', labels.value.documents],
        ]
      : [
          ['overview', labels.value.overview],
          ['insurance', labels.value.insurance],
          ['events', labels.value.events],
          ['documents', labels.value.documents],
          ['payments', labels.value.payments],
        ],
)
const activePage = computed(() =>
  route.meta.page === 'add-employee' ? 'employees' : (route.meta.page ?? 'overview'),
)

const roleLabel = (item: Profile): string => t(roleLabels[item.role])
const selectPage = (page: string): void => {
  const routeName = session.activePortal
    ? routeNames[session.activePortal][page]
    : undefined
  if (routeName) void router.push({ name: routeName })
}
const login = async (selected: Profile): Promise<void> => {
  const portal = loginPortal.value
  if (!portal) return
  loginError.value = ''
  let token = ''
  if (useApi) {
    try {
      const response = await fetch(`${apiUrl}/api/auth/login`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ profileId: selected.id }),
      })
      if (!response.ok) throw new Error(t('Profilen kunde inte öppnas.'))
      token = ((await response.json()) as { token: string }).token
    } catch (error) {
      loginError.value =
        error instanceof Error
          ? error.message
          : t('Profilen kunde inte öppnas.')
      return
    }
  }
  session.start(selected, portal, token)
  await router.push({ name: routeNames[portal].overview })
}
const selectCompany = (companyId: string): void => {
  session.selectCompany(companyId)
  void portal.loadCompanyData()
}

const logout = async (): Promise<void> => {
  if (useApi && session.sessionToken)
    await fetch(`${apiUrl}/api/auth/logout`, {
      method: 'POST',
      headers: { Authorization: `Bearer ${session.sessionToken}` },
    }).catch(() => undefined)
  session.end()
  selectedProfileId.value = ''
  loginError.value = ''
  await router.push({ name: 'login' })
}

watchEffect(() => {
  document.documentElement.lang = locale.value
  document.title = `${customer.name} — ${session.activePortal === 'SYSTEM' ? t('Systemadmin') : session.isCompany ? t('Företag') : t('Privat')}`
  const favicon = document.querySelector<HTMLLinkElement>('#customer-favicon')
  if (favicon) favicon.href = faviconUrl
})
watch(
  [() => session.profile, () => route.fullPath],
  ([profile]) => {
    if (!route.matched.length) return
    if (!profile) {
      const unknownPortal =
        route.name === 'portal-login' &&
        !portals.value.some((item) => item.id === loginPortal.value)
      if (!isLoginRoute.value || unknownPortal)
        void router.replace({ name: 'login' })
      return
    }
    if (isLoginRoute.value) {
      void router.replace({
        name: routeNames[session.activePortal ?? 'PRIVATE'].overview,
      })
      return
    }
    const portal = route.meta.portal
    const allowedPortals = profile.portals ?? [profile.portal]
    if (
      !portal ||
      !allowedPortals.includes(portal) ||
      (route.meta.roles &&
        !route.meta.roles.includes(profile.role) &&
        profile.role !== 'SYSTEM_ADMIN')
    )
      void router.replace({
        name: routeNames[session.activePortal ?? 'PRIVATE'].overview,
      })
  },
  { immediate: true },
)
void session.loadProfileCompanies()
watch(loginPortal, () => {
  selectedProfileId.value = ''
  loginError.value = ''
})
</script>

<template>
  <LoginScreen
    v-if="!session.profile"
    :customer-name="customer.name"
    :customer-icon-url="faviconUrl"
    :portals="portals"
    :active-portal="loginPortal"
    :profiles="loginProfiles"
    :selected-profile-id="selectedProfileId"
    :login-error="loginError"
    :role-label="roleLabel"
    :labels="loginLabels"
    @select-profile="selectedProfileId = $event"
    @submit="selectedProfile && login(selectedProfile)"
  />
  <div v-else class="app">
    <AppHeader
      :customer-name="customer.name"
      :customer-icon-url="faviconUrl"
      :portal-label="portalLabel"
      :company-label="t('Arbetsgivare')"
      :companies="companies"
      :selected-company-id="session.companyId || session.selectedCompanyName"
      :signed-in-as-label="t('Inloggad som')"
      :profile-name="session.profile.name"
      :sign-out-label="t('Logga ut')"
      @sign-out="logout"
      @select-company="selectCompany"
    />
    <main id="mainContent" class="shell" tabindex="-1">
      <PortalNav
        :items="nav"
        :active="activePage"
        @select="selectPage"
      />
      <RouterView />
      <div v-if="customer.locales.length > 1" class="language-switch">
        <label for="language">{{ t('Språk') }}</label>
        <select id="language" v-model="locale">
          <option value="sv">{{ t('Svenska') }}</option>
          <option value="en">{{ t('English') }}</option>
        </select>
      </div>
    </main>
  </div>
</template>

<style scoped>
.shell {
  max-width: var(--shell-max-width, 1440px);
  margin: auto;
  padding: var(--space-shell-padding, 32px 24px 72px);
}
.language-switch {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 28px;
  color: var(--muted);
  font-size: 0.9rem;
}
.language-switch select {
  border: 1px solid var(--border);
  border-radius: var(--radius-control);
  padding: 6px 8px;
  background: var(--surface);
  color: var(--ink);
}
</style>
