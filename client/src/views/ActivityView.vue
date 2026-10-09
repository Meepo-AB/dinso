<script setup lang="ts">
import { computed } from 'vue'
import DataTable, { type DataTableColumn } from '../components/DataTable.vue'
import EmptyState from '../components/EmptyState.vue'
import PageHeader from '../components/PageHeader.vue'
import Panel from '../components/Panel.vue'
import StatusBadge from '../components/StatusBadge.vue'
type Row = { title: string; date: string; detail: string; status: string }
type StatusTone = 'positive' | 'pending' | 'attention' | 'neutral'

const props = defineProps<{
  page: 'events' | 'documents' | 'payments'
  title: string
  description: string
  rows: Row[]
  t: (source: string) => string
  statusTone: (status: string) => StatusTone
}>()

const columns = computed<DataTableColumn[]>(() => [
  {
    key: 'title',
    label:
      props.page === 'documents'
        ? props.t('Dokument')
        : props.page === 'payments'
          ? props.t('Utbetalning')
          : props.t('Händelse'),
  },
  { key: 'date', label: props.t('Datum') },
  {
    key: 'detail',
    label: props.page === 'documents' ? props.t('Typ') : props.t('Belopp'),
    align: 'right',
  },
  { key: 'status', label: props.t('Status') },
])
</script>
<template>
  <section>
    <PageHeader :title="title" :description="description" />
    <Panel>
      <EmptyState
        v-if="rows.length === 0"
        :title="t('Inget att visa ännu')"
        :description="t('När det finns händelser här visas de här.')"
      />
      <DataTable
        v-else
        :columns="columns"
        :rows="rows"
      >
        <template #cell-title="{ row }">
          <strong>{{ (row as Row).title }}</strong>
        </template>
        <template #cell-status="{ row }">
          <StatusBadge
            :label="t((row as Row).status)"
            :tone="statusTone((row as Row).status)"
          />
        </template>
      </DataTable>
    </Panel>
  </section>
</template>
