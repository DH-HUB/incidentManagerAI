<template>
  <div class="top">
    <button @click="$emit('refresh')">Recalculer</button>
  </div>

  <div v-if="!pairs?.length" class="empty">
    Aucun doublon détecté (ou pas assez d'incidents).
  </div>

  <ul class="list">
    <li v-for="(p, idx) in pairs" :key="idx" class="item">
      <div>
        <strong>#{{ p.incidentIdA }}</strong> ↔ <strong>#{{ p.incidentIdB }}</strong>
      </div>
      <div class="sim">Similarité: {{ (p.similarity * 100).toFixed(1) }}%</div>
    </li>
  </ul>
</template>

<script setup>
defineProps({
  pairs: { type: Array, default: () => [] }
})

defineEmits(['refresh'])
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
.sim {
  opacity: 0.75;
  font-size: 13px;
  margin-top: 4px;
}
.empty {
  opacity: 0.75;
  padding: 8px 0;
}
</style>
