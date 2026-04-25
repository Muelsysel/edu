<template>
  <div class="consumer-audit-inbox">
    <header class="inbox-header">
      <div class="greeting-box">
        <h1>您好，{{ nickName || name }}专家 👋</h1>
        <p>这是您的专属审核工作台，今天有 <strong class="text-blue">{{ stats.totalPending || 0 }}</strong> 项教学成果等待您的审阅。</p>
      </div>
      <div class="stats-pills">
        <div class="stat-pill">
          <span class="label">今日新增</span>
          <span class="value">{{ stats.todayNew || 0 }}</span>
        </div>
        <div class="stat-pill">
          <span class="label">本月已审</span>
          <span class="value text-green">{{ stats.monthPassed || 0 }}</span>
        </div>
      </div>
    </header>

    <div class="inbox-toolbar">
      <div class="capsule-tabs">
        <div class="capsule active">
          <i class="el-icon-office-building"></i> 审核队列
        </div>
      </div>

      <div class="filter-group">
        <el-select v-if="activeTab === 'school'" v-model="queryParams.collegeId" placeholder="所有学院" clearable class="round-select" @change="handleQuery">
          <el-option v-for="item in collegeOptions" :key="item.deptId" :label="item.deptName" :value="item.deptId" />
        </el-select>

        <el-select v-model="queryParams.category" placeholder="所有类别" clearable class="round-select" @change="handleQuery">
          <el-option v-for="dict in dict.type.edu_achievement_category" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>

        <el-input v-model="queryParams.title" placeholder="检索成果名称..." clearable class="round-input" @keyup.enter.native="handleQuery" @clear="handleQuery">
          <i slot="prefix" class="el-input__icon el-icon-search"></i>
        </el-input>
      </div>
    </div>

    <div class="task-grid" v-loading="loading">
      <el-empty v-if="achievementList.length === 0" description="太棒了！您的待办队列已清空" :image-size="160"></el-empty>

      <div
        v-for="item in achievementList"
        :key="item.achievementId"
        class="task-card"
        @click="openReviewMode(item)"
      >
        <div class="card-top">
          <div class="teacher-avatar">{{ (item.teacherName || 'T').charAt(0) }}</div>
          <div class="teacher-info">
            <span class="name">{{ item.teacherName || '匿名申报人' }}</span>
            <span class="time">{{ parseTime(item.createTime, '{m}-{d} {h}:{i}') }} 提交</span>
          </div>
          <div class="card-action">
            <el-button type="primary" round size="small" class="process-btn">去审阅</el-button>
          </div>
        </div>

        <h3 class="task-title" :title="item.title">{{ item.title }}</h3>

        <div class="task-tags">
          <dict-tag :options="dict.type.edu_achievement_level" :value="item.level" class="tag-level"/>
          <dict-tag :options="dict.type.edu_achievement_category" :value="item.category" class="tag-category"/>
          <span class="" v-if="activeTab === 'school'">
            <i class="el-icon-location-information"></i> {{ collegeNameById(item.collegeId) }}
          </span>
        </div>
      </div>
    </div>

    <div class="pagination-wrapper" v-show="total > 0">
      <pagination :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />
    </div>

    <el-dialog
      :visible.sync="reviewOpen"
      fullscreen
      custom-class="immersive-review-dialog"
      :show-close="false"
      destroy-on-close
    >
      <div class="review-layout" v-loading="detailLoading">

        <div class="review-header">
          <div class="back-btn" @click="reviewOpen = false">
            <i class="el-icon-back"></i> 返回收件箱
          </div>
          <div class="review-title">
            <span class="badge">当前审阅</span> {{ viewForm.title }}
          </div>
          <div class="header-right"></div>
        </div>

        <div class="review-body">
          <div class="reader-pane">
            <div class="reader-content">
              <h1 class="doc-title">{{ viewForm.title }}</h1>
              <div class="doc-meta">
                <span class="meta-item"><i class="el-icon-user"></i> {{ viewForm.teacherName || '-' }}</span>
                <span class="meta-item"><i class="el-icon-school"></i> {{ collegeNameById(viewForm.collegeId) }}</span>
                <dict-tag :options="dict.type.edu_achievement_category" :value="viewForm.category" class="meta-tag"/>
                <dict-tag :options="dict.type.edu_achievement_level" :value="viewForm.level" class="meta-tag"/>
              </div>

              <div class="doc-section">
                <h3>成果详细报告</h3>
                <div class="rich-text-content" v-html="viewForm.content"></div>
              </div>

              <div class="doc-section" v-if="viewForm.fileUrl">
                <h3>佐证附件</h3>
                <div class="file-gallery">
                  <a v-for="(url, index) in parseFiles(viewForm.fileUrl)" :key="index" :href="url" target="_blank" class="file-card">
                    <div class="file-icon"><i class="el-icon-document"></i></div>
                    <div class="file-name">{{ url.substring(url.lastIndexOf('/') + 1) || `附件材料_${index+1}` }}</div>
                  </a>
                </div>
              </div>
            </div>
          </div>

          <div class="action-pane">

            <div class="workflow-card">
              <h3><i class="el-icon-position"></i> 审核流转追踪</h3>
              <el-timeline class="compact-timeline">
                <el-timeline-item v-for="(node, index) in timelineNodes" :key="index" :color="node.color" :icon="node.icon">
                  <div class="timeline-node">
                    <div class="node-title">{{ node.name }}</div>
                    <div class="node-time" v-if="node.time">{{ parseTime(node.time, '{y}-{m}-{d} {h}:{i}') }}</div>
                    <div class="node-status" :class="node.statusClass" v-if="node.statusLabel">{{ node.statusLabel }}</div>
                    <div class="node-comment" v-if="node.comment">"{{ node.comment }}"</div>
                  </div>
                </el-timeline-item>
              </el-timeline>
            </div>

            <div class="decision-card">
              <h3><i class="el-icon-edit-outline"></i> 您的审批意见</h3>
              <el-form ref="auditForm" :model="auditForm" :rules="auditRules">
                <el-form-item prop="auditOpinion">
                  <el-input
                    v-model="auditForm.auditOpinion"
                    type="textarea"
                    :rows="5"
                    placeholder="请填写审批意见（驳回时必填）..."
                    maxlength="500"
                    show-word-limit
                    class="decision-textarea"
                  />
                </el-form-item>

                <div class="decision-actions">
                  <el-button
                    class="decision-btn btn-reject"
                    :loading="submitLoading"
                    @click="executeAudit('2')"
                  >
                    <i class="el-icon-close"></i> 予以驳回
                  </el-button>

                  <el-button
                    type="primary"
                    class="decision-btn btn-pass"
                    :loading="submitLoading"
                    @click="executeAudit('1')"
                  >
                    <i class="el-icon-check"></i> 同意通过
                  </el-button>
                </div>
              </el-form>
            </div>

          </div>
        </div>
      </div>
    </el-dialog>

  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import { isSchoolAuditor } from '@/utils/role-route'
