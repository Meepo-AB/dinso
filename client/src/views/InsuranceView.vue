<script setup lang="ts">
import { computed } from 'vue'
import DataTable, { type DataTableColumn } from '../components/DataTable.vue'
import DemoNotice from '../components/DemoNotice.vue'
import PageHeader from '../components/PageHeader.vue'
import Panel from '../components/Panel.vue'
import StatusBadge from '../components/StatusBadge.vue'

type Insurance = {
  name: string
  type: string
  status: string
  value: string
  detail: string
}
type StatusTone = 'positive' | 'pending' | 'attention' | 'neutral'
const props = defineProps<{
  title: string
  description: string
  insurance: Insurance[]
  allocation: number
  confirmed: boolean
  error: string
  maxFunds: number
  t: (source: string, values?: Record<string, string | number>) => string
  statusTone: (status: string) => StatusTone
}>()
const emit = defineEmits<{
  'update:allocation': [value: number]
  confirm: []
}>()

const columns = computed<DataTableColumn[]>(() => [
  { key: 'name', label: props.t('Försäkring') },
  { key: 'type', label: props.t('Typ') },
  { key: 'status', label: props.t('Status') },
  { key: 'value', label: props.t('Värde'), align: 'right' },
])
const remainingAllocation = computed(() => 100 - props.allocation)

function updateAllocation(event: Event): void {
  emit('update:allocation', Number((event.target as HTMLInputElement).value))
}
</script>

<template>
  <section>
    <PageHeader :title="title" :description="description" />
    <Panel>
      <DataTable
        :columns="columns"
        :rows="insurance"
      >
        <template #cell-name="{ row }">
          <strong>{{ (row as Insurance).name }}</strong>
          <br />
          <span class="cell-label">{{ t((row as Insurance).detail) }}</span>
        </template>
        <template #cell-type="{ row }">
          {{ t((row as Insurance).type) }}
        </template>
        <template #cell-status="{ row }">
          <StatusBadge
            :label="t((row as Insurance).status)"
            :tone="statusTone((row as Insurance).status)"
          />
        </template>
      </DataTable>
    </Panel>
    <Panel :title="t('Ändra fondfördelning')" class="allocation-panel">
      <p class="panel-lede">
        {{ t('Fördelningen gäller din fondförsäkring') }}
      </p>
      <div class="form-row">
        <label>
          {{ t('Global index') }}
          <input
            :value="allocation"
            type="number"
            min="0"
            max="100"
            :aria-label="t('Andel global index')"
            @input="updateAllocation"
          />
          {{ t('%') }}
        </label>
        <label>
          {{ t('Svenska aktier') }}
          <input
            :value="remainingAllocation"
            type="number"
            readonly
            :aria-label="t('Andel svenska aktier')"
          />
          {{ t('%') }}
        </label>
        <button class="button" @click="emit('confirm')">
          {{ t('Bekräfta fördelning') }}
        </button>
      </div>
      <DemoNotice v-if="confirmed">
        {{
          t(
            'Fördelningen har sparats. Ingen signering eller extern överföring genomförs.',
          )
        }}
      </DemoNotice>
      <p v-else-if="error" class="form-error" role="alert">{{ error }}</p>
    </Panel>
  </section>
</template>

<style scoped>
.cell-label,
.panel-lede {
  color: var(--muted);
}
.cell-label {
  font-size: 0.83rem;
}
.allocation-panel {
  margin-top: 24px;
}
.panel-lede {
  line-height: 1.55;
  margin: 0 0 12px;
}
.form-row {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
  align-items: center;
}
.form-row label {
  display: flex;
  gap: 8px;
  align-items: center;
  color: var(--muted);
  font-size: 0.92rem;
}
.form-row input {
  min-width: 90px;
  border: 1px solid var(--border);
  border-radius: var(--radius-control);
  padding: 10px;
  background: var(--surface);
  color: var(--ink);
}
.form-row input:read-only {
  background: var(--canvas);
  color: var(--muted);
}
.form-error {
  color: var(--color-danger, #b42318);
  margin: 12px 0 0;
}
@media (max-width: 760px) {
  .form-row {
    display: grid;
    grid-template-columns: 1fr;
  }
  .form-row label,
  .form-row input,
  .form-row .button {
    width: 100%;
  }
  .form-row label {
    justify-content: space-between;
  }
}
</style>
