<template>
  <div v-if="!selected" class="empty">
    Sélectionne un incident pour afficher des suggestions (basées sur les incidents résolus).
  </div>

  <div v-else>
    <div class="top">
      <button @click="load" :disabled="loading">Charger</button>
    </div>

    <p class="hint">
      Incident sélectionné: <strong>#{{ selected.id }}</strong> — {{ selected.title }}
    </p>

    <div v-if="loading" class="hint">Chargement...</div>
    <div v-else-if="!suggestions.length" class="hint">
    Aucune suggestion : aucun incident résolu similaire (ou base de connaissances trop petite).
    </div>


    <ul class="list">
      <li v-for="s in suggestions" :key="s.resolvedIncidentId" class="item">
        <div class="line">
          <strong>#{{ s.resolvedIncidentId }}</strong> — {{ s.title }}
          <span class="sim">{{ (s.similarity * 100).toFixed(1) }}%</span>
        </div>
        <pre class="solution">{{ s.solution }}</pre>
      </li>
    </ul>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import { getSuggestions } from '../api'

const props = defineProps({
  selected: { type: Object, default: null }
})

const suggestions = ref([])
const loading = ref(false)

watch(
  () => props.selected?.id,
  async () => {
    suggestions.value = []
  }
)

async function load() {
  if (!props.selected?.id) return
  loading.value = true
  try {
    suggestions.value = await getSuggestions(props.selected.id)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.top {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 8px;
}
button {
  border: 1px solid #111827;
  background: #111827;
  color: white;
  border-radius: 10px;
  padding: 8px 10px;
  cursor: pointer;
}
.list {
  list-style: none;
  margin: 0;
  padding: 0;
  display: grid;
  gap: 10px;
}
.item {
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  padding: 10px;
}
.line {
  display: flex;
  justify-content: space-between;
  gap: 10px;
}
.sim {
  opacity: 0.75;
}
.solution {
  white-space: pre-wrap;
  margin: 8px 0 0 0;
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, "Liberation Mono", "Courier New", monospace;
  font-size: 13px;
  opacity: 0.9;
}
.hint, .empty {
  opacity: 0.8;
}
</style>
