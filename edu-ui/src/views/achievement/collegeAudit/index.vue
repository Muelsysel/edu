<template>
  <div class="portal-audit-page">
    <section class="audit-head">
      <div>
        <span>Audit Center</span>
        <h1>审核工作台</h1>
        <p>集中处理待审核的教学成果申报。</p>
      </div>
      <div class="head-stats">
        <div>
          <strong>{{ stats.totalPending || 0 }}</strong>
          <span>待审核</span>
        </div>
        <div>
          <strong>{{ stats.todayNew || 0 }}</strong>
          <span>今日新增</span>
        </div>
        <div>
          <strong>{{ stats.monthPassed || 0 }}</strong>
          <span>本月通过</span>
        </div>
      </div>
    </section>

    <section class="audit-filter">
      <el-select v-model="queryParams.collegeId" placeholder="学院" clearable filterable @change="handleQuery">
        <el-option v-for="item in collegeOptions" :key="item.deptId" :label="item.deptName" :value="item.deptId" />
      </el-select>
      <el-select v-model="queryParams.category" placeholder="成果类型" clearable @change="handleQuery">
        <el-option v-for="dict in dict.type.edu_achievement_category" :key="dict.value" :label="dict.label" :value="dict.value" />
      </el-select>
      <el-input
        v-model="queryParams.title"
        placeholder="搜索成果标题"
        clearable
        prefix-icon="el-icon-search"
        @keyup.enter.native="handleQuery"
        @clear="handleQuery"
      />
      <el-button type="primary" icon="el-icon-search" @click="handleQuery">查询</el-button>
      <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
      <el-button
        type="success"
        icon="el-icon-video-play"
        :disabled="loading || achievementList.length === 0"
        @click="startSequentialAudit"
      >
        开始审核
      </el-button>
    </section>

    <section class="task-board" v-loading="loading">
      <el-empty v-if="achievementList.length === 0" description="暂无待审核成果" />

      <article v-for="item in achievementList" :key="item.achievementId" class="task-row" @click="openReviewMode(item)">
        <div class="teacher-avatar">{{ (item.teacherName || 'T').charAt(0) }}</div>
        <div class="task-main">
          <h3 :title="item.title">{{ item.title }}</h3>
          <div class="task-meta">
            <span><i class="el-icon-user" /> {{ item.teacherName || '未知教师' }}</span>
            <span><i class="el-icon-office-building" /> {{ collegeNameById(item.collegeId) }}</span>
            <dict-tag :options="dict.type.edu_achievement_category" :value="item.category" />
            <dict-tag :options="dict.type.edu_achievement_level" :value="item.level" />
            <span><i class="el-icon-time" /> {{ parseTime(item.createTime, '{y}-{m}-{d}') }}</span>
          </div>
        </div>
        <el-button type="primary" plain size="small" @click.stop="openReviewMode(item)">办理</el-button>
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
      :visible.sync="reviewOpen"
      fullscreen
      custom-class="portal-review-dialog"
      :show-close="false"
      destroy-on-close
    >
      <div class="review-layout" v-loading="detailLoading">
        <header class="review-header">
          <el-button icon="el-icon-arrow-left" plain @click="closeReview">返回</el-button>
          <div>
            <span>审核办理</span>
            <h2>{{ viewForm.title }}</h2>
          </div>
          <div class="sequence-pill" v-if="batchMode">
            第 {{ currentReviewIndex + 1 }} / {{ reviewQueue.length }} 项
          </div>
          <div class="header-placeholder" v-else></div>
        </header>

        <main class="review-body">
          <section class="document-pane">
            <h1>{{ viewForm.title }}</h1>
            <div class="doc-meta">
              <span><i class="el-icon-user" /> {{ viewForm.teacherName || '-' }}</span>
              <span><i class="el-icon-office-building" /> {{ collegeNameById(viewForm.collegeId) }}</span>
              <dict-tag :options="dict.type.edu_achievement_category" :value="viewForm.category" />
              <dict-tag :options="dict.type.edu_achievement_level" :value="viewForm.level" />
            </div>

            <div class="doc-section">
              <h3>成果报告</h3>
              <div class="rich-content" v-html="viewForm.content || '暂无内容'"></div>
            </div>

            <div class="doc-section" v-if="viewForm.fileUrl">
              <h3>附件材料</h3>
              <div class="file-grid">
                <a v-for="(url, index) in parseFiles(viewForm.fileUrl)" :key="index" :href="url" target="_blank">
                  <i class="el-icon-document" />
                  <span>{{ url.substring(url.lastIndexOf('/') + 1) || '附件' + (index + 1) }}</span>
                </a>
              </div>
            </div>
          </section>

          <aside class="decision-pane">
            <section class="side-card">
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
            </section>

            <section class="side-card decision-card">
              <h3>审核意见</h3>
              <el-form ref="auditForm" :model="auditForm" :rules="auditRules">
                <el-form-item prop="auditOpinion">
                  <el-input
                    v-model="auditForm.auditOpinion"
                    type="textarea"
                    :rows="5"
                    placeholder="请输入审核意见"
                    maxlength="500"
                    show-word-limit
                  />
                </el-form-item>
                <div class="decision-actions">
                  <el-button :loading="submitLoading" @click="executeAudit('2')">驳回</el-button>
                  <el-button type="primary" :loading="submitLoading" @click="executeAudit('1')">通过</el-button>
                </div>
                <el-button
                  v-if="batchMode"
                  class="skip-btn"
                  plain
                  :disabled="submitLoading || detailLoading"
                  @click="goNextReview(true)"
                >
                  跳过当前，审核下一项
                </el-button>
              </el-form>
            </section>
          </aside>
        </main>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import { isSchoolAuditor } from '@/utils/role-route'
