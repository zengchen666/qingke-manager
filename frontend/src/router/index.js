import { createRouter, createWebHashHistory } from 'vue-router'
import { getToken } from '@/utils/auth'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/',
    component: () => import('@/layout/MainLayout.vue'),
    redirect: '/home',
    children: [
      {
        path: 'home',
        name: 'Home',
        component: () => import('@/views/Home.vue'),
        meta: { title: '首页' }
      },
      {
        path: 'clue/list',
        name: 'ClueList',
        component: () => import('@/views/clue/List.vue'),
        meta: { title: '线索列表' }
      },
      {
        path: 'dept/list',
        name: 'DeptList',
        component: () => import('@/views/dept/List.vue'),
        meta: { title: '部门管理' }
      },
      {
        path: 'user/list',
        name: 'UserList',
        component: () => import('@/views/user/List.vue'),
        meta: { title: '用户管理' }
      }
    ]
  },
  { path: '/:pathMatch(.*)*', redirect: '/' }
]

const router = createRouter({
  // 用 hash 模式：静态部署到 nginx 时不需要额外的服务端路由规则
  history: createWebHashHistory(),
  routes
})

// 全局前置守卫：未登录一律拦回登录页（后端也会校验 token，这里是提前拦截，避免无效请求）
router.beforeEach((to, from, next) => {
  if (to.path === '/login') {
    return next()
  }
  if (!getToken()) {
    return next('/login')
  }
  next()
})

export default router
