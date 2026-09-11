import { type Customer } from '../../src/data/customer'

const config: Customer = {
  key: 'finbanken', name: 'FinBanken', locales: ['en'], defaultLocale: 'en',
  profiles: [{ id: 'finbanken-portfolio', name: 'Elin Berg', role: 'PRIVATE_CUSTOMER', portal: 'PRIVATE', description: 'Fund-focused portfolio with investment and risk coverage.', preview: '2 insurance policies · documents and transactions' }, { id: 'finbanken-payment', name: 'Oscar Lind Aknar', role: 'PRIVATE_CUSTOMER', portal: 'PRIVATE', description: 'Insurance and upcoming pension payments.', preview: '2 insurance policies · 2 payments' }, { id: 'finbanken-system-admin', name: 'Alex Lund', role: 'SYSTEM_ADMIN', portal: 'SYSTEM', description: 'System administrator with an overview of company administrators.', preview: 'No company administrators' }],
  insurance: [{ name: 'Global Equity Select', type: 'Fund insurance', value: '1 506 800 kr', status: 'Active', detail: '8 funds · Return protection' }, { name: 'Income protection', type: 'Risk insurance', value: '850 000 kr', status: 'Active', detail: 'Coverage until age 67' }],
  transactions: [{ date: '10 Sep 2026', title: 'Employer contribution', amount: '+4 850 kr', status: 'Booked' }, { date: '3 Sep 2026', title: 'Global Index fund switch', amount: '0 kr', status: 'Completed' }], documents: [{ name: 'Annual statement 2025', date: '12 Jan 2026', kind: 'Annual statement' }, { name: 'Investment guide', date: '10 Sep 2025', kind: 'Guide' }], employments: [], rules: { maxFunds: 10, leaveMonths: 0, leaveTypes: [], fees: false, company: false }, privateOverviewMetricLayout: 'cards'
}
export default config
