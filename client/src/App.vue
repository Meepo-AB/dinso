<script setup lang="ts">
import { computed, ref, watch, watchEffect } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import customer from '@customer/config'
import faviconUrl from '@customer/favicon.svg'
import '@customer/theme.css'
import type { Portal, Profile } from './data/customer'
import type { Page } from './router'
import { translate } from './i18n'
import { statusTone } from './data/status'
import AppHeader from './components/AppHeader.vue'
import PortalNav from './components/PortalNav.vue'
import PageHeader from './components/PageHeader.vue'
import Panel from './components/Panel.vue'
import DataTable from './components/DataTable.vue'
import StatusBadge from './components/StatusBadge.vue'
import DemoNotice from './components/DemoNotice.vue'
import LoginScreen from './components/LoginScreen.vue'
import EmptyState from './components/EmptyState.vue'
import AddEmployeeFlow, { type EmployeeDraft } from './components/AddEmployeeFlow.vue'
import CaseModal, { type CaseDetails } from './components/CaseModal.vue'
import OverviewView from './views/OverviewView.vue'
import InsuranceView from './views/InsuranceView.vue'
import PlansView from './views/PlansView.vue'
import CasesView from './views/CasesView.vue'
import ActivityView from './views/ActivityView.vue'
import SystemAdminView from './views/SystemAdminView.vue'

const route = useRoute()
const router = useRouter()
const profile = ref<Profile | null>(null)
const selectedPortal = ref<Portal | null>(null)
const selectedProfileId = ref('')
const activePortal = computed<Portal | null>(() => profile.value?.portal ?? null)

const routeNames: Record<Portal, Record<Page, string | undefined>> = {
  PRIVATE: {
    overview: 'private-overview', insurance: 'private-insurance', events: 'private-events', documents: 'private-documents', payments: 'private-payments',
    employees: undefined, plans: undefined, cases: undefined, 'add-employee': undefined,
  },
  COMPANY: {
    overview: 'company-overview', employees: 'company-employees', plans: 'company-plans', cases: 'company-cases', documents: 'company-documents', 'add-employee': 'company-add-employee',
    insurance: undefined, events: undefined, payments: undefined,
  },
  SYSTEM: {
    overview: 'system-admin-overview', insurance: undefined, events: undefined, documents: undefined, payments: undefined,
    employees: undefined, plans: undefined, cases: undefined, 'add-employee': undefined,
  },
}

const page = computed<Page>({
  get: () => route.meta.page ?? 'overview',
  set: nextPage => {
    const portal = activePortal.value
    const name = portal && routeNames[portal][nextPage]
    if (name) void router.push({ name })
  },
})
const locale = ref(customer.defaultLocale)
const t = (source: string, values?: Record<string, string | number>) => translate(locale.value, source, values)
const allocation = ref(60)
const allocationsByProfile = new Map<string, number>()
const portalInsurance = ref(customer.insurance)
type CompanyEmployee = { id?: string; name: string; plan: string; salary: string; status: string }
const companyEmployees = ref<CompanyEmployee[]>(customer.employments.map(item => ({ ...item })))
const portalRows = ref<{ transactions: { id: string; title: string; date: string; detail: string; status: string }[]; documents: { id: string; title: string; date: string; detail: string; status: string }[]; payments: { id: string; title: string; date: string; detail: string; status: string }[] }>({ transactions: [], documents: [], payments: [] })
const confirmed = ref(false)
const fundInsuranceId = ref('')
type CompanyContext = {
  companyId: string
  companyName: string
  companies: { id: string; name: string }[]
  employeeCount: number
  planCount: number
  plans: { id: string; name: string; monthlyPremium?: string }[]
}

const companyContext = ref<CompanyContext>({ companyId: '', companyName: '', companies: [], employeeCount: 0, planCount: 0, plans: [] })
type CompanyCase = CaseDetails & { id: string; [key: string]: unknown }
const portalCompanyCases = ref<CompanyCase[]>([])
const casesByCompany = ref<Record<string, CompanyCase[]>>({
  first: [
    { id: 'salary-mio', name: 'Löneändring · Mio Sten', status: 'Att granska', value: '18 sep.', detail: 'Mios månadslön har registrerats som 52 000 kr. Kontrollera uppgifterna och godkänn ändringen.' },
    { id: 'leave-nora', name: 'Tjänstledighet · Nora Holst', status: 'Pågående', value: '30 sep.', detail: 'Begäran om tjänstledighet från 1 oktober till 30 november väntar på handläggning.' },
    { id: 'enrolment-ivar', name: 'Nyanslutning · Ivar Holm', status: 'Komplett', value: '20 sep.', detail: 'Anslutningen är komplett och redo för nästa premieflöde.' },
  ],
  second: [
    { id: 'salary-alva', name: 'Löneändring · Alva Norberg', status: 'Att granska', value: '21 sep.', detail: 'Alvas månadslön har registrerats som 44 000 kr. Kontrollera uppgifterna och godkänn ändringen.' },
    { id: 'enrolment-hugo', name: 'Nyanslutning · Hugo Dahl', status: 'Pågående', value: '24 sep.', detail: 'Nya anslutningsuppgifter behöver kompletteras innan ärendet kan slutföras.' },
    { id: 'agreement-saga', name: 'Avtalstillägg · Saga Mark', status: 'Komplett', value: '14 sep.', detail: 'Avtalstillägget är komplett och klart för nästa steg.' },
  ],
})
const selectedCase = ref<CompanyCase | null>(null)
const selectedEmployee = ref<CompanyEmployee | null>(null)
const employeeAction = ref<'salary' | 'leave' | 'end'>('salary')
const salaryDraft = ref(0)
const leaveReason = ref('PARENTAL_LEAVE')
const leaveUntil = ref('2026-11-30')
const endDate = ref('2026-10-01')
const employeeActionError = ref('')
const employeeActionSaving = ref(false)
const search = ref('')
const actionMessage = ref('')
const loginError = ref('')
const sessionToken = ref('')
const useApi = import.meta.env.VITE_USE_API === 'true'
const apiUrl = import.meta.env.VITE_API_URL?.replace(/\/$/, '') ?? ''

