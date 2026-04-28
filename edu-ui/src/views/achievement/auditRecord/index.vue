<template>
  <div class="portal-record-page">
    <section class="record-head">
      <div>
        <span>Audit Archive</span>
        <h1>审核档案</h1>
        <p>查询学校审核办理记录与成果归档信息。</p>
      </div>
      <div class="head-stats">
        <div>
          <strong>{{ stats.totalCount || total || 0 }}</strong>
          <span>记录总数</span>
        </div>
        <div>
          <strong>{{ stats.monthPassed || 0 }}</strong>
          <span>本月通过</span>
        </div>
        <div>
          <strong>{{ stats.monthRejected || 0 }}</strong>
          <span>本月驳回</span>
        </div>
      </div>
    </section>

    <section class="record-filter">
      <el-form :model="queryParams" ref="queryForm" :inline="true" size="small">
        <el-form-item label="成果标题" prop="achievementTitle">
          <el-input v-model="queryParams.achievementTitle" placeholder="请输入关键词" clearable @keyup.enter.native="handleQuery" />
        </el-form-item>
        <el-form-item label="审核层级" prop="auditLevel">
          <el-select v-model="queryParams.auditLevel" placeholder="全部" clearable>
            <el-option label="学校审核" value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="审核结果" prop="auditResult">
          <el-select v-model="queryParams.auditResult" placeholder="全部" clearable>
            <el-option label="通过" value="1" />
            <el-option label="驳回" value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="审核人" prop="auditorName">
          <el-input v-model="queryParams.auditorName" placeholder="审核人" clearable @keyup.enter.native="handleQuery" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="handleQuery">查询</el-button>
          <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </section>

    <section class="record-table">
      <el-table v-loading="loading" :data="recordList" :row-class-name="tableRowClassName" stripe>
        <el-table-column label="序号" type="index" width="70" align="center" />
        <el-table-column label="成果标题" align="left" prop="achievementTitle" :show-overflow-tooltip="true" min-width="240">
          <template slot-scope="scope">
            <span class="cell-title">{{ scope.row.achievementTitle }}</span>
          </template>
        </el-table-column>
        <el-table-column label="审核层级" align="center" width="110">
          <template>
            <el-tag size="mini" class="level-tag">学校审核</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="审核结果" align="center" width="110">
          <template slot-scope="scope">
            <span :class="['result-dot', scope.row.auditResult === '1' ? 'pass' : 'reject']"></span>
            <span :class="scope.row.auditResult === '1' ? 'text-pass' : 'text-reject'">
              {{ scope.row.auditResult === '1' ? '通过' : '驳回' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="审核意见" align="left" prop="auditOpinion" :show-overflow-tooltip="true" min-width="180">
          <template slot-scope="scope">
            <span>{{ scope.row.auditOpinion || scope.row.auditComment || '无审核意见' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="所属学院" align="center" prop="collegeId" :formatter="collegeFormat" width="150" />
        <el-table-column label="成果类型" align="center" width="120">
          <template slot-scope="scope">
            <dict-tag :options="dict.type.edu_achievement_category" :value="scope.row.category" />
          </template>
        </el-table-column>
        <el-table-column label="审核人" align="center" width="110">
          <template slot-scope="scope">
            <span>{{ scope.row.auditorName || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="审核时间" align="center" prop="createTime" width="170">
          <template slot-scope="scope">
            <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d} {h}:{i}') }}</span>
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
    </section>
  </div>
</template>

<script>
import { listAuditRecord, getStatistics } from '@/api/achievement/audit'
import { listDept } from '@/api/system/dept'

export default {
  name: 'AuditRecord',
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
    }
  },
  created() {
    this.getCollegeList()
    this.getList()
    this.fetchStats()
  },
  methods: {
    getList() {
      this.loading = true
      listAuditRecord(this.queryParams).then(response => {
        this.recordList = response.rows
        this.total = response.total
        this.loading = false
      })
    },
    fetchStats() {
      getStatistics({}).then(res => {
        this.stats = res || {}
      })
    },
    getCollegeList() {
      listDept().then(response => {
        this.collegeOptions = response.data || []
      })
    },
    collegeFormat(row) {
      if (!row.collegeId) return '-'
      const c = this.collegeOptions.find(item => item.deptId == row.collegeId)
      return c ? c.deptName : row.collegeId
    },
    tableRowClassName({ row }) {
      if (row.auditResult === '2') {
        return 'row-reject'
      }
      return ''
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.queryParams = {
        pageNum: 1,
        pageSize: 10,
        achievementTitle: null,
        auditLevel: null,
        auditResult: null,
        auditorName: null
      }
      this.handleQuery()
    }
  }
}
</script>

<style lang="scss" scoped>
.portal-record-page {
  max-width: 1460px;
  margin: 0 auto;
  padding: 28px 20px 60px;
}

.record-head {
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

.record-head span:first-child {
  display: block;
  margin-bottom: 10px;
  color: #0b5c95;
  font-size: 13px;
  text-transform: uppercase;
  letter-spacing: 1px;
}

.record-head h1 {
  margin: 0;
  font-size: 32px;
}

.record-head p {
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

.record-filter,
.record-table {
  background: #fff;
  border: 1px solid #dbe6f2;
  box-shadow: 0 12px 28px rgba(18, 70, 122, 0.08);
}

.record-filter {
  margin-top: 22px;
  padding: 18px 22px 0;
}

.record-filter ::v-deep .el-input__inner,
.record-filter .el-button {
  border-radius: 0;
}

.record-table {
  margin-top: 18px;
  padding: 22px;
}

.record-table ::v-deep .el-table__header th {
  background: #f4f8fc !important;
  color: #173b63;
  font-weight: 700;
}

.cell-title {
  color: #173b63;
  font-weight: 600;
}

.level-tag {
  color: #0b4f93;
  border-color: #9bc3e9;
  background: #edf6ff;
}

.result-dot {
  width: 7px;
  height: 7px;
  display: inline-block;
  margin-right: 6px;
  border-radius: 50%;
}

.result-dot.pass {
  background: #15935f;
}

.result-dot.reject {
  background: #d14545;
}

.text-pass {
  color: #15935f;
}

.text-reject {
  color: #d14545;
}

.record-table ::v-deep .row-reject td {
  background: #fff8f8;
}

::v-deep .pagination-container {
  margin-top: 22px;
}

@media (max-width: 900px) {
  .record-head {
    display: block;
  }

  .head-stats {
    margin-top: 20px;
  }
}
</style>
