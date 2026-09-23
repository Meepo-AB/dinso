<script setup lang="ts">
import ActionCard from '../components/ActionCard.vue'
import { computed } from 'vue'
import DataTable, { type DataTableColumn } from '../components/DataTable.vue'
import MetricGrid from '../components/MetricGrid.vue'
import PageHeader from '../components/PageHeader.vue'
import Panel from '../components/Panel.vue'
import StatusBadge from '../components/StatusBadge.vue'

type StatusTone = 'positive' | 'pending' | 'attention' | 'neutral'
type Row = { name: string; status: string; value: string; detail: string }
const props = defineProps<{
  isCompany: boolean
  title: string
  description: string
  metrics: { label: string; value: string }[]
  bottomMetrics: boolean
  rows: Row[]
  nextStep: { description: string; action: string }
  t: (source: string) => string
  statusTone: (status: string) => StatusTone
}>()
const emit = defineEmits<{
  navigate: [page: 'insurance' | 'cases']
  openCase: [item: Row]
}>()

const columns = computed<DataTableColumn[]>(() => [
  {
    key: 'name',
    label: props.t(props.isCompany ? 'Ärende' : 'Försäkring'),
  },
  { key: 'status', label: props.t('Status') },
  {
    key: 'value',
    label: props.t(props.isCompany ? 'Förfaller' : 'Värde'),
    align: 'right',
  },
])
</script>

<template>
  <section>
    <PageHeader :title="title" :description="description" />
    <MetricGrid v-if="!bottomMetrics" :metrics="metrics" />
    <div class="split">
      <Panel :title="t(isCompany ? 'Att hantera' : 'Dina försäkringar')">
        <DataTable
          :columns="columns"
          :rows="rows"
        >
          <template #cell-name="{ row }">
            <button
              v-if="isCompany"
              class="table-link"
              type="button"
              @click="emit('openCase', row as Row)"
            >
              {{ (row as Row).name }}
            </button>
            <template v-else>
              <strong>{{ (row as Row).name }}</strong>
              <br />
              <span class="cell-label">{{ t((row as Row).detail) }}</span>
            </template>
          </template>
          <template #cell-status="{ row }">
            <StatusBadge
              :label="t((row as Row).status)"
              :tone="statusTone((row as Row).status)"
            />
          </template>
        </DataTable>
      </Panel>
      <ActionCard
        :title="t('Nästa steg')"
        :description="nextStep.description"
        :action-label="nextStep.action"
        @action="emit('navigate', isCompany ? 'cases' : 'insurance')"
      />
    </div>
    <MetricGrid v-if="bottomMetrics" :metrics="metrics" layout="bottom-bar" />
  </section>
</template>

<style scoped>
.split {
  display: grid;
  grid-template-columns: 1.3fr 0.7fr;
  gap: 24px;
  margin-top: 24px;
}
.cell-label {
  font-size: 0.83rem;
  color: var(--muted);
}
.table-link {
  display: inline;
  padding: 0;
  min-height: auto;
  border: 0;
  background: transparent;
  color: var(--primary);
  font-weight: 700;
  text-align: left;
  text-decoration: underline;
  text-decoration-thickness: 1px;
  text-underline-offset: 3px;
}
.table-link:hover {
  color: var(--primary-strong);
}
@media (max-width: 760px) {
  .split {
    grid-template-columns: 1fr;
  }
}
</style>
