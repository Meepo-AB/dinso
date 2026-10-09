import { existsSync } from 'node:fs'
import { resolve } from 'node:path'
import { defineConfig, loadEnv } from 'vite'
import vue from '@vitejs/plugin-vue'

const customerPorts = {
  svenskebanken: 5173,
  pensionsbolaget: 5174,
  finbanken: 5175,
} as const
const customerKeys = Object.keys(customerPorts) as Array<
  keyof typeof customerPorts
>

export default defineConfig(({ mode }) => {
  const customer =
    loadEnv(mode, process.cwd(), '').VITE_CUSTOMER_CONFIG ||
    mode ||
    'svenskebanken'
  if (!customerKeys.includes(customer as (typeof customerKeys)[number]))
    throw new Error(`Unknown Dinso customer: ${customer}`)
  const overlay = resolve(import.meta.dirname, 'customers', customer)
  for (const file of ['config.ts', 'theme.css'])
    if (!existsSync(resolve(overlay, file)))
      throw new Error(`${customer} is missing required overlay file ${file}`)
  return {
    plugins: [vue()],
    resolve: { alias: { '@customer': overlay } },
    define: { __CUSTOMER__: JSON.stringify(customer) },
    server: {
      port: customerPorts[customer as keyof typeof customerPorts],
      strictPort: true,
    },
    build: { outDir: `dist/${customer}`, emptyOutDir: true },
  }
})
