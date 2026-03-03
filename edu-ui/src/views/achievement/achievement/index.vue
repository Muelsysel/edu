<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="成果标题" prop="title">
        <el-input
          v-model="queryParams.title"
          placeholder="请输入成果标题"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="教师 ID(关联 sys_user 表)" prop="teacherId">
        <el-input
          v-model="queryParams.teacherId"
          placeholder="请输入教师 ID(关联 sys_user 表)"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="学院 ID(关联 sys_dept 表)" prop="collegeId">
        <el-input
          v-model="queryParams.collegeId"
          placeholder="请输入学院 ID(关联 sys_dept 表)"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['achievement:achievement:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['achievement:achievement:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['achievement:achievement:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['achievement:achievement:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="achievementList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="成果 ID" align="center" prop="achievementId" />
      <el-table-column label="成果标题" align="center" prop="title" />
      <el-table-column label="教师 ID" align="center" prop="teacherId" />
      <el-table-column label="学院 ID" align="center" prop="collegeId" />
      <el-table-column label="状态 (0:草稿 1:院级审核中 2:校级审核中 3:已通过 4:已驳回)" align="center" prop="status" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['achievement:achievement:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['achievement:achievement:remove']"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改教学成果管理对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="成果标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入成果标题" />
        </el-form-item>
        <el-form-item label="成果内容">
          <editor v-model="form.content" :min-height="192"/>
        </el-form-item>
        <el-form-item label="文件存储路径 (MinIO 路径)" prop="fileUrl">
          <file-upload v-model="form.fileUrl"/>
        </el-form-item>
        <el-form-item label="教师 ID(关联 sys_user 表)" prop="teacherId">
          <el-input v-model="form.teacherId" placeholder="请输入教师 ID(关联 sys_user 表)" />
        </el-form-item>
        <el-form-item label="学院 ID(关联 sys_dept 表)" prop="collegeId">
          <el-input v-model="form.collegeId" placeholder="请输入学院 ID(关联 sys_dept 表)" />
        </el-form-item>
        <el-form-item label="删除标志 (0 代表存在 2 代表删除)" prop="delFlag">
          <el-input v-model="form.delFlag" placeholder="请输入删除标志 (0 代表存在 2 代表删除)" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listAchievement, getAchievement, delAchievement, addAchievement, updateAchievement } from "@/api/achievement/achievement"

export default {
  name: "Achievement",
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 教学成果管理表格数据
      achievementList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        title: null,
        teacherId: null,
        collegeId: null,
        status: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        achievementId: [
          { required: true, message: "成果 ID不能为空", trigger: "blur" }
        ],
        title: [
          { required: true, message: "成果标题不能为空", trigger: "blur" }
        ],
        content: [
          { required: true, message: "成果内容不能为空", trigger: "blur" }
        ],
        teacherId: [
          { required: true, message: "教师 ID(关联 sys_user 表)不能为空", trigger: "blur" }
        ],
        collegeId: [
          { required: true, message: "学院 ID(关联 sys_dept 表)不能为空", trigger: "blur" }
        ],
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /** 查询教学成果管理列表 */
    getList() {
      this.loading = true
      listAchievement(this.queryParams).then(response => {
        this.achievementList = response.rows
        this.total = response.total
        this.loading = false
      })
    },
    // 取消按钮
    cancel() {
      this.open = false
      this.reset()
    },
    // 表单重置
    reset() {
      this.form = {
        achievementId: null,
        title: null,
        content: null,
        fileUrl: null,
        teacherId: null,
        collegeId: null,
        status: null,
        createBy: null,
        createTime: null,
        updateBy: null,
        updateTime: null,
        delFlag: null
      }
      this.resetForm("form")
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm")
      this.handleQuery()
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.achievementId)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = "添加教学成果管理"
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const achievementId = row.achievementId || this.ids
      getAchievement(achievementId).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改教学成果管理"
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.achievementId != null) {
            updateAchievement(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addAchievement(this.form).then(response => {
              this.$modal.msgSuccess("新增成功")
              this.open = false
              this.getList()
            })
          }
        }
      })
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const achievementIds = row.achievementId || this.ids
      this.$modal.confirm('是否确认删除教学成果管理编号为"' + achievementIds + '"的数据项？').then(function() {
        return delAchievement(achievementIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('achievement/achievement/export', {
        ...this.queryParams
      }, `achievement_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>
