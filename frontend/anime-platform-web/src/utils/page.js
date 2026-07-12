export function normalizePage(data) {
  if (!data) return { list: [], total: 0, current: 1, size: 10, pages: 0 }
  if (Array.isArray(data.records)) return { list: data.records, total: Number(data.total || 0), current: Number(data.current || 1), size: Number(data.size || 10), pages: Number(data.pages || 0) }
  if (Array.isArray(data.content)) return { list: data.content, total: Number(data.totalElements || 0), current: Number(data.number || 0) + 1, size: Number(data.size || 10), pages: Number(data.totalPages || 0) }
  if (Array.isArray(data)) return { list: data, total: data.length, current: 1, size: data.length, pages: 1 }
  return { list: [], total: 0, current: 1, size: 10, pages: 0 }
}
