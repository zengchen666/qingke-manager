<template>
  <el-card shadow="never">
    <!-- 查询条件 -->
    <el-form :inline="true">
      <el-form-item label="手机号">
        <el-input
          v-model="query.phone"
          placeholder="请输入手机号"
          clearable
          style="width: 160px"
          @keyup.enter="handleSearch"
        />
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="query.status" placeholder="全部" clearable style="width: 140px">
          <el-option v-for="s in CLUE_STATUS" :key="s.value" :label="s.label" :value="s.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="渠道">
        <el-select v-model="query.channel" placeholder="全部" clearable style="width: 140px">
          <el-option v-for="c in CHANNELS" :key="c.value" :label="c.label" :value="c.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="归属人">
        <el-input
          v-model="query.assignName"
          placeholder="请输入姓名"
          clearable
          style="width: 150px"
          @keyup.enter="handleSearch"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="handleReset">重置</el-button>
      </el-form-item>
      <el-form-item class="float-right">
        <el-button type="primary" @click="openAddDialog">新增线索</el-button>
      </el-form-item>
    </el-form>

    <!-- 数据表格 -->
    <el-table v-loading="loading" :data="rows" border stripe>
      <el-table-column prop="id" label="ID" width="70" align="center" />
      <el-table-column prop="name" label="客户姓名" min-width="100" />
      <el-table-column prop="phone" label="手机号" min-width="120" />
      <el-table-column label="渠道" width="100" align="center">
        <template #default="{ row }">{{ dictLabel(CHANNELS, row.channel) }}</template>
      </el-table-column>
      <el-table-column label="意向学科" min-width="150">
        <template #default="{ row }">{{ dictLabel(SUBJECTS, row.subject) }}</template>
      </el-table-column>
      <el-table-column label="意向等级" width="130">
        <template #default="{ row }">{{ dictLabel(LEVELS, row.level) }}</template>
      </el-table-column>
      <el-table-column prop="assignName" label="归属人" width="100">
        <template #default="{ row }">{{ row.assignName || '-' }}</template>
      </el-table-column>
      <el-table-column label="状态" width="100" align="center">
        <template #default="{ row }">
          <el-tag :type="statusTagType(row.status)">{{ dictLabel(CLUE_STATUS, row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="nextTime" label="下次跟进时间" min-width="160">
        <template #default="{ row }">{{ row.nextTime || '-' }}</template>
      </el-table-column>
      <el-table-column label="操作" width="90" align="center" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="openDetail(row)">详情</el-button>
        </template>
      </el-table-column>
    </el-table>

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

    <!-- 新增线索 -->
    <el-dialog v-model="addVisible" title="新增线索" width="560px">
      <el-form ref="addFormRef" :model="addForm" :rules="addRules" label-width="90px">
        <el-row :gutter="12">
          <el-col :span="12">
            <el-form-item label="客户姓名" prop="name">
              <el-input v-model="addForm.name" placeholder="请输入姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="addForm.phone" placeholder="11 位手机号" maxlength="11" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="性别">
              <el-radio-group v-model="addForm.gender">
                <el-radio :value="1">男</el-radio>
                <el-radio :value="2">女</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="年龄">
              <el-input-number v-model="addForm.age" :min="1" :max="120" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="渠道来源" prop="channel">
              <el-select v-model="addForm.channel" placeholder="请选择" style="width: 100%">
                <el-option v-for="c in CHANNELS" :key="c.value" :label="c.label" :value="c.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="微信号">
              <el-input v-model="addForm.wechat" placeholder="选填" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="QQ号">
              <el-input v-model="addForm.qq" placeholder="选填" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="活动ID">
              <el-input-number v-model="addForm.activityId" :min="1" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="addVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleAdd">保存</el-button>
      </template>
    </el-dialog>

    <!-- 线索详情抽屉 -->
    <el-drawer v-model="detailVisible" title="线索详情" size="62%">
      <template v-if="detail.id">
        <el-descriptions :column="2" border size="small">
          <el-descriptions-item label="客户姓名">{{ detail.name }}</el-descriptions-item>
          <el-descriptions-item label="手机号">{{ detail.phone }}</el-descriptions-item>
          <el-descriptions-item label="性别">{{ detail.gender === 1 ? '男' : '女' }}</el-descriptions-item>
          <el-descriptions-item label="年龄">{{ detail.age }}</el-descriptions-item>
          <el-descriptions-item label="渠道">{{ dictLabel(CHANNELS, detail.channel) }}</el-descriptions-item>
          <el-descriptions-item label="微信号">{{ detail.wechat || '-' }}</el-descriptions-item>
          <el-descriptions-item label="归属人">{{ detail.assignName || '未分配' }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="statusTagType(detail.status)">{{ dictLabel(CLUE_STATUS, detail.status) }}</el-tag>
          </el-descriptions-item>
        </el-descriptions>

        <!-- 操作区 -->
        <div class="action-bar">
          <el-select
            v-model="assignUserId"
            placeholder="选择线索专员"
            style="width: 200px"
            clearable
          >
            <el-option v-for="u in lineUsers" :key="u.id" :label="u.name" :value="u.id" />
          </el-select>
          <el-button type="primary" :disabled="!assignUserId" @click="handleAssign">分配线索</el-button>
          <el-button type="warning" @click="handleToBusiness">转为商机</el-button>
        </div>

        <!-- 跟进记录 -->
        <h4 class="section-title">跟进记录</h4>
        <el-timeline v-if="detail.trackRecords && detail.trackRecords.length">
          <el-timeline-item
            v-for="r in detail.trackRecords"
            :key="r.id"
            :timestamp="r.createTime"
            placement="top"
          >
            <div class="track-head">
              <span class="track-user">{{ r.assignName }}</span>
              <el-tag size="small">{{ dictLabel(SUBJECTS, r.subject) }}</el-tag>
              <el-tag size="small" type="warning">{{ dictLabel(LEVELS, r.level) }}</el-tag>
            </div>
            <div class="track-record">{{ r.record }}</div>
            <div v-if="r.nextTime" class="track-next">下次跟进：{{ r.nextTime }}</div>
          </el-timeline-item>
        </el-timeline>
        <el-empty v-else description="暂无跟进记录" :image-size="60" />

        <!-- 跟进表单 -->
        <h4 class="section-title">新增跟进</h4>
        <el-form :model="trackForm" label-width="90px" class="track-form">
          <el-row :gutter="12">
            <el-col :span="12">
              <el-form-item label="意向学科">
                <el-select v-model="trackForm.subject" placeholder="请选择" style="width: 100%">
                  <el-option v-for="s in SUBJECTS" :key="s.value" :label="s.label" :value="s.value" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="意向等级">
                <el-select v-model="trackForm.level" placeholder="请选择" style="width: 100%">
                  <el-option v-for="l in LEVELS" :key="l.value" :label="l.label" :value="l.value" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="24">
              <el-form-item label="下次跟进">
                <el-date-picker
                  v-model="trackForm.nextTime"
                  type="datetime"
                  placeholder="选择时间"
                  value-format="YYYY-MM-DD HH:mm:ss"
                  style="width: 100%"
                />
              </el-form-item>
            </el-col>
            <el-col :span="24">
              <el-form-item label="跟进记录">
                <el-input
                  v-model="trackForm.record"
                  type="textarea"
                  :rows="3"
                  placeholder="记录本次沟通情况"
                />
              </el-form-item>
            </el-col>
          </el-row>
          <el-form-item>
            <el-button type="primary" :loading="tracking" @click="handleTrack">提交跟进</el-button>
          </el-form-item>
        </el-form>
      </template>
    </el-drawer>
  </el-card>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { addClue, assignClue, getClueById, listClue, toBusiness, trackClue } from '@/api/clue'
import { listUserByRole } from '@/api/user'

// ---- 字典（后端用数字存，这里做展示映射）----
const CLUE_STATUS = [
  { value: 1, label: '待分配' },
  { value: 2, label: '待跟进' },
  { value: 3, label: '跟进中' }
]
const CHANNELS = [
  { value: 1, label: '线上活动' },
  { value: 2, label: '推广介绍' }
]
const SUBJECTS = [
  { value: 1, label: 'AI智能应用开发(Java)' },
  { value: 2, label: 'AI大模型开发(Python)' },
  { value: 3, label: 'AI鸿蒙开发' },
  { value: 4, label: 'AI大数据' },
  { value: 5, label: 'AI嵌入式' },
  { value: 6, label: 'AI测试' },
  { value: 7, label: 'AI运维' }
]
const LEVELS = [
  { value: 1, label: '近期学习' },
  { value: 2, label: '打算学习' },
  { value: 3, label: '进行了解' },
  { value: 4, label: '打酱油' }
]

function dictLabel(list, value) {
  if (value === null || value === undefined) return '-'
  const hit = list.find((i) => i.value === value)
  return hit ? hit.label : '-'
}

function statusTagType(status) {
  return { 1: 'warning', 2: 'primary', 3: 'success', 4: 'info', 5: 'danger' }[status] || 'info'
}

const loading = ref(false)
const saving = ref(false)
const tracking = ref(false)
const rows = ref([])
const total = ref(0)

const query = reactive({
  phone: '',
  status: null,
  channel: null,
  assignName: '',
  page: 1,
  pageSize: 10
})

// 新增表单
const addVisible = ref(false)
const addFormRef = ref()
const addForm = reactive({
  name: '',
  phone: '',
  gender: 1,
  age: 20,
  channel: null,
  wechat: '',
  qq: '',
  activityId: null
})
const addRules = {
  name: [{ required: true, message: '请输入客户姓名', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  channel: [{ required: true, message: '请选择渠道来源', trigger: 'change' }]
}

// 详情
const detailVisible = ref(false)
const detail = ref({})
const assignUserId = ref(null)
const lineUsers = ref([])
const trackForm = reactive({
  subject: null,
  level: null,
  nextTime: null,
  record: ''
})

async function loadData() {
  loading.value = true
  try {
    const res = await listClue(query)
    rows.value = res.data.rows || []
    total.value = res.data.total || 0
  } catch (e) {
    // 交给拦截器
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  query.page = 1
  loadData()
}

function handleReset() {
  query.phone = ''
  query.status = null
  query.channel = null
  query.assignName = ''
  handleSearch()
}

function openAddDialog() {
  addForm.name = ''
  addForm.phone = ''
  addForm.gender = 1
  addForm.age = 20
  addForm.channel = null
  addForm.wechat = ''
  addForm.qq = ''
  addForm.activityId = null
  addVisible.value = true
}

async function handleAdd() {
  try {
    await addFormRef.value.validate()
  } catch (e) {
    return
  }
  saving.value = true
  try {
    await addClue(addForm)
    ElMessage.success('新增成功')
    addVisible.value = false
    loadData()
  } catch (e) {
    // 手机号重复由拦截器提示
  } finally {
    saving.value = false
  }
}

async function openDetail(row) {
  const res = await getClueById(row.id)
  detail.value = res.data
  assignUserId.value = null
  trackForm.subject = res.data.subject
  trackForm.level = res.data.level
  trackForm.nextTime = res.data.nextTime
  trackForm.record = ''
  detailVisible.value = true
  // 线索专员下拉（懒加载，只在打开详情时拉一次）
  if (!lineUsers.value.length) {
    const userRes = await listUserByRole('line')
    lineUsers.value = userRes.data || []
  }
}

async function handleAssign() {
  try {
    await assignClue(detail.value.id, assignUserId.value)
    ElMessage.success('分配成功')
    openDetail({ id: detail.value.id })
    loadData()
  } catch (e) {
    // 交给拦截器
  }
}

async function handleTrack() {
  if (!trackForm.record) {
    ElMessage.warning('请填写跟进记录')
    return
  }
  tracking.value = true
  try {
    await trackClue({
      id: detail.value.id,
      subject: trackForm.subject,
      level: trackForm.level,
      nextTime: trackForm.nextTime,
      record: trackForm.record
    })
    ElMessage.success('跟进成功')
    openDetail({ id: detail.value.id })
    loadData()
  } catch (e) {
    // 交给拦截器
  } finally {
    tracking.value = false
  }
}

function handleToBusiness() {
  ElMessageBox.confirm('确定要把该线索转为商机吗？', '提示', { type: 'warning' })
    .then(async () => {
      await toBusiness(detail.value.id)
      ElMessage.success('已转为商机')
      detailVisible.value = false
      loadData()
    })
    .catch(() => {})
}

onMounted(loadData)
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

.action-bar {
  display: flex;
  gap: 10px;
  align-items: center;
  margin: 16px 0;
  padding: 12px;
  background: #f7f9fc;
  border-radius: 6px;
}

.section-title {
  margin: 18px 0 12px;
  padding-left: 8px;
  border-left: 3px solid #409eff;
  font-size: 15px;
}

.track-head {
  display: flex;
  gap: 8px;
  align-items: center;
  margin-bottom: 4px;
}

.track-user {
  font-weight: 600;
}

.track-record {
  color: #303133;
  margin: 4px 0;
}

.track-next {
  color: #909399;
  font-size: 12px;
}

.track-form {
  margin-top: 8px;
}
</style>
