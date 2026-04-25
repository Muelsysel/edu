<template>
  <div class="mine-dashboard-container">

    <div class="dashboard-header">
      <div class="header-left">
        <h1 class="page-title">我的教学成果</h1>
        <p class="page-subtitle">共计 {{ total }} 项申报，继续努力填报吧！</p>
      </div>

      <div class="header-right">
        <div class="search-bar">
          <el-input
            v-model="queryParams.title"
            placeholder="搜索成果标题..."
            clearable
            prefix-icon="el-icon-search"
            class="elegant-input"
            @keyup.enter.native="handleQuery"
            @clear="handleQuery"
          >
            <el-button slot="append" icon="el-icon-right" @click="handleQuery"></el-button>
          </el-input>
        </div>
      </div>
    </div>

    <div class="status-tabs">
      <div class="tab-item" :class="{ active: queryParams.status === undefined }" @click="switchStatus(undefined)">
        <i class="el-icon-menu"></i> 全部申报
      </div>
      <div class="tab-item" :class="{ active: queryParams.status === '0' }" @click="switchStatus('0')">
        <i class="el-icon-edit-outline"></i> 草稿箱
      </div>
      <div class="tab-item" :class="{ active: queryParams.status === '2' }" @click="switchStatus('2')">
        <i class="el-icon-loading"></i> 审核中
      </div>
      <div class="tab-item pass" :class="{ active: queryParams.status === '3' }" @click="switchStatus('3')">
        <i class="el-icon-circle-check"></i> 已通过
      </div>
      <div class="tab-item reject" :class="{ active: queryParams.status === '4' }" @click="switchStatus('4')">
        <i class="el-icon-circle-close"></i> 被驳回
      </div>
    </div>

    <div class="achievement-grid" v-loading="loading">
      <el-empty v-if="list.length === 0" description="该状态下暂无申报数据" :image-size="120"></el-empty>

      <div
        v-for="item in list"
        :key="item.achievementId"
        class="achievement-card"
        :class="getCardClass(item.status)"
        @click="viewDetail(item)"
      >
        <div v-if="item.status === '3'" class="status-watermark watermark-pass">APPROVED</div>
        <div v-if="item.status === '4'" class="status-watermark watermark-reject">REJECTED</div>

        <div class="card-header">
          <div class="tags-group">
            <dict-tag :options="dict.type.edu_achievement_category" :value="item.category" class="custom-dict-tag category-tag"/>
            <dict-tag :options="dict.type.edu_achievement_level" :value="item.level" class="custom-dict-tag level-tag"/>
          </div>
          <div class="status-dot-wrap">
            <span class="status-dot" :class="'dot-' + item.status"></span>
            <span class="status-text">{{ statusLabel(item.status) }}</span>
          </div>
        </div>

        <div class="card-body">
          <h3 class="card-title" :title="item.title">{{ item.title }}</h3>
          <div class="card-time">
            <i class="el-icon-time"></i> {{ parseTime(item.updateTime || item.createTime, '{y}年{m}月{d}日 {h}:{i}') }} 更新
          </div>
        </div>

        <div class="card-footer" @click.stop>
          <div class="footer-actions">
            <el-button type="text" class="action-btn view" @click="viewDetail(item)">
              <i class="el-icon-data-analysis"></i> 追踪详情
            </el-button>
            <el-button v-if="canEdit(item.status)" type="text" class="action-btn edit" @click="editRow(item)">
              <i class="el-icon-edit"></i> 继续编辑
            </el-button>
            <el-button v-if="canWithdraw(item.status)" type="text" class="action-btn withdraw" @click="withdrawRow(item)">
              <i class="el-icon-refresh-left"></i> 撤回
            </el-button>
          </div>
        </div>
      </div>
    </div>

    <div class="pagination-wrapper" v-show="total > 0">
      <pagination :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />
    </div>

    <el-dialog
      :visible.sync="detailOpen"
      width="1000px"
      custom-class="elegant-dialog progress-dialog"
      :show-close="false"
      destroy-on-close
    >
      <div slot="title" class="dialog-header">
        <div class="dialog-title">成果申报与审核追踪</div>
        <div class="dialog-close" @click="detailOpen = false"><i class="el-icon-close"></i></div>
      </div>

      <div class="dialog-body-layout" v-loading="detailLoading">
        <div class="info-section">
          <div class="info-header">
            <h2 class="detail-title">{{ detail.title }}</h2>
            <div class="detail-tags">
              <dict-tag :options="dict.type.edu_achievement_category" :value="detail.category" />
              <dict-tag :options="dict.type.edu_achievement_level" :value="detail.level" />
            </div>
          </div>

          <div class="info-content-box">
            <div class="info-meta-row">
              <span class="meta-label">所属学院：</span>
              <span class="meta-value">{{ getDeptName(detail.collegeId) }}</span>
            </div>
            <div class="info-meta-row">
              <span class="meta-label">提交时间：</span>
              <span class="meta-value">{{ parseTime(detail.createTime) }}</span>
            </div>

            <div class="content-divider"></div>
            <div class="meta-label mb-8">成果详细报告：</div>
            <div class="rich-text-content" v-html="detail.content"></div>

            <div class="content-divider"></div>
            <div class="meta-label mb-8">佐证材料：</div>
            <div v-if="!detail.fileUrl" class="empty-text">未上传佐证材料</div>
            <div v-else class="file-list">
              <a v-for="(file, index) in parseFiles(detail.fileUrl)" :key="index" :href="file.url" target="_blank" class="file-link-item">
                <i class="el-icon-paperclip"></i> {{ file.name }}
              </a>
            </div>
          </div>
        </div>

        <div class="progress-section">
          <h3 class="progress-title"><i class="el-icon-s-promotion"></i> 流程追踪</h3>
          <div class="timeline-container">
            <el-timeline>
              <el-timeline-item v-for="(node, index) in timelineNodes" :key="index" :type="node.type" :color="node.color" :size="node.size || 'large'" :icon="node.icon">
                <div class="timeline-node-card" :class="{ 'is-active': node.isActive, 'is-rejected': node.isRejected }">
                  <div class="node-header">
                    <span class="node-name">{{ node.name }}</span>
                    <span class="node-time" v-if="node.time">{{ parseTime(node.time, '{m}-{d} {h}:{i}') }}</span>
                  </div>
                  <div v-if="node.statusLabel" class="node-status-wrap">
                    <el-tag :type="node.statusType" size="mini" effect="dark" class="rounded-tag">{{ node.statusLabel }}</el-tag>
                    <span class="node-auditor" v-if="node.auditor">办理人：{{ node.auditor }}</span>
                  </div>
                  <div v-if="node.comment" class="node-comment">"{{ node.comment }}"</div>
                  <div v-if="node.isWaiting" class="node-waiting"><i class="el-icon-loading"></i> 等待处理中...</div>
                </div>
              </el-timeline-item>
            </el-timeline>
          </div>
        </div>
      </div>

      <div slot="footer" class="dialog-footer">
        <el-button @click="detailOpen = false" round>关 闭</el-button>
        <el-button v-if="canEdit(detail.status)" type="primary" round @click="handleEditFromDetail(detail)">编辑此申报</el-button>
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
        pageSize: 12, // 改为12更适合网格布局 (3列或4列)
        title: undefined,
        status: undefined
      }
    }
  },
  computed: {
    timelineNodes() {
      const status = this.detail.status;
      const records = this.auditRecords || [];
      const findRecord = (auditLevel) => records.find(r => r.auditLevel === auditLevel);
      let nodes = [];

      nodes.push({ name: '教师发起申报', time: this.detail.createTime, color: '#10b981', icon: 'el-icon-check', statusLabel: '已提交', statusType: 'success' });

      const schoolRecord = findRecord('2');
      if (schoolRecord) {
        const isPass = schoolRecord.auditStatus === '1';
        nodes.push({ name: '审核', time: schoolRecord.auditTime, auditor: schoolRecord.auditBy, color: isPass ? '#10b981' : '#ef4444', icon: isPass ? 'el-icon-check' : 'el-icon-close', statusLabel: isPass ? '审核通过并归档' : '已驳回', statusType: isPass ? 'success' : 'danger', comment: schoolRecord.auditComment, isRejected: !isPass });
      } else {
        if (status === '2') nodes.push({ name: '审核', color: '#2563eb', isActive: true, isWaiting: true });
        else nodes.push({ name: '审核', color: '#cbd5e1' });
      }
      return nodes;
    }
  },
  created() {
    this.getList();
    this.getDeptList();
  },
  methods: {
    getList() {
      this.loading = true;

      // 如果查询参数为 '1,2'（审核中），需要处理给后端的传参方式
      // 假设后端支持传入数组或逗号分隔，如果不支持，需要后端改造或前端发两次请求。
      // 这里传回原始状态，如果你的后端不支持 '1,2'，请通知后端将 status 改为 in 查询。
      let query = { ...this.queryParams };

      teacherListAchievement(query).then(res => {
        this.list = res.rows || [];
        this.total = res.total || 0
      }).finally(() => { this.loading = false })
    },
    getDeptList() { listDept().then(res => { this.deptList = res.data || [] }); },
    getDeptName(deptId) {
      if (!deptId) return '未知';
      const dept = this.deptList.find(d => d.deptId == deptId);
      return dept ? dept.deptName : deptId;
    },
    handleQuery() { this.queryParams.pageNum = 1; this.getList() },

    // 创新点：胶囊导航切换逻辑
    switchStatus(status) {
      this.queryParams.status = status;
      this.handleQuery();
    },

    getCardClass(status) {
      return `card-status-${status}`;
    },

    statusLabel(status) { const map = { '0': '草稿箱', '2': '审核中', '3': '已通过', '4': '被驳回' }; return map[status] || '未知' },
    canEdit(status) { return status === '0' || status === '4' },
    canWithdraw(status) { return status === '2' },

    viewDetail(row) {
      this.detailOpen = true;
      this.detailLoading = true;
      this.auditRecords = [];
      Promise.all([
        teacherGetAchievement(row.achievementId),
        getAuditProgress(row.achievementId)
      ]).then(([detailRes, progressRes]) => {
        this.detail = detailRes.data || {};
        this.auditRecords = progressRes.rows || [];
      }).finally(() => { this.detailLoading = false; })
    },

    editRow(row) { this.$router.push({ path: '/portal/declare', query: { id: row.achievementId } }); },
    handleEditFromDetail(detail) { this.detailOpen = false; this.editRow(detail); },

    withdrawRow(row) {
      this.$confirm('撤回后申报将退回草稿箱。是否确认撤回？', '撤回确认', {
        confirmButtonText: '确认撤回', cancelButtonText: '取消', type: 'warning', customClass: 'elegant-confirm-box'
      }).then(() => teacherWithdraw(row.achievementId)).then(() => {
        this.$message.success('已成功撤回'); this.getList()
      })
    },

    parseFiles(urlStr) {
      if (!urlStr) return [];
      return urlStr.split(',').map(url => {
        const name = url.substring(url.lastIndexOf('/') + 1);
        return { name: name || '附件', url };
      });
    }
  }
}
</script>

