<template>
  <div class="audit-archive-container">
    <div class="stats-overview">
      <div class="stat-card bg-blue">
        <div class="stat-icon"><i class="el-icon-collection"></i></div>
        <div class="stat-info">
          <p>总审核档案数</p>
          <h3>{{ stats.totalCount || total }}</h3>
        </div>
      </div>
      <div class="stat-card bg-green">
        <div class="stat-icon"><i class="el-icon-circle-check"></i></div>
        <div class="stat-info">
          <p>审核通过</p>
          <h3>{{ stats.monthPassed || 0 }}</h3>
        </div>
      </div>
      <div class="stat-card bg-red">
        <div class="stat-icon"><i class="el-icon-warning-outline"></i></div>
        <div class="stat-info">
          <p>予以驳回</p>
          <h3>{{ stats.monthRejected || 0 }}</h3>
        </div>
      </div>
    </div>

    <div class="elegant-panel search-panel">
      <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" class="clean-form">
        <el-form-item label="成果标题" prop="achievementTitle">
          <el-input v-model="queryParams.achievementTitle" placeholder="关键词检索" clearable class="round-input" @keyup.enter.native="handleQuery" />
        </el-form-item>
        <el-form-item label="审核级别" prop="auditLevel">
          <el-select v-model="queryParams.auditLevel" placeholder="全部级别" clearable popper-class="elegant-select-dropdown" style="width: 130px">
            <el-option label="校级审核" value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="结果" prop="auditResult">
          <el-select v-model="queryParams.auditResult" placeholder="全部结果" clearable popper-class="elegant-select-dropdown" style="width: 110px">
            <el-option label="通过" value="1" />
            <el-option label="驳回" value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="审核人" prop="auditorName">
          <el-input v-model="queryParams.auditorName" placeholder="姓名" clearable class="round-input" style="width: 130px" @keyup.enter.native="handleQuery" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery" round class="search-btn">查 询</el-button>
          <el-button @click="resetQuery" round>重 置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="elegant-panel table-panel">
      <el-table
        v-loading="loading"
        :data="recordList"
        class="clean-table"
        :row-class-name="tableRowClassName"
        stripe
      >
        <el-table-column label="NO." type="index" width="60" align="center" />

        <el-table-column label="成果标题" align="left" prop="achievementTitle" :show-overflow-tooltip="true" min-width="240">
          <template slot-scope="scope">
            <span class="cell-title">{{ scope.row.achievementTitle }}</span>
          </template>
        </el-table-column>

        <el-table-column label="审核级别" align="center" width="110">
          <template slot-scope="scope">
            <el-tag
              class="elegant-tag tag-school"
              size="mini"
            >
              审核
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="审核结果" align="center" width="100">
          <template slot-scope="scope">
            <div class="result-indicator">
              <span :class="['dot', scope.row.auditResult === '1' ? 'dot-pass' : 'dot-reject']"></span>
              <span :class="['text', scope.row.auditResult === '1' ? 'text-green' : 'text-red']">
                {{ scope.row.auditResult === '1' ? '通过' : '驳回' }}
              </span>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="审核意见" align="left" prop="auditOpinion" :show-overflow-tooltip="true" min-width="180">
          <template slot-scope="scope">
            <span class="opinion-text">{{ scope.row.auditOpinion || '（未填写意见）' }}</span>
          </template>
        </el-table-column>

        <el-table-column label="归属学院" align="center" prop="collegeId" :formatter="collegeFormat" width="150" />

        <el-table-column label="成果类型" align="center" width="120">
          <template slot-scope="scope">
            <dict-tag :options="dict.type.edu_achievement_category" :value="scope.row.category" />
          </template>
        </el-table-column>

        <el-table-column label="审核人" align="center" width="100">
          <template slot-scope="scope">
            <span class="auditor-name">{{ scope.row.auditorName }}</span>
          </template>
        </el-table-column>

        <el-table-column label="办理时间" align="center" prop="createTime" width="160">
          <template slot-scope="scope">
            <span class="cell-time">{{ parseTime(scope.row.createTime, '{y}-{m}-{d} {h}:{i}') }}</span>
          </template>
        </el-table-column>
      </el-table>

      <pagination
        v-show="total > 0"
        :total="total"
        :page.sync="queryParams.pageNum"
        :limit.sync="queryParams.pageSize"
        @pagination="getList"
      />
    </div>
  </div>
</template>

<script>
import { listAuditRecord, getStatistics } from "@/api/achievement/audit";
import { listDept } from "@/api/system/dept";

