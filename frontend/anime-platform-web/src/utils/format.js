export const formatDate = (value) => value ? new Date(value).toLocaleString('zh-CN', { hour12: false }) : '—'
export const imageUrl = (value) => value || ''
export const truncate = (text, length = 90) => !text ? '暂无简介' : (text.length > length ? `${text.slice(0, length)}…` : text)
