<template>
  <div class="app-container">
    <!-- 搜索区域 -->
    <div class="search-card">
      <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch">
        <el-form-item label="成果标题" prop="title">
          <el-input v-model="queryParams.title" placeholder="请输入成果标题" clearable @keyup.enter.native="handleQuery" style="width: 200px" />
        </el-form-item>
        <el-form-item label="教师姓名" prop="teacherName">
          <el-input v-model="queryParams.teacherName" placeholder="请输入教师姓名" clearable @keyup.enter.native="handleQuery" style="width: 160px" />
        </el-form-item>
        <el-form-item label="归属学院" prop="collegeId">
          <el-select v-model="queryParams.collegeId" placeholder="全部学院" clearable style="width: 160px">
            <el-option v-for="item in collegeOptions" :key="item.deptId" :label="item.deptName" :value="item.deptId" />
          </el-select>
        </el-form-item>
        <el-form-item label="审核状态" prop="status">
          <el-select v-model="queryParams.status" placeholder="全部状态" clearable style="width: 130px">
            <el-option label="草稿" value="0" />
            <el-option label="审核中" value="2" />
            <el-option label="已通过" value="3" />
            <el-option label="已驳回" value="4" />
          </el-select>
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

    <!-- 操作栏 -->
    <div class="toolbar">
      <div class="toolbar-left">
        <el-button type="primary" icon="el-icon-plus" @click="handleAdd" v-hasPermi="['achievement:achievement:add']">新增申报</el-button>
        <el-button type="danger" plain icon="el-icon-delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['achievement:achievement:remove']">批量删除</el-button>
      </div>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </div>

    <!-- 数据表格 -->
    <div class="table-card">
      <el-table v-loading="loading" :data="achievementList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="50" align="center" />
        <el-table-column label="序号" type="index" width="60" align="center" />
        <el-table-column label="成果标题" align="left" prop="title" :show-overflow-tooltip="true" min-width="200">
          <template slot-scope="scope">
            <span class="link-text" @click="handleUpdate(scope.row)">{{ scope.row.title }}</span>
          </template>
        </el-table-column>
        <el-table-column label="教师姓名" align="center" prop="teacherName" width="120">
          <template slot-scope="scope">
            <span>{{ scope.row.teacherName || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="归属学院" align="center" prop="collegeId" :formatter="collegeFormat" width="150" />
        <el-table-column label="成果类型" align="center" prop="category" width="100">
          <template slot-scope="scope">
            <dict-tag :options="dict.type.edu_achievement_category" :value="scope.row.category" />
          </template>
        </el-table-column>
        <el-table-column label="审核状态" align="center" width="100">
          <template slot-scope="scope">
            <span :class="['status-tag', 'status-' + scope.row.status]">
              {{ statusLabelFormat(scope.row.status) }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="提交时间" align="center" prop="createTime" width="110">
          <template slot-scope="scope">
            <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d}') }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="160">
          <template slot-scope="scope">
            <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)" v-hasPermi="['achievement:achievement:edit']">编辑</el-button>
            <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" v-hasPermi="['achievement:achievement:remove']" style="color: #F56C6C;">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />
    </div>

    <!-- 编辑弹窗 -->
    <el-dialog :title="title" :visible.sync="open" width="780px" append-to-body class="modern-dialog">
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row v-if="form.achievementId">
          <el-col :span="24">
            <div class="audit-control-bar">
              <span class="audit-label">审核状态控制</span>
              <el-radio-group v-model="form.status" size="small">
                <el-radio-button label="0">草稿</el-radio-button>
                <el-radio-button label="2">审核中</el-radio-button>
                <el-radio-button label="3">已通过</el-radio-button>
                <el-radio-button label="4">已驳回</el-radio-button>
              </el-radio-group>
            </div>
          </el-col>
        </el-row>

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
          <el-col :span="12">
            <el-form-item label="归属学院" prop="collegeId">
              <el-select v-model="form.collegeId" placeholder="请选择学院" style="width: 100%" clearable>
                <el-option v-for="item in collegeOptions" :key="item.deptId" :label="item.deptName" :value="item.deptId" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="证明材料">
              <file-upload v-model="form.fileUrl" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="成果详述">
              <editor v-model="form.content" :min-height="240" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer">
        <el-button @click="cancel">取 消</el-button>
        <el-button type="primary" @click="submitForm">保 存</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listAchievement, getAchievement, delAchievement, addAchievement, updateAchievement } from "@/api/achievement/achievement"
import { listDept } from "@/api/system/dept";
import { parseTime } from "@/utils/ruoyi";

