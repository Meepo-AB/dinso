<script setup lang="ts">
import { computed, ref } from 'vue'

export interface EmployeeDraft {
  name: string
  planId: string
  salary: number
  startsOn: string
}

const props = defineProps<{
  plans: { id: string; name: string }[]
  t: (source: string) => string
  onSubmit: (employee: EmployeeDraft) => Promise<string | undefined>
}>()

const emit = defineEmits<{ cancel: [] }>()

const step = ref(1)
const submitError = ref('')
const isSubmitting = ref(false)
const draft = ref<EmployeeDraft>({
  name: '',
  planId: props.plans[0]?.id ?? '',
  salary: 42000,
  startsOn: '2026-10-01',
})

const selectedPlan = computed(() => props.plans.find(plan => plan.id === draft.value.planId)?.name ?? '')
const canContinue = computed(() => step.value === 1
  ? draft.value.name.trim().length > 1 && draft.value.salary > 0 && Boolean(draft.value.startsOn)
  : Boolean(draft.value.planId))

function next() {
  if (!canContinue.value) return
  step.value += 1
}

async function submit() {
  submitError.value = ''
  isSubmitting.value = true
  const error = await props.onSubmit({ ...draft.value, name: draft.value.name.trim() })
  isSubmitting.value = false
  if (error) submitError.value = error
}
</script>

<template>
  <section class="employee-flow" aria-labelledby="add-employee-title">
    <div class="employee-flow__topbar">
      <button class="back-link" type="button" @click="emit('cancel')">{{ t('Tillbaka till medarbetare') }}</button>
      <p>{{ t('Ny medarbetare') }}</p>
    </div>

    <header class="employee-flow__header">
      <h1 id="add-employee-title">{{ t('Lägg till en ny medarbetare') }}</h1>
      <p>{{ t('Registrera anställningen i tre korta steg. Ändringen sparas bara här.') }}</p>
    </header>

    <ol class="stepper" :aria-label="t('Steg i registreringen')">
      <li :class="{ 'is-current': step === 1, 'is-complete': step > 1 }"><span>1</span>{{ t('Uppgifter') }}</li>
      <li :class="{ 'is-current': step === 2, 'is-complete': step > 2 }"><span>2</span>{{ t('Avtal') }}</li>
      <li :class="{ 'is-current': step === 3 }"><span>3</span>{{ t('Bekräfta') }}</li>
    </ol>

    <form class="employee-flow__body" @submit.prevent="step === 3 ? submit() : next()">
      <fieldset v-if="step === 1" class="form-section">
        <legend>{{ t('Medarbetarens uppgifter') }}</legend>
        <p>{{ t('Ange uppgifterna som behövs för att starta en ny anställning.') }}</p>
        <div class="field-grid">
          <label class="field field--wide">
            <span>{{ t('Namn') }}</span>
            <input v-model="draft.name" required autocomplete="name" :placeholder="t('Till exempel Kim Andersson')" />
          </label>
          <label class="field">
            <span>{{ t('Startdatum') }}</span>
            <input v-model="draft.startsOn" required type="date" />
          </label>
          <label class="field">
            <span>{{ t('Månadslön') }}</span>
            <div class="number-input"><input v-model.number="draft.salary" required min="1" type="number" inputmode="numeric" /><span>{{ t('kr') }}</span></div>
          </label>
        </div>
      </fieldset>

      <fieldset v-else-if="step === 2" class="form-section">
        <legend>{{ t('Välj pensionsplan') }}</legend>
        <p>{{ t('Medarbetaren ansluts till vald plan från och med startdatumet.') }}</p>
        <div class="plan-options">
          <label v-for="plan in plans" :key="plan.id" class="plan-option" :class="{ 'is-selected': draft.planId === plan.id }">
            <input v-model="draft.planId" name="plan" type="radio" :value="plan.id" />
            <span><strong>{{ plan.name }}</strong><small>{{ t('Aktiv pensionsplan') }}</small></span>
          </label>
        </div>
      </fieldset>

      <section v-else class="form-section review" aria-labelledby="review-heading">
        <h2 id="review-heading">{{ t('Granska innan du registrerar') }}</h2>
        <p>{{ t('Kontrollera uppgifterna. Du kan gå tillbaka och ändra dem innan registrering.') }}</p>
        <dl>
          <div><dt>{{ t('Namn') }}</dt><dd>{{ draft.name }}</dd></div>
          <div><dt>{{ t('Startdatum') }}</dt><dd>{{ draft.startsOn }}</dd></div>
          <div><dt>{{ t('Månadslön') }}</dt><dd>{{ draft.salary.toLocaleString('sv-SE') }} {{ t('kr') }}</dd></div>
          <div><dt>{{ t('Pensionsplan') }}</dt><dd>{{ selectedPlan }}</dd></div>
        </dl>
      </section>

      <p v-if="submitError" class="form-error" role="alert">{{ submitError }}</p>
      <div class="flow-actions">
        <button v-if="step > 1" class="button secondary" type="button" @click="step -= 1">{{ t('Tillbaka') }}</button>
        <button v-if="step < 3" class="button" type="submit" :disabled="!canContinue">{{ t('Fortsätt') }}</button>
        <button v-else class="button" type="submit" :disabled="isSubmitting">
          {{ isSubmitting ? t('Registrerar…') : t('Registrera medarbetare') }}
        </button>
      </div>
    </form>
  </section>