import { schoolAuditList, schoolAuditHandle, getAuditDetail, getStatistics, getAuditProgress } from '@/api/achievement/audit'
import { listDept } from '@/api/system/dept'

export default {
  name: 'ConsumerAuditInbox',
  dicts: ['edu_achievement_category', 'edu_achievement_level'],
  data() {
    return {
      activeTab: 'school',
      loading: true,
      detailLoading: false,
      submitLoading: false,
      total: 0,
      achievementList: [],
      collegeOptions: [],
      stats: { totalPending: 0, todayNew: 0, weekNew: 0, monthPassed: 0 },
      queryParams: { pageNum: 1, pageSize: 12, title: undefined, category: undefined, collegeId: undefined },
      reviewOpen: false,
      batchMode: false,
      reviewQueue: [],
      currentReviewIndex: 0,
      viewForm: {},
      auditRecords: [],
      auditForm: { achievementId: null, achievementTitle: '', auditResult: null, auditOpinion: '' },
      auditRules: {
        auditOpinion: [{
          validator: (rule, value, callback) => {
            if (this.auditForm.auditResult === '2' && !value) callback(new Error('驳回时必须填写审核意见'))
            else callback()
          },
          trigger: 'blur'
        }]
      }
    }
  },
  computed: {
    ...mapGetters(['roles', 'name', 'nickName']),
    isSchoolRole() {
      return isSchoolAuditor(this.roles) || this.roles.includes('auditor') || this.roles.includes('admin')
    },
    timelineNodes() {
      const status = this.viewForm.status
      const records = this.auditRecords || []
      const findRecord = lvl => records.find(r => r.auditLevel === lvl)
      const nodes = []

      nodes.push({ name: '教师提交', time: this.viewForm.createTime, color: '#15935f', icon: 'el-icon-check', statusLabel: '已提交' })

      const sr = findRecord('2')
      if (sr) {
        const isPass = sr.auditStatus === '1'
        nodes.push({
          name: '学校审核',
          time: sr.auditTime,
          color: isPass ? '#15935f' : '#d14545',
          icon: isPass ? 'el-icon-check' : 'el-icon-close',
          statusLabel: isPass ? '审核通过' : '已驳回',
          comment: sr.auditComment
        })
      } else if (status === '2') {
        nodes.push({ name: '学校审核', color: '#0b4f93', icon: 'el-icon-loading', statusLabel: '当前阶段' })
      } else {
        nodes.push({ name: '学校审核', color: '#c7d3df' })
      }
      return nodes
    }
  },
  created() {
    this.initActiveTab()
    this.getCollegeList()
    this.getList()
    this.fetchStats()
  },
  watch: {
    '$route.path'() {
      this.resetQuery()
    }
  },
  methods: {
    initActiveTab() {
      this.activeTab = 'school'
    },
    fetchStats() {
      getStatistics({}).then(res => {
        this.stats = res || {}
      }).catch(() => {})
    },
    getList() {
      this.loading = true
      schoolAuditList(this.queryParams).then(res => {
        this.achievementList = res.rows || []
        this.total = res.total || 0
      }).finally(() => {
        this.loading = false
      })
    },
    getCollegeList() {
      listDept().then(res => {
        this.collegeOptions = res.data || []
      })
    },
    collegeNameById(id) {
      if (!id) return '-'
      const c = this.collegeOptions.find(item => item.deptId == id)
      return c ? c.deptName : id
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.queryParams = { pageNum: 1, pageSize: 12, title: undefined, category: undefined, collegeId: undefined }
      this.handleQuery()
    },
    closeReview() {
      this.reviewOpen = false
      this.batchMode = false
      this.reviewQueue = []
      this.currentReviewIndex = 0
    },
    startSequentialAudit() {
      if (this.achievementList.length === 0) {
        this.$message.info('当前没有待审核成果')
        return
      }
      this.batchMode = true
      this.reviewQueue = [...this.achievementList]
      this.currentReviewIndex = 0
      this.openReviewMode(this.reviewQueue[0], true)
    },
    openReviewMode(row, fromBatch = false) {
      if (!fromBatch) {
        this.batchMode = false
        this.reviewQueue = []
        this.currentReviewIndex = 0
      }
      this.reviewOpen = true
      this.detailLoading = true
      this.auditRecords = []
      this.auditForm = { achievementId: row.achievementId, achievementTitle: row.title, auditResult: null, auditOpinion: '' }

      Promise.all([
        getAuditDetail(row.achievementId),
        getAuditProgress(row.achievementId)
      ]).then(([dRes, pRes]) => {
        this.viewForm = dRes.data || {}
        this.auditRecords = pRes.rows || []
      }).finally(() => {
        this.detailLoading = false
      })
    },
    goNextReview(allowFinishMessage = false) {
      if (!this.batchMode) {
        this.reviewOpen = false
        return
      }

      const nextIndex = this.currentReviewIndex + 1
      if (nextIndex >= this.reviewQueue.length) {
        this.batchMode = false
        this.reviewQueue = []
        this.currentReviewIndex = 0
        this.reviewOpen = false
        this.getList()
        this.fetchStats()
        if (allowFinishMessage) {
          this.$message.success('本页待审核成果已处理完')
        }
        return
      }

      this.currentReviewIndex = nextIndex
      this.openReviewMode(this.reviewQueue[this.currentReviewIndex], true)
    },
    executeAudit(resultType) {
      this.auditForm.auditResult = resultType
      this.$refs.auditForm.validate(valid => {
        if (!valid) return
        const actionText = resultType === '1' ? '通过' : '驳回'

        this.$confirm(`确认${actionText}该申报吗？`, '办理确认', {
          confirmButtonText: '确认办理',
          cancelButtonText: '取消',
          type: resultType === '1' ? 'success' : 'warning',
          customClass: 'elegant-confirm-box'
        }).then(() => {
          this.submitLoading = true
          schoolAuditHandle(this.auditForm).then(() => {
            this.$message.success('处理完成')
            if (this.batchMode) {
              this.goNextReview(true)
            } else {
              this.reviewOpen = false
              this.getList()
              this.fetchStats()
            }
          }).finally(() => {
            this.submitLoading = false
          })
        }).catch(() => {})
      })
    },
    parseFiles(urlStr) {
      if (!urlStr) return []
      return urlStr.split(',').filter(u => u)
    }
  }
}
</script>

