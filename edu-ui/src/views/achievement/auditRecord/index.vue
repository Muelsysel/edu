<template>
  <div class="app-container">
    <!-- 搜索区域 -->
    <div class="search-card">
      <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch">
        <el-form-item label="成果标题" prop="achievementTitle">
          <el-input v-model="queryParams.achievementTitle" placeholder="请输入成果标题" clearable @keyup.enter.native="handleQuery" style="width: 180px" />
        </el-form-item>
        <el-form-item label="审核级别" prop="auditLevel">
          <el-select v-model="queryParams.auditLevel" placeholder="全部级别" clearable style="width: 130px">
            <el-option label="院级审核" value="1" />
            <el-option label="校级审核" value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="审核结果" prop="auditResult">
          <el-select v-model="queryParams.auditResult" placeholder="全部结果" clearable style="width: 120px">
            <el-option label="通过" value="1" />
            <el-option label="驳回" value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="审核人" prop="auditorName">
          <el-input v-model="queryParams.auditorName" placeholder="请输入审核人" clearable @keyup.enter.native="handleQuery" style="width: 140px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
          <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 页头 -->
    <div class="page-header">
      <div class="ph-left">
        <i class="el-icon-notebook-2 ph-icon"></i>
        <div>
          <h3>审核记录</h3>
          <p>所有审核操作的历史记录</p>
        </div>
      </div>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </div>

    <!-- 数据表格 -->
    <div class="table-card">
      <el-table v-loading="loading" :data="recordList">
        <el-table-column label="序号" type="index" width="60" align="center" />
        <el-table-column label="成果标题" align="left" prop="achievementTitle" :show-overflow-tooltip="true" min-width="200" />
        <el-table-column label="成果类型" align="center" prop="category" width="100">
          <template slot-scope="scope">
            <dict-tag :options="dict.type.edu_achievement_category" :value="scope.row.category" />
          </template>
        </el-table-column>
        <el-table-column label="归属学院" align="center" prop="collegeId" :formatter="collegeFormat" width="140" />
        <el-table-column label="审核级别" align="center" width="100">
          <template slot-scope="scope">
            <span :class="['level-tag', scope.row.auditLevel === '1' ? 'level-college' : 'level-school']">
              {{ scope.row.auditLevel === '1' ? '院级' : '校级' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="审核结果" align="center" width="90">
          <template slot-scope="scope">
            <span :class="['result-tag', scope.row.auditResult === '1' ? 'result-pass' : 'result-reject']">
              <i :class="scope.row.auditResult === '1' ? 'el-icon-circle-check' : 'el-icon-circle-close'"></i>
              {{ scope.row.auditResult === '1' ? '通过' : '驳回' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="审核意见" align="left" prop="auditOpinion" :show-overflow-tooltip="true" min-width="140">
          <template slot-scope="scope">
            <span style="color: #606266;">{{ scope.row.auditOpinion || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="审核人" align="center" prop="auditorName" width="100" />
        <el-table-column label="审核时间" align="center" prop="createTime" width="150">
          <template slot-scope="scope">
            <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d} {h}:{i}') }}</span>
          </template>
        </el-table-column>
      </el-table>
      <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />
    </div>
  </div>
</template>

<script>
import { listAuditRecord } from "@/api/achievement/audit";
import { listDept } from "@/api/system/dept";
import { parseTime } from "@/utils/ruoyi";

export default {
  name: "AuditRecord",
  dicts: ['edu_achievement_category'],
  data() {
    return {
      loading: true, showSearch: true, total: 0, recordList: [], collegeOptions: [],
      queryParams: {
        pageNum: 1, pageSize: 10, achievementTitle: null, auditLevel: null, auditResult: null, auditorName: null
      }
    };
  },
  created() { this.getCollegeList(); this.getList(); },
  methods: {
    parseTime,
    getList() {
      this.loading = true;
      listAuditRecord(this.queryParams).then(response => {
        this.recordList = response.rows; this.total = response.total; this.loading = false;
      });
    },
    getCollegeList() { listDept().then(response => { this.collegeOptions = response.data; }); },
    collegeFormat(row) {
      if (!row.collegeId) return '-';
      const c = this.collegeOptions.find(item => item.deptId === row.collegeId);
      return c ? c.deptName : row.collegeId;
    },
    handleQuery() { this.queryParams.pageNum = 1; this.getList(); },
    resetQuery() { this.resetForm("queryForm"); this.handleQuery(); }
  }
};
</script>

<style scoped>
.search-card { background: #fff; padding: 20px 20px 4px; border-radius: 12px; box-shadow: 0 1px 4px rgba(0,0,0,0.06); margin-bottom: 16px; }
.page-header {
  display: flex; justify-content: space-between; align-items: center;
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  border-radius: 14px; padding: 20px 24px; margin-bottom: 16px; color: #fff;
}
.ph-left { display: flex; align-items: center; gap: 14px; }
.ph-icon { font-size: 36px; opacity: 0.85; }
.ph-left h3 { margin: 0 0 4px; font-size: 17px; }
.ph-left p { margin: 0; font-size: 13px; opacity: 0.85; }
.table-card { background: #fff; padding: 16px; border-radius: 12px; box-shadow: 0 1px 4px rgba(0,0,0,0.06); }
.table-card >>> .el-table th { background-color: #f5f7fa !important; font-weight: 600; }

/* 审核级别标签 */
.level-tag { display: inline-block; padding: 2px 12px; border-radius: 20px; font-size: 12px; font-weight: 500; }
.level-college { background: #ecf5ff; color: #409EFF; }
.level-school { background: #fef0f0; color: #f56c6c; }

/* 审核结果标签 */
.result-tag { display: inline-flex; align-items: center; gap: 4px; padding: 2px 10px; border-radius: 20px; font-size: 12px; font-weight: 500; }
.result-pass { background: #f0f9eb; color: #67c23a; }
.result-reject { background: #fef0f0; color: #f56c6c; }
</style>