const isCompany = computed(() => activePortal.value === 'COMPANY')
const useBottomPrivateOverviewMetrics = computed(() => !isCompany.value && customer.privateOverviewMetricLayout === 'bottom-bar')
const selectedProfile = computed(() => customer.profiles.find(item => item.id === selectedProfileId.value) ?? null)

const labels = computed(() => ({
  overview: t('Översikt'), insurance: t('Försäkringar'), events: t('Händelser'), documents: t('Dokument'),
  employees: t('Medarbetare'), plans: t('Avtal'), cases: t('Ärenden'), payments: t('Utbetalningar'), system: t('Systemadmin'),
  open: t('Öppna demo'), reset: t('Återställ demo'), logout: t('Byt profil'),
}))

const nav = computed(() => activePortal.value === 'SYSTEM'
  ? [['overview', labels.value.overview]]
  : isCompany.value
    ? [['overview', labels.value.overview], ['employees', labels.value.employees], ['plans', labels.value.plans], ['cases', labels.value.cases], ['documents', labels.value.documents]]
    : [['overview', labels.value.overview], ['insurance', labels.value.insurance], ['events', labels.value.events], ['documents', labels.value.documents], ['payments', labels.value.payments]])

const companyAdmins = computed<Record<string, string>[]>(() => customer.profiles
  .filter(item => item.role === 'COMPANY_ADMIN')
  .map(item => ({
    name: item.name,
    companies: (item.companies?.length ? item.companies : item.company ? [item.company] : []).join(', ') || t('Inga företag kopplade'),
  })))

const employees = computed(() => companyEmployees.value.filter(item => item.name.toLowerCase().includes(search.value.toLowerCase())))
const companyPlanOptions = computed(() => companyContext.value.plans.length > 0
  ? companyContext.value.plans
  : [...new Set(customer.employments.map(item => item.plan))].map(name => ({ id: name, name })))
const leaveReasons = computed(() => customer.rules.leaveTypes.map(reason => ({
  code: reason === 'Föräldraledighet' ? 'PARENTAL_LEAVE' : 'STUDIES',
  label: reason,
})))
const availableCompanies = computed(() => companyContext.value.companies.length > 0
  ? companyContext.value.companies
  : (profile.value?.companies ?? []).map(name => ({ id: name, name })))
const selectedCompanyName = computed(() => companyContext.value.companyName || profile.value?.company || '')
const isFirstCompany = computed(() => companyContext.value.companyId !== ''
  ? companyContext.value.companyId === availableCompanies.value[0]?.id
  : selectedCompanyName.value === availableCompanies.value[0]?.name)

const companyDocuments = computed(() => isFirstCompany.value
  ? [
      { id: 'invoice-september', title: 'Faktura september', date: '8 sep. 2026', detail: 'Faktura', status: t('Tillgängligt') },
      { id: 'plan-summary', title: 'Plansammanställning', date: '1 sep. 2026', detail: 'Sammanställning', status: t('Tillgängligt') },
    ]
  : [
      { id: 'invoice-august', title: 'Faktura augusti', date: '8 aug. 2026', detail: 'Faktura', status: t('Tillgängligt') },
      { id: 'membership-report', title: 'Anslutningsrapport', date: '29 aug. 2026', detail: 'Sammanställning', status: t('Tillgängligt') },
    ])

const activityRows = computed(() => {
  if (isCompany.value && page.value === 'documents') return companyDocuments.value
  if (!isCompany.value && portalRows.value[page.value as 'transactions' | 'documents' | 'payments']?.length) return portalRows.value[page.value as 'transactions' | 'documents' | 'payments']
  if (page.value === 'documents') return customer.documents.map(item => ({ id: item.name, title: item.name, date: item.date, detail: t(item.kind), status: t('Tillgängligt') }))
  return customer.transactions.map(item => ({ id: item.title, title: t(item.title), date: item.date, detail: item.amount, status: t(item.status) }))
})

