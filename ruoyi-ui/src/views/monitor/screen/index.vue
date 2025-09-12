<template>
  <iframe :src="src" style="width:100%;height:100vh;border:0;"></iframe>
</template>

<script>
import { getToken } from '@/utils/auth'
export default {
  name: 'ScreenEmbed',
  computed: {
    src() {
      // 强制用绝对地址 + 端口，并保证结尾有 /
      const base = (process.env.VUE_APP_SCREEN_URL || 'http://localhost:8089/screen/').replace(/\/?$/, '/')
      const t = getToken() || ''
      return `${base}?token=${encodeURIComponent(t)}`
    }
  }
}
</script>

