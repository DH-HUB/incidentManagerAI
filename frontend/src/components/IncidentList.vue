<template>
  <div class="top">
    <button @click="$emit('refresh')">Rafraîchir</button>
  </div>

  <div v-if="!items?.length" class="empty">Aucun incident</div>

  <ul class="list">
    <li
      v-for="it in items"
      :key="it.id"
      class="item"
      :class="{ selected: it.id === selectedId }"
    >
      <div class="meta" @click="$emit('select', it)">
        <div class="title">
          <strong>#{{ it.id }}</strong> — {{ it.title }}
        </div>
        <div class="badges">
          <span class="badge">{{ labelPriority(it.priority) }}</span>
          <span class="badge muted">{{ labelStatus(it.status) }}</span>
        </div>
        <div class="desc">{{ it.description }}</div>
      </div>

      <div class="actions">
        <button class="danger" @click="remove(it.id)">Supprimer</button>
      </div>
    </li>
  </ul>
</template>

<script setup>
import { deleteIncident } from '../api'

const props = defineProps({
  items: { type: Array, default: () => [] },
  selectedId: { type: Number, default: null }
})

const emit = defineEmits(['refresh', 'select', 'deleted'])

async function remove(id) {
  if (!confirm(`Supprimer l'incident #${id} ?`)) return
  await deleteIncident(id)
  emit('deleted')
}

function labelPriority(p) {
  if (p === 'LOW') return 'Faible'
  if (p === 'CRITICAL') return 'Critique'
  return 'Moyen'
}

function labelStatus(s) {
  if (s === 'IN_PROGRESS') return 'En cours'
  if (s === 'RESOLVED') return 'Résolu'
  return 'Ouvert'
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
button.danger {
  border-color: #991b1b;
  background: #991b1b;
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
  padding: 12px;
  display: grid;
  grid-template-columns: 1fr auto;
  gap: 10px;
}
.item.selected {
  outline: 2px solid #111827;
}
.meta {
  cursor: pointer;
}
.title {
  margin-bottom: 6px;
}
.desc {
  opacity: 0.85;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.badges {
  display: flex;
  gap: 8px;
  margin-bottom: 8px;
}
.badge {
  border: 1px solid #d1d5db;
  border-radius: 999px;
  padding: 2px 8px;
  font-size: 12px;
}
.badge.muted {
  opacity: 0.75;
}
.actions {
  display: flex;
  align-items: flex-start;
}
.empty {
  opacity: 0.75;
  padding: 8px 0;
}
</style>
