import { employees, type Customer } from '../../src/data/customer'

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
      description:
        'Bred portfölj med fonder, traditionell pension och riskskydd.',
      preview: '3 försäkringar · dokument och transaktioner',
    },
    {
      id: 'svenskebanken-payment',
      name: 'Oscar Lind Aknar',
      role: 'PRIVATE_CUSTOMER',
      portal: 'PRIVATE',
      description: 'Försäkringar och kommande pensionsutbetalningar.',
      preview: '3 försäkringar · 2 utbetalningar',
    },
    {
      id: 'svenskebanken-admin',
      name: 'Maja Eklund',
      role: 'COMPANY_ADMIN',
      portal: 'COMPANY',
      description: 'Administrerar flera planer och aktiva ärenden.',
      preview: 'Nordljus Teknik AB · 28 medarbetare',
      company: 'Nordljus Teknik AB',
    },
    {
      id: 'svenskebanken-multi',
      name: 'Norah Sjöberg',
      role: 'COMPANY_ADMIN',
      portal: 'COMPANY',
      description: 'Visar företagsval med olika stora bestånd.',
      preview: '2 företag · 34 medarbetare',
      company: 'Västhamn Gruppen AB',
      companies: ['Västhamn Gruppen AB', 'Nordljus Teknik AB'],
    },
    {
      id: 'svenskebanken-viewer',
      name: 'Linn Åström',
      role: 'COMPANY_VIEWER',
      portal: 'COMPANY',
      description: 'Ser företagets data med läsbehörighet.',
      preview: 'Nordljus Teknik AB · Läsbehörighet',
      company: 'Nordljus Teknik AB',
    },
    {
      id: 'svenskebanken-system-admin',
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
      name: 'Tjänstepension Flex',
      type: 'Fondförsäkring',
      value: '1 284 600 kr',
      status: 'Aktiv',
      detail: '6 fonder · Återbetalningsskydd',
    },
    {
      name: 'Trygg Traditionell',
      type: 'Traditionell försäkring',
      value: '842 300 kr',
      status: 'Aktiv',
      detail: 'Garanterad ränta · Efterlevandeskydd',
    },
    {
      name: 'Sjuk- och livsskydd',
      type: 'Riskförsäkring',
      value: '1 250 000 kr',
      status: 'Aktiv',
      detail: 'Premiebefrielse · Gäller till 67 år',
    },
  ],
  transactions: [
    {
      date: '10 sep. 2026',
      title: 'Premie från Nordljus Teknik AB',
      amount: '+4 850 kr',
      status: 'Bokförd',
    },
    {
      date: '3 sep. 2026',
      title: 'Fondbyte Global Index',
      amount: '0 kr',
      status: 'Genomförd',
    },
    {
      date: '28 aug. 2026',
      title: 'Försäkringsavgift',
      amount: '−89 kr',
      status: 'Bokförd',
    },
  ],
  documents: [
    { name: 'Årsbesked 2025', date: '12 jan. 2026', kind: 'Årsbesked' },
    { name: 'Försäkringsvillkor Flex', date: '10 sep. 2025', kind: 'Villkor' },
  ],
  employments: employees('Flexpension 2024', 'ITP 1'),
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