const companyCases = computed(() => useApi ? portalCompanyCases.value : casesByCompany.value[isFirstCompany.value ? 'first' : 'second'])
const canManageCompany = computed(() => profile.value?.role === 'COMPANY_ADMIN' || profile.value?.role === 'SYSTEM_ADMIN')
const canApproveCases = canManageCompany

function openCase(item: CompanyCase) {
  selectedCase.value = item
}

function closeCase() {
  selectedCase.value = null
}

function manageEmployee(employee: CompanyEmployee) {
  selectedEmployee.value = employee
  salaryDraft.value = Number(employee.salary.replace(/[^0-9]/g, '')) || 0
  employeeAction.value = 'salary'
  employeeActionError.value = ''
}

async function saveEmployeeAction() {
  const employee = selectedEmployee.value
  if (!employee) return
  employeeActionError.value = ''
  employeeActionSaving.value = true

  try {
    if (useApi) {
      if (!employee.id || !companyContext.value.companyId) throw new Error(t('Åtgärden kunde inte sparas.'))
      const baseUrl = `${apiUrl}/api/company/employments/${employee.id}`
      const request = employeeAction.value === 'salary'
        ? { path: '/salary', body: { salary: salaryDraft.value } }
        : employeeAction.value === 'leave'
          ? { path: '/leave', body: { reason: leaveReason.value, until: leaveUntil.value } }
          : { path: '/end', body: { endsOn: endDate.value } }
      const response = await fetch(`${baseUrl}${request.path}?companyId=${companyContext.value.companyId}`, {
        method: 'PUT',
        headers: { Authorization: `Bearer ${sessionToken.value}`, 'Content-Type': 'application/json' },
        body: JSON.stringify(request.body),
      })
      if (!response.ok) throw new Error(t('Åtgärden kunde inte sparas.'))
      await loadCompanyEmployees()
    } else if (employeeAction.value === 'salary') {
      employee.salary = `${salaryDraft.value.toLocaleString('sv-SE')} ${t('kr/mån')}`
    } else if (employeeAction.value === 'leave') {
      employee.status = t('Tjänstledig')
    } else {
      employee.status = t('Avslutad')
    }

    actionMessage.value = employeeAction.value === 'salary'
      ? t('Löneändringen har sparats i demon.')
      : employeeAction.value === 'leave'
        ? t('Tjänstledigheten har registrerats i demon.')
        : t('Anställningen har avslutats i demon.')
    selectedEmployee.value = null
  } catch (error) {
    employeeActionError.value = error instanceof Error ? error.message : t('Åtgärden kunde inte sparas.')
  } finally {
    employeeActionSaving.value = false
  }
}

async function approveCase() {
  const item = selectedCase.value
  if (!item || !canApproveCases.value) return

  if (useApi) {
    if (!companyContext.value.companyId) return
    const response = await fetch(`${apiUrl}/api/company/cases/${item.id}/approve?companyId=${companyContext.value.companyId}`, {
      method: 'PUT',
      headers: { Authorization: `Bearer ${sessionToken.value}` },
    })
    if (!response.ok) {
      actionMessage.value = t('Ärendet kunde inte godkännas.')
      return
    }
    await loadCompanyCases()
  } else {
    item.status = 'Godkänd'
  }

  actionMessage.value = t('Ärendet har godkänts i demon.')
  selectedCase.value = null
}

const companyPlans = computed(() => companyContext.value.plans.length > 0
  ? companyContext.value.plans.map(plan => ({ name: plan.name, members: `${companyContext.value.employeeCount} anslutna`, premium: `${Number(plan.monthlyPremium).toLocaleString('sv-SE')} kr/mån` }))
  : isFirstCompany.value
    ? [{ name: 'Flexpension 2024', members: '17 anslutna', premium: '78 420 kr/mån' }, { name: 'ITP 1', members: '11 anslutna', premium: '54 870 kr/mån' }]
    : [{ name: 'Ledningspension', members: '3 anslutna', premium: '42 600 kr/mån' }, { name: 'Flexpension', members: '3 anslutna', premium: '18 900 kr/mån' }])

const overviewMetrics = computed(() => isCompany.value
  ? [
      { label: t('Försäkrade medarbetare'), value: String(companyContext.value.employeeCount || 28) },
      { label: t('Pågående ärenden'), value: String(companyCases.value.filter(item => !['Komplett', 'Godkänd'].includes(item.status)).length) },
      { label: t('Aktiva pensionsplaner'), value: String(companyContext.value.planCount || companyPlans.value.length) },
    ]
  : [
      { label: t('Totalt försäkringsvärde'), value: '2 126 900 kr' },
      { label: t('Nästa utbetalning'), value: '25 sep.' },
      { label: t('Aktiva försäkringar'), value: String(customer.insurance.length) },
    ])