</template>

<style scoped>
.employee-flow { max-width: 920px; margin: 0 auto; }
.employee-flow__topbar { display: flex; justify-content: space-between; align-items: center; gap: 16px; margin-bottom: 32px; }
.employee-flow__topbar p { margin: 0; color: var(--muted); font-size: .88rem; font-weight: 700; }
.back-link { border: 0; padding: 0; color: var(--primary); background: transparent; font-weight: 700; text-decoration: underline; text-underline-offset: 3px; }
.employee-flow__header { max-width: 620px; margin-bottom: 32px; }
.employee-flow__header h1 { margin: 0; font-family: var(--font-display, var(--font-sans)); font-size: clamp(1.75rem, 3.2vw, 2.5rem); letter-spacing: var(--heading-tracking, -0.02em); }
.employee-flow__header p, .form-section > p { color: var(--muted); line-height: 1.55; }
.employee-flow__header p { margin: 10px 0 0; }
.stepper { display: grid; grid-template-columns: repeat(3, 1fr); gap: 8px; margin: 0 0 24px; padding: 0; list-style: none; }
.stepper li { display: flex; align-items: center; gap: 8px; padding: 10px 0; border-bottom: 2px solid var(--border); color: var(--muted); font-size: .9rem; font-weight: 700; }
.stepper span { display: inline-grid; place-items: center; width: 24px; height: 24px; border: 1px solid currentColor; border-radius: 50%; font-size: .78rem; }
.stepper .is-current { color: var(--primary); border-color: var(--primary); }
.stepper .is-complete { color: var(--ink); border-color: var(--ink); }
.employee-flow__body { background: var(--surface); border: var(--panel-border, 1px solid var(--border)); border-radius: var(--radius-panel); box-shadow: var(--shadow-panel, none); padding: clamp(24px, 4vw, 40px); }
.form-section { margin: 0; padding: 0; border: 0; }
.form-section legend, .form-section h2 { margin: 0; padding: 0; font-family: var(--font-display, var(--font-sans)); color: var(--ink); font-size: 1.35rem; font-weight: 700; }
.form-section > p { margin: 8px 0 24px; }
.field-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 20px; }
.field { display: grid; gap: 8px; color: var(--ink); font-size: .92rem; font-weight: 700; }
.field--wide { grid-column: 1 / -1; }
.field input, .number-input { width: 100%; min-height: 46px; border: 1px solid var(--border); border-radius: var(--radius-control); background: var(--canvas); color: var(--ink); }
.field input { padding: 10px 12px; }
.number-input { display: flex; align-items: center; padding-right: 12px; }
.number-input input { min-width: 0; border: 0; background: transparent; }
.number-input span { color: var(--muted); font-size: .88rem; font-weight: 600; }
.plan-options { display: grid; gap: 12px; }
.plan-option { display: flex; gap: 12px; align-items: flex-start; padding: 16px; border: 1px solid var(--border); border-radius: var(--radius-control); background: var(--canvas); cursor: pointer; }
.plan-option.is-selected { border-color: var(--primary); box-shadow: inset 0 0 0 1px var(--primary); }
.plan-option input { margin-top: 3px; accent-color: var(--primary); }
.plan-option span { display: grid; gap: 3px; }
.plan-option small { color: var(--muted); }
.review dl { margin: 0; border-top: 1px solid var(--border); }
.review dl div { display: grid; grid-template-columns: minmax(140px, .7fr) 1.3fr; gap: 16px; padding: 15px 0; border-bottom: 1px solid var(--border); }
.review dt { color: var(--muted); }
.review dd { margin: 0; font-weight: 700; font-variant-numeric: tabular-nums; }
.form-error { margin: 20px 0 0; color: var(--danger, #b42318); font-weight: 600; }
.flow-actions { display: flex; justify-content: flex-end; gap: 12px; margin-top: 32px; }
@media (max-width: 620px) { .employee-flow__topbar { margin-bottom: 24px; } .employee-flow__topbar p { text-align: right; } .stepper { gap: 4px; } .stepper li { align-items: flex-start; font-size: .78rem; line-height: 1.25; } .stepper span { width: 22px; height: 22px; } .field-grid { grid-template-columns: 1fr; } .field--wide { grid-column: auto; } .review dl div { grid-template-columns: 1fr; gap: 3px; } .flow-actions { justify-content: stretch; } .flow-actions .button { flex: 1; } }
@media (max-width: 380px) { .stepper li { flex-direction: column; gap: 4px; } .employee-flow__body { padding: 20px 16px; } }
</style>
