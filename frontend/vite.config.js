import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { fileURLToPath, URL } from 'node:url'

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    vue()

    // ---------------------------------------------------------------------
    // 可选优化：Element Plus 按需引入（默认关闭）
    //
    // 启用方法：
    //   1) npm i -D unplugin-vue-components
    //   2) 取消下面两行注释
    //   3) 把 main.js 里的 app.use(ElementPlus, { locale: zhCn }) 去掉，
    //      改成在 App.vue 用 <el-config-provider :locale="zhCn"> 包裹 <router-view />
    //
    // 实测收益（本项目 build 产物对比）：
    //   开启前主包 index-*.js = 1060 KB（gzip 349 KB）
    //   开启后主包 index-*.js =  150 KB（gzip  59 KB），降幅约 86%
    //   组件自动拆成独立 chunk，按需加载
    //
    // 未默认开启的原因：本项目在开发环境下未完成浏览器渲染实测（验证环境的浏览器组件
    // 下载失败），为保证「克隆下来就能跑」不做未验证的改动。你在自己机器上验证通过后
    // 完全可以启用。
    // ---------------------------------------------------------------------
    // import Components from 'unplugin-vue-components/vite'
    // import { ElementPlusResolver } from 'unplugin-vue-components/resolvers'
    // Components({ resolvers: [ElementPlusResolver()], dts: false })
  ],

  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },

  server: {
    // 显式绑定 IPv4：Vite 默认绑 localhost，在 Windows 上可能只监听 IPv6 的 ::1，
    // 造成「浏览器能开、程序探测端口却连不上」的情况
    host: '127.0.0.1',
    // 与课程文档约定一致：前端固定 90 端口，8080 留给后端
    port: 90,
    // 开发期由 Vite 代理 /api 到后端，等价于生产环境的 nginx 反向代理，
    // 这样开发时不必依赖 nginx
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, '')
      }
    }
  }
})
