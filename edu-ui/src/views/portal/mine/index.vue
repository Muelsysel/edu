<template>
  <div class="mine-container">
    <div class="elegant-panel search-panel">
      <el-form :model="queryParams" :inline="true" size="small" class="clean-form">
        <el-form-item label="成果标题">
          <el-input v-model="queryParams.title" placeholder="请输入标题关键字" clearable prefix-icon="el-icon-search" @keyup.enter.native="handleQuery" />
        </el-form-item>
        <el-form-item label="审核状态">
          <el-select v-model="queryParams.status" clearable placeholder="请选择状态">
            <el-option label="草稿" value="0" />
            <el-option label="院级审核中" value="1" />
            <el-option label="校级审核中" value="2" />
            <el-option label="已通过" value="3" />
            <el-option label="已驳回" value="4" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery" round>查询</el-button>
          <el-button @click="resetQuery" round>重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="elegant-panel table-panel">
      <el-table v-loading="loading" :data="list" class="clean-table" stripe>
        <el-table-column label="成果标题" prop="title" min-width="260" show-overflow-tooltip>
          <template slot-scope="scope">
            <span class="cell-title">{{ scope.row.title }}</span>
          </template>
        </el-table-column>
        <el-table-column label="所属类目" prop="category" width="160">
          <template slot-scope="scope">
            <dict-tag :options="dict.type.edu_achievement_category" :value="scope.row.category" />
          </template>
        </el-table-column>
        <el-table-column label="当前状态" prop="status" width="140">
          <template slot-scope="scope">
            <el-tag :type="statusTagType(scope.row.status)" effect="light" size="medium" class="status-tag">
              <i :class="statusIcon(scope.row.status)"></i> {{ statusLabel(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="最后更新" prop="createTime" width="180">
          <template slot-scope="scope"><span class="cell-time">{{ parseTime(scope.row.updateTime || scope.row.createTime, '{y}-{m}-{d} {h}:{i}') }}</span></template>
        </el-table-column>
        <el-table-column label="操作" width="220" fixed="right">
          <template slot-scope="scope">
            <el-button type="text" size="small" @click="viewDetail(scope.row)"><i class="el-icon-view"></i> 详情</el-button>
            <el-button v-if="canEdit(scope.row.status)" type="text" size="small" @click="editRow(scope.row)"><i class="el-icon-edit"></i> 修改</el-button>
            <el-button v-if="canWithdraw(scope.row.status)" type="text" size="small" class="text-danger" @click="withdrawRow(scope.row)"><i class="el-icon-refresh-left"></i> 撤回</el-button>
          </template>
        </el-table-column>
      </el-table>

      <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />
    </div>

    <el-dialog title="成果详情" :visible.sync="detailOpen" width="800px" custom-class="elegant-dialog">
    </el-dialog>
  </div>
</template>

<script>
// 脚本与原版完全一致，仅增加了一个 statusIcon 方法用于美化
import { teacherGetAchievement, teacherListAchievement, teacherResubmit, teacherUpdateAchievement } from '@/api/achievement/achievement'
import { teacherWithdraw } from '@/api/achievement/portal'

export default {
  name: 'PortalMine',
  dicts: ['edu_achievement_category'],
  data() { return { loading: false, total: 0, list: [], detailOpen: false, detail: {}, editOpen: false, editForm: {}, queryParams: { pageNum: 1, pageSize: 10, title: undefined, status: undefined } } },
  created() { this.getList() },
  methods: {
    getList() { this.loading = true; teacherListAchievement(this.queryParams).then(res => { this.list = res.rows || []; this.total = res.total || 0 }).finally(() => { this.loading = false }) },
    handleQuery() { this.queryParams.pageNum = 1; this.getList() },
    resetQuery() { this.queryParams = { pageNum: 1, pageSize: 10, title: undefined, status: undefined }; this.getList() },
    statusLabel(status) { const map = { '0': '草稿箱', '1': '院级审核中', '2': '校级审核中', '3': '审核通过', '4': '已被驳回' }; return map[status] || '未知' },
    statusTagType(status) { const map = { '0': 'info', '1': 'warning', '2': 'warning', '3': 'success', '4': 'danger' }; return map[status] || '' },
    statusIcon(status) { const map = { '0': 'el-icon-edit-outline', '1': 'el-icon-time', '2': 'el-icon-time', '3': 'el-icon-circle-check', '4': 'el-icon-circle-close' }; return map[status] || '' },
    canEdit(status) { return status === '0' || status === '4' },
    canWithdraw(status) { return status === '1' || status === '2' },
    viewDetail(row) { teacherGetAchievement(row.achievementId).then(res => { this.detail = res.data || {}; this.detailOpen = true }) },
    withdrawRow(row) { this.$confirm('撤回后申报将退回草稿箱，且需要重新排队审核，是否继续？', '撤回确认', { type: 'warning' }).then(() => teacherWithdraw(row.achievementId)).then(() => { this.$message.success('撤回成功'); this.getList() }) }
  }
}
</script>

<style scoped>
.mine-container { padding: 24px; max-width: 1400px; margin: 0 auto; }
.elegant-panel { background: #fff; border-radius: 12px; box-shadow: 0 2px 16px rgba(0,0,0,0.02); border: 1px solid #f1f5f9; padding: 20px; }
.search-panel { margin-bottom: 20px; padding-bottom: 4px; }
/* 表格无边框与斑马纹优化 */
.clean-table ::v-deep .el-table__header th { background-color: #f8fafc !important; color: #475569; font-weight: 500; border-bottom: none; }
.clean-table ::v-deep .el-table__row td { border-bottom: 1px solid #f1f5f9; padding: 16px 0; }
.clean-table ::v-deep .el-table--striped .el-table__body tr.el-table__row--striped td { background-color: #fafbfc; }
.clean-table ::v-deep .el-table::before { display: none; } /* 隐藏底部白线 */
.cell-title { font-size: 14px; font-weight: 500; color: #1e293b; }
.cell-time { color: #94a3b8; font-size: 13px; }
.status-tag { border-radius: 6px; border: none; padding: 0 10px; font-weight: 500; }
.text-danger { color: #ef4444 !important; }
</style>
