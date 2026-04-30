<template>
  <div class="app-container">
    <!-- 统计卡片 -->
    <el-row :gutter="16" class="stat-row">
      <el-col :span="6">
        <div class="stat-card stat-total">
          <div class="stat-accent"></div>
          <div class="stat-body">
            <div class="stat-icon"><i class="el-icon-files"></i></div>
            <div class="stat-info">
              <div class="stat-value">{{ totalCount }}</div>
              <div class="stat-label">全部记录</div>
            </div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card stat-pass">
          <div class="stat-accent accent-success"></div>
          <div class="stat-body">
            <div class="stat-icon"><i class="el-icon-circle-check"></i></div>
            <div class="stat-info">
              <div class="stat-value">{{ passCount }}</div>
              <div class="stat-label">已通过</div>
            </div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card stat-audit">
          <div class="stat-accent accent-warning"></div>
          <div class="stat-body">
            <div class="stat-icon"><i class="el-icon-time"></i></div>
            <div class="stat-info">
              <div class="stat-value">{{ auditCount }}</div>
              <div class="stat-label">审核中</div>
            </div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card stat-reject">
          <div class="stat-accent accent-danger"></div>
          <div class="stat-body">
            <div class="stat-icon"><i class="el-icon-circle-close"></i></div>
            <div class="stat-info">
              <div class="stat-value">{{ rejectCount }}</div>
              <div class="stat-label">被驳回</div>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 列表区 -->
    <div class="list-card">
      <el-tabs v-model="activeTab" @tab-click="handleTabClick" class="modern-tabs">
        <el-tab-pane label="全部" name="all"></el-tab-pane>
        <el-tab-pane label="审核中" name="2"></el-tab-pane>
        <el-tab-pane label="已通过" name="3"></el-tab-pane>
        <el-tab-pane label="已驳回" name="4"></el-tab-pane>
      </el-tabs>

      <div class="search-inline">
        <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch">
          <el-form-item label="成果标题" prop="title">
            <el-input v-model="queryParams.title" placeholder="请输入标题搜索" clearable @keyup.enter.native="handleQuery" style="width: 180px"/>
          </el-form-item>
          <el-form-item label="成果类型" prop="category">
            <el-select v-model="queryParams.category" placeholder="全部类型" clearable style="width: 130px">
              <el-option v-for="dict in dict.type.edu_achievement_category" :key="dict.value" :label="dict.label" :value="dict.value" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
            <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <el-table v-loading="loading" :data="achievementList">
        <el-table-column label="成果标题" align="left" prop="title" :show-overflow-tooltip="true" min-width="200">
          <template slot-scope="scope">
            <span class="link-text" @click="handleView(scope.row)">{{ scope.row.title }}</span>
          </template>
        </el-table-column>
        <el-table-column label="成果类型" align="center" prop="category" width="100">
          <template slot-scope="scope">
            <dict-tag :options="dict.type.edu_achievement_category" :value="scope.row.category"/>
          </template>
        </el-table-column>
        <el-table-column label="归属学院" align="center" prop="collegeId" :formatter="collegeFormat" width="150" />
        <el-table-column label="状态" align="center" prop="status" width="100">
          <template slot-scope="scope">
            <span :class="['status-tag', 'status-' + scope.row.status]">
              {{ statusFormat(scope.row.status).label }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="提交时间" align="center" prop="createTime" width="110">
          <template slot-scope="scope">
            <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d}') }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="240">
          <template slot-scope="scope">
            <el-button size="mini" type="text" icon="el-icon-view" @click="handleView(scope.row)">详情</el-button>
            <el-button size="mini" type="text" icon="el-icon-s-order" @click="handleProgress(scope.row)" style="color:#6366f1;">进度</el-button>
            <el-button v-if="scope.row.status === '0'"
              size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
              v-hasPermi="['achievement:achievement:teacherUpdate']">修改</el-button>
            <el-button v-if="scope.row.status === '4'"
              size="mini" type="text" icon="el-icon-refresh-right" @click="handleResubmit(scope.row)"
              style="color:#1e40af; font-weight:600;">重新提交</el-button>
            <el-button v-if="scope.row.status === '0' || scope.row.status === '4'"
              size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
              v-hasPermi="['achievement:achievement:teacherDel']" style="color: #ef4444;">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <pagination v-show="listTotal>0" :total="listTotal" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />
    </div>

    <!-- 详情弹窗 -->
    <el-dialog title="成果详情" :visible.sync="viewOpen" width="700px" append-to-body class="modern-dialog">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="成果标题">{{ form.title }}</el-descriptions-item>
        <el-descriptions-item label="成果类型">
          <dict-tag :options="dict.type.edu_achievement_category" :value="form.category"/>
        </el-descriptions-item>
        <el-descriptions-item label="归属学院">{{ collegeNameById(form.collegeId) }}</el-descriptions-item>
        <el-descriptions-item label="证明材料">
          <div class="file-preview-list">
            <template v-for="(url, index) in fileList">
              <el-image v-if="isImage(url)" :key="index" :src="url" :preview-src-list="imageList" style="width:80px;height:80px;margin:4px;border-radius:8px;cursor:pointer;" fit="cover" />
              <el-button v-else-if="isPdf(url)" :key="'pdf'+index" size="small" type="primary" plain icon="el-icon-view" @click="previewPdf(url)" style="margin:4px;">预览PDF {{index+1}}</el-button>
              <el-link v-else :key="'f'+index" :href="url" target="_blank" type="primary" :underline="false" class="file-link"><i class="el-icon-download"></i> 附件 {{index+1}}</el-link>
            </template>
          </div>
        </el-descriptions-item>
        <el-descriptions-item label="成果描述">
          <div v-html="form.content" class="editor-view"></div>
        </el-descriptions-item>
      </el-descriptions>
      <!-- 审核进度条 -->
      <div v-if="form.status && form.status !== '0'" style="margin-top:16px;">
        <h4 style="margin-bottom:12px; font-family: 'Noto Serif SC', serif; color: #0f172a;">审核进度</h4>
          <el-steps :active="stepActive" finish-status="success" align-center>
            <el-step title="已提交" :description="parseTime(form.createTime, '{y}-{m}-{d}')"></el-step>
            <el-step title="审核" :description="getStepDesc('2')" :status="getStepStatus('2')"></el-step>
            <el-step title="最终结果" :description="form.status === '3' ? '已通过' : (form.status === '4' ? '已驳回' : '等待中')" :status="form.status === '3' ? 'success' : (form.status === '4' ? 'error' : 'wait')"></el-step>
          </el-steps>
      </div>
      <div slot="footer"><el-button @click="viewOpen = false">关 闭</el-button></div>
    </el-dialog>

    <!-- 审核进度弹窗 -->
    <el-dialog title="审核进度" :visible.sync="progressOpen" width="650px" append-to-body class="modern-dialog">
      <el-steps :active="progressStepActive" finish-status="success" direction="vertical" style="min-height:200px;">
        <el-step title="教师提交" :description="progressAchievement.createTime ? parseTime(progressAchievement.createTime, '{y}-{m}-{d} {h}:{i}') : ''" status="finish"></el-step>
        <el-step v-for="(record, i) in progressRecords" :key="i"
          :title="'审核'"
          :description="(record.auditorName || '') + ' · ' + (record.auditResult === '1' ? '通过' : '驳回') + (record.auditOpinion ? ' · ' + record.auditOpinion : '') + ' · ' + parseTime(record.createTime, '{y}-{m}-{d} {h}:{i}')"
          :status="record.auditResult === '1' ? 'finish' : 'error'"
        ></el-step>
      </el-steps>
      <div slot="footer"><el-button @click="progressOpen = false">关 闭</el-button></div>
    </el-dialog>

    <!-- PDF 预览弹窗 -->
    <el-dialog title="PDF 预览" :visible.sync="pdfOpen" width="80%" append-to-body class="modern-dialog">
      <iframe :src="pdfUrl" style="width:100%;height:70vh;border:none;border-radius:8px;"></iframe>
      <div slot="footer"><el-button @click="pdfOpen = false">关 闭</el-button></div>
    </el-dialog>

    <!-- 修改弹窗 -->
    <el-dialog :title="editTitle" :visible.sync="editOpen" width="780px" append-to-body class="modern-dialog">
      <el-form ref="editForm" :model="form" :rules="rules" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="成果标题" prop="title">
              <el-input v-model="form.title" placeholder="请输入成果标题" maxlength="100" show-word-limit />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="成果类型" prop="category">
              <el-select v-model="form.category" placeholder="请选择类型" style="width: 100%">
                <el-option v-for="dict in dict.type.edu_achievement_category" :key="dict.value" :label="dict.label" :value="dict.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="证明材料"><file-upload v-model="form.fileUrl" /></el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="成果详述"><editor v-model="form.content" :min-height="240" /></el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer">
        <el-button @click="editOpen = false">取 消</el-button>
        <el-button type="primary" @click="submitForm">{{ form.status === '4' ? '重新提交' : '保 存' }}</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  teacherListAchievement, teacherDelAchievement, teacherGetAchievement,
  teacherAddAchievement, teacherUpdateAchievement, teacherListAllAchievement,
  teacherResubmit
} from "@/api/achievement/achievement";
import { getAuditProgress } from "@/api/achievement/audit";
import { listDept } from "@/api/system/dept";
import { parseTime } from "@/utils/util";
import dict from "@/utils/dict";