const portalTitles = computed<Record<string, { title: string; description: string }>>(() => ({
  insurance: { title: t('Försäkringar'), description: t('Detaljer om sparande, skydd och val för varje försäkring.') },
  employees: { title: t('Medarbetare'), description: t('Sök och följ anställningar inom {company}.', { company: selectedCompanyName.value }) },
  plans: { title: t('Pensionsplaner'), description: t('Avtal, premieflöden och anslutna medarbetare.') },
}))

const activityTitles = computed(() => {
  if (page.value === 'documents') return { title: t('Dokument'), description: t('Tillgängliga dokument med lokal metadata.') }
  if (page.value === 'payments') return { title: t('Utbetalningar'), description: t('Kommande, pågående och avslutade utbetalningar.') }
  return { title: isCompany.value ? t('Ärenden') : t('Händelser'), description: t('Senaste händelser och uppdateringar.') }
})

const portals = computed(() => [
  { id: 'PRIVATE' as Portal, title: t('Individportalen'), description: t('Se pension, försäkringar, händelser och dokument.'), available: selectedProfile.value?.portals?.includes('PRIVATE') ?? selectedProfile.value?.portal === 'PRIVATE' },
  { id: 'COMPANY' as Portal, title: t('Företagsportalen'), description: t('Administrera medarbetare, avtal och ärenden.'), available: selectedProfile.value?.portals?.includes('COMPANY') ?? selectedProfile.value?.portal === 'COMPANY' },
  { id: 'SYSTEM' as Portal, title: t('Systemadmin'), description: t('Se företagsadministratörer och vilka företag de hanterar.'), available: selectedProfile.value?.portals?.includes('SYSTEM') ?? selectedProfile.value?.portal === 'SYSTEM' },
])

function roleLabel(item: Profile) {
  return t(item.role === 'SYSTEM_ADMIN' ? 'Systemadministratör' : item.role === 'COMPANY_VIEWER' ? 'Läsbehörighet' : item.role === 'COMPANY_ADMIN' ? 'Företagsadmin' : 'Privatkund')
}

function selectPage(nextPage: string) {
  page.value = nextPage as Page
}

async function login(selected: Profile) {
  loginError.value = ''
  if (useApi) {
    try {
      const response = await fetch(`${apiUrl}/api/auth/login`, { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ profileId: selected.id }) })
      if (!response.ok) throw new Error(t('Profilen kunde inte öppnas.'))
      const result = await response.json() as { token: string }
      sessionToken.value = result.token
    } catch (error) { loginError.value = error instanceof Error ? error.message : t('Profilen kunde inte öppnas.'); return }
  }
  const portal = selectedPortal.value
  if (!portal) return
  profile.value = selected; confirmed.value = false; actionMessage.value = ''
  await router.push({ name: routeNames[portal].overview })
  allocation.value = allocationsByProfile.get(selected.id) ?? 60
  if (useApi && portal === 'COMPANY') await loadCompanyContext()
  if (useApi && portal === 'PRIVATE') await loadPrivateContext()
}

async function logout() {
  if (useApi && sessionToken.value) await fetch(`${apiUrl}/api/auth/logout`, { method: 'POST', headers: { Authorization: `Bearer ${sessionToken.value}` } }).catch(() => undefined)
  profile.value = null
  sessionToken.value = ''
  selectedPortal.value = null
  selectedProfileId.value = ''
  loginError.value = ''
  await router.push({ name: 'login' })
}

async function loadPrivateRows() {
  const headers = { Authorization: `Bearer ${sessionToken.value}` }
  const [transactions, documents, payments] = await Promise.all([fetch(`${apiUrl}/api/private/transactions`, { headers }), fetch(`${apiUrl}/api/private/documents`, { headers }), fetch(`${apiUrl}/api/private/payments`, { headers })])
  if (transactions.ok) portalRows.value.transactions = (await transactions.json() as { insuranceId: string; bookedOn: string; description: string; amount: string; status: string }[]).map(item => ({ id: item.insuranceId + item.bookedOn, title: t(item.description), date: item.bookedOn, detail: `${item.amount} kr`, status: t(item.status) }))
  if (documents.ok) portalRows.value.documents = (await documents.json() as { title: string; type: string; publishedOn: string }[]).map(item => ({ id: item.title, title: item.title, date: item.publishedOn, detail: t(item.type), status: t('Tillgängligt') }))
  if (payments.ok) portalRows.value.payments = (await payments.json() as { paymentDate: string; grossAmount: string; status: string }[]).map(item => ({ id: item.paymentDate + item.status, title: t('Pensionsutbetalning'), date: item.paymentDate, detail: `${item.grossAmount} kr`, status: t(item.status) }))
}