export default {
  name: "Achievement",
  dicts: ['edu_achievement_category'],
  data() {
    return {
      loading: true,
      ids: [],
      single: true,
      multiple: true,
      showSearch: true,
      total: 0,
      achievementList: [],
      collegeOptions: [],
      title: "",
      open: false,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        title: null,
        status: null,
        category: null,
        teacherName: null,
        collegeId: null
      },
      form: {},
      rules: {
        title: [{ required: true, message: "成果标题不能为空", trigger: "blur" }],
        content: [{ required: true, message: "成果内容不能为空", trigger: "blur" }],
        category: [{ required: true, message: "请选择成果类型", trigger: "change" }]
      }
    }
  },
  created() {
    this.getCollegeList();
    this.getList();
  },
  methods: {
    parseTime,
    collegeFormat(row) {
      if (!row.collegeId) return '-';
      const college = this.collegeOptions.find(item => item.deptId === row.collegeId);
      return college ? college.deptName : row.collegeId;
    },
    statusLabelFormat(status) {
      const map = { '0': '草稿', '2': '审核中', '3': '已通过', '4': '已驳回' };
      return map[status] || '未知';
    },
    getList() {
      this.loading = true;
      listAchievement(this.queryParams).then(response => {
        this.achievementList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    getCollegeList() {
      listDept().then(response => {
        this.collegeOptions = response.data;
      });
    },
    cancel() {
      this.open = false;
      this.reset();
    },
    reset() {
      this.form = {
        achievementId: null, title: null, content: null, fileUrl: null,
        category: null, collegeId: null, teacherId: null, status: "0"
      };
      this.resetForm("form");
    },
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.achievementId);
      this.single = selection.length !== 1;
      this.multiple = !selection.length;
    },
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "申报教学成果";
    },
    handleUpdate(row) {
      this.reset();
      const achievementId = row.achievementId || this.ids;
      getAchievement(achievementId).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "编辑教学成果";
      });
    },
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.achievementId != null) {
            updateAchievement(this.form).then(() => {
              this.$modal.msgSuccess("更新成功");
              this.open = false;
              this.getList();
            });
          } else {
            addAchievement(this.form).then(() => {
              this.$modal.msgSuccess("申报成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    handleDelete(row) {
      const achievementIds = row.achievementId || this.ids;
      this.$modal.confirm('确认删除选中的成果记录？').then(() => {
        return delAchievement(achievementIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    }
  }
}
</script>

<style scoped>
.search-card {
  background: #fff;
  padding: 20px 20px 4px;
  border-radius: 12px;
  box-shadow: 0 1px 4px rgba(0,0,0,0.06);
  margin-bottom: 16px;
}
.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}
.toolbar-left { display: flex; gap: 8px; }
.table-card {
  background: #fff;
  padding: 16px;
  border-radius: 12px;
  box-shadow: 0 1px 4px rgba(0,0,0,0.06);
}
.table-card >>> .el-table { border-radius: 8px; overflow: hidden; }
.table-card >>> .el-table th { background-color: #f5f7fa !important; color: #303133; font-weight: 600; }
.link-text { color: #409EFF; cursor: pointer; }
.link-text:hover { text-decoration: underline; }

/* 胶囊型状态标签 */
.status-tag {
  display: inline-block;
  padding: 2px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
  line-height: 20px;
}
.status-0 { background: #f4f4f5; color: #909399; }
.status-2 { background: rgba(37, 99, 235, 0.08); color: #2563eb; }
.status-3 { background: #f0f9eb; color: #67c23a; }
.status-4 { background: #fef0f0; color: #f56c6c; }

/* 审核状态控制栏 */
.audit-control-bar {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 10px;
  padding: 14px 20px;
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
}
.audit-label { color: #fff; font-weight: 600; font-size: 14px; white-space: nowrap; }
.audit-control-bar >>> .el-radio-button__inner { border-radius: 6px !important; border: none !important; margin: 0 2px; }

/* 弹窗优化 */
.modern-dialog >>> .el-dialog { border-radius: 16px; overflow: hidden; }
.modern-dialog >>> .el-dialog__header { background: #f8f9fc; padding: 18px 24px; border-bottom: 1px solid #ebeef5; }
.modern-dialog >>> .el-dialog__title { font-weight: 600; font-size: 16px; }
.modern-dialog >>> .el-dialog__body { padding: 24px; }
.modern-dialog >>> .el-dialog__footer { padding: 12px 24px 20px; border-top: 1px solid #ebeef5; }
</style>
