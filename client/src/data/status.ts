export type StatusTone = 'positive' | 'pending' | 'attention' | 'neutral'

const positive = new Set([
  'Aktiv',
  'Active',
  'ACTIVE',
  'Komplett',
  'Complete',
  'Godkänd',
  'Approved',
  'Bokförd',
  'Booked',
  'BOOKED',
  'Genomförd',
  'Completed',
  'COMPLETED',
  'Tillgängligt',
  'Available',
  'AVAILABLE',
])
const attention = new Set([
  'Att granska',
  'Needs review',
  'Kommande',
  'Upcoming',
  'UPCOMING',
  'PENDING',
])

export function statusTone(status: string): StatusTone {
  if (positive.has(status)) return 'positive'
  if (attention.has(status)) return 'attention'
  if (
    status === 'Tjänstledig' ||
    status === 'On leave' ||
    status === 'LEAVE' ||
    status === 'ENDED'
  )
    return 'neutral'
  return 'pending'
}