<style lang="scss" scoped>
.portal-audit-page {
  max-width: 1440px;
  margin: 0 auto;
  padding: 28px 20px 60px;
}

.audit-head {
  min-height: 176px;
  padding: 32px 38px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 24px;
  color: #173b63;
  background:
    linear-gradient(90deg, rgba(249, 253, 255, 0.95), rgba(231, 245, 255, 0.88)),
    url('~@/assets/images/portal-hero-ink.png') center 62% / cover;
  border: 1px solid #d7e6f4;
  border-bottom: 4px solid #d6a23a;
}

.audit-head span:first-child {
  display: block;
  margin-bottom: 10px;
  color: #0b5c95;
  font-size: 13px;
  text-transform: uppercase;
  letter-spacing: 1px;
}

.audit-head h1 {
  margin: 0;
  font-size: 32px;
}

.audit-head p {
  margin: 12px 0 0;
  color: #4f6f8b;
}

.head-stats {
  display: grid;
  grid-template-columns: repeat(3, 96px);
  gap: 1px;
  background: #d7e6f4;
  border: 1px solid #d7e6f4;
}

.head-stats div {
  padding: 16px 10px;
  background: rgba(255, 255, 255, 0.8);
  text-align: center;
}

.head-stats strong {
  display: block;
  font-size: 26px;
}

