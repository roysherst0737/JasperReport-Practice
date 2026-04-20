export async function uploadCsv(file) {
  const formData = new FormData()
  formData.append('file', file)

  const response = await fetch('http://localhost:8080/api/report/upload', {
    method: 'POST',
    body: formData
  })

  if (!response.ok) {
    const text = await response.text()
    throw new Error(text || 'Server error')
  }

  return await response.blob()
}