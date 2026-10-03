<template>
  <el-card shadow="never">
    <!-- 查询条件 -->
    <el-form :inline="true">
      <el-form-item label="姓名">
        <el-input
          v-model="query.name"
          placeholder="请输入姓名"
          clearable
          style="width: 160px"
          @keyup.enter="handleSearch"
        />
      </el-form-item>
      <el-form-item label="手机号">
        <el-input
          v-model="query.phone"
          placeholder="请输入手机号"
          clearable
          style="width: 180px"
          @keyup.enter="handleSearch"
        />
      </el-form-item>
      <el-form-item label="部门">
        <el-select v-model="query.deptId" placeholder="全部" clearable style="width: 160px">
          <el-option v-for="d in allDepts" :key="d.id" :label="d.name" :value="d.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="query.status" placeholder="全部" clearable style="width: 120px">
          <el-option label="正常" :value="1" />
          <el-option label="停用" :value="0" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="handleReset">重置</el-button>
      </el-form-item>
      <el-form-item class="float-right">
        <el-button type="danger" :disabled="!selection.length" @click="handleBatchDelete">
          批量删除
        </el-button>
        <el-button type="primary" @click="openDialog()">新增用户</el-button>
      </el-form-item>
    </el-form>

    <!-- 数据表格 -->
    <el-table v-loading="loading" :data="rows" border stripe @selection-change="(v) => (selection = v)">
      <el-table-column type="selection" width="45" align="center" />
      <el-table-column prop="id" label="ID" width="70" align="center" />
      <el-table-column prop="username" label="用户名" min-width="110" />
      <el-table-column prop="name" label="姓名" min-width="90" />
      <el-table-column prop="phone" label="手机号" min-width="120" />
      <el-table-column prop="email" label="邮箱" min-width="180" show-overflow-tooltip />
      <el-table-column label="性别" width="70" align="center">
        <template #default="{ row }">{{ row.gender === 1 ? '男' : '女' }}</template>
      </el-table-column>
      <el-table-column label="状态" width="80" align="center">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'">
            {{ row.status === 1 ? '正常' : '停用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="deptName" label="部门" min-width="110" />
      <el-table-column prop="roleName" label="角色" min-width="110" />
      <el-table-column label="操作" width="90" align="center" fixed="right">
        <template #default="{ row }">
          <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <el-pagination
      v-model:current-page="query.page"
      v-model:page-size="query.pageSize"
      class="pagination"
      :page-sizes="[5, 10, 20, 50]"
      :total="total"
      layout="total, sizes, prev, pager, next, jumper"
      @size-change="loadData"
      @current-change="loadData"
    />

    <!-- 新增对话框 -->
    <el-dialog v-model="dialogVisible" title="新增用户" width="560px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-row :gutter="12">
          <el-col :span="12">
            <el-form-item label="用户名" prop="username">
              <el-input v-model="form.username" placeholder="登录账号，唯一" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="姓名" prop="name">
              <el-input v-model="form.name" placeholder="真实姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="form.phone" placeholder="11 位手机号" maxlength="11" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="form.email" placeholder="唯一邮箱" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="性别">
              <el-radio-group v-model="form.gender">
                <el-radio :value="1">男</el-radio>
                <el-radio :value="2">女</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态">
              <el-radio-group v-model="form.status">
                <el-radio :value="1">正常</el-radio>
                <el-radio :value="0">停用</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="部门">
              <el-select v-model="form.deptId" placeholder="请选择" style="width: 100%">
                <el-option v-for="d in allDepts" :key="d.id" :label="d.name" :value="d.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="角色">
              <el-select v-model="form.roleId" placeholder="请选择" style="width: 100%">
                <el-option v-for="r in ROLES" :key="r.id" :label="r.name" :value="r.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="备注">
              <el-input v-model="form.remark" type="textarea" :rows="2" maxlength="50" show-word-limit />
            </el-form-item>
          </el-col>
        </el-row>
        <el-alert type="info" :closable="false" show-icon>
          新增用户的初始密码为「用户名 + 123」，后端会做 MD5 加密后存储
        </el-alert>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { addUser, deleteUser, listUser } from '@/api/user'
import { listDept } from '@/api/dept'

// 角色是固定枚举，后端暂未提供角色列表接口，这里直接写死
const ROLES = [
  { id: 1, name: '管理员' },
  { id: 2, name: '线索专员' },
  { id: 3, name: '高级专员' },
  { id: 4, name: '商机专员' },
  { id: 5, name: '普通员工' }
]

const loading = ref(false)
const saving = ref(false)
const rows = ref([])
const total = ref(0)
const selection = ref([])
const allDepts = ref([])

const query = reactive({
  name: '',
  phone: '',
  deptId: null,
  status: null,
  page: 1,
  pageSize: 10
})

const dialogVisible = ref(false)
const formRef = ref()
const form = reactive({
  username: '',
  name: '',
  phone: '',
  email: '',
  gender: 1,
  status: 1,
  deptId: null,
  roleId: null,
  remark: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  email: [{ type: 'email', message: '邮箱格式不正确', trigger: 'blur' }]
}

async function loadData() {
  loading.value = true
  try {
    const res = await listUser(query)
    rows.value = res.data.rows || []
    total.value = res.data.total || 0
  } catch (e) {
    // 交给拦截器
  } finally {
    loading.value = false
  }
}

/** 部门下拉数据：一次性拉全部（部门数量少，没必要做联动搜索） */
async function loadDepts() {
  const res = await listDept({ page: 1, pageSize: 200 })
  allDepts.value = res.data.rows || []
}

function handleSearch() {
  query.page = 1
  loadData()
}

function handleReset() {
  query.name = ''
  query.phone = ''
  query.deptId = null
  query.status = null
  handleSearch()
}

function openDialog() {
  form.username = ''
  form.name = ''
  form.phone = ''
  form.email = ''
  form.gender = 1
  form.status = 1
  form.deptId = null
  form.roleId = null
  form.remark = ''
  dialogVisible.value = true
}

async function handleSave() {
  try {
    await formRef.value.validate()
  } catch (e) {
    return
  }

  saving.value = true
  try {
    await addUser(form)
    ElMessage.success('新增成功')
    dialogVisible.value = false
    loadData()
  } catch (e) {
    // 唯一键冲突等由拦截器提示
  } finally {
    saving.value = false
  }
}

function handleDelete(row) {
  ElMessageBox.confirm(`确定要删除用户「${row.name}」吗？`, '警告', { type: 'warning' })
    .then(async () => {
      await deleteUser([row.id])
      ElMessage.success('删除成功')
      if (rows.value.length === 1 && query.page > 1) {
        query.page -= 1
      }
      loadData()
    })
    .catch(() => {})
}

function handleBatchDelete() {
  const ids = selection.value.map((u) => u.id)
  if (!ids.length) return
  ElMessageBox.confirm(`确定要删除选中的 ${ids.length} 个用户吗？`, '警告', { type: 'warning' })
    .then(async () => {
      await deleteUser(ids)
      ElMessage.success('删除成功')
      selection.value = []
      if (rows.value.length === ids.length && query.page > 1) {
        query.page -= 1
      }
      loadData()
    })
    .catch(() => {})
}

onMounted(() => {
  loadData()
  loadDepts()
})
</script>

<style scoped>
.pagination {
  margin-top: 16px;
  justify-content: flex-end;
}

.float-right {
  float: right;
  margin-right: 8px;
}
</style>
