export type Role =
  | 'PRIVATE_CUSTOMER'
  | 'COMPANY_ADMIN'
  | 'COMPANY_VIEWER'
  | 'SYSTEM_ADMIN'
export const roleLabels: Record<Role, string> = {
  PRIVATE_CUSTOMER: 'Privatkund',
  COMPANY_ADMIN: 'Företagsadmin - Redigeringsbehörighet',
  COMPANY_VIEWER: 'Företagsadmin - Läsbehörighet',
  SYSTEM_ADMIN: 'Systemadministratör',
}
export type Portal = 'PRIVATE' | 'COMPANY' | 'SYSTEM'
export type ProfilePortal = Portal
export interface Profile {
  id: string
  name: string
  role: Role
  portal: ProfilePortal
  portals?: Portal[]
}
export interface ProfileCompany {
  id: string
  name: string
  employees: number
}
export type PrivateOverviewMetricLayout = 'cards' | 'bottom-bar'

export interface Customer {
  key: string
  name: string
  locales: readonly string[]
  defaultLocale: string
  profiles: Profile[]
  rules: {
    maxFunds: number
    leaveMonths: number
    leaveTypes: string[]
    fees: boolean
    company: boolean
  }
  privateOverviewMetricLayout: PrivateOverviewMetricLayout
}