import { schoolAuditList, schoolAuditHandle, getAuditDetail, getStatistics, getAuditProgress } from "@/api/achievement/audit";
import { listDept } from "@/api/system/dept";

export default {
  name: "ConsumerAuditInbox",
  dicts: ['edu_achievement_category', 'edu_achievement_level'],
  data() {
    return {
      activeTab: 'school',
      loading: true, detailLoading: false, submitLoading: false,
      total: 0,
      achievementList: [],
      collegeOptions: [],
      stats: { totalPending: 0, todayNew: 0, weekNew: 0, monthPassed: 0 },
      queryParams: { pageNum: 1, pageSize: 12, title: undefined, category: undefined, collegeId: undefined },

      reviewOpen: false,
      viewForm: {},
      auditRecords: [],

      auditForm: { achievementId: null, achievementTitle: '', auditResult: null, auditOpinion: '' },
      auditRules: {
        // 动态校验：如果不通过（2），必须填写意见
        auditOpinion: [{
          validator: (rule, value, callback) => {
            if (this.auditForm.auditResult === '2' && !value) callback(new Error('驳回时必须填写审批意见'));
            else callback();
          },
          trigger: 'blur'
        }]
      }
    };
  },
  computed: {
    ...mapGetters(['roles', 'name', 'nickName']),
    isSchoolRole() { return isSchoolAuditor(this.roles) || this.roles.includes('auditor') || this.roles.includes('admin'); },

    // 生成时间线逻辑
    timelineNodes() {
      const status = this.viewForm.status;
      const records = this.auditRecords || [];
      const findRecord = (lvl) => records.find(r => r.auditLevel === lvl);
      let nodes = [];

      nodes.push({ name: '教师发起申报', time: this.viewForm.createTime, color: '#10b981', icon: 'el-icon-check', statusLabel: '已提交', statusClass: 'text-green' });

      const sr = findRecord('2');
      if (sr) {
        const isPass = sr.auditStatus === '1';
        nodes.push({ name: '审核', time: sr.auditTime, color: isPass ? '#10b981' : '#ef4444', icon: isPass ? 'el-icon-check' : 'el-icon-close', statusLabel: isPass ? '审核通过' : '已驳回', statusClass: isPass ? 'text-green' : 'text-red', comment: sr.auditComment });
      } else {
        if (status === '2') nodes.push({ name: '审核 (当前阶段)', color: '#2563eb', icon: 'el-icon-loading' });
        else nodes.push({ name: '审核', color: '#cbd5e1' });
      }
      return nodes;
    }
  },
  created() {
    this.initActiveTab();
    this.getCollegeList();
    this.getList();
    this.fetchStats();
  },
  watch: {
    '$route.path'(newPath) {
      this.resetQuery();
    }
  },
  methods: {
    initActiveTab() {
      this.activeTab = 'school';
    },
    fetchStats() { getStatistics({}).then(res => { this.stats = res || {}; }).catch(()=>{}); },
    getList() {
      this.loading = true;
      schoolAuditList(this.queryParams).then(res => {
        this.achievementList = res.rows || [];
        this.total = res.total || 0;
      }).finally(() => { this.loading = false; });
    },
    getCollegeList() { listDept().then(res => { this.collegeOptions = res.data || []; }); },
    collegeNameById(id) {
      if (!id) return '-';
      const c = this.collegeOptions.find(item => item.deptId == id);
      return c ? c.deptName : id;
    },
    handleQuery() { this.queryParams.pageNum = 1; this.getList(); },
    resetQuery() {
      this.queryParams = { pageNum: 1, pageSize: 12, title: undefined, category: undefined, collegeId: undefined };
      this.handleQuery();
    },

    // 【核心】打开沉浸式审阅模式
    openReviewMode(row) {
      this.reviewOpen = true;
      this.detailLoading = true;
      this.auditRecords = [];
      this.auditForm = { achievementId: row.achievementId, achievementTitle: row.title, auditResult: null, auditOpinion: '' };

      Promise.all([ getAuditDetail(row.achievementId), getAuditProgress(row.achievementId) ]).then(([dRes, pRes]) => {
        this.viewForm = dRes.data || {};
        this.auditRecords = pRes.rows || [];
      }).finally(() => { this.detailLoading = false; });
    },

    // 【核心】执行审批动作
    executeAudit(resultType) {
      this.auditForm.auditResult = resultType;
      this.$refs["auditForm"].validate(valid => {
        if (!valid) return;
        const actionText = resultType === '1' ? '同意通过' : '予以驳回';

        this.$confirm(`您确定【${actionText}】该申报吗？`, '办理确认', {
          confirmButtonText: '确认办理', cancelButtonText: '取消',
          type: resultType === '1' ? 'success' : 'warning',
          customClass: 'elegant-confirm-box'
        }).then(() => {
          this.submitLoading = true;
          schoolAuditHandle(this.auditForm).then(() => {
            this.$message.success("处理完成！");
            this.reviewOpen = false;
            this.getList();
            this.fetchStats();
          }).finally(() => { this.submitLoading = false; });
        }).catch(() => {});
      });
    },

    parseFiles(urlStr) {
      if (!urlStr) return [];
      return urlStr.split(',').filter(u => u);
    }
  }
};
</script>

