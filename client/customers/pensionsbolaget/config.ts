import { type Customer } from '../../src/data/customer'

const config: Customer = {
  key: 'pensionsbolaget',
  name: 'PensionsBolaget',
  locales: ['sv', 'en'],
  defaultLocale: 'sv',
  profiles: [
    {
      id: 'pensionsbolaget-portfolio',
      name: 'Elin Berg',
      role: 'PRIVATE_CUSTOMER',
      portal: 'PRIVATE',
    },
    {
      id: 'pensionsbolaget-payment',
      name: 'Oscar Lind Aknar',
      role: 'PRIVATE_CUSTOMER',
      portal: 'PRIVATE',
    },
    {
      id: 'pensionsbolaget-admin',
      name: 'Maja Eklund',
      role: 'COMPANY_ADMIN',
      portal: 'COMPANY',
    },
    {
      id: 'pensionsbolaget-multi',
      name: 'Norah Sjöberg',
      role: 'COMPANY_ADMIN',
      portal: 'COMPANY',
    },
    {
      id: 'pensionsbolaget-viewer',
      name: 'Linn Åström',
      role: 'COMPANY_VIEWER',
      portal: 'COMPANY',
    },
    {
      id: 'pensionsbolaget-system-admin',
      name: 'Alex Lund',
      role: 'SYSTEM_ADMIN',
      portal: 'SYSTEM',
    },
  ],
  rules: {
    maxFunds: 5,
    leaveMonths: 12,
    leaveTypes: ['Föräldraledighet'],
    fees: false,
    company: true,
  },
  privateOverviewMetricLayout: 'bottom-bar',
}
export default config
