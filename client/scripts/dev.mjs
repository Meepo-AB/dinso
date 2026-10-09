import { spawn } from 'node:child_process'

const customers = {
  svenskebanken: 'http://localhost:8081',
  pensionsbolaget: 'http://localhost:8082',
  finbanken: 'http://localhost:8083',
}

const customersFromArguments = process.argv.slice(2)
  .filter(argument => argument.startsWith('--') && argument.slice(2) in customers)
  .map(argument => argument.slice(2))
const customersFromNpmConfig = Object.keys(customers)
  .filter(customer => process.env[`npm_config_${customer}`] === 'true')
const selectedCustomers = [...new Set([...customersFromArguments, ...customersFromNpmConfig])]
if (selectedCustomers.length > 1) {
  throw new Error(`Choose one customer: ${Object.keys(customers).map(customer => `--${customer}`).join(', ')}`)
}

const customer = selectedCustomers[0] ?? 'svenskebanken'
const viteArguments = process.argv.slice(2).filter(argument => `--${customer}` !== argument)
const environment = {
  ...process.env,
  VITE_CUSTOMER_CONFIG: customer,
  VITE_USE_API: 'true',
  VITE_API_URL: customers[customer],
}

console.log(`Starting ${customer} with API ${customers[customer]}`)

const vite = spawn(process.platform === 'win32' ? 'vite.cmd' : 'vite', ['--mode', customer, ...viteArguments], {
  env: environment,
  stdio: 'inherit',
})

vite.on('exit', code => process.exit(code ?? 1))
vite.on('error', error => {
  console.error(`Could not start Vite: ${error.message}`)
  process.exit(1)
})
