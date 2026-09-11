<script setup lang="ts">
import PageHeader from '../components/PageHeader.vue'
import Panel from '../components/Panel.vue'

defineProps<{ title: string; description: string; plans: { name: string; members: string; premium: string }[]; t: (source: string) => string }>()
</script>
<template><section><PageHeader :title="title" :description="description" /><div class="grid"><Panel v-for="plan in plans" :key="plan.name" :title="t(plan.name)"><p class="panel-lede">{{ t(plan.members) }}</p><strong class="panel-figure">{{ plan.premium }}</strong></Panel></div></section></template>
<style scoped>.grid{display:grid;grid-template-columns:repeat(3,1fr);gap:var(--space-metric-gap,16px)}.panel-lede{color:var(--muted);line-height:1.55;margin:0 0 12px}.panel-figure{font-family:var(--font-display,var(--font-sans));font-size:1.4rem;color:var(--ink);font-variant-numeric:tabular-nums}@media(max-width:760px){.grid{grid-template-columns:1fr}}</style>