async function confirmAllocation() {
  if (useApi && fundInsuranceId.value) {
    const response = await fetch(`${apiUrl}/api/private/insurances/${fundInsuranceId.value}/fund-allocation`, { method: 'PUT', headers: { Authorization: `Bearer ${sessionToken.value}`, 'Content-Type': 'application/json' }, body: JSON.stringify({ allocation: [{ fundName: 'Global Index', percent: allocation.value }, { fundName: 'Svenska Aktier', percent: 100 - allocation.value }] }) })
    if (!response.ok) { actionMessage.value = t('Fondfördelningen kunde inte sparas.'); return }
    const saved = await response.json() as { fundHoldings: { fundName: string; allocationPercent: string }[] }
    const globalIndex = saved.fundHoldings.find(item => item.fundName === 'Global Index')
    if (globalIndex) allocation.value = Number(globalIndex.allocationPercent)
  }
  if (profile.value) allocationsByProfile.set(profile.value.id, allocation.value)
  confirmed.value = true
}

async function loadPrivateContext() {
  const headers = { Authorization: `Bearer ${sessionToken.value}` }
  const response = await fetch(`${apiUrl}/api/private/overview`, { headers })
  if (!response.ok) return
  const result = await response.json() as { insurances: { id: string; productName: string; type: string; status: string; value: string }[] }
  fundInsuranceId.value = result.insurances.find(item => item.type === 'FUND')?.id ?? ''
  portalInsurance.value = result.insurances.map(item => ({ name: item.productName, type: item.type, status: item.status, value: `${Number(item.value).toLocaleString(locale.value === 'en' ? 'en-GB' : 'sv-SE')} kr`, detail: '' }))
  if (fundInsuranceId.value) {
    const insurance = await fetch(`${apiUrl}/api/private/insurances/${fundInsuranceId.value}`, { headers })
    if (insurance.ok) {
      const detail = await insurance.json() as { fundHoldings: { fundName: string; allocationPercent: string }[] }
      const globalIndex = detail.fundHoldings.find(item => item.fundName === 'Global Index')
      if (globalIndex) allocation.value = Number(globalIndex.allocationPercent)
    }
  }
  await loadPrivateRows()
}

async function loadCompanyContext(companyId?: string) {
  const headers = { Authorization: `Bearer ${sessionToken.value}` }
  const response = await fetch(`${apiUrl}/api/company/companies`, { headers })
  if (!response.ok) return
  const companies = await response.json() as { id: string; name: string }[]
  const company = companies.find(item => item.id === companyId)
    ?? companies.find(item => item.name === profile.value?.company)
    ?? companies[0]
  if (!company) return
  const [overview, plans] = await Promise.all([
    fetch(`${apiUrl}/api/company/overview?companyId=${company.id}`, { headers }),
    fetch(`${apiUrl}/api/company/plans?companyId=${company.id}`, { headers }),
  ])
  const companyOverview = overview.ok ? await overview.json() as { employees: number; plans: number } : { employees: 0, plans: 0 }
  companyContext.value = {
    companyId: company.id,
    companyName: company.name,
    companies,
    employeeCount: companyOverview.employees,
    planCount: companyOverview.plans,
    plans: plans.ok ? await plans.json() as { id: string; name: string; monthlyPremium: string }[] : [],
  }
  await Promise.all([loadCompanyEmployees(), loadCompanyCases()])
}

async function loadCompanyCases() {
  if (!companyContext.value.companyId) return
  const response = await fetch(`${apiUrl}/api/company/cases?companyId=${companyContext.value.companyId}`, { headers: { Authorization: `Bearer ${sessionToken.value}` } })
  if (!response.ok) return
  const result = await response.json() as { id: string; name: string; status: string; dueOn: string; detail: string }[]
  portalCompanyCases.value = result.map(item => ({ id: item.id, name: item.name, status: t(item.status), value: item.dueOn, detail: item.detail }))
}

function selectCompany(companyId: string) {
  search.value = ''
  if (useApi) {
    void loadCompanyContext(companyId)
    return
  }
  const company = availableCompanies.value.find(item => item.id === companyId)
  if (company) companyContext.value = { ...companyContext.value, companyId: company.id, companyName: company.name }
}

async function loadCompanyEmployees() {
  if (!companyContext.value.companyId) return
  const response = await fetch(`${apiUrl}/api/company/employments?companyId=${companyContext.value.companyId}`, { headers: { Authorization: `Bearer ${sessionToken.value}` } })
  if (!response.ok) return
  const result = await response.json() as { id: string; personName: string; planName: string; monthlySalary: string; status: string }[]
  companyEmployees.value = result.map(item => ({ id: item.id, name: item.personName, plan: item.planName, salary: `${Number(item.monthlySalary).toLocaleString(locale.value === 'en' ? 'en-GB' : 'sv-SE')} ${t('kr/mån')}`, status: t(item.status) }))
}

