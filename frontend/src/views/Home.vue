<template>
  <div class="home">
    <h2 class="welcome">欢迎您，进入轻客管家系统</h2>

    <el-row :gutter="16">
      <el-col :span="12">
        <el-card shadow="never" class="mb">
          <template #header><span class="card-title">线索概览</span></template>
          <el-row :gutter="12">
            <el-col :span="8" v-for="item in clueItems" :key="item.label" class="stat-col">
              <div class="stat-box">
                <div class="stat-label">{{ item.label }}</div>
                <div class="stat-value">{{ item.value }}</div>
              </div>
            </el-col>
          </el-row>
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card shadow="never" class="mb">
          <template #header><span class="card-title">商机概览</span></template>
          <el-row :gutter="12">
            <el-col :span="8" v-for="item in businessItems" :key="item.label" class="stat-col">
              <div class="stat-box">
                <div class="stat-label">{{ item.label }}</div>
                <div class="stat-value">{{ item.value }}</div>
              </div>
            </el-col>
          </el-row>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { getOverview } from '@/api/report'

const overview = ref({})

onMounted(async () => {
  const res = await getOverview()
  overview.value = res.data || {}
})

// 后端字段是 snake_case 的驼峰形式，这里只做字段名到中文文案的映射
const clueItems = computed(() => [
  { label: '总线索数', value: overview.value.clueTotal ?? 0 },
  { label: '待分配线索数', value: overview.value.clueWaitAllot ?? 0 },
  { label: '待跟进线索数', value: overview.value.clueWaitFollow ?? 0 },
  { label: '跟进中线索数', value: overview.value.clueFollowing ?? 0 },
  { label: '伪线索数', value: overview.value.clueFalse ?? 0 },
  { label: '转商机线索数', value: overview.value.clueConvertBusiness ?? 0 }
])

const businessItems = computed(() => [
  { label: '总商机数', value: overview.value.businessTotal ?? 0 },
  { label: '待分配商机数', value: overview.value.businessWaitAllot ?? 0 },
  { label: '待跟进商机数', value: overview.value.businessWaitFollow ?? 0 },
  { label: '跟进中商机数', value: overview.value.businessFollowing ?? 0 },
  { label: '回收商机数', value: overview.value.businessFalse ?? 0 },
  { label: '转客户商机数', value: overview.value.businessConvertCustomer ?? 0 }
])
</script>

<style scoped>
.welcome {
  text-align: center;
  font-size: 24px;
  font-weight: 500;
  color: #303133;
  margin: 6px 0 18px;
}

.mb {
  margin-bottom: 4px;
}

.card-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

/* 每个统计格子等宽三列 */
.stat-col {
  margin-bottom: 16px;
}

.stat-box {
  border: 1px solid #dcdfe6;
  border-radius: 6px;
  text-align: center;
  padding: 12px 4px;
  background: #fff;
}

.stat-label {
  font-size: 13px;
  color: #606266;
}

.stat-value {
  margin-top: 6px;
  font-size: 24px;
  font-weight: 600;
  color: #409eff;
}
</style>
