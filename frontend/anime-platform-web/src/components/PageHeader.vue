<template><div class="page-header"><div><p v-if="eyebrow" class="eyebrow">{{ eyebrow }}</p><h1>{{ title }}</h1><p v-if="description" class="muted">{{ description }}</p></div><div><slot /></div></div></template>
<script setup>defineProps({ title: String, description: String, eyebrow: String })</script>
