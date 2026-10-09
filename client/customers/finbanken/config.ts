import { type Customer } from '../../src/data/customer'

const config: Customer = {
  key: 'finbanken',
  name: 'FinBanken',
  locales: ['en'],
  defaultLocale: 'en',
  profiles: [
    {
      id: 'finbanken-portfolio',
      name: 'Elin Berg',
      role: 'PRIVATE_CUSTOMER',
      portal: 'PRIVATE',
    },
    {
      id: 'finbanken-payment',
      name: 'Oscar Lind Aknar',
      role: 'PRIVATE_CUSTOMER',
      portal: 'PRIVATE',
    },
    {
      id: 'finbanken-system-admin',
      name: 'Alex Lund',
      role: 'SYSTEM_ADMIN',
      portal: 'SYSTEM',
    },
  ],
  rules: {
    maxFunds: 10,
    leaveMonths: 0,
    leaveTypes: [],
    fees: false,
    company: false,
  },
  privateOverviewMetricLayout: 'cards',
}
export default config
