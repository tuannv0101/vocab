const BASE_URL = '/api';

async function request(endpoint, options = {}) {
  const url = `${BASE_URL}${endpoint}`;
  const headers = {
    'Content-Type': 'application/json',
    ...(options.headers || {})
  };

  const config = {
    ...options,
    headers
  };

  if (config.body && typeof config.body === 'object' && !(config.body instanceof FormData)) {
    config.body = JSON.stringify(config.body);
  }

  const response = await fetch(url, config);
  if (!response.ok) {
    let errorMsg = `HTTP Error: ${response.status}`;
    try {
      const errorData = await response.json();
      if (errorData.message) errorMsg = errorData.message;
    } catch (_) {}
    throw new Error(errorMsg);
  }

  return response.json();
}

export const api = {
  // Sets
  getSets: () => request('/sets'),
  getSet: (id) => request(`/sets/${id}`),
  createSet: (data) => request('/sets', { method: 'POST', body: data }),
  updateSet: (id, data) => request(`/sets/${id}`, { method: 'PUT', body: data }),
  deleteSet: (id) => request(`/sets/${id}`, { method: 'DELETE' }),
  resetSetProgress: (id) => request(`/sets/${id}/reset`, { method: 'POST' }),
  getDuplicates: (id) => request(`/sets/${id}/duplicates`),
  deduplicateSet: (id) => request(`/sets/${id}/deduplicate`, { method: 'POST' }),
  cleanPartOfSpeech: () => request('/sets/clean-pos', { method: 'POST' }),

  // Words
  getWordsBySet: (setId, starred = false) => request(`/sets/${setId}/words?starred=${starred}`),
  addWord: (data) => request('/words', { method: 'POST', body: data }),
  updateWord: (id, data) => request(`/words/${id}`, { method: 'PUT', body: data }),
  deleteWord: (id) => request(`/words/${id}`, { method: 'DELETE' }),
  toggleStar: (id) => request(`/words/${id}/star`, { method: 'POST' }),
  updateMastery: (id, masteryLevel) => request(`/words/${id}/mastery`, { method: 'POST', body: { masteryLevel } }),
  recordAnswer: (wordId, correct) => request('/study/answer', { method: 'POST', body: { wordId, correct } }),

  // Bulk Import
  bulkImport: (data) => request('/import', { method: 'POST', body: data }),
  batchAddWords: (data) => request('/words/batch', { method: 'POST', body: data }),
  parsePreview: (rawText) => request('/import/preview', { method: 'POST', body: { rawText } }),

  // Search
  searchWords: (query) => request(`/words/search?q=${encodeURIComponent(query)}`)
};
