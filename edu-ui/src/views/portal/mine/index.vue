<template>
  <div class="portal-mine-page">
    <section class="mine-head">
      <div>
        <span>My Applications</span>
        <h1>我的成果</h1>
        <p>查看申报记录、审核状态与归档结果。</p>
      </div>
      <el-button type="primary" icon="el-icon-edit-outline" @click="$router.push('/portal/declare')">新增申报</el-button>
    </section>

    <section class="filter-panel">
      <div class="status-tabs">
        <button :class="{ active: queryParams.status === undefined }" @click="switchStatus(undefined)">全部</button>
        <button :class="{ active: queryParams.status === '0' }" @click="switchStatus('0')">草稿</button>
        <button :class="{ active: queryParams.status === '2' }" @click="switchStatus('2')">审核中</button>
        <button :class="{ active: queryParams.status === '3' }" @click="switchStatus('3')">已通过</button>
        <button :class="{ active: queryParams.status === '4' }" @click="switchStatus('4')">被驳回</button>
      </div>

      <el-input
        v-model="queryParams.title"
        placeholder="搜索成果标题"
        clearable
        prefix-icon="el-icon-search"
        class="search-input"
        @keyup.enter.native="handleQuery"
        @clear="handleQuery"
      />
    </section>

    <section class="mine-list" v-loading="loading">
      <el-empty v-if="!list.length" description="暂无申报记录" />

      <article v-for="item in list" :key="item.achievementId" class="achievement-row">
        <div class="date-card">
          <strong>{{ parseTime(item.createTime, '{d}') || '--' }}</strong>
          <span>{{ parseTime(item.createTime, '{y}-{m}') || '---- --' }}</span>
        </div>

        <div class="achievement-main" @click="viewDetail(item)">
          <div class="title-line">
            <h3 :title="item.title">{{ item.title }}</h3>
            <span :class="['status-chip', 'status-' + item.status]">{{ statusLabel(item.status) }}</span>
          </div>

          <div class="meta-line">
            <dict-tag :options="dict.type.edu_achievement_category" :value="item.category" />
            <dict-tag :options="dict.type.edu_achievement_level" :value="item.level" />
            <span><i class="el-icon-office-building" /> {{ getDeptName(item.collegeId) }}</span>
            <span><i class="el-icon-time" /> {{ parseTime(item.createTime, '{y}-{m}-{d}') }}</span>
          </div>
        </div>

        <div class="row-actions">
          <el-button size="mini" plain @click="viewDetail(item)">查看</el-button>
          <el-button v-if="canEdit(item.status)" size="mini" type="primary" plain @click="editRow(item)">修改</el-button>
          <el-button v-if="canWithdraw(item.status)" size="mini" type="warning" plain @click="withdrawRow(item)">撤回</el-button>
        </div>
      </article>

      <pagination
        v-show="total > 0"
        :total="total"
        :page.sync="queryParams.pageNum"
        :limit.sync="queryParams.pageSize"
        @pagination="getList"
      />
    </section>

    <el-dialog
      :visible.sync="detailOpen"
      width="920px"
      custom-class="portal-detail-dialog"
      append-to-body
      destroy-on-close
    >
      <div slot="title" class="dialog-title">
        <span>成果详情</span>
        <em :class="['status-chip', 'status-' + detail.status]">{{ statusLabel(detail.status) }}</em>
      </div>

      <div v-loading="detailLoading" class="detail-body">
        <h2>{{ detail.title }}</h2>
        <div class="detail-meta">
          <dict-tag :options="dict.type.edu_achievement_category" :value="detail.category" />
          <dict-tag :options="dict.type.edu_achievement_level" :value="detail.level" />
          <span><i class="el-icon-office-building" /> {{ getDeptName(detail.collegeId) }}</span>
          <span><i class="el-icon-time" /> {{ parseTime(detail.createTime, '{y}-{m}-{d} {h}:{i}') }}</span>
        </div>

        <div class="detail-grid">
          <section class="detail-section">
            <h3>成果报告</h3>
            <div class="rich-content" v-html="detail.content || '暂无内容'"></div>
          </section>

          <aside class="side-section">
            <h3>审核进度</h3>
            <el-timeline>
              <el-timeline-item
                v-for="(node, index) in timelineNodes"
                :key="index"
                :color="node.color"
                :icon="node.icon"
              >
                <div class="timeline-node">
                  <strong>{{ node.name }}</strong>
                  <span v-if="node.statusLabel">{{ node.statusLabel }}</span>
                  <p v-if="node.time">{{ parseTime(node.time, '{y}-{m}-{d} {h}:{i}') }}</p>
                  <p v-if="node.comment">意见：{{ node.comment }}</p>
                </div>
              </el-timeline-item>
            </el-timeline>

            <div v-if="detail.fileUrl" class="file-list">
              <h3>附件材料</h3>
              <a v-for="(file, index) in parseFiles(detail.fileUrl)" :key="index" :href="file.url" target="_blank">
                <i class="el-icon-document" /> {{ file.name }}
              </a>
            </div>
          </aside>
        </div>

        <div class="dialog-actions" v-if="canEdit(detail.status)">
          <el-button type="primary" icon="el-icon-edit" @click="handleEditFromDetail(detail)">修改申报</el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { teacherGetAchievement, teacherListAchievement } from '@/api/achievement/achievement'
