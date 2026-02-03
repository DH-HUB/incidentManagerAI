import axios from 'axios'

export const api = axios.create({
  baseURL: '/api'
})

export async function listIncidents() {
  const { data } = await api.get('/incidents')
  return data
}

export async function createIncident(payload) {
  const { data } = await api.post('/incidents', payload)
  return data
}

export async function updateIncident(id, payload) {
  const { data } = await api.put(`/incidents/${id}`, payload)
  return data
}

export async function deleteIncident(id) {
  await api.delete(`/incidents/${id}`)
}

export async function detectDuplicates() {
  const { data } = await api.get('/incidents/duplicates')
  return data
}

export async function getSuggestions(id) {
  const { data } = await api.get(`/incidents/${id}/suggestions`)
  return data
}
