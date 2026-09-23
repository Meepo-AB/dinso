<script setup lang="ts">
import { computed } from 'vue'
import DataTable, { type DataTableColumn } from '../components/DataTable.vue'
import EmptyState from '../components/EmptyState.vue'
import PageHeader from '../components/PageHeader.vue'
import Panel from '../components/Panel.vue'

const props = defineProps<{
  title: string
  description: string
  panelTitle: string
  panelDescription: string
  rows: Record<string, string>[]
  t: (source: string) => string
}>()

const columns = computed<DataTableColumn[]>(() => [
  { key: 'name', label: props.t('Företagsadministratör') },
  { key: 'companies', label: props.t('Företag') },
])
</script>

<template>
  <section>
    <PageHeader :title="title" :description="description" />

    <Panel :title="panelTitle">
      <p class="panel-lede">{{ panelDescription }}</p>
      <EmptyState
        v-if="rows.length === 0"
        :title="t('Inga företagsadministratörer')"
        :description="
          t('Det finns inga företagsadministratörer i den här kundvarianten.')
        "
      />
      <DataTable
        v-else
        :caption="panelTitle"
        :columns="columns"
        :rows="rows"
      >
        <template #cell-name="{ row }">
          <strong>{{ row.name }}</strong>
        </template>
      </DataTable>
    </Panel>
  </section>
</template>

<style scoped>
.panel-lede {
  max-width: 64ch;
  margin: 0 0 24px;
  color: var(--muted);
  line-height: 1.55;
}
</style>
