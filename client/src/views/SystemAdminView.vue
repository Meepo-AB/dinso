<script setup lang="ts">
import { computed } from 'vue'
import DataTable from '../components/DataTable.vue'
import EmptyState from '../components/EmptyState.vue'
import PageHeader from '../components/PageHeader.vue'
import Panel from '../components/Panel.vue'

export interface ActionOption { code: string; label: string }
export interface PermissionProfile { id: string; name: string; roleLabel: string; actions: string[]; canUseCompanyPortal: boolean }

const props = defineProps<{
  title: string
  description: string
  panelTitle: string
  panelDescription: string
  rows: Record<string, string>[]
  permissionsTitle: string
  permissionsDescription: string
  actionOptions: ActionOption[]
  profiles: PermissionProfile[]
  savingId: string | null
  t: (source: string) => string
}>()

const emit = defineEmits<{ 'toggle-action': [profileId: string, action: string, granted: boolean] }>()

const permissionColumns = computed(() => [
  { key: 'name', label: props.t('Demoprofil') },
  ...props.actionOptions.map(option => ({ key: option.code, label: option.label })),
])

const permissionRows = computed(() => props.profiles.map(profile => ({
  id: profile.id,
  name: profile.name,
  role: profile.roleLabel,
  canUseCompanyPortal: profile.canUseCompanyPortal,
  ...Object.fromEntries(props.actionOptions.map(option => [option.code, profile.actions.includes(option.code)])),
})))

function toggle(row: Record<string, unknown>, code: string, event: Event) {
  const granted = (event.target as HTMLInputElement).checked
  emit('toggle-action', row.id as string, code, granted)
}
</script>

<template>
  <section>
    <PageHeader :title="title" :description="description" />

    <Panel :title="panelTitle">
      <p class="panel-lede">{{ panelDescription }}</p>
      <EmptyState
        v-if="rows.length === 0"
        :title="t('Inga företagsadministratörer')"
        :description="t('Det finns inga företagsadministratörer i den här kundvarianten.')"
      />
      <DataTable
        v-else
        :caption="panelTitle"
        :columns="[
          { key: 'name', label: t('Företagsadministratör') },
          { key: 'companies', label: t('Företag') },
        ]"
        :rows="rows"
      >
        <template #cell-name="{ row }"><strong>{{ row.name }}</strong></template>
      </DataTable>
    </Panel>

    <Panel class="permissions-panel" :title="permissionsTitle">
      <p class="panel-lede">{{ permissionsDescription }}</p>
      <EmptyState
        v-if="profiles.length === 0"
        :title="t('Inga demoprofiler')"
        :description="t('Det finns inga demoprofiler att administrera i den här kundvarianten.')"
      />
      <DataTable
        v-else
        :caption="permissionsTitle"
        :columns="permissionColumns"
        :rows="permissionRows"
      >
        <template #cell-name="{ row }">
          <strong>{{ row.name }}</strong>
          <span class="role-hint">{{ row.role }}</span>
          <span v-if="!row.canUseCompanyPortal" class="role-hint">{{ t('Har inte tillgång till företagsportalen') }}</span>
        </template>
        <template v-for="option in actionOptions" :key="option.code" v-slot:[`cell-${option.code}`]="{ row }">
          <label
            class="permission-checkbox"
            :title="row.canUseCompanyPortal ? undefined : t('Denna demoprofil saknar tillgång till företagsportalen och kan inte tilldelas åtgärder där.')"
          >
            <input
              type="checkbox"
              :checked="row[option.code] as boolean"
              :disabled="savingId === row.id || !row.canUseCompanyPortal"
              :aria-label="`${option.label} — ${row.name}`"
              @change="toggle(row, option.code, $event)"
            />
          </label>
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

.permissions-panel {
  margin-top: 24px;
}

.role-hint {
  display: block;
  margin-top: 2px;
  font-size: 0.78rem;
  color: var(--muted);
}

.permission-checkbox {
  display: flex;
  justify-content: center;
}

.permission-checkbox input {
  width: 18px;
  height: 18px;
  accent-color: var(--primary);
}

.permission-checkbox input:disabled {
  opacity: 0.35;
  cursor: not-allowed;
}
</style>