export default {
  name: "TeacherAchievement",
  dicts: ['edu_achievement_category'],
  data() {
    return {
      activeTab: 'all',
      loading: true,
      showSearch: true,
      listTotal: 0,
      totalCount: 0,
      passCount: 0,
      auditCount: 0,
      rejectCount: 0,
      collegeOptions: [],
      achievementList: [],
      editOpen: false,
      viewOpen: false,
      progressOpen: false,
      pdfOpen: false,
      pdfUrl: '',
      editTitle: "",
      form: {},
      progressAchievement: {},
      progressRecords: [],
      progressStepActive: 0,
      queryParams: {
        pageNum: 1, pageSize: 10, title: undefined, category: undefined, status: undefined
      },
      rules: {
        title: [{ required: true, message: "成果标题不能为空", trigger: "blur" }],
        category: [{ required: true, message: "请选择成果类型", trigger: "change" }]
      }
    };
  },
  created() {
    this.getList();
    this.getAllList();
    this.getCollegeList();
  },
  methods: {
    dict,
    parseTime,
    collegeFormat(row) {
      if (!row.collegeId) return '-';
      const c = this.collegeOptions.find(item => item.deptId === row.collegeId);
      return c ? c.deptName : row.collegeId;
    },
    collegeNameById(id) {
      if (!id) return '-';
      const c = this.collegeOptions.find(item => item.deptId === id);
      return c ? c.deptName : id;
    },
    getAllList() {
      teacherListAllAchievement().then(response => {
        this.totalCount = response.total;
        this.passCount = response.pass;
        this.auditCount = response.audit;
        this.rejectCount = response.reject;
      });
    },
    getList() {
      this.loading = true;
      teacherListAchievement(this.queryParams).then(response => {
        this.achievementList = response.rows;
        this.listTotal = response.total;
        this.loading = false;
      });
    },
    getCollegeList() {
      listDept().then(response => { this.collegeOptions = response.data; });
    },
    statusFormat(status) {
      const m = { '0': { label: '草稿', type: 'info' }, '2': { label: '审核中', type: 'warning' }, '3': { label: '已通过', type: 'success' }, '4': { label: '已驳回', type: 'danger' } };
      return m[status] || { label: '未知', type: '' };
    },
    handleTabClick(tab) {
      this.queryParams.status = tab.name === 'all' ? undefined : tab.name;
      this.handleQuery();
    },
    handleQuery() { this.queryParams.pageNum = 1; this.getList(); },
    resetQuery() { this.resetForm("queryForm"); this.handleQuery(); },
    reset() {
      this.form = { achievementId: null, title: null, category: null, fileUrl: null, content: null, status: "2" };
      this.resetForm("editForm");
    },
    loadProgress(achievementId) {
      this.progressRecords = [];
      this.progressStepActive = 1;
      return getAuditProgress(achievementId).then(res => {
        this.progressRecords = res.rows || [];
        this.progressStepActive = 1 + this.progressRecords.length;
      });
    },
    handleView(row) {
      this.form = { ...row };
      if (row.status && row.status !== '0') {
        this.loadProgress(row.achievementId).finally(() => {
          this.viewOpen = true;
        });
        return;
      }
      this.progressRecords = [];
      this.progressStepActive = 0;
      this.viewOpen = true;
    },
    handleUpdate(row) {
      this.reset();
      teacherGetAchievement(row.achievementId).then(response => {
        this.form = response.data;
        this.editOpen = true;
        this.editTitle = "修改成果";
      });
    },
    handleResubmit(row) {
      this.reset();
      teacherGetAchievement(row.achievementId).then(response => {
        this.form = response.data;
        this.editOpen = true;
        this.editTitle = "重新提交成果";
      });
    },
    handleProgress(row) {
      this.progressAchievement = row;
      this.loadProgress(row.achievementId).then(() => {
        this.progressOpen = true;
      });
    },
    submitForm() {
      this.$refs["editForm"].validate(valid => {
        if (valid) {
          if (this.form.achievementId != null && this.form.status === '4') {
            teacherResubmit(this.form).then(() => {
              this.$modal.msgSuccess("重新提交成功，已进入审核流程"); this.editOpen = false; this.getList(); this.getAllList();
            });
          } else if (this.form.achievementId != null) {
            teacherUpdateAchievement(this.form).then(() => {
              this.$modal.msgSuccess("修改成功"); this.editOpen = false; this.getList(); this.getAllList();
            });
          } else {
            teacherAddAchievement(this.form).then(() => {
              this.$modal.msgSuccess("新增成功"); this.editOpen = false; this.getList(); this.getAllList();
            });
          }
        }
      });
    },
    handleDelete(row) {
      this.$modal.confirm('确认删除 "' + row.title + '"？').then(() => {
        return teacherDelAchievement(row.achievementId);
      }).then(() => {
        this.getList(); this.getAllList(); this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    isImage(url) { return /\.(jpg|jpeg|png|gif|bmp|webp)$/i.test(url); },
    isPdf(url) { return /\.pdf$/i.test(url); },
    previewPdf(url) { this.pdfUrl = url; this.pdfOpen = true; },
    getStepDesc(level) {
      const r = this.progressRecords && this.progressRecords.find(r => r.auditLevel === level);
      if (!r) return '等待中';
      return (r.auditorName || '') + ' · ' + (r.auditResult === '1' ? '通过' : '驳回');
    },
    getStepStatus(level) {
      const r = this.progressRecords && this.progressRecords.find(r => r.auditLevel === level);
      if (!r) return 'wait';
      return r.auditResult === '1' ? 'finish' : 'error';
    }
  },
  computed: {
    fileList() {
      return (this.form.fileUrl || '').split(',').filter(u => u);
    },
    imageList() {
      return this.fileList.filter(u => this.isImage(u));
    },
    stepActive() {
      const s = this.form.status;
      if (s === '2') return 1;
      if (s === '3' || s === '4') return 2;
      return 0;
    }
  }
};
</script>

<style scoped>
.stat-row { margin-bottom: 16px; }
.stat-card {
  background: #fff;
  border-radius: 12px;
  border: 1px solid #e2e8f0;
  box-shadow: 0 1px 3px rgba(15, 23, 42, 0.04);
  overflow: hidden;
  transition: all 0.25s ease;
}
.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(15, 23, 42, 0.06);
}

.stat-accent {
  height: 3px;
  background: linear-gradient(90deg, #1e40af, #6366f1);
}
.stat-accent.accent-success { background: linear-gradient(90deg, #10b981, #34d399); }
.stat-accent.accent-warning { background: linear-gradient(90deg, #f59e0b, #fbbf24); }
.stat-accent.accent-danger { background: linear-gradient(90deg, #ef4444, #f87171); }

.stat-body {
  display: flex; align-items: center; gap: 16px;
  padding: 18px 20px;
}
.stat-icon {
  font-size: 28px; color: #64748b; opacity: 0.6;
}
.stat-value {
  font-size: 26px; font-weight: 700; color: #0f172a; line-height: 1.2;
  font-family: 'Noto Serif SC', serif;
}
.stat-label { font-size: 12px; color: #94a3b8; margin-top: 2px; }

.list-card {
  background: #fff; border-radius: 14px; padding: 20px;
  border: 1px solid #e2e8f0;
  box-shadow: 0 1px 3px rgba(15, 23, 42, 0.04);
}
.modern-tabs >>> .el-tabs__header { margin-bottom: 16px; }
.search-inline { margin-bottom: 12px; }
.link-text { color: #1e40af; cursor: pointer; font-weight: 500; }
.link-text:hover { text-decoration: underline; }

/* 状态标签 */
.status-tag { display: inline-block; padding: 2px 12px; border-radius: 20px; font-size: 12px; font-weight: 500; }
.status-0 { background: #f1f5f9; color: #94a3b8; }
.status-1 { background: rgba(245, 158, 11, 0.08); color: #f59e0b; }
.status-2 { background: rgba(99, 102, 241, 0.08); color: #6366f1; }
.status-3 { background: rgba(16, 185, 129, 0.08); color: #10b981; }
.status-4 { background: rgba(239, 68, 68, 0.08); color: #ef4444; }

/* 弹窗 */
.modern-dialog >>> .el-dialog { border-radius: 16px; overflow: hidden; }
.modern-dialog >>> .el-dialog__header { background: #f8fafc; padding: 18px 24px; border-bottom: 1px solid #e2e8f0; }
.modern-dialog >>> .el-dialog__title { font-weight: 600; font-family: 'Noto Serif SC', serif; color: #0f172a; }
.modern-dialog >>> .el-dialog__body { padding: 24px; }
.editor-view { padding: 12px; border: 1px solid #e2e8f0; border-radius: 8px; max-height: 400px; overflow-y: auto; background-color: #fafbfc; }
.file-link { padding: 4px 10px; background: rgba(30, 64, 175, 0.06); border-radius: 6px; margin-right: 8px; }
</style>
