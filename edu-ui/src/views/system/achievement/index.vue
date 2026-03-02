<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="成果名称" prop="title">
        <el-input
          v-model="queryParams.title"
          placeholder="请输入成果名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="成果类型" prop="type">
        <el-select v-model="queryParams.type" placeholder="请选择成果类型" clearable>
          <el-option
            v-for="dict in dict.type.edu_achievement_type"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="成果级别" prop="level">
        <el-select v-model="queryParams.level" placeholder="请选择成果级别" clearable>
          <el-option
            v-for="dict in dict.type.edu_achievement_level"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="获得时间" prop="gainDate">
        <el-date-picker clearable
          v-model="queryParams.gainDate"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择获得时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="教师ID" prop="teacherId">
        <el-input
          v-model="queryParams.teacherId"
          placeholder="请输入教师ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="教师姓名" prop="teacherName">
        <el-input
          v-model="queryParams.teacherName"
          placeholder="请输入教师姓名"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="归属学院ID" prop="deptId">
        <el-select v-model="queryParams.deptId" placeholder="请选择归属学院ID" clearable>
          <el-option
            v-for="dict in dict.type.edu_college_list"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="审核状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择审核状态" clearable>
          <el-option
            v-for="dict in dict.type.edu_achievement_status"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
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
          v-hasPermi="['system:achievement:add']"
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
          v-hasPermi="['system:achievement:edit']"
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
          v-hasPermi="['system:achievement:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['system:achievement:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="achievementList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="成果ID" align="center" prop="achievementId" />
      <el-table-column label="成果名称" align="center" prop="title" />
      <el-table-column label="成果类型" align="center" prop="type">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.edu_achievement_type" :value="scope.row.type"/>
        </template>
      </el-table-column>
      <el-table-column label="成果级别" align="center" prop="level">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.edu_achievement_level" :value="scope.row.level"/>
        </template>
      </el-table-column>
      <el-table-column label="获得时间" align="center" prop="gainDate" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.gainDate, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="教师ID" align="center" prop="teacherId" />
      <el-table-column label="教师姓名" align="center" prop="teacherName" />
      <el-table-column label="归属学院ID" align="center" prop="deptId">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.edu_college_list" :value="scope.row.deptId"/>
        </template>
      </el-table-column>
      <el-table-column label="审核状态" align="center" prop="status">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.edu_achievement_status" :value="scope.row.status"/>
        </template>
      </el-table-column>
      <el-table-column label="附件存储URL(OSS地址)" align="center" prop="fileUrl" />
      <el-table-column label="备注/驳回原因" align="center" prop="remark" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['system:achievement:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:achievement:remove']"
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
        <el-form-item label="成果名称" prop="title">
          <el-input v-model="form.title" placeholder="请输入成果名称" />
        </el-form-item>
        <el-form-item label="成果类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择成果类型">
            <el-option
              v-for="dict in dict.type.edu_achievement_type"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="成果级别" prop="level">
          <el-select v-model="form.level" placeholder="请选择成果级别">
            <el-option
              v-for="dict in dict.type.edu_achievement_level"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="获得时间" prop="gainDate">
          <el-date-picker clearable
            v-model="form.gainDate"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择获得时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="教师ID" prop="teacherId">
          <el-input v-model="form.teacherId" placeholder="请输入教师ID" />
        </el-form-item>
        <el-form-item label="教师姓名" prop="teacherName">
          <el-input v-model="form.teacherName" placeholder="请输入教师姓名" />
        </el-form-item>
        <el-form-item label="归属学院ID" prop="deptId">
          <el-select v-model="form.deptId" placeholder="请选择归属学院ID">
            <el-option
              v-for="dict in dict.type.edu_college_list"
              :key="dict.value"
              :label="dict.label"
              :value="parseInt(dict.value)"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="审核状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择审核状态">
            <el-option
              v-for="dict in dict.type.edu_achievement_status"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="附件存储URL(OSS地址)" prop="fileUrl">
          <el-input v-model="form.fileUrl" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="备注/驳回原因" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入内容" />
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
import { listAchievement, getAchievement, delAchievement, addAchievement, updateAchievement } from "@/api/system/achievement"

export default {
  name: "Achievement",
  dicts: ['edu_achievement_level', 'edu_college_list', 'edu_achievement_type', 'edu_achievement_status'],
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
        type: null,
        level: null,
        gainDate: null,
        teacherId: null,
        teacherName: null,
        deptId: null,
        status: null,
        fileUrl: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        title: [
          { required: true, message: "成果名称不能为空", trigger: "blur" }
        ],
        teacherId: [
          { required: true, message: "教师ID不能为空", trigger: "blur" }
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
        type: null,
        level: null,
        gainDate: null,
        teacherId: null,
        teacherName: null,
        deptId: null,
        status: null,
        fileUrl: null,
        remark: null,
        createBy: null,
        createTime: null,
        updateBy: null,
        updateTime: null
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
      this.download('system/achievement/export', {
        ...this.queryParams
      }, `achievement_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>
