<script setup lang="ts">
export interface DataTableColumn {
  key: string
  label: string
  align?: 'left' | 'right'
}

defineProps<{ columns: DataTableColumn[]; rows: Record<string, unknown>[]; caption?: string }>()
</script>

<template>
  <div class="data-table-wrap">
    <table class="data-table">
      <caption v-if="caption" class="sr-only">{{ caption }}</caption>
      <thead>
        <tr>
          <th v-for="column in columns" :key="column.key" scope="col" :class="{ 'is-right': column.align === 'right' }">
            {{ column.label }}
          </th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="(row, index) in rows" :key="index">
          <td v-for="column in columns" :key="column.key" :data-label="column.label" :class="{ 'is-right': column.align === 'right' }">
            <slot :name="`cell-${column.key}`" :row="row" :value="row[column.key]">
              {{ row[column.key] }}
            </slot>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<style scoped>
.data-table-wrap {
  overflow-x: auto;
}

.data-table {
  width: 100%;
  border-collapse: collapse;
  min-width: 560px;
}

.data-table th {
  text-align: left;
  font-size: 0.78rem;
  font-weight: 600;
  color: var(--muted);
  letter-spacing: var(--label-tracking, 0);
  text-transform: var(--label-case, none);
  padding: 0 16px 12px 0;
  border-bottom: var(--table-head-border, 1px solid var(--border));
}

.data-table th.is-right,
.data-table td.is-right {
  text-align: right;
}

.data-table td {
  padding: var(--space-row-padding, 16px) 16px 16px 0;
  border-top: 1px solid var(--border);
  font-variant-numeric: tabular-nums;
  vertical-align: middle;
}

.data-table tbody tr:hover {
  background: var(--row-hover, transparent);
}

.sr-only {
  position: absolute;
  width: 1px;
  height: 1px;
  overflow: hidden;
  clip: rect(0 0 0 0);
}

@media (max-width: 640px) {
  .data-table-wrap {
    overflow: visible;
  }

  .data-table,
  .data-table tbody,
  .data-table tr,
  .data-table td {
    display: block;
    width: 100%;
  }

  .data-table {
    min-width: 0;
  }

  .data-table thead {
    position: absolute;
    width: 1px;
    height: 1px;
    overflow: hidden;
    clip: rect(0 0 0 0);
  }

  .data-table tbody {
    display: grid;
    gap: 16px;
  }

  .data-table tr {
    padding: 4px 0;
    border-top: 1px solid var(--border);
  }

  .data-table td {
    display: grid;
    grid-template-columns: minmax(6.5rem, 42%) 1fr;
    gap: 12px;
    padding: 10px 0;
    border: 0;
    text-align: left;
  }

  .data-table td::before {
    content: attr(data-label);
    color: var(--muted);
    font-size: 0.78rem;
    font-weight: 600;
  }

  .data-table td.is-right {
    text-align: left;
  }
}
</style>
