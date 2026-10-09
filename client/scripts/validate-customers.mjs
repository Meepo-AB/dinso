import { existsSync, readdirSync, readFileSync } from 'node:fs'
import { resolve } from 'node:path'

const root = resolve(import.meta.dirname, '..', 'customers')
const required = ['svenskebanken', 'pensionsbolaget', 'finbanken']
for (const customer of required) {
  const directory = resolve(root, customer)
  for (const file of ['config.ts', 'theme.css']) if (!existsSync(resolve(directory, file))) throw new Error(`${customer} is missing ${file}`)
  const locales = readdirSync(resolve(directory, 'locales')).filter(file => file.endsWith('.json')).sort()
  if (customer === 'finbanken' ? locales.join() !== 'en.json' : locales.join() !== 'en.json,sv.json') throw new Error(`${customer} has an invalid locale overlay`)
  const config = readFileSync(resolve(directory, 'config.ts'), 'utf8')
  if (!config.includes(`key: '${customer}'`)) throw new Error(`${customer} config key does not match its directory`)
  if (customer === 'finbanken' && (config.includes("portal: 'COMPANY'") || config.includes("locales: ['sv'"))) throw new Error('FinBanken must not expose company profiles or Swedish')
}
console.log('Customer overlay validation passed.')
