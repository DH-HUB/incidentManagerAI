<template>
  <div class="container">
    <header class="header">
      <h1>IncidentManagerAI</h1>
      <p class="subtitle">Gestion d'incidents + détection de doublons + suggestions</p>
    </header>

    <main class="grid">
      <section class="card">
        <h2>Créer / mettre à jour</h2>
        <IncidentForm
          :selected="selected"
          @created="onCreated"
          @updated="onUpdated"
          @cleared="selected = null"
        />
      </section>

      <section class="card">
        <h2>Incidents</h2>
        <IncidentList
          :items="incidents"
          :selectedId="selected?.id"
          @refresh="refresh"
          @select="selectIncident"
          @deleted="onDeleted"
        />
      </section>

      <section class="card">
        <h2>Doublons détectés</h2>
        <DuplicatesPanel :pairs="duplicates" @refresh="loadDuplicates" />
      </section>

      <section class="card">
        <h2>Suggestions</h2>
        <SuggestionsPanel :selected="selected" />
      </section>
    </main>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { listIncidents, detectDuplicates } from './api'
import IncidentForm from './components/IncidentForm.vue'
import IncidentList from './components/IncidentList.vue'
import DuplicatesPanel from './components/DuplicatesPanel.vue'
import SuggestionsPanel from './components/SuggestionsPanel.vue'

const incidents = ref([])
const duplicates = ref([])
const selected = ref(null)

async function refresh() {
  incidents.value = await listIncidents()
}

async function loadDuplicates() {
  duplicates.value = await detectDuplicates()
}

function selectIncident(it) {
  selected.value = it
}

async function onCreated() {
  await refresh()
  await loadDuplicates()
}

async function onUpdated() {
  await refresh()
  await loadDuplicates()
}

async function onDeleted() {
  selected.value = null
  await refresh()
  await loadDuplicates()
}

onMounted(async () => {
  await refresh()
  await loadDuplicates()
})
</script>

<style scoped>
.container {
  max-width: 1100px;
  margin: 0 auto;
  padding: 24px;
  font-family: ui-sans-serif, system-ui, -apple-system, Segoe UI, Roboto, Helvetica, Arial, "Apple Color Emoji", "Segoe UI Emoji";
}

.header {
  margin-bottom: 18px;
}
.subtitle {
  margin-top: 6px;
  opacity: 0.8;
}

.grid {
  display: grid;
  grid-template-columns: 1fr;
  gap: 16px;
}

@media (min-width: 960px) {
  .grid {
    grid-template-columns: 1fr 1fr;
  }
}

.card {
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  padding: 16px;
  background: #fff;
  box-shadow: 0 1px 8px rgba(0,0,0,0.04);
}

h1, h2 {
  margin: 0 0 10px 0;
}
</style>
