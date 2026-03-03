<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="80px">
      <el-form-item label="成果标题" prop="title">
        <el-input v-model="queryParams.title" placeholder="请输入成果标题" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="审核状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="状态选择" clearable>
          <el-option label="待审核" value="1" />
          <el-option label="审核中" value="2" />
          <el-option label="已通过" value="3" />
          <el-option label="已驳回" value="4" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['achievement:achievement:add']">新增申报</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete" v-hasPermi="['achievement:achievement:remove']">批量删除</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="achievementList" @selection-change="handleSelectionChange" border stripe>
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="序号" type="index" width="60" align="center" />
      <el-table-column label="成果标题" align="left" prop="title" :show-overflow-tooltip="true" min-width="200" />

      <el-table-column label="成果类型" align="center" prop="category" width="120">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.edu_achievement_category" :value="scope.row.category" />
        </template>
      </el-table-column>

      <el-table-column label="当前进程" align="center" width="150">
        <template slot-scope="scope">
          <el-tag :type="statusTypeFormat(scope.row.status)">
            {{ statusLabelFormat(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>

      <el-table-column label="提交时间" align="center" prop="createTime" width="160">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>

      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="180">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)" v-hasPermi="['achievement:achievement:edit']">管理/修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" v-hasPermi="['achievement:achievement:remove']" class="text-danger">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <el-dialog :title="title" :visible.sync="open" width="780px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="24" v-if="form.achievementId">
            <el-alert title="管理提醒：修改下方状态将直接影响审核结果" type="warning" show-icon :closable="false" style="margin-bottom: 20px;" />
            <el-form-item label="审核结果">
              <el-radio-group v-model="form.status">
                <el-radio label="1">待院审</el-radio>
                <el-radio label="2">校审中</el-radio>
                <el-radio label="3">已通过</el-radio>
                <el-radio label="4">已驳回</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>

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
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">保 存</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listAchievement, getAchievement, delAchievement, addAchievement, updateAchievement } from "@/api/achievement/achievement"

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
      title: "",
      open: false,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        title: null,
        status: null,
        category: null
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
    this.getList()
  },
  methods: {
    statusTypeFormat(status) {
      const map = { '0': 'info', '1': 'warning', '2': 'warning', '3': 'success', '4': 'danger' };
      return map[status] || 'info';
    },
    statusLabelFormat(status) {
      const map = { '0': '草稿', '1': '待院审', '2': '校审中', '3': '已通过', '4': '已驳回' };
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
    cancel() {
      this.open = false;
      this.reset();
    },
    reset() {
      this.form = {
        achievementId: null,
        title: null,
        content: null,
        fileUrl: null,
        category: null,
        status: "0"
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
        this.title = "编辑/审核教学成果";
      });
    },
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.achievementId != null) {
            updateAchievement(this.form).then(response => {
              this.$modal.msgSuccess("更新成功");
              this.open = false;
              this.getList();
            });
          } else {
            addAchievement(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除选中的成果？').then(function() {
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
.text-danger { color: #ff4949; }
.mb8 { margin-bottom: 8px; }
.el-form-item :deep(.editor) { line-height: normal; }
</style>