<style lang="scss" scoped>
/* ================= 全局变量 ================= */
$primary-blue: #2563eb;
$bg-color: #f8fafc;
$text-dark: #0f172a;
$text-normal: #334155;
$text-light: #64748b;
$success: #10b981;
$warning: #f59e0b;
$danger: #ef4444;
$border-color: #e2e8f0;

.mine-dashboard-container { padding: 32px 24px; max-width: 1440px; margin: 0 auto; min-height: 80vh; }

/* ================= 头部统计与搜索 ================= */
.dashboard-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  margin-bottom: 32px;
}
.page-title { font-size: 28px; font-weight: 600; color: $text-dark; margin: 0 0 8px 0; }
.page-subtitle { font-size: 14px; color: $text-light; margin: 0; }

.elegant-input ::v-deep .el-input__inner {
  border-radius: 20px 0 0 20px;
  background: #fff;
  border: 1px solid #e2e8f0;
  width: 280px;
}
.elegant-input ::v-deep .el-input-group__append {
  border-radius: 0 20px 20px 0;
  background: $primary-blue;
  color: white;
  border: 1px solid $primary-blue;
  button { padding: 0 20px; }
}

/* ================= 胶囊导航 ================= */
.status-tabs {
  display: flex;
  gap: 12px;
  margin-bottom: 24px;
  flex-wrap: wrap;
}
.tab-item {
  padding: 8px 20px;
  border-radius: 24px;
  background: #fff;
  border: 1px solid #e2e8f0;
  color: $text-light;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  gap: 6px;

  &:hover { background: #f1f5f9; transform: translateY(-1px); }
  &.active {
    background: $primary-blue;
    color: #fff;
    border-color: $primary-blue;
    box-shadow: 0 4px 12px rgba(37, 99, 235, 0.2);
  }
  &.pass.active { background: $success; border-color: $success; box-shadow: 0 4px 12px rgba(16, 185, 129, 0.2); }
  &.reject.active { background: $danger; border-color: $danger; box-shadow: 0 4px 12px rgba(239, 68, 68, 0.2); }
}

/* ================= 卡片网格布局 ================= */
.achievement-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 24px;
}

