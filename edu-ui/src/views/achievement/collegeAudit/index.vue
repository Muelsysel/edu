<template>
  <div class="app-container">
    <!-- 搜索区域 -->
    <div class="search-card">
      <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch">
        <el-form-item label="成果标题" prop="title">
          <el-input v-model="queryParams.title" placeholder="请输入成果标题" clearable @keyup.enter.native="handleQuery" style="width: 200px" />
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

    <!-- 工作台头部 -->
    <div class="workbench-header">
      <div class="wh-left">
        <i class="el-icon-s-check wh-icon"></i>
        <div>
          <h3>院级审核工作台</h3>
          <p>本院待审核成果共 <strong>{{ total }}</strong> 条</p>
        </div>
      </div>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </div>

    <!-- 数据表格 -->
    <div class="table-card">
      <el-table v-loading="loading" :data="achievementList">
        <el-table-column label="序号" type="index" width="60" align="center" />
        <el-table-column label="成果标题" align="left" prop="title" :show-overflow-tooltip="true" min-width="200">
          <template slot-scope="scope">
            <span class="link-text" @click="handleView(scope.row)">{{ scope.row.title }}</span>
          </template>
        </el-table-column>
        <el-table-column label="教师姓名" align="center" prop="teacherName" width="120">
          <template slot-scope="scope">{{ scope.row.teacherName || '-' }}</template>
        </el-table-column>
        <el-table-column label="成果类型" align="center" prop="category" width="100">
          <template slot-scope="scope">
            <dict-tag :options="dict.type.edu_achievement_category" :value="scope.row.category" />
          </template>
        </el-table-column>
        <el-table-column label="状态" align="center" width="80">
          <template><span class="status-tag status-1">待院审</span></template>
        </el-table-column>
        <el-table-column label="提交时间" align="center" prop="createTime" width="110">
          <template slot-scope="scope">{{ parseTime(scope.row.createTime, '{y}-{m}-{d}') }}</template>
        </el-table-column>
        <el-table-column label="操作" align="center" width="160">
          <template slot-scope="scope">
            <el-button size="mini" type="text" icon="el-icon-view" @click="handleView(scope.row)">详情</el-button>
            <el-button size="mini" type="text" icon="el-icon-s-check" @click="handleAudit(scope.row)" style="color:#67C23A; font-weight:600;">审核</el-button>
          </template>
        </el-table-column>
      </el-table>
      <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />
    </div>

    <!-- 详情弹窗 -->
    <el-dialog title="成果详情" :visible.sync="viewOpen" width="700px" append-to-body class="modern-dialog">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="成果标题">{{ viewForm.title }}</el-descriptions-item>
        <el-descriptions-item label="教师姓名">{{ viewForm.teacherName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="成果类型">
          <dict-tag :options="dict.type.edu_achievement_category" :value="viewForm.category"/>
        </el-descriptions-item>
        <el-descriptions-item label="归属学院">{{ collegeNameById(viewForm.collegeId) }}</el-descriptions-item>
        <el-descriptions-item label="证明材料">
          <div v-for="(url, index) in (viewForm.fileUrl || '').split(',')" :key="index">
            <el-link :href="url" target="_blank" type="primary" v-if="url" :underline="false" class="file-link">
              <i class="el-icon-document"></i> 附件 {{index+1}}
            </el-link>
          </div>
        </el-descriptions-item>
        <el-descriptions-item label="成果详述">
          <div v-html="viewForm.content" class="editor-view"></div>
        </el-descriptions-item>
      </el-descriptions>
      <div slot="footer"><el-button @click="viewOpen = false">关 闭</el-button></div>
    </el-dialog>

    <!-- 审核弹窗 -->
    <el-dialog title="院级审核" :visible.sync="auditOpen" width="560px" append-to-body class="modern-dialog">
      <el-form ref="auditForm" :model="auditForm" :rules="auditRules" label-width="100px">
        <div class="audit-target">
          <i class="el-icon-document"></i>
          <span>{{ auditForm.achievementTitle }}</span>
        </div>
        <el-form-item label="审核结果" prop="auditResult">
          <el-radio-group v-model="auditForm.auditResult" class="audit-radio-group">
            <el-radio label="1">
              <span class="audit-option pass"><i class="el-icon-circle-check"></i> 通过（进入校级审核）</span>
            </el-radio>
            <el-radio label="2">
              <span class="audit-option reject"><i class="el-icon-circle-close"></i> 驳回</span>
            </el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="审核意见" prop="auditOpinion">
          <el-input v-model="auditForm.auditOpinion" type="textarea" :rows="4" placeholder="请输入审核意见（驳回时建议说明原因）" maxlength="500" show-word-limit />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="auditOpen = false">取 消</el-button>
        <el-button type="primary" @click="submitAudit" :loading="submitLoading">确认提交</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { collegeAuditList, collegeAuditHandle, getAuditDetail } from "@/api/achievement/audit";
import { listDept } from "@/api/system/dept";
import { parseTime } from "@/utils/ruoyi";

export default {
  name: "CollegeAudit",
  dicts: ['edu_achievement_category'],
  data() {
    return {
      loading: true, showSearch: true, total: 0, achievementList: [], collegeOptions: [],
      viewOpen: false, viewForm: {},
      auditOpen: false, submitLoading: false,
      auditForm: { achievementId: null, achievementTitle: '', auditResult: null, auditOpinion: '' },
      auditRules: { auditResult: [{ required: true, message: "请选择审核结果", trigger: "change" }] },
      queryParams: { pageNum: 1, pageSize: 10, title: null, category: null }
    };
  },
  created() { this.getCollegeList(); this.getList(); },
  methods: {
    parseTime,
    getList() {
      this.loading = true;
      collegeAuditList(this.queryParams).then(response => {
        this.achievementList = response.rows; this.total = response.total; this.loading = false;
      });
    },
    getCollegeList() { listDept().then(response => { this.collegeOptions = response.data; }); },
    collegeNameById(id) {
      if (!id) return '-';
      const c = this.collegeOptions.find(item => item.deptId === id);
      return c ? c.deptName : id;
    },
    handleQuery() { this.queryParams.pageNum = 1; this.getList(); },
    resetQuery() { this.resetForm("queryForm"); this.handleQuery(); },
    handleView(row) {
      getAuditDetail(row.achievementId).then(response => { this.viewForm = response.data; this.viewOpen = true; });
    },
    handleAudit(row) {
      this.auditForm = { achievementId: row.achievementId, achievementTitle: row.title, auditResult: null, auditOpinion: '' };
      this.auditOpen = true;
    },
    submitAudit() {
      this.$refs["auditForm"].validate(valid => {
        if (valid) {
          const t = this.auditForm.auditResult === '1' ? '通过' : '驳回';
          this.$modal.confirm('确认对该成果执行【' + t + '】操作？').then(() => {
            this.submitLoading = true;
            collegeAuditHandle(this.auditForm).then(() => {
              this.$modal.msgSuccess("审核操作成功"); this.auditOpen = false; this.submitLoading = false; this.getList();
            }).catch(() => { this.submitLoading = false; });
          }).catch(() => {});
        }
      });
    }
  }
};
</script>

<style scoped>
.search-card { background: #fff; padding: 20px 20px 4px; border-radius: 12px; box-shadow: 0 1px 4px rgba(0,0,0,0.06); margin-bottom: 16px; }
.workbench-header {
  display: flex; justify-content: space-between; align-items: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 14px; padding: 20px 24px; margin-bottom: 16px; color: #fff;
}
.wh-left { display: flex; align-items: center; gap: 14px; }
.wh-icon { font-size: 36px; opacity: 0.85; }
.wh-left h3 { margin: 0 0 4px; font-size: 17px; }
.wh-left p { margin: 0; font-size: 13px; opacity: 0.85; }
.wh-left strong { font-size: 16px; }
.table-card { background: #fff; padding: 16px; border-radius: 12px; box-shadow: 0 1px 4px rgba(0,0,0,0.06); }
.table-card >>> .el-table th { background-color: #f5f7fa !important; font-weight: 600; }
.link-text { color: #409EFF; cursor: pointer; }
.link-text:hover { text-decoration: underline; }
.status-tag { display: inline-block; padding: 2px 12px; border-radius: 20px; font-size: 12px; font-weight: 500; }
.status-1 { background: #fdf6ec; color: #e6a23c; }

/* 审核弹窗 */
.modern-dialog >>> .el-dialog { border-radius: 16px; overflow: hidden; }
.modern-dialog >>> .el-dialog__header { background: #f8f9fc; padding: 18px 24px; border-bottom: 1px solid #ebeef5; }
.modern-dialog >>> .el-dialog__title { font-weight: 600; }
.modern-dialog >>> .el-dialog__body { padding: 24px; }
.audit-target { background: #f0f5ff; border-radius: 10px; padding: 12px 16px; margin-bottom: 20px; font-size: 14px; color: #409EFF; font-weight: 500; display: flex; align-items: center; gap: 8px; }
.audit-radio-group { display: flex; flex-direction: column; gap: 12px; }
.audit-option { font-size: 14px; }
.audit-option.pass { color: #67C23A; }
.audit-option.reject { color: #F56C6C; }
.editor-view { padding: 12px; border: 1px solid #EBEEF5; border-radius: 8px; max-height: 400px; overflow-y: auto; background: #fafafa; }
.file-link { padding: 4px 10px; background: #f0f5ff; border-radius: 6px; margin-right: 8px; }
</style>
