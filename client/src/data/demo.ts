export type Role =
  | 'PRIVATE_CUSTOMER'
  | 'COMPANY_ADMIN'
  | 'COMPANY_VIEWER'
  | 'SYSTEM_ADMIN'
export type Portal = 'PRIVATE' | 'COMPANY'
export type CustomerKey = 'svenskebanken' | 'pensionsbolaget' | 'finbanken'
export interface Profile {
  id: string
  name: string
  role: Role
  portal: Portal
  description: string
  preview: string
  company?: string
  companies?: string[]
}
export interface Customer {
  name: string
  locales: string[]
  defaultLocale: string
  profiles: Profile[]
  insurance: {
    name: string
    type: string
    value: string
    status: string
    detail: string
  }[]
  transactions: {
    date: string
    title: string
    amount: string
    status: string
  }[]
  documents: { name: string; date: string; kind: string }[]
  employments: { name: string; plan: string; salary: string; status: string }[]
  rules: {
    maxFunds: number
    leaveMonths: number
    leaveTypes: string[]
    fees: boolean
    company: boolean
  }
}

const privateProfiles = (prefix: string): Profile[] => [
  {
    id: `${prefix}-private-portfolio`,
    name: 'Elin Berg',
    role: 'PRIVATE_CUSTOMER',
    portal: 'PRIVATE',
    description:
      'Bred portfölj med fonder, traditionell pension och riskskydd.',
    preview: '3 försäkringar · 6 dokument',
  },
  {
    id: `${prefix}-private-payment`,
    name: 'Oscar Lind Aknar',
    role: 'PRIVATE_CUSTOMER',
    portal: 'PRIVATE',
    description: 'Försäkringar och kommande pensionsutbetalningar.',
    preview: '3 försäkringar · 2 utbetalningar',
  },
]
const companies = (prefix: string): Profile[] => [
  {
    id: `${prefix}-company-admin`,
    name: 'Maja Eklund',
    role: 'COMPANY_ADMIN',
    portal: 'COMPANY',
    description:
      'Administrerar en organisation med flera planer och aktiva ärenden.',
    preview: 'Nordljus Teknik AB · 28 medarbetare',
    company: 'Nordljus Teknik AB',
  },
  {
    id: `${prefix}-company-multi`,
    name: 'Norah Sjöberg',
    role: 'COMPANY_ADMIN',
    portal: 'COMPANY',
    description: 'Väljer mellan två bolag med olika stora bestånd.',
    preview: '2 företag · 34 medarbetare',
    company: 'Västhamn Gruppen AB',
    companies: ['Västhamn Gruppen AB', 'Nordljus Teknik AB'],
  },
  {
    id: `${prefix}-company-viewer`,
    name: 'Linn Åström',
    role: 'COMPANY_VIEWER',
    portal: 'COMPANY',
    description: 'Ser företagets data och ärenden med läsbehörighet.',
    preview: 'Nordljus Teknik AB · Läsbehörighet',
    company: 'Nordljus Teknik AB',
  },
]
const commonInsurance = [
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
]
const base = (
  name: string,
  prefix: string,
  rules: Customer['rules'],
): Customer => ({
  name,
  locales: ['sv', 'en'],
  defaultLocale: 'sv',
  profiles: [...privateProfiles(prefix), ...companies(prefix)],
  rules,
  insurance: commonInsurance,
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
    {
      date: '15 aug. 2026',
      title: 'Utbetalning pension',
      amount: '+12 640 kr',
      status: 'Pågående',
    },
  ],
  documents: [
    { name: 'Årsbesked 2025', date: '12 jan. 2026', kind: 'Årsbesked' },
    { name: 'Försäkringsvillkor Flex', date: '10 sep. 2025', kind: 'Villkor' },
    { name: 'Utbetalningsplan', date: '4 aug. 2026', kind: 'Bekräftelse' },
  ],
  employments: [
    'Alva Norberg',
    'Mio Sten',
    'Tilde Rask',
    'Hugo Dahl',
    'Nora Holst',
    'Ivar Holm',
    'Saga Mark',
    'Leo Nyberg',
  ].map((name, i) => ({
    name,
    plan: i % 2 ? 'ITP 1' : 'Flexpension',
    salary: `${38 + i * 3} 000 kr/mån`,
    status: ['Aktiv', 'Aktiv', 'Tjänstledig', 'Kommande'][i % 4],
  })),
})
export const customers: Record<CustomerKey, Customer> = {
  svenskebanken: base('SvenskeBanken', 'sb', {
    maxFunds: 10,
    leaveMonths: 18,
    leaveTypes: ['Föräldraledighet', 'Studier'],
    fees: true,
    company: true,
  }),
  pensionsbolaget: {
    ...base('PensionsBolaget', 'pb', {
      maxFunds: 5,
      leaveMonths: 12,
      leaveTypes: ['Föräldraledighet'],
      fees: false,
      company: true,
    }),
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
      ...commonInsurance.slice(2),
    ],
  },
  finbanken: {
    ...base('FinBanken', 'fb', {
      maxFunds: 10,
      leaveMonths: 0,
      leaveTypes: [],
      fees: false,
      company: false,
    }),
    locales: ['en'],
    defaultLocale: 'en',
    profiles: privateProfiles('fb'),
    insurance: [
      {
        name: 'Global Equity Select',
        type: 'Fund insurance',
        value: '1 506 800 kr',
        status: 'Active',
        detail: '8 funds · Return protection',
      },
      {
        name: 'Income protection',
        type: 'Risk insurance',
        value: '850 000 kr',
        status: 'Active',
        detail: 'Coverage until age 67',
      },
    ],
  },
}
