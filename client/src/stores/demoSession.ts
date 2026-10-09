import { computed, ref } from 'vue'
import { defineStore } from 'pinia'
import customer from '@customer/config'
import type { Portal, Profile, ProfileCompany } from '../data/customer'

type Company = { id: string; name: string }

const useApi = import.meta.env.VITE_USE_API === 'true'
const apiUrl = import.meta.env.VITE_API_URL?.replace(/\/$/, '') ?? ''

type StoredSession = {
  profileId: string
  portal: Portal
  token: string
  companyId: string
}

const storageKey = `dinso:${customer.key}:demo-session`

const readStoredSession = (): StoredSession | null => {
  try {
    const stored = window.sessionStorage.getItem(storageKey)
    if (!stored) return null

    const value = JSON.parse(stored) as Partial<StoredSession>
    if (
      typeof value.profileId !== 'string' ||
      typeof value.portal !== 'string' ||
      !['PRIVATE', 'COMPANY', 'SYSTEM'].includes(value.portal) ||
      typeof value.token !== 'string' ||
      typeof value.companyId !== 'string'
    )
      return null

    return value as StoredSession
  } catch {
    return null
  }
}

export const useDemoSessionStore = defineStore('demo-session', () => {
  const storedSession = readStoredSession()
  const storedProfile = storedSession
    ? (customer.profiles.find((item) => item.id === storedSession.profileId) ?? null)
    : null
  const allowedPortals = storedProfile?.portals ?? (storedProfile ? [storedProfile.portal] : [])
  const canRestore = storedProfile && storedSession && allowedPortals.includes(storedSession.portal)
  const profile = ref<Profile | null>(canRestore ? storedProfile : null)
  const selectedPortal = ref<Portal | null>(
    canRestore ? storedSession.portal : null,
  )
  const sessionToken = ref(canRestore ? storedSession.token : '')
  const companyId = ref(canRestore ? storedSession.companyId : '')
  const companyName = ref('')
  const companies = ref<Company[]>([])
  const profileCompanies = ref<Record<string, ProfileCompany[]>>({})

  const persist = (): void => {
    if (!profile.value || !selectedPortal.value) return
    window.sessionStorage.setItem(
      storageKey,
      JSON.stringify({
        profileId: profile.value.id,
        portal: selectedPortal.value,
        token: sessionToken.value,
        companyId: companyId.value,
      } satisfies StoredSession),
    )
  }

  if (!canRestore && storedSession) window.sessionStorage.removeItem(storageKey)

  const activePortal = computed<Portal | null>(
    () => profile.value?.portal ?? null,
  )
  const isCompany = computed<boolean>(() => activePortal.value === 'COMPANY')
  const canManageCompany = computed<boolean>(
    () =>
      profile.value?.role === 'COMPANY_ADMIN' ||
      profile.value?.role === 'SYSTEM_ADMIN',
  )
  const canApproveCases = computed<boolean>(() => canManageCompany.value)
  const availableCompanies = computed<Company[]>(() =>
    companies.value.length > 0
      ? companies.value
      : (profileCompanies.value[profile.value?.id ?? ''] ?? []),
  )
  const selectedCompanyName = computed<string>(
    () => companyName.value || availableCompanies.value[0]?.name || '',
  )

  const loadProfileCompanies = async (): Promise<void> => {
    if (!useApi) return
    try {
      const response = await fetch(`${apiUrl}/api/demo/profiles/companies`)
      if (!response.ok) return
      const items = (await response.json()) as {
        profileId: string
        companies: ProfileCompany[]
      }[]
      profileCompanies.value = Object.fromEntries(
        items.map((item) => [item.profileId, item.companies]),
      )
    } catch {
      profileCompanies.value = {}
    }
  }

  const start = (
    selectedProfile: Profile,
    portal: Portal,
    token = '',
  ): void => {
    profile.value = selectedProfile
    selectedPortal.value = portal
    sessionToken.value = token
    companyId.value = ''
    companyName.value = ''
    companies.value = []
    persist()
  }

  const end = (): void => {
    profile.value = null
    selectedPortal.value = null
    sessionToken.value = ''
    companyId.value = ''
    companyName.value = ''
    companies.value = []
    window.sessionStorage.removeItem(storageKey)
  }

  const setCompanies = (items: Company[]): void => {
    companies.value = items
    const selected =
      items.find((item) => item.id === companyId.value) ??
      items.find((item) => item.name === companyName.value) ??
      items[0]
    if (!selected) return
    companyId.value = selected.id
    companyName.value = selected.name
    persist()
  }

  const selectCompany = (id: string): void => {
    const company = availableCompanies.value.find((item) => item.id === id)
    if (!company) return
    companyId.value = company.id
    companyName.value = company.name
    persist()
  }

  return {
    profile,
    selectedPortal,
    sessionToken,
    companyId,
    companyName,
    companies,
    profileCompanies,
    activePortal,
    isCompany,
    canManageCompany,
    canApproveCases,
    availableCompanies,
    selectedCompanyName,
    start,
    end,
    setCompanies,
    selectCompany,
    loadProfileCompanies,
  }
})