<style lang="scss" scoped>
/* ================= C端风格色彩系统 ================= */
$primary-blue: #2563eb;
$bg-light: #f4f7fb;
$text-dark: #0f172a;
$text-normal: #334155;
$text-light: #64748b;
$success: #10b981;
$danger: #ef4444;

.consumer-audit-inbox {
  max-width: 1320px;
  margin: 0 auto;
  padding: 32px 24px;
  min-height: 80vh;
}

/* 头部收件箱风格 */
.inbox-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 40px;
}
.greeting-box h1 { font-size: 32px; color: $text-dark; margin: 0 0 8px; font-weight: 700; letter-spacing: -0.5px;}
.greeting-box p { font-size: 15px; color: $text-light; margin: 0; }
.text-blue { color: $primary-blue; }
.text-green { color: $success; }
.text-red { color: $danger; }

.stats-pills { display: flex; gap: 16px; }
.stat-pill {
  background: #fff;
  border-radius: 16px;
  padding: 12px 24px;
  box-shadow: 0 4px 16px rgba(0,0,0,0.03);
  display: flex;
  flex-direction: column;
  border: 1px solid #e2e8f0;
}
.stat-pill .label { font-size: 12px; color: $text-light; margin-bottom: 4px;}
.stat-pill .value { font-size: 24px; font-weight: 700; color: $text-dark;}

