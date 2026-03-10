<template>
  <div class="app-container">
    <el-card shadow="hover" :header="form.achievementId ? '📑 成果管理与审核' : '📝 教学成果申报填写'">
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">

        <el-row v-if="form.achievementId" class="audit-section">
          <el-col :span="24">
            <el-form-item label="当前审核进程" prop="status">
              <el-radio-group v-model="form.status" :disabled="!isAdmin">
                <el-radio label="1">待院审</el-radio>
                <el-radio label="2">待校审</el-radio>
                <el-radio label="3" class="success-radio">已通过</el-radio>
                <el-radio label="4" class="danger-radio">已驳回</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider v-if="form.achievementId">申报详情</el-divider>

        <el-form-item label="成果标题" prop="title">
          <el-input
            v-model="form.title"
            placeholder="请输入成果标题"
            :disabled="form.status === '3'"
          />
        </el-form-item>

        <el-form-item label="成果类型" prop="category">
          <el-radio-group v-model="form.category" :disabled="form.status === '3'">
            <el-radio
              v-for="dict in dict.type.edu_achievement_category"
              :key="dict.value"
              :label="dict.value"
            >{{ dict.label }}</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="归属学院" prop="collegeId">
          <el-select
            v-model="form.collegeId"
            placeholder="请选择学院"
            style="width: 100%"
            clearable
            :disabled="form.status === '3'"
          >
            <el-option v-for="item in collegeOptions" :key="item.deptId" :label="item.deptName" :value="item.deptId" />
          </el-select>
        </el-form-item>

        <el-form-item label="证明材料" prop="fileUrl">
          <file-upload v-model="form.fileUrl" :limit="5" :fileSize="50" v-if="form.status !== '3'" />
          <div v-else class="file-link-view">
            <el-link v-for="(url, i) in (form.fileUrl || '').split(',')" :key="i" :href="url" target="_blank" type="primary">预览附件 {{i+1}}</el-link>
          </div>
        </el-form-item>

        <el-form-item label="成果详述" prop="content">
          <editor v-model="form.content" :min-height="200" :readOnly="form.status === '3'" />
        </el-form-item>

        <el-form-item style="text-align: center; margin-top: 30px;">
          <el-button type="primary" :loading="loading" @click="submitForm">
            {{ form.achievementId ? '保存修改/审核结果' : '立即提交申报' }}
          </el-button>
          <el-button @click="handleClose">返回列表</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
// 导入增加获取详情和更新的接口
import { teacherAddAchievement , teacherListAchievement,teacherUpdateAchievement ,} from "@/api/achievement/achievement";
import { listDept } from "@/api/system/dept";

export default {
  name: "MyAchievement",
  dicts: ['edu_achievement_category'],
  data() {
    return {
      loading: false,
      collegeOptions: [],
      form: {
        achievementId: undefined,
        title: undefined,
        category: '1',
        collegeId: undefined,
        fileUrl: undefined,
        content: undefined,
        status: '1'
      },
      rules: {
        title: [{ required: true, message: "标题必填", trigger: "blur" }],
        category: [{ required: true, message: "请选择成果类型", trigger: "change" }],
        collegeId: [{ required: true, message: "请选择学院", trigger: "change" }],
        content: [{ required: true, message: "详述不能为空", trigger: "blur" }]
      }
    };
  },
  // 在 computed 中增加判断（更简洁）
  computed: {
    // 判断当前用户是否为管理员
    isAdmin() {
      // 获取用户角色的标准方式
      const roles = this.$store.getters && this.$store.getters.roles;
      return roles.includes('admin');
    }
  },
  created() {
    this.getCollegeList();
    // 关键：判断 URL 是否带有 ID，如果有则是修改/审核模式
    const achievementId = this.$route.query.id;
    if (achievementId) {
      this.handleGetDetail(achievementId);
    }
  },
  methods: {
    /** 获取成果详情回显 */
    handleGetDetail(id) {
      this.loading = true;
      teacherListAchievement(id).then(response => {
        this.form = response.data;
        this.loading = false;
      });
    },
    /** 查询学院列表 */
    getCollegeList() {
      listDept().then(response => {
        this.collegeOptions = response.data;
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          this.loading = true;
          // 根据是否有 ID 判断是新增还是修改
          if (this.form.achievementId) {
            teacherUpdateAchievement(this.form).then(response => {
              this.$modal.msgSuccess("管理修改成功");
              this.loading = false;
              this.$router.back(); // 修改完返回
            });
          } else {
            teacherAddAchievement(this.form).then(response => {
              this.$modal.msgSuccess("申报成功");
              this.loading = false;
              this.reset();
            });
          }
        }
      });
    },
    handleClose() {
      this.$router.back();
    },
    reset() {
      this.form = {
        achievementId: undefined,
        title: undefined,
        category: '1',
        collegeId: undefined,
        fileUrl: undefined,
        content: undefined,
        status: '1'
      };
      this.resetForm("form");
    }
  }
};
</script>

<style scoped>
.audit-section {
  background-color: #fafafa;
  padding: 20px 10px 5px 10px;
  border-radius: 8px;
  border-left: 5px solid #409EFF;
  margin-bottom: 20px;
}
.success-radio /deep/ .el-radio__input.is-checked + .el-radio__label {
  color: #67C23A;
}
.danger-radio  /deep/ .el-radio__input.is-checked + .el-radio__label {
  color: #F56C6C;
}
.file-link-view .el-link {
  margin-right: 15px;
}
</style>
