<template>
  <div class="app-container">
    <el-row :gutter="20" class="mb20">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-label">全部记录</div>
            <div class="stat-value text-primary">{{ total }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-label">已通过</div>
            <div class="stat-value text-success">{{ passCount }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-label">待审核</div>
            <div class="stat-value text-warning">{{ auditCount }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-label">被驳回</div>
            <div class="stat-value text-danger">{{ rejectCount }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-tabs v-model="activeTab" type="border-card" @tab-click="handleTabClick">
      <el-tab-pane label="全部" name="all"></el-tab-pane>
      <el-tab-pane label="待审核" name="1"></el-tab-pane>
      <el-tab-pane label="已通过" name="3"></el-tab-pane>
      <el-tab-pane label="已驳回" name="4"></el-tab-pane>

      <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch">
        <el-form-item label="成果标题" prop="title">
          <el-input v-model="queryParams.title" placeholder="请输入成果标题" clearable @keyup.enter.native="handleQuery"/>
        </el-form-item>
        <el-form-item label="成果类型" prop="category">
          <el-select v-model="queryParams.category" placeholder="请选择" clearable>
            <el-option v-for="dict in dict.type.edu_achievement_category" :key="dict.value" :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
          <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table v-loading="loading" :data="achievementList">
        <el-table-column label="成果标题" align="center" prop="title" :show-overflow-tooltip="true" />
        <el-table-column label="成果类型" align="center" prop="category">
          <template slot-scope="scope">
            <dict-tag :options="dict.type.edu_achievement_category" :value="scope.row.category"/>
          </template>
        </el-table-column>

        <el-table-column label="归属学院" align="center" prop="collegeId" :formatter="collegeFormat" width="150" />

        <el-table-column label="状态" align="center" prop="status">
          <template slot-scope="scope">
            <el-tag :type="statusFormat(scope.row.status).type">
              {{ statusFormat(scope.row.status).label }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="提交时间" align="center" prop="createTime" width="180" />
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
          <template slot-scope="scope">
            <el-button size="mini" type="text" icon="el-icon-view" @click="handleView(scope.row)">详情</el-button>

            <!--审核中和审核完成都不能删除，只有草稿与被拒绝可以修改-->
            <el-button
              v-if="scope.row.status === '0' || scope.row.status === '4'"
              size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
              v-hasPermi="['achievement:achievement:teacherUpdate']"
            >修改</el-button>

            <el-button
              v-if="scope.row.status === '0' || scope.row.status === '4'"
              size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
              v-hasPermi="['achievement:achievement:teacherDel']"
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
    </el-tabs>

    <el-dialog title="成果详情预览" :visible.sync="viewOpen" width="700px" append-to-body>
      <el-descriptions :column="1" border>
        <el-descriptions-item label="成果标题">{{ form.title }}</el-descriptions-item>
        <el-descriptions-item label="成果类型">
          <dict-tag :options="dict.type.edu_achievement_category" :value="form.category"/>
        </el-descriptions-item>
        <el-descriptions-item label="证明材料">
          <div v-for="(url, index) in (form.fileUrl || '').split(',')" :key="index">
            <el-link :href="url" target="_blank" type="primary" v-if="url">附件 {{index+1}}</el-link>
          </div>
        </el-descriptions-item>
        <el-descriptions-item label="成果描述">
          <div v-html="form.content" class="editor-view"></div>
        </el-descriptions-item>
      </el-descriptions>
      <div slot="footer">
        <el-button @click="viewOpen = false">关 闭</el-button>
      </div>
    </el-dialog>

    <el-dialog :title="title" :visible.sync="open" width="780px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
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
// 【重要引入】：引入了 getAchievement 获取详情，以及新增和修改的API
import {
  teacherListAchievement,
  teacherDelAchievement,
  teacherGetAchievement,
  teacherAddAchievement,
  teacherUpdateAchievement,
  teacherListAllAchievement,
} from "@/api/achievement/achievement";
import {listDept} from "@/api/system/dept";
import dict from "@/utils/dict";
import {parseTime} from "@/utils/ruoyi";


export default {
  name: "TeacherAchievement",
  dicts: ['edu_achievement_category'],
  data() {
    return {
      activeTab: 'all',
      loading: true,
      showSearch: true,
      total: 0,
      passCount: 0,
      auditCount: 0,
      rejectCount: 0,
      collegeOptions: [],
      achievementList: [],
      // 新增和修改的弹窗
      open: false,
      // 详情查看的弹窗
      viewOpen: false,
      // 弹窗标题
      title: "",
      // 表单参数
      form: {},
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        title: undefined,
        category: undefined,
        status: undefined
      },
      // 表单校验规则
      rules: {
        title: [
          { required: true, message: "成果标题不能为空", trigger: "blur" }
        ],
        category: [
          { required: true, message: "请选择成果类型", trigger: "change" }
        ]
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
    /** 查询列表 */
    parseTime,
    // 学院名称格式化
    collegeFormat(row, column) {
      if (!row.collegeId) return '-';
      const college = this.collegeOptions.find(item => item.deptId === row.collegeId);
      // 如果后端直接返回了 collegeName 可以写成 row.collegeName || (college ? college.deptName : row.collegeId)
      return college ? college.deptName : row.collegeId;
    },
    // 获取所有的成果并统计各状态数量
    getAllList() {
      teacherListAllAchievement().then(response => {
        this.total = response.total;
        this.passCount = response.pass;
        this.auditCount = response.audit;
        this.rejectCount =  response.reject;
      });
    },
    getList() {
      this.loading = true;
      teacherListAchievement(this.queryParams).then(response => {
        this.achievementList = response.rows;
        this.loading = false;
      });
    },
    /** 查询学院列表 */
    getCollegeList() {
      listDept().then(response => {
        this.collegeOptions = response.data;
      });
    },



    /** 统计各状态数量 */
    updateStats() {
      let pass = 0;
      let audit = 0;
      let reject = 0;

      this.achievementList.forEach(item => {
        if (item.status === '3') {
          pass++;
        } else if (item.status === '1' || item.status === '2') {
          audit++;
        } else if (item.status === '4') {
          reject++;
        }
      });

      this.passCount = pass;
      this.auditCount = audit;
      this.rejectCount = reject;
    },
    /** 状态标签格式化 */
    statusFormat(status) {
      const statusMap = {
        '0': { label: '草稿', type: 'info' },
        '1': { label: '待院审', type: 'warning' },
        '2': { label: '待校审', type: 'warning' },
        '3': { label: '已通过', type: 'success' },
        '4': { label: '已驳回', type: 'danger' }
      };
      return statusMap[status] || { label: '未知', type: '' };
    },
    /** Tab点击处理 */
    handleTabClick(tab) {
      this.queryParams.status = tab.name === 'all' ? undefined : tab.name;
      this.handleQuery();
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    // 表单重置
    reset() {
      this.form = {
        achievementId: null,
        title: null,
        category: null,
        fileUrl: null,
        content: null,
        // 教师提交默认都是待院审状态，具体看你的业务需求，也可以传 0（草稿）
        status: "1"
      };
      this.resetForm("form");
    },
    /** 取消按钮 */
    cancel() {
      this.open = false;
      this.reset();
    },
    /** 查看详情 */
    handleView(row) {
      this.form = row;
      this.viewOpen = true; // 改用 viewOpen 控制详情弹窗
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "新增成果";
    },
    /** 修改按钮操作（改造为弹窗） */
    handleUpdate(row) {
      this.reset();
      const achievementId = row.achievementId;
      // 先根据 ID 从后端查询完整的数据回显到表单
      teacherGetAchievement(achievementId).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改成果";
      });
    },
    /** 提交按钮（保存表单） */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          // 如果存在 achievementId，说明是修改操作
          if (this.form.achievementId != null) {
            teacherUpdateAchievement(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            // 否则是新增操作
            teacherAddAchievement(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const achievementId = row.achievementId;
      this.$modal.confirm('是否确认删除标题为 "' + row.title + '" 的成果记录？').then(function () {
        return teacherDelAchievement(achievementId);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    }
  }
};
</script>

<style scoped>
.mb20 { margin-bottom: 20px; }
.stat-card { text-align: center; border-radius: 8px; }
.stat-label { font-size: 14px; color: #909399; }
.stat-value { font-size: 26px; font-weight: bold; margin-top: 10px; }
.text-primary { color: #409EFF; }
.text-success { color: #67C23A; }
.text-warning { color: #E6A23C; }
.text-danger { color: #F56C6C; }
.editor-view { padding: 12px; border: 1px solid #EBEEF5; border-radius: 4px; max-height: 400px; overflow-y: auto; background-color: #fafafa; }
</style>