export default {
  name: "AuditRecord",
  dicts: ['edu_achievement_category'],
  data() {
    return {
      loading: true,
      total: 0,
      recordList: [],
      collegeOptions: [],
      stats: { totalCount: 0, monthPassed: 0, monthRejected: 0 },
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        achievementTitle: null,
        auditLevel: null,
        auditResult: null,
        auditorName: null
      }
    };
  },
  created() {
    this.getCollegeList();
    this.getList();
    this.fetchStats();
  },
  methods: {
    getList() {
      this.loading = true;
      listAuditRecord(this.queryParams).then(response => {
        this.recordList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    fetchStats() {
      getStatistics({}).then(res => {
        this.stats = res || {};
      });
    },
    getCollegeList() {
      listDept().then(response => {
        this.collegeOptions = response.data || [];
      });
    },
    collegeFormat(row) {
      if (!row.collegeId) return '-';
      const c = this.collegeOptions.find(item => item.deptId == row.collegeId);
      return c ? c.deptName : row.collegeId;
    },
    // 为驳回行添加特殊的背景类名
    tableRowClassName({row}) {
      if (row.auditResult === '2') {
        return 'row-reject';
      }
      return '';
    },
    handleQuery() { this.queryParams.pageNum = 1; this.getList(); },
    resetQuery() {
      this.queryParams = { pageNum: 1, pageSize: 10, achievementTitle: null, auditLevel: null, auditResult: null, auditorName: null };
      this.handleQuery();
    }
  }
};
</script>

<style lang="scss" scoped>
$primary-blue: #2563eb;
$success: #10b981;
$danger: #ef4444;
$school-purple: #8b5cf6;
$bg-light: #f8fafc;
$text-dark: #0f172a;
$border-color: #e2e8f0;

.audit-archive-container { padding: 24px; max-width: 1440px; margin: 0 auto; }

/* 1. 统计概览区样式 */
.stats-overview {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24px;
  margin-bottom: 24px;
}
.stat-card {
  padding: 24px;
  border-radius: 16px;
  color: #fff;
  display: flex;
  align-items: center;
  gap: 20px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.05);
  transition: transform 0.3s;
  &:hover { transform: translateY(-4px); }
}
.bg-blue { background: linear-gradient(135deg, #3b82f6, #1d4ed8); }
.bg-green { background: linear-gradient(135deg, #10b981, #059669); }
.bg-red { background: linear-gradient(135deg, #f87171, #dc2626); }

.stat-icon {
  width: 56px; height: 56px;
  border-radius: 14px;
  background: rgba(255, 255, 255, 0.2);
  display: flex; align-items: center; justify-content: center;
  font-size: 28px;
}
.stat-info p { margin: 0 0 4px 0; font-size: 14px; opacity: 0.9; }
.stat-info h3 { margin: 0; font-size: 32px; font-weight: 700; }

/* 2. 搜索面板 */
.elegant-panel {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.03);
  border: 1px solid #f1f5f9;
  padding: 20px;
  margin-bottom: 20px;
}
.search-panel { padding-bottom: 4px; }
.round-input ::v-deep .el-input__inner { border-radius: 8px; }
.search-btn { padding: 8px 24px; }

/* 3. 表格定制 */
.clean-table ::v-deep .el-table__header th {
  background-color: $bg-light !important;
  color: #475569;
  font-weight: 600;
  border-bottom: none;
}
.clean-table ::v-deep .el-table__row td { border-bottom: 1px solid #f1f5f9; padding: 12px 0; }

/* 驳回行高亮逻辑 */
.clean-table ::v-deep .row-reject {
  background-color: #fff8f8 !important;
}

.cell-title { font-weight: 600; color: $text-dark; }
.cell-time { color: #94a3b8; font-size: 13px; }

/* 审核级别标签定制 */
.elegant-tag { border: none; font-weight: 600; border-radius: 4px; }

.tag-school { background: #f5f3ff; color: $school-purple; }

/* 审核结果指示灯 */
.result-indicator {
  display: flex; align-items: center; justify-content: center; gap: 8px;
  .dot { width: 8px; height: 8px; border-radius: 50%; }
  .dot-pass { background: $success; box-shadow: 0 0 8px rgba(16, 185, 129, 0.5); }
  .dot-reject { background: $danger; box-shadow: 0 0 8px rgba(239, 68, 68, 0.5); }
  .text { font-size: 13px; font-weight: 700; }
  .text-green { color: $success; }
  .text-red { color: $danger; }
}

.opinion-text { font-size: 13px; color: #64748b; }
.auditor-name { font-weight: 500; color: #334155; background: #f1f5f9; padding: 2px 8px; border-radius: 4px;}

@media (max-width: 992px) {
  .stats-overview { grid-template-columns: 1fr; }
}
</style>
