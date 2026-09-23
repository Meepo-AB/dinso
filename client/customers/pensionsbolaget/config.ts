import { employees, type Customer } from '../../src/data/customer'

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
      description: 'Tjänstepension med flera pensionsdelar och skydd.',
      preview: '3 pensionsdelar · 5 fonder',
    },
    {
      id: 'pensionsbolaget-payment',
      name: 'Oscar Lind Aknar',
      role: 'PRIVATE_CUSTOMER',
      portal: 'PRIVATE',
      description: 'Försäkringar och kommande pensionsutbetalningar.',
      preview: '3 försäkringar · 2 utbetalningar',
    },
    {
      id: 'pensionsbolaget-admin',
      name: 'Maja Eklund',
      role: 'COMPANY_ADMIN',
      portal: 'COMPANY',
      description: 'Administrerar pensionsplaner och ärenden.',
      preview: 'Horisont Konsult AB · 34 medarbetare',
      company: 'Horisont Konsult AB',
    },
    {
      id: 'pensionsbolaget-multi',
      name: 'Norah Sjöberg',
      role: 'COMPANY_ADMIN',
      portal: 'COMPANY',
      description: 'Väljer mellan två arbetsgivare.',
      preview: '2 företag · 37 medarbetare',
      company: 'Horisont Gruppen AB',
      companies: ['Horisont Gruppen AB', 'Horisont Konsult AB'],
    },
    {
      id: 'pensionsbolaget-viewer',
      name: 'Linn Åström',
      role: 'COMPANY_VIEWER',
      portal: 'COMPANY',
      description: 'Ser pensionsdata med läsbehörighet.',
      preview: 'Horisont Konsult AB · Läsbehörighet',
      company: 'Horisont Konsult AB',
    },
    {
      id: 'pensionsbolaget-system-admin',
      name: 'Alex Lund',
      role: 'SYSTEM_ADMIN',
      portal: 'SYSTEM',
      description:
        'Systemadministratör med översikt över företagsadministratörer.',
      preview: '2 företagsadmin · 3 företag',
    },
  ],
  insurance: [
    {
      name: 'Tjänstepension 2018',
      type: 'Fondförsäkring',
      value: '1 943 200 kr',
      status: 'Aktiv',
      detail: '5 fonder · Återbetalningsskydd',
    },
    {
      name: 'Livsvarig pension',
      type: 'Utbetalning',
      value: '8 940 kr/mån',
      status: 'Pågående',
      detail: 'Nästa utbetalning 25 sep.',
    },
    {
      name: 'Familjeskydd',
      type: 'Riskförsäkring',
      value: '980 000 kr',
      status: 'Aktiv',
      detail: 'Gäller till 67 år',
    },
  ],
  transactions: [
    {
      date: '10 sep. 2026',
      title: 'Premie till Tjänstepension 2018',
      amount: '+6 240 kr',
      status: 'Bokförd',
    },
    {
      date: '25 aug. 2026',
      title: 'Pensionsutbetalning',
      amount: '+8 940 kr',
      status: 'Pågående',
    },
  ],
  documents: [
    { name: 'Årsbesked 2025', date: '12 jan. 2026', kind: 'Årsbesked' },
    { name: 'Utbetalningsplan', date: '4 aug. 2026', kind: 'Bekräftelse' },
  ],
  employments: employees('Tjänstepension 2018', 'Livslång pension'),
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
