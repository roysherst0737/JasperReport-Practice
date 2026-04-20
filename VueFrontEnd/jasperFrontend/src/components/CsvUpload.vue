<template>
  <div class="card">
    <input type="file" accept=".csv" @change="onFileChange" />
    <button :disabled="!file || loading" @click="submit">
      {{ loading ? 'Generating...' : 'Upload CSV and Download PDF' }}
    </button>

    <p v-if="message">{{ message }}</p>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { uploadCsv } from '../api/report'

const file = ref(null)
const loading = ref(false)
const message = ref('')

const onFileChange = (event) => {
  const selected = event.target.files?.[0]
  file.value = selected || null
  message.value = ''
}

const submit = async () => {
  if (!file.value) {
    message.value = 'Please select a CSV file first.'
    return
  }

  loading.value = true
  message.value = ''

  try {
    const blob = await uploadCsv(file.value)

    const url = window.URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = 'report.pdf'
    a.click()
    window.URL.revokeObjectURL(url)

    message.value = 'PDF generated successfully.'
  } catch (error) {
    message.value = error.message || 'Upload failed.'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.card {
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 20px;
  display: grid;
  gap: 16px;
}

button {
  width: fit-content;
  padding: 10px 16px;
  cursor: pointer;
}
</style>