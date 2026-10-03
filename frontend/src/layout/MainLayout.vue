<template>
  <el-container class="layout">
    <!-- 左侧菜单 -->
    <el-aside width="210px" class="aside">
      <div class="logo">轻客管家</div>
      <el-menu
        :default-active="route.path"
        router
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409eff"
      >
        <el-menu-item index="/home">
          <el-icon><HomeFilled /></el-icon>
          <span>首页</span>
        </el-menu-item>

        <el-sub-menu index="clue">
          <template #title>
            <el-icon><UserFilled /></el-icon>
            <span>线索管理</span>
          </template>
          <el-menu-item index="/clue/list">线索列表</el-menu-item>
        </el-sub-menu>

        <el-sub-menu index="system">
          <template #title>
            <el-icon><Setting /></el-icon>
            <span>系统管理</span>
          </template>
          <el-menu-item index="/dept/list">部门管理</el-menu-item>
          <el-menu-item index="/user/list">用户管理</el-menu-item>
        </el-sub-menu>
      </el-menu>
    </el-aside>

    <el-container>
      <!-- 顶栏 -->
      <el-header class="header">
        <el-dropdown trigger="click" @command="handleCommand">
          <span class="user-box">
            <el-avatar :size="30" :src="user.image">
              {{ (user.name || '?').charAt(0) }}
            </el-avatar>
            <span class="user-name">{{ user.name }}</span>
            <el-icon><ArrowDown /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="logout">退出</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </el-header>

      <!-- 内容区 -->
      <el-main class="main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowDown, HomeFilled, Setting, UserFilled } from '@element-plus/icons-vue'
import { clearLogin, getUser } from '@/utils/auth'

const route = useRoute()
const router = useRouter()

// 登录信息在 localStorage，这里直接读一次（刷新页面即重新读取）
const user = computed(() => getUser() || {})

function handleCommand(command) {
  if (command !== 'logout') return
  ElMessageBox.confirm('确定要退出登录吗？', '提示', { type: 'warning' })
    .then(() => {
      clearLogin()
      router.replace('/login')
      ElMessage.success('已退出登录')
    })
    .catch(() => {
      // 用户点了取消，什么都不做
    })
}
</script>

<style scoped>
.layout {
  height: 100vh;
}

.aside {
  background-color: #304156;
  overflow-x: hidden;
}

.logo {
  height: 60px;
  line-height: 60px;
  text-align: center;
  color: #fff;
  font-size: 20px;
  font-weight: 700;
  letter-spacing: 2px;
}

/* 去掉 el-menu 自带的右边框，和侧边栏背景色融为一体 */
.aside :deep(.el-menu) {
  border-right: none;
}

.header {
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: flex-end;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
}

.user-box {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  color: #333;
}

.user-name {
  font-size: 14px;
}

.main {
  background-color: #f0f2f5;
  padding: 16px;
}
</style>
