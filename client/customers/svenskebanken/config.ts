import { type Customer } from '../../src/data/customer'

const config: Customer = {
  key: 'svenskebanken',
  name: 'SvenskeBanken',
  locales: ['sv', 'en'],
  defaultLocale: 'sv',
  profiles: [
    {
      id: 'svenskebanken-portfolio',
      name: 'Elin Berg',
      role: 'PRIVATE_CUSTOMER',
      portal: 'PRIVATE',
    },
    {
      id: 'svenskebanken-payment',
      name: 'Oscar Lind Aknar',
      role: 'PRIVATE_CUSTOMER',
      portal: 'PRIVATE',
    },
    {
      id: 'svenskebanken-admin',
      name: 'Maja Eklund',
      role: 'COMPANY_ADMIN',
      portal: 'COMPANY',
    },
    {
      id: 'svenskebanken-multi',
      name: 'Norah Sjöberg',
      role: 'COMPANY_ADMIN',
      portal: 'COMPANY',
    },
    {
      id: 'svenskebanken-viewer',
      name: 'Linn Åström',
      role: 'COMPANY_VIEWER',
      portal: 'COMPANY',
    },
    {
      id: 'svenskebanken-system-admin',
      name: 'Alex Lund',
      role: 'SYSTEM_ADMIN',
      portal: 'SYSTEM',
    },
  ],
  rules: {
    maxFunds: 10,
    leaveMonths: 18,
    leaveTypes: ['Föräldraledighet', 'Studier'],
    fees: true,
    company: true,
  },
  privateOverviewMetricLayout: 'cards',
}
export default config
