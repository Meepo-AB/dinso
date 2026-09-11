import { defineComponent } from 'vue'
import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'

export type Page = 'overview' | 'insurance' | 'events' | 'documents' | 'payments' | 'employees' | 'plans' | 'cases' | 'add-employee'
export type PortalRoute = 'PRIVATE' | 'COMPANY' | 'SYSTEM'

declare module 'vue-router' {
  interface RouteMeta {
    page?: Page
    portal?: PortalRoute
    roles?: string[]
  }
}

const RouteAnchor = defineComponent({ render: () => null })

const routes: RouteRecordRaw[] = [
  { path: '/', name: 'login', component: RouteAnchor },
  { path: '/privat', name: 'private-overview', component: RouteAnchor, meta: { portal: 'PRIVATE', page: 'overview' } },
  { path: '/privat/forsakringar', name: 'private-insurance', component: RouteAnchor, meta: { portal: 'PRIVATE', page: 'insurance' } },
  { path: '/privat/handelser', name: 'private-events', component: RouteAnchor, meta: { portal: 'PRIVATE', page: 'events' } },
  { path: '/privat/dokument', name: 'private-documents', component: RouteAnchor, meta: { portal: 'PRIVATE', page: 'documents' } },
  { path: '/privat/utbetalningar', name: 'private-payments', component: RouteAnchor, meta: { portal: 'PRIVATE', page: 'payments' } },
  { path: '/foretag', name: 'company-overview', component: RouteAnchor, meta: { portal: 'COMPANY', page: 'overview' } },
  { path: '/foretag/medarbetare', name: 'company-employees', component: RouteAnchor, meta: { portal: 'COMPANY', page: 'employees' } },
  { path: '/foretag/medarbetare/lagg-till', name: 'company-add-employee', component: RouteAnchor, meta: { portal: 'COMPANY', page: 'add-employee', roles: ['COMPANY_ADMIN'] } },
  { path: '/foretag/avtal', name: 'company-plans', component: RouteAnchor, meta: { portal: 'COMPANY', page: 'plans' } },
  { path: '/foretag/arenden', name: 'company-cases', component: RouteAnchor, meta: { portal: 'COMPANY', page: 'cases' } },
  { path: '/foretag/dokument', name: 'company-documents', component: RouteAnchor, meta: { portal: 'COMPANY', page: 'documents' } },
  { path: '/systemadmin', name: 'system-admin-overview', component: RouteAnchor, meta: { portal: 'SYSTEM', page: 'overview', roles: ['SYSTEM_ADMIN'] } },
  { path: '/:pathMatch(.*)*', redirect: '/' },
]

export const router = createRouter({
  history: createWebHistory(),
  routes,
})