/* 柔性工具栏 */
.inbox-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 32px;
}

.capsule-tabs {
  display: flex;
  background: #e2e8f0;
  padding: 4px;
  border-radius: 12px;
}
.capsule {
  padding: 10px 24px;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  color: $text-light;
  cursor: pointer;
  transition: all 0.3s;
  display: flex; align-items: center; gap: 6px;
}
.capsule.active {
  background: #fff;
  color: $primary-blue;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
}

.filter-group { display: flex; gap: 12px; }
.round-select ::v-deep .el-input__inner,
.round-input ::v-deep .el-input__inner {
  border-radius: 20px;
  border: 1px solid #cbd5e1;
  background: #fff;
  height: 40px;
  line-height: 40px;
  &:focus { border-color: $primary-blue; }
}

/* 任务卡片网格 */
.task-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(340px, 1fr));
  gap: 24px;
}

.task-card {
  background: #fff;
  border-radius: 20px;
  padding: 24px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.02);
  border: 1px solid #f1f5f9;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;

  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 16px 32px rgba(15, 23, 42, 0.08);
    border-color: #cbd5e1;
    .process-btn { background: $primary-blue; color: #fff; }
  }
}

.card-top {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}
.teacher-avatar {
  width: 40px; height: 40px;
  border-radius: 50%;
  background: linear-gradient(135deg, #60a5fa, #2563eb);
  color: #fff;
  display: flex; justify-content: center; align-items: center;
  font-weight: bold; font-size: 16px; margin-right: 12px;
}
.teacher-info {
  flex: 1; display: flex; flex-direction: column;
}
.teacher-info .name { font-size: 14px; font-weight: 600; color: $text-dark;}
.teacher-info .time { font-size: 12px; color: $text-light;}

.process-btn {
  background: #eff6ff; color: $primary-blue; border: none; font-weight: 600; transition: all 0.3s;
}

.task-title {
  font-size: 18px; line-height: 1.5; color: $text-dark; margin: 0 0 20px 0;
  display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden;
}

.task-tags {
  display: flex; flex-wrap: wrap; gap: 8px;
}
.task-tags ::v-deep .el-tag { border: none; border-radius: 6px; padding: 0 10px; height: 26px; line-height: 26px; font-weight: 500;}

.pagination-wrapper { margin-top: 40px; display: flex; justify-content: center; }

/* ================= 沉浸式审阅全屏弹窗 ================= */
.immersive-review-dialog ::v-deep .el-dialog {
  background: $bg-light;
  margin: 0 !important;
  height: 100vh;
  width: 100vw;
  display: flex;
  flex-direction: column;
}
.immersive-review-dialog ::v-deep .el-dialog__header { display: none; }
.immersive-review-dialog ::v-deep .el-dialog__body { padding: 0; flex: 1; display: flex; flex-direction: column; overflow: hidden;}

.review-header {
  height: 64px; background: #fff; border-bottom: 1px solid #e2e8f0;
  display: flex; align-items: center; justify-content: space-between; padding: 0 32px;
}
.back-btn {
  font-size: 15px; font-weight: 600; color: $text-light; cursor: pointer; transition: color 0.3s;
  &:hover { color: $text-dark; }
}
.review-title {
  font-size: 16px; font-weight: 600; color: $text-dark;
  .badge { background: $primary-blue; color: #fff; padding: 2px 8px; border-radius: 4px; font-size: 12px; margin-right: 8px; }
}

.review-body {
  flex: 1; display: flex; overflow: hidden;
}

/* 左侧：文章阅读区 (Notion风格) */
.reader-pane {
  flex: 7; overflow-y: auto; padding: 40px; display: flex; justify-content: center;
}
.reader-content {
  width: 100%; max-width: 860px; background: #fff; border-radius: 16px; padding: 64px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.02); border: 1px solid #e2e8f0; min-height: 100%;
}
.doc-title { font-size: 36px; color: $text-dark; margin: 0 0 24px; line-height: 1.3;}
.doc-meta { display: flex; align-items: center; gap: 16px; margin-bottom: 40px; padding-bottom: 24px; border-bottom: 1px solid #f1f5f9; }
.meta-item { font-size: 14px; color: $text-light; }
.meta-tag ::v-deep .el-tag { border-radius: 4px; border: none; }

.doc-section { margin-bottom: 40px; }
.doc-section h3 { font-size: 18px; color: $text-dark; margin: 0 0 16px; }
.rich-text-content { font-size: 16px; line-height: 2; color: #334155; }
.rich-text-content ::v-deep img { max-width: 100%; border-radius: 8px; }

.file-gallery { display: grid; grid-template-columns: repeat(auto-fill, minmax(200px, 1fr)); gap: 16px; }
.file-card {
  display: flex; align-items: center; padding: 12px; background: #f8fafc; border: 1px solid #e2e8f0; border-radius: 8px;
  text-decoration: none; color: $text-dark; transition: all 0.2s;
  &:hover { border-color: $primary-blue; background: #eff6ff; }
}
.file-icon { font-size: 24px; color: $primary-blue; margin-right: 12px; }
.file-name { font-size: 13px; font-weight: 500; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;}

/* 右侧：操作与时间线 (钉钉/飞书风格) */
.action-pane {
  flex: 3; width: 400px; min-width: 360px; max-width: 450px; background: #f8fafc; border-left: 1px solid #e2e8f0;
  display: flex; flex-direction: column; overflow-y: auto; padding: 24px;
}

.workflow-card, .decision-card {
  background: #fff; border-radius: 12px; padding: 24px; margin-bottom: 24px;
  border: 1px solid #e2e8f0; box-shadow: 0 2px 10px rgba(0,0,0,0.02);
}
.workflow-card h3, .decision-card h3 { font-size: 16px; margin: 0 0 20px; display: flex; align-items: center; gap: 8px; color: $text-dark; }

.compact-timeline ::v-deep .el-timeline-item { padding-bottom: 16px; }
.timeline-node { font-size: 14px; }
.node-title { font-weight: 600; color: $text-dark; margin-bottom: 4px; }
.node-time { font-size: 12px; color: $text-light; margin-bottom: 4px; }
.node-comment { font-size: 13px; background: #f1f5f9; padding: 8px; border-radius: 6px; margin-top: 8px; color: #475569;}

.decision-textarea ::v-deep .el-textarea__inner {
  background: #f8fafc; border: 1px solid #e2e8f0; border-radius: 8px; padding: 12px;
  &:focus { background: #fff; border-color: $primary-blue; }
}

.decision-actions {
  display: flex; gap: 12px; margin-top: 20px;
}
.decision-btn {
  flex: 1; padding: 14px 0; border-radius: 8px; font-size: 15px; font-weight: 600; border: none;
}
.btn-reject { background: #fef2f2; color: $danger; transition: all 0.3s; &:hover { background: #fee2e2; } }
.btn-pass { background: $primary-blue; color: #fff; box-shadow: 0 4px 12px rgba(37,99,235,0.2); transition: all 0.3s; &:hover { background: #1d4ed8; transform: translateY(-1px);} }
</style>