.achievement-card {
  position: relative;
  background: #fff;
  border-radius: 16px;
  padding: 24px;
  border: 1px solid #e2e8f0;
  box-shadow: 0 2px 10px rgba(0,0,0,0.02);
  cursor: pointer;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);

  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 12px 24px rgba(15, 23, 42, 0.06);
    .footer-actions { opacity: 1; transform: translateY(0); }
  }
}

/* 卡片顶部：标签与状态点 */
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
  z-index: 1;
}
.tags-group { display: flex; gap: 8px; flex-wrap: wrap; }

.status-dot-wrap { display: flex; align-items: center; gap: 6px; }
.status-dot { width: 8px; height: 8px; border-radius: 50%; }
.status-text { font-size: 13px; font-weight: 500; color: $text-normal; }

/* 状态动态着色 */
.dot-0 { background: #cbd5e1; }
.dot-1, .dot-2 { background: $warning; box-shadow: 0 0 0 3px rgba(245, 158, 11, 0.2); animation: pulse 2s infinite;}
.dot-3 { background: $success; }
.dot-4 { background: $danger; }

@keyframes pulse {
  0% { box-shadow: 0 0 0 0 rgba(245, 158, 11, 0.4); }
  70% { box-shadow: 0 0 0 6px rgba(245, 158, 11, 0); }
  100% { box-shadow: 0 0 0 0 rgba(245, 158, 11, 0); }
}

/* 卡片主体 */
.card-body { flex: 1; z-index: 1; }
.card-title {
  font-size: 18px;
  font-weight: 600;
  color: $text-dark;
  line-height: 1.4;
  margin: 0 0 16px 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.card-time { font-size: 13px; color: $text-light; }

/* 卡片底部操作区 */
.card-footer {
  margin-top: 20px;
  padding-top: 16px;
  border-top: 1px dashed #e2e8f0;
  z-index: 1;
}
.footer-actions {
  display: flex;
  gap: 16px;
  opacity: 0.6; /* 默认半透明 */
  transform: translateY(2px);
  transition: all 0.3s;
}
.action-btn { padding: 0; font-size: 14px; font-weight: 500; }
.action-btn.view { color: $primary-blue; }
.action-btn.edit { color: $warning; }
.action-btn.withdraw { color: $danger; }

/* 卡片边框状态修饰 */
.card-status-1, .card-status-2 { border-left: 4px solid $warning; }
.card-status-3 { border-left: 4px solid $success; }
.card-status-4 { border-left: 4px solid $danger; background: #fffcfc;}

/* 印章水印效果 */
.status-watermark {
  position: absolute;
  right: -20px;
  top: 20px;
  font-size: 42px;
  font-weight: 900;
  letter-spacing: 2px;
  transform: rotate(15deg);
  opacity: 0.05;
  pointer-events: none;
  z-index: 0;
}
.watermark-pass { color: $success; }
.watermark-reject { color: $danger; }

/* 字典Tag定制去除边框 */
.custom-dict-tag ::v-deep .el-tag {
  border: none;
  border-radius: 4px;
  padding: 0 8px;
  height: 24px;
  line-height: 24px;
}

.pagination-wrapper { margin-top: 40px; display: flex; justify-content: center; }

/* ================= 详情弹窗 (保持原样极度美观) ================= */
.progress-dialog ::v-deep .el-dialog__header { display: none; }
.progress-dialog ::v-deep .el-dialog__body { padding: 0; }
.dialog-header { display: flex; justify-content: space-between; align-items: center; padding: 20px 24px; border-bottom: 1px solid $border-color; background: #fff; border-radius: 12px 12px 0 0; }
.dialog-title { font-size: 18px; font-weight: 600; color: $text-dark; }
.dialog-close { cursor: pointer; font-size: 20px; color: $text-light; transition: color 0.2s; &:hover { color: $text-dark; } }

.dialog-body-layout { display: flex; min-height: 500px; background: #fafbfc; }
.info-section { flex: 3; padding: 32px; background: #fff; border-right: 1px solid $border-color; max-height: 65vh; overflow-y: auto; }
.info-header { margin-bottom: 24px; }
.detail-title { margin: 0 0 12px 0; font-size: 22px; color: $text-dark; line-height: 1.4; }
.detail-tags { display: flex; gap: 8px; }

.info-content-box { background: #f8fafc; border-radius: 8px; padding: 20px; border: 1px solid #e2e8f0; }
.info-meta-row { margin-bottom: 12px; font-size: 14px; }
.meta-label { color: $text-light; width: 90px; display: inline-block; }
.meta-value { color: $text-dark; font-weight: 500;}
.content-divider { height: 1px; background: $border-color; margin: 20px 0; }
.mb-8 { margin-bottom: 8px; }

.rich-text-content { font-size: 14px; line-height: 1.8; color: #334155; background: #fff; padding: 16px; border-radius: 6px; border: 1px solid #e2e8f0; }
.rich-text-content ::v-deep img { max-width: 100%; height: auto; border-radius: 4px; }

.empty-text { color: #94a3b8; font-size: 13px; font-style: italic; }
.file-list { display: flex; flex-direction: column; gap: 10px; }
.file-link-item { display: inline-flex; align-items: center; gap: 6px; font-size: 13px; color: $primary-blue; background: #eff6ff; padding: 8px 12px; border-radius: 6px; text-decoration: none; transition: background 0.2s; &:hover { background: #dbeafe; } }

.progress-section { flex: 2; padding: 32px 24px; background: #fafbfc; max-height: 65vh; overflow-y: auto; }
.progress-title { margin: 0 0 24px 0; font-size: 16px; font-weight: 600; color: $text-dark; display: flex; align-items: center; gap: 8px; i { color: $primary-blue; font-size: 18px; } }

.timeline-container { padding-left: 4px; }
.timeline-container ::v-deep .el-timeline-item__tail { border-left-width: 2px; border-left-color: #cbd5e1; }
.timeline-node-card { padding: 12px 16px; border-radius: 8px; background: #fff; border: 1px solid #e2e8f0; transition: all 0.3s; &.is-active { border-color: #bfdbfe; background: #eff6ff; box-shadow: 0 4px 12px rgba(37, 99, 235, 0.05); } &.is-rejected { border-color: #fecaca; background: #fef2f2; } }
.node-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px; }
.node-name { font-weight: 600; color: $text-dark; font-size: 14px; }
.node-time { font-size: 12px; color: #94a3b8; }
.node-status-wrap { display: flex; align-items: center; gap: 12px; margin-bottom: 8px; }
.rounded-tag { border-radius: 4px; border: none; padding: 0 8px; font-weight: 500;}
.node-auditor { font-size: 12px; color: #64748b; }
.node-comment { font-size: 13px; color: #475569; background: #f1f5f9; padding: 8px 12px; border-radius: 6px; border-left: 3px solid #cbd5e1; margin-top: 8px; }
.is-rejected .node-comment { border-left-color: #ef4444; background: #fff; }
.node-waiting { font-size: 13px; color: $primary-blue; display: flex; align-items: center; gap: 6px; margin-top: 4px;}
.dialog-footer { padding: 16px 24px; border-top: 1px solid $border-color; text-align: right; background: #fff; border-radius: 0 0 12px 12px; }

@media (max-width: 960px) {
  .dashboard-header { flex-direction: column; align-items: flex-start; gap: 16px;}
  .dialog-body-layout { flex-direction: column; }
  .info-section, .progress-section { max-height: none; border-right: none; }
  .progress-section { border-top: 1px solid $border-color; }
}
</style>

<style>
/* 覆盖弹窗圆角 */
.elegant-dialog { border-radius: 12px !important; overflow: hidden; }
</style>