async function addEmployee(employee: EmployeeDraft): Promise<string | undefined> {
  if (useApi && companyContext.value.companyId) {
    const response = await fetch(`${apiUrl}/api/company/employees?companyId=${companyContext.value.companyId}`, { method: 'POST', headers: { Authorization: `Bearer ${sessionToken.value}`, 'Content-Type': 'application/json' }, body: JSON.stringify(employee) })
    if (!response.ok) return t('Medarbetaren kunde inte registreras.')
    await loadCompanyEmployees()
  } else {
    const plan = companyPlanOptions.value.find(item => item.id === employee.planId)
    companyEmployees.value.push({ name: employee.name, plan: plan?.name ?? '', salary: `${employee.salary.toLocaleString('sv-SE')} ${t('kr/mån')}`, status: t('Aktiv') })
  }
  search.value = ''
  page.value = 'employees'
  actionMessage.value = t('Medarbetaren är registrerad.')
  return undefined
}

watchEffect(() => {
  document.documentElement.lang = locale.value
  document.title = `${customer.name} — ${activePortal.value === 'SYSTEM' ? t('Systemadmin') : isCompany.value ? t('Företag') : t('Privat')}`
  const favicon = document.querySelector<HTMLLinkElement>('#customer-favicon')
  if (favicon) favicon.href = faviconUrl
})

watch([profile, () => route.fullPath], ([activeProfile]) => {
  if (!activeProfile && route.name !== 'login') {
    void router.replace({ name: 'login' })
    return
  }
  if (!activeProfile || route.name === 'login') return

  const portal = route.meta.portal
  const roles = route.meta.roles
  const allowedPortals = activeProfile.portals ?? [activeProfile.portal]
  if (!portal || !allowedPortals.includes(portal) || (roles && !roles.includes(activeProfile.role) && activeProfile.role !== 'SYSTEM_ADMIN')) {
    void router.replace({ name: routeNames[activePortal.value ?? 'PRIVATE'].overview })
  }
}, { immediate: true })
watch(selectedProfileId, () => { selectedPortal.value = null })
watch(page, nextPage => { if (useApi && activePortal.value === 'PRIVATE' && ['events', 'documents', 'payments'].includes(nextPage)) void loadPrivateRows() })
</script>