.head-stats span {
  display: block;
  margin-top: 6px;
  color: #6c8299;
  font-size: 13px;
}

.audit-filter,
.task-board {
  background: #fff;
  border: 1px solid #dbe6f2;
  box-shadow: 0 12px 28px rgba(18, 70, 122, 0.08);
}

.audit-filter {
  margin-top: 22px;
  padding: 18px 22px;
  display: grid;
  grid-template-columns: 170px 160px minmax(220px, 1fr) auto auto auto;
  gap: 12px;
}

.audit-filter ::v-deep .el-input__inner,
.audit-filter .el-button {
  border-radius: 0;
}

.task-board {
  margin-top: 18px;
  padding: 6px 28px 28px;
}

.task-row {
  display: grid;
  grid-template-columns: 54px minmax(0, 1fr) 90px;
  align-items: center;
  gap: 18px;
  padding: 22px 0;
  border-bottom: 1px solid #edf2f7;
  cursor: pointer;
}

.task-row:hover {
  background: #f8fbff;
}

.teacher-avatar {
  width: 46px;
  height: 46px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #0b4f93;
  color: #fff;
  font-size: 20px;
  font-weight: 700;
}

.task-main {
  min-width: 0;
}

.task-main h3 {
  margin: 0;
  color: #173b63;
  font-size: 19px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.task-meta {
  margin-top: 12px;
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  color: #70869b;
  font-size: 13px;
}

.review-layout {
  min-height: 100vh;
  background: #f3f7fb;
}

.review-header {
  height: 86px;
  padding: 0 28px;
  display: grid;
  grid-template-columns: 120px minmax(0, 1fr) 120px;
  align-items: center;
  background: #fff;
  border-bottom: 1px solid #dbe6f2;
}

.review-header span {
  color: #6b7d91;
  font-size: 13px;
}

.review-header h2 {
  margin: 6px 0 0;
  color: #0d3564;
  font-size: 22px;
  text-align: center;
}

.sequence-pill {
  justify-self: end;
  padding: 8px 14px;
  border: 1px solid #cfddeb;
  background: #f8fbff;
  color: #0b4f93;
  font-weight: 600;
}

.review-body {
  max-width: 1480px;
  margin: 0 auto;
  padding: 26px 20px 50px;
  display: grid;
  grid-template-columns: minmax(0, 1fr) 380px;
  gap: 22px;
}

.document-pane,
.side-card {
  background: #fff;
  border: 1px solid #dbe6f2;
  box-shadow: 0 10px 24px rgba(18, 70, 122, 0.06);
}

.document-pane {
  padding: 36px 44px;
}

.document-pane h1 {
  margin: 0;
  color: #0d3564;
  font-size: 28px;
}

.doc-meta {
  margin: 18px 0 28px;
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  color: #70869b;
}

.doc-section {
  padding-top: 20px;
  border-top: 1px solid #e7eef7;
}

.doc-section + .doc-section {
  margin-top: 24px;
}

.doc-section h3,
.side-card h3 {
  margin: 0 0 16px;
  color: #0b4f93;
}

.rich-content {
  color: #24384d;
  line-height: 1.9;
}

.rich-content ::v-deep img {
  max-width: 100%;
}

.file-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 12px;
}

.file-grid a {
  padding: 13px 14px;
  color: #0b4f93;
  border: 1px solid #dbe6f2;
  background: #f8fbff;
}

.decision-pane {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.side-card {
  padding: 22px;
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

.decision-card ::v-deep .el-textarea__inner {
  border-radius: 0;
}

.decision-actions {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.decision-actions .el-button {
  border-radius: 0;
}

.decision-actions .el-button--primary {
  background: #0b4f93;
  border-color: #0b4f93;
}

.skip-btn {
  width: 100%;
  margin: 12px 0 0;
  border-radius: 0;
}

@media (max-width: 980px) {
  .audit-head,
  .audit-filter {
    display: block;
  }

  .head-stats {
    margin-top: 20px;
  }

  .audit-filter > * {
    width: 100%;
    margin-bottom: 10px;
  }

  .review-body {
    grid-template-columns: 1fr;
  }
}
</style>
