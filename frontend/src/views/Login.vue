<template>
  <div class="login-page">
    <el-card class="login-card" shadow="always">
      <h2 class="title">轻客管家</h2>
      <p class="subtitle">轻量客户线索管理平台</p>

      <el-form ref="formRef" :model="form" :rules="rules" size="large" @keyup.enter="handleLogin">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" :prefix-icon="User" clearable />
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="请输入密码"
            :prefix-icon="Lock"
            show-password
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" class="login-btn" :loading="loading" @click="handleLogin">
            登 录
          </el-button>
        </el-form-item>
      </el-form>

      <p class="tip">测试账号：zhangsan / zhangsan123</p>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Lock, User } from '@element-plus/icons-vue'
import request from '@/api/request'
import { setToken, setUser } from '@/utils/auth'

const router = useRouter()
const formRef = ref()
const loading = ref(false)

const form = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

async function handleLogin() {
  // 校验不通过时 validate() 会 reject，这里直接拦住，红色提示由 el-form 自动显示
  try {
    await formRef.value.validate()
  } catch (e) {
    return
  }

  loading.value = true
  try {
    const res = await request.post('/login', {
      username: form.username,
      password: form.password
    })
    // token 单独存，其余用户信息（id/name/image/roleLabel）留给页面展示
    const { token, ...userInfo } = res.data
    setToken(token)
    setUser(userInfo)
    ElMessage.success('登录成功')
    router.replace('/')
  } catch (e) {
    // 失败提示已在 axios 响应拦截器里统一处理，这里不用再弹
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #1f3b73 0%, #2b5876 100%);
}

.login-card {
  width: 380px;
  padding: 8px 12px;
  border-radius: 8px;
}

.title {
  text-align: center;
  margin: 6px 0 4px;
  color: #1f2d3d;
  letter-spacing: 2px;
}

.subtitle {
  text-align: center;
  margin: 0 0 18px;
  font-size: 13px;
  color: #909399;
}

.login-btn {
  width: 100%;
}

.tip {
  text-align: center;
  font-size: 12px;
  color: #909399;
  margin: 4px 0 6px;
}
</style>