import { teacherWithdraw } from '@/api/achievement/portal'
import { getAuditProgress } from '@/api/achievement/audit'
import { listDept } from '@/api/system/dept'

export default {
  name: 'PortalMine',
  dicts: ['edu_achievement_category', 'edu_achievement_level'],
  data() {
    return {
      loading: false,
      detailLoading: false,
      total: 0,
      list: [],
      detailOpen: false,
      detail: {},
      auditRecords: [],
      deptList: [],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        title: undefined,
        status: undefined
      }
    }
  },
  computed: {
    timelineNodes() {
      const status = this.detail.status
      const records = this.auditRecords || []
      const findRecord = auditLevel => records.find(r => r.auditLevel === auditLevel)
      const nodes = []

      nodes.push({
        name: '教师提交',
        time: this.detail.createTime,
        color: '#15935f',
        icon: 'el-icon-check',
        statusLabel: '已提交'
      })

      const schoolRecord = findRecord('2')
      if (schoolRecord) {
        const isPass = schoolRecord.auditStatus === '1'
        nodes.push({
          name: '学校审核',
          time: schoolRecord.auditTime,
          auditor: schoolRecord.auditBy,
          color: isPass ? '#15935f' : '#d14545',
          icon: isPass ? 'el-icon-check' : 'el-icon-close',
          statusLabel: isPass ? '审核通过并归档' : '已驳回',
          comment: schoolRecord.auditComment
        })
      } else if (status === '2') {
        nodes.push({ name: '学校审核', color: '#0b4f93', icon: 'el-icon-loading', statusLabel: '审核中' })
      } else {
        nodes.push({ name: '学校审核', color: '#c7d3df', statusLabel: '待进入' })
      }
      return nodes
    }
  },
  created() {
    this.getList()
    this.getDeptList()
  },
  methods: {
    getList() {
      this.loading = true
      teacherListAchievement({ ...this.queryParams }).then(res => {
        this.list = res.rows || []
        this.total = res.total || 0
      }).finally(() => {
        this.loading = false
      })
    },
    getDeptList() {
      listDept().then(res => {
        this.deptList = res.data || []
      })
    },
    getDeptName(deptId) {
      if (!deptId) return '未知'
      const dept = this.deptList.find(d => d.deptId == deptId)
      return dept ? dept.deptName : deptId
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    switchStatus(status) {
      this.queryParams.status = status
      this.handleQuery()
    },
    statusLabel(status) {
      const map = { '0': '草稿', '2': '审核中', '3': '已通过', '4': '被驳回' }
      return map[status] || '未知'
    },
    canEdit(status) {
      return status === '0' || status === '4'
    },
    canWithdraw(status) {
      return status === '2'
    },
    viewDetail(row) {
      this.detailOpen = true
      this.detailLoading = true
      this.auditRecords = []
      Promise.all([
        teacherGetAchievement(row.achievementId),
        getAuditProgress(row.achievementId)
      ]).then(([detailRes, progressRes]) => {
        this.detail = detailRes.data || {}
        this.auditRecords = progressRes.rows || []
      }).finally(() => {
        this.detailLoading = false
      })
    },
    editRow(row) {
      this.$router.push({ path: '/portal/declare', query: { id: row.achievementId } })
    },
    handleEditFromDetail(detail) {
      this.detailOpen = false
      this.editRow(detail)
    },
    withdrawRow(row) {
      this.$confirm('撤回后申报将退回草稿状态，是否确认撤回？', '撤回确认', {
        confirmButtonText: '确认撤回',
        cancelButtonText: '取消',
        type: 'warning',
        customClass: 'elegant-confirm-box'
      }).then(() => teacherWithdraw(row.achievementId)).then(() => {
        this.$message.success('已成功撤回')
        this.getList()
      })
    },
    parseFiles(urlStr) {
      if (!urlStr) return []
      return urlStr.split(',').map(url => {
        const name = url.substring(url.lastIndexOf('/') + 1)
        return { name: name || '附件', url }
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.portal-mine-page {
  max-width: 1440px;
  margin: 0 auto;
  padding: 28px 20px 60px;
}

.mine-head {
  min-height: 168px;
  padding: 32px 38px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 22px;
  color: #173b63;
  background:
    linear-gradient(90deg, rgba(249, 253, 255, 0.95), rgba(231, 245, 255, 0.88)),
    url('~@/assets/images/portal-hero-ink.png') center 62% / cover;
  border: 1px solid #d7e6f4;
  border-bottom: 4px solid #d6a23a;
}

.mine-head span {
  display: block;
  margin-bottom: 10px;
  color: #0b5c95;
  font-size: 13px;
  text-transform: uppercase;
  letter-spacing: 1px;
}

.mine-head h1 {
  margin: 0;
  font-size: 32px;
}

.mine-head p {
  margin: 12px 0 0;
  color: #4f6f8b;
}

.mine-head .el-button {
  border-radius: 0;
  background: #fff8e6;
  border-color: #d6a23a;
  color: #8a5b08;
}

.filter-panel,
.mine-list {
  background: #fff;
  border: 1px solid #dbe6f2;
  box-shadow: 0 12px 28px rgba(18, 70, 122, 0.08);
}

.filter-panel {
  margin-top: 22px;
  padding: 18px 22px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 18px;
}

.status-tabs {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.status-tabs button {
  height: 34px;
  padding: 0 18px;
  border: 1px solid #cfddeb;
  background: #fff;
  color: #425b75;
  cursor: pointer;
}

.status-tabs button.active {
  background: #0b4f93;
  border-color: #0b4f93;
  color: #fff;
}

.search-input {
  width: 280px;
}

.search-input ::v-deep .el-input__inner {
  border-radius: 0;
}

.mine-list {
  margin-top: 18px;
  padding: 6px 28px 28px;
}

.achievement-row {
  display: grid;
  grid-template-columns: 78px minmax(0, 1fr) 250px;
  gap: 20px;
  align-items: center;
  padding: 22px 0;
  border-bottom: 1px solid #edf2f7;
}

.date-card {
  width: 68px;
  height: 74px;
  border: 1px solid #dbe6f2;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: #f8fbff;
  color: #0b4f93;
}

.date-card strong {
  font-size: 28px;
  line-height: 1;
}

.date-card span {
  margin-top: 8px;
  font-size: 12px;
  color: #6b7d91;
}

.achievement-main {
  min-width: 0;
  cursor: pointer;
}

.title-line {
  display: flex;
  align-items: center;
  gap: 12px;
}

.title-line h3 {
  margin: 0;
  color: #173b63;
  font-size: 19px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.meta-line {
  margin-top: 13px;
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  color: #70869b;
  font-size: 13px;
}

.status-chip {
  flex: 0 0 auto;
  padding: 4px 9px;
  font-size: 12px;
  font-style: normal;
  border: 1px solid #cfddeb;
  color: #536b82;
  background: #f7fbff;
}

.status-0 {
  color: #8a5b08;
  border-color: #e8c978;
  background: #fff8e6;
}

.status-2 {
  color: #0b4f93;
  border-color: #9bc3e9;
  background: #edf6ff;
}

.status-3 {
  color: #187048;
  border-color: #9ed7bf;
  background: #eefbf5;
}

.status-4 {
  color: #a73535;
  border-color: #efb0b0;
  background: #fff1f1;
}

.row-actions {
  text-align: right;
}

.row-actions .el-button,
.dialog-actions .el-button {
  border-radius: 0;
}

::v-deep .pagination-container {
  margin-top: 24px;
}

::v-deep .portal-detail-dialog {
  width: min(1180px, calc(100vw - 48px)) !important;
}

::v-deep .portal-detail-dialog .el-dialog__body {
  padding: 24px 32px 32px;
}

.dialog-title {
  display: flex;
  align-items: center;
  gap: 12px;
  color: #0d3564;
  font-weight: 700;
}

.detail-body h2 {
  margin: 0;
  color: #0d3564;
  font-size: 24px;
}

.detail-meta {
  margin: 16px 0 24px;
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  color: #70869b;
}

.detail-grid {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 280px;
  gap: 26px;
}

.detail-section,
.side-section {
  border-top: 2px solid #e7eef7;
  padding-top: 18px;
}

.detail-section h3,
.side-section h3 {
  margin: 0 0 14px;
  color: #0b4f93;
}

.rich-content {
  color: #24384d;
  line-height: 1.85;
}

.rich-content ::v-deep img {
  max-width: 100%;
}

.timeline-node strong {
  display: block;
  color: #173b63;
}

.timeline-node span,
.timeline-node p {
  margin: 5px 0 0;
  color: #6b7d91;
  font-size: 13px;
}

.file-list {
  margin-top: 24px;
}

.file-list a {
  display: block;
  padding: 10px 0;
  color: #0b4f93;
  border-bottom: 1px solid #edf2f7;
}

.dialog-actions {
  margin-top: 24px;
  text-align: right;
}

@media (max-width: 900px) {
  .mine-head,
  .filter-panel {
    display: block;
  }

  .mine-head .el-button,
  .search-input {
    width: 100%;
    margin-top: 18px;
  }

  .achievement-row {
    grid-template-columns: 64px minmax(0, 1fr);
  }

  .row-actions {
    grid-column: 1 / -1;
    text-align: left;
  }

  .detail-grid {
    grid-template-columns: 1fr;
  }
}
</style>