<template>
  <LoginScreen
    v-if="!profile"
    :customer-name="customer.name"
    :customer-icon-url="faviconUrl"
    :portals="portals"
    :selected-portal="selectedPortal"
    :profiles="customer.profiles.map(item => ({ ...item, description: t(item.description), preview: t(item.preview) }))"
    :selected-profile-id="selectedProfileId"
    :login-error="loginError"
    :role-label="roleLabel"
    :labels="{
      heading: t('Välj demo-person och portal'),
      intro: t('Utforska {customer} med fiktiv, återställbar demo-data. Ingen inloggning eller extern tjänst används.', { customer: customer.name }),
      switchHint: t('Du kan byta portal eller demo-person genom att logga ut.'),
      portalQuestion: t('Vilken portal vill du logga in i?'),
      portalLabel: t('Välj portal'),
      profileQuestion: t('Välj demo-person'),
      profileLabel: t('Demo-person'),
      chooseProfileFirst: t('Välj en demo-person för att se tillgängliga portaler.'),
      chooseProfile: t('Välj demo-person'),
      hint: t('Välj en demo-person och sedan en portal för att fortsätta.'),
      open: labels.open,
    }"
    @select-portal="selectedPortal = $event"
    @select-profile="selectedProfileId = $event"
    @submit="selectedProfile && selectedPortal && (selectedProfile.portals?.includes(selectedPortal) ?? selectedPortal === selectedProfile.portal) && login(selectedProfile)"
  />

  <div v-else class="app">
    <AppHeader
      :customer-name="customer.name"
      :customer-icon-url="faviconUrl"
      :portal-label="t(activePortal === 'SYSTEM' ? 'Systemadmin' : isCompany ? 'Företag' : 'Privat')"
      :company-label="t('Arbetsgivare')"
      :companies="isCompany ? availableCompanies : []"
      :selected-company-id="companyContext.companyId || selectedCompanyName"
      :signed-in-as-label="t('Inloggad som')"
      :profile-name="profile.name"
      :sign-out-label="t('Logga ut')"
      @sign-out="logout"
      @select-company="selectCompany"
    />

    <main id="mainContent" class="shell" tabindex="-1">
      <PortalNav :items="nav as [string, string][]" :active="page === 'add-employee' ? 'employees' : page" @select="selectPage" />

      <AddEmployeeFlow
        v-if="page === 'add-employee'"
        :plans="companyPlanOptions"
        :t="t"
        :on-submit="addEmployee"
        @cancel="page = 'employees'"
      />

      <SystemAdminView
        v-if="activePortal === 'SYSTEM'"
        :title="t('Systemadmin')"
        :description="t('Se företagsadministratörer och vilka företag de hanterar.')"
        :panel-title="t('Företagsadministratörer')"
        :panel-description="t('Här visas företagsadministratörer i den valda kundvariantens demo-data.')"
        :rows="companyAdmins"
        :t="t"
      />

      <OverviewView
        v-else-if="page === 'overview'"
        :is-company="isCompany"
        :title="isCompany ? selectedCompanyName : t('Hej {name}', { name: profile.name.split(' ')[0] })"
        :description="t(isCompany ? 'Följ planer, medarbetare och sådant som behöver hanteras.' : 'Din pension och dina försäkringar – samlade på ett ställe.')"
        :metrics="overviewMetrics"
        :bottom-metrics="useBottomPrivateOverviewMetrics"
        :rows="isCompany ? companyCases : portalInsurance"
        :next-step="{ description: t(isCompany ? 'Granska den registrerade löneändringen innan den 18 september.' : 'Se hur ditt innehav är fördelat och justera fondvikter.'), action: t(isCompany ? 'Visa ärenden' : 'Visa försäkringar') }"
        :t="t"
        :status-tone="statusTone"
        @navigate="page = $event"
        @open-case="openCase($event as CompanyCase)"
      />

      <InsuranceView v-else-if="page === 'insurance'" :title="portalTitles.insurance.title" :description="portalTitles.insurance.description" :insurance="portalInsurance" :allocation="allocation" :confirmed="confirmed" :max-funds="customer.rules.maxFunds" :t="t" :status-tone="statusTone" @update:allocation="allocation = $event" @confirm="confirmAllocation" />

      <section v-else-if="page === 'employees'">
        <PageHeader :title="portalTitles.employees.title" :description="portalTitles.employees.description">
          <template v-if="canManageCompany" #action>
            <button class="button" @click="page = 'add-employee'">{{ t('Lägg till medarbetare') }}</button>
          </template>
        </PageHeader>
        <Panel>
          <input v-model="search" class="search-input" :placeholder="t('Sök medarbetare')" :aria-label="t('Sök medarbetare')" />
          <EmptyState
            v-if="employees.length === 0"
            :title="t('Inga medarbetare matchar sökningen')"
            :description="t('Prova ett annat namn, eller rensa sökfältet för att se alla medarbetare.')"
          >
            <template #action>
              <button class="button secondary" @click="search = ''">{{ t('Rensa sökning') }}</button>
            </template>
          </EmptyState>
          <DataTable
            v-else
            :columns="[
              { key: 'name', label: t('Namn') },
              { key: 'plan', label: t('Plan') },
              { key: 'salary', label: t('Lön'), align: 'right' },
              { key: 'status', label: t('Status') },
              ...(canManageCompany ? [{ key: 'actions', label: t('Åtgärder'), align: 'right' as const }] : []),
            ]"
            :rows="employees"
          >
            <template #cell-name="{ row }"><strong>{{ (row as { name: string }).name }}</strong></template>
            <template #cell-plan="{ row }">{{ t((row as { plan: string }).plan) }}</template>
            <template #cell-status="{ row }">
              <StatusBadge :label="t((row as { status: string }).status)" :tone="statusTone((row as { status: string }).status)" />
            </template>
            <template #cell-actions="{ row }">
              <button class="table-link" type="button" @click="manageEmployee(row as CompanyEmployee)">{{ t('Hantera') }}</button>
            </template>
          </DataTable>
        </Panel>

        <Panel v-if="selectedEmployee && canManageCompany" class="employee-action-panel" :title="t('Hantera {name}', { name: selectedEmployee.name })">
          <p class="panel-lede">{{ t('Ändringar sparas endast i demon och gäller den valda anställningen.') }}</p>
          <form class="employee-action-form" @submit.prevent="saveEmployeeAction">
            <label class="field">
              <span>{{ t('Åtgärd') }}</span>
              <select v-model="employeeAction">
                <option value="salary">{{ t('Ändra lön') }}</option>
                <option value="leave">{{ t('Registrera tjänstledighet') }}</option>
                <option value="end">{{ t('Avsluta anställning') }}</option>
              </select>
            </label>

            <label v-if="employeeAction === 'salary'" class="field">
              <span>{{ t('Ny månadslön') }}</span>
              <input v-model.number="salaryDraft" min="1" required type="number" inputmode="numeric" />
            </label>

            <template v-else-if="employeeAction === 'leave'">
              <label class="field">
                <span>{{ t('Orsak') }}</span>
                <select v-model="leaveReason">
                  <option v-for="reason in leaveReasons" :key="reason.code" :value="reason.code">{{ t(reason.label) }}</option>
                </select>
              </label>
              <label class="field">
                <span>{{ t('Tjänstledig till och med') }}</span>
                <input v-model="leaveUntil" required type="date" />
              </label>
            </template>

            <label v-else class="field">
              <span>{{ t('Sista anställningsdag') }}</span>
              <input v-model="endDate" required type="date" />
            </label>

            <p v-if="employeeAction === 'end'" class="destructive-note">{{ t('Kontrollera datumet innan du avslutar anställningen.') }}</p>
            <p v-if="employeeActionError" class="form-error" role="alert">{{ employeeActionError }}</p>
            <div class="form-actions">
              <button class="button secondary" type="button" :disabled="employeeActionSaving" @click="selectedEmployee = null">{{ t('Avbryt') }}</button>
              <button class="button" :disabled="employeeActionSaving" type="submit">{{ employeeActionSaving ? t('Sparar…') : t('Spara ändring') }}</button>
            </div>
          </form>
        </Panel>
      </section>

      <PlansView v-else-if="page === 'plans'" :title="portalTitles.plans.title" :description="portalTitles.plans.description" :plans="companyPlans" :t="t" />

      <CasesView v-else-if="page === 'cases'" :title="activityTitles.title" :cases="companyCases" :t="t" :status-tone="statusTone" @open="openCase($event as CompanyCase)" />

      <ActivityView v-else :page="page as 'events' | 'documents' | 'payments'" :title="activityTitles.title" :description="activityTitles.description" :rows="activityRows" :t="t" :status-tone="statusTone" />

      <DemoNotice v-if="actionMessage">{{ actionMessage }}</DemoNotice>

      <CaseModal
        v-if="selectedCase"
        :item="{ ...selectedCase, name: t(selectedCase.name), status: t(selectedCase.status), detail: t(selectedCase.detail) }"
        :can-approve="canApproveCases"
        :labels="{
          eyebrow: t('Ärende'), close: t('Stäng'), status: t('Status'), due: t('Förfaller'), details: t('Detaljer'),
          approve: t('Godkänn ärende'), approved: t('Godkänd'), readOnly: t('Du har läsbehörighet och kan inte godkänna ärenden.'),
        }"
        @close="closeCase"
        @approve="approveCase"
      />

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

.grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: var(--space-metric-gap, 16px);
}

.split {
  display: grid;
  grid-template-columns: 1.3fr 0.7fr;
  gap: 24px;
  margin-top: 24px;
}

.cell-label {
  font-size: 0.83rem;
  color: var(--muted);
}

.table-link {
  display: inline;
  padding: 0;
  min-height: auto;
  border: 0;
  background: transparent;
  color: var(--primary);
  font-weight: 700;
  text-align: left;
  text-decoration: underline;
  text-decoration-thickness: 1px;
  text-underline-offset: 3px;
}

.table-link:hover {
  color: var(--primary-strong);
}

.panel-lede {
  color: var(--muted);
  line-height: 1.55;
  margin: 0 0 12px;
}

.panel-figure {
  font-family: var(--font-display, var(--font-sans));
  font-size: 1.4rem;
  color: var(--ink);
  font-variant-numeric: tabular-nums;
}

.form-row {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
  align-items: center;
}

.form-row label {
  display: flex;
  gap: 8px;
  align-items: center;
  color: var(--muted);
  font-size: 0.92rem;
}

.form-row input,
.search-input {
  border: 1px solid var(--border);
  border-radius: var(--radius-control);
  padding: 10px;
  background: var(--surface);
  color: var(--ink);
}

.form-row input {
  min-width: 90px;
}

.form-row input:read-only {
  background: var(--canvas);
  color: var(--muted);
}

.search-input {
  width: 100%;
  margin-bottom: 16px;
  max-width: 320px;
  display: block;
}

.employee-action-panel {
  margin-top: 24px;
}

.employee-action-form {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px;
}

.field {
  display: grid;
  gap: 6px;
  color: var(--muted);
  font-size: 0.92rem;
}

.field select,
.field input {
  width: 100%;
  border: 1px solid var(--border);
  border-radius: var(--radius-control);
  padding: 10px;
  background: var(--surface);
  color: var(--ink);
}

.destructive-note,
.form-error {
  grid-column: 1 / -1;
  margin: 0;
  padding: 12px 14px;
  border-radius: var(--radius-control);
  line-height: 1.5;
}

.destructive-note {
  color: var(--color-danger);
  background: var(--color-danger-surface);
  border: 1px solid var(--color-danger-border);
}

.form-error {
  color: var(--color-danger);
  background: var(--color-danger-surface);
  border: 1px solid var(--color-danger-border);
}

.form-actions {
  grid-column: 1 / -1;
  display: flex;
  gap: 12px;
  justify-content: flex-end;
}

.language-switch {
  margin-top: 32px;
  display: flex;
  gap: 8px;
  align-items: center;
}

.language-switch label {
  font-size: 0.85rem;
  color: var(--muted);
}

.language-switch select {
  border: 1px solid var(--border);
  border-radius: var(--radius-control);
  padding: 6px 10px;
  background: var(--surface);
  color: var(--ink);
}

@media (max-width: 760px) {
  .shell {
    padding: 24px 16px;
  }

  .grid,
  .split {
    grid-template-columns: 1fr;
  }

  .form-row,
  .employee-action-form {
    display: grid;
    grid-template-columns: 1fr;
  }

  .form-row label,
  .form-row input,
  .form-row .button,
  .search-input,
  .language-switch select {
    width: 100%;
    max-width: none;
  }

  .form-row label {
    justify-content: space-between;
  }

  .form-actions {
    display: grid;
    grid-template-columns: 1fr;
  }

  .language-switch {
    align-items: stretch;
    flex-direction: column;
  }
}
</style>
