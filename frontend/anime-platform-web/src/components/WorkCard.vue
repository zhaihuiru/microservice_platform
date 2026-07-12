<template>
  <article class="content-card work-card" @click="$router.push(`/works/${work.id}`)">
    <div class="cover-wrap"><img v-if="work.coverUrl" :src="work.coverUrl" :alt="work.title"><div v-else class="cover-placeholder">ANIME</div><span class="status-pill">{{ work.status || '未设置' }}</span></div>
    <div class="card-body"><h3>{{ work.title }}</h3><p>{{ truncate(work.description, 70) }}</p><div class="card-meta"><span>{{ work.releaseDate || '日期待定' }}</span><span>查看详情 →</span></div></div>
  </article>
</template>
<script setup>import { truncate } from '../utils/format'; defineProps({ work: { type: Object, required: true } })</script>
