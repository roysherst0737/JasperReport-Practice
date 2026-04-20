<template>
  <div class="page">
    <h1>CSV to Jasper PDF</h1>
    <CsvUpload />
  </div>
</template>

<script setup>
import CsvUpload from './components/CsvUpload.vue'
</script>

<style>
body {
  font-family: Arial, sans-serif;
  margin: 0;
  padding: 0;
}

.page {
  max-width: 720px;
  margin: 40px auto;
  padding: 24px;
}
</style>