<script setup lang="ts">
import DataTable from '../components/DataTable.vue'
import PageHeader from '../components/PageHeader.vue'
import Panel from '../components/Panel.vue'
import StatusBadge from '../components/StatusBadge.vue'
type Case = { id: string; name: string; status: string; value: string }
type StatusTone = 'positive' | 'pending' | 'attention' | 'neutral'
const props = defineProps<{ title: string; cases: Case[]; t: (source: string) => string; statusTone: (status: string) => StatusTone }>()
const emit = defineEmits<{ open: [item: Case] }>()
</script>
<template><section><PageHeader :title="title" :description="t('Klicka på ett ärende för att se detaljer och godkänna det.')" /><Panel><DataTable :columns="[{key:'name',label:t('Ärende')},{key:'status',label:t('Status')},{key:'value',label:t('Förfaller'),align:'right'}]" :rows="cases"><template #cell-name="{row}"><button class="table-link" type="button" @click="emit('open', row as Case)">{{ t((row as Case).name) }}</button></template><template #cell-status="{row}"><StatusBadge :label="t((row as Case).status)" :tone="statusTone((row as Case).status)" /></template></DataTable></Panel></section></template>
<style scoped>.table-link{display:inline;padding:0;min-height:auto;border:0;background:transparent;color:var(--primary);font-weight:700;text-align:left;text-decoration:underline;text-decoration-thickness:1px;text-underline-offset:3px}.table-link:hover{color:var(--primary-strong)}</style>
