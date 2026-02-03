<template>
  <form @submit.prevent="submit" class="form">
    <label>
      Titre
      <input v-model="form.title" required maxlength="200" />
    </label>

    <label>
      Description
      <textarea v-model="form.description" required maxlength="4000" rows="4"></textarea>
    </label>

    <div class="row">
      <label>
        Priorité
        <select v-model="form.priority">
          <option value="LOW">Faible</option>
          <option value="MEDIUM">Moyen</option>
          <option value="CRITICAL">Critique</option>
        </select>
      </label>

      <label>
        Statut
        <select v-model="form.status">
          <option value="OPEN">Ouvert</option>
          <option value="IN_PROGRESS">En cours</option>
          <option value="RESOLVED">Résolu</option>
        </select>
      </label>
    </div>

    <label>
      Solution (si résolu)
      <textarea v-model="form.solution" maxlength="4000" rows="3" placeholder="Ex: redémarrage, patch, contournement..."></textarea>
    </label>

    <div class="actions">
      <button type="submit">{{ isEdit ? 'Mettre à jour' : 'Créer' }}</button>
      <button type="button" class="secondary" @click="clear">Réinitialiser</button>
    </div>

    <p v-if="error" class="error">{{ error }}</p>
  </form>
</template>

<script setup>
import { computed, reactive, watch, ref } from 'vue'
import { createIncident, updateIncident } from '../api'

const props = defineProps({
  selected: { type: Object, default: null }
})

const emit = defineEmits(['created', 'updated', 'cleared'])

const error = ref(null)

const form = reactive({
  title: '',
  description: '',
  priority: 'MEDIUM',
  status: 'OPEN',
  solution: ''
})

const isEdit = computed(() => !!props.selected?.id)

watch(
  () => props.selected,
  (val) => {
    if (!val) return
    form.title = val.title || ''
    form.description = val.description || ''
    form.priority = val.priority || 'MEDIUM'
    form.status = val.status || 'OPEN'
    form.solution = val.solution || ''
  },
  { immediate: true }
)

async function submit() {
  error.value = null
  try {
    const payload = {
      title: form.title,
      description: form.description,
      priority: form.priority,
      status: form.status,
      solution: form.solution || null
    }
    if (isEdit.value) {
      await updateIncident(props.selected.id, payload)
      emit('updated')
    } else {
      await createIncident(payload)
      emit('created')
    }
    clear()
  } catch (e) {
    error.value = e?.response?.data?.message || e.message || 'Erreur inconnue'
  }
}

function clear() {
  form.title = ''
  form.description = ''
  form.priority = 'MEDIUM'
  form.status = 'OPEN'
  form.solution = ''
  error.value = null
  emit('cleared')
}
</script>

<style scoped>
.form label {
  display: flex;
  flex-direction: column;
  gap: 6px;
  margin-bottom: 12px;
}

input, textarea, select {
  border: 1px solid #d1d5db;
  border-radius: 10px;
  padding: 10px;
  font-size: 14px;
}

.row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.actions {
  display: flex;
  gap: 10px;
  align-items: center;
}

button {
  border: 1px solid #111827;
  background: #111827;
  color: white;
  border-radius: 10px;
  padding: 10px 12px;
  cursor: pointer;
}

button.secondary {
  background: transparent;
  color: #111827;
}

.error {
  margin-top: 10px;
  color: #b91c1c;
}
</style>
