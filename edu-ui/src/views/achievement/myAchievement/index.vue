<template>
  <div class="app-container">
    <el-card shadow="hover" header="📝 教学成果申报填写">
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">

        <el-form-item label="成果标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入成果标题" />
        </el-form-item>

        <el-form-item label="成果类型" prop="category">
          <el-radio-group v-model="form.category">
            <el-radio
              v-for="dict in dict.type.edu_achievement_category"
              :key="dict.value"
              :label="dict.value"
            >{{ dict.label }}</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="归属学院" prop="collegeId">
          <el-select v-model="form.collegeId" placeholder="请选择学院" style="width: 100%" clearable>
            <el-option v-for="item in collegeOptions" :key="item.deptId" :label="item.deptName" :value="item.deptId" />
          </el-select>
        </el-form-item>

        <el-form-item label="证明材料" prop="fileUrl">
          <file-upload v-model="form.fileUrl" :limit="5" :fileSize="50" />
          <div class="el-upload__tip" style="color: #e6a23c">最多上传 5 个附件</div>
        </el-form-item>

        <el-form-item label="成果详述" prop="content">
          <editor v-model="form.content" :min-height="200" />
        </el-form-item>

        <el-form-item style="text-align: center">
          <el-button type="primary" :loading="loading" @click="submitForm">提交申报</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
// 必须导入 API
import { myAddAchievement } from "@/api/achievement/achievement";
import { listDept } from "@/api/system/dept";

export default {
  name: "MyAchievement",
  // 核心：必须声明 dicts，RuoYi 才会去后台查字典
  dicts: ['edu_achievement_category'],
  data() {
    return {
      loading: false,
      // 学院选项存储
      collegeOptions: [],
      // 表单参数
      form: {
        title: undefined,
        category: '1',
        collegeId: undefined,
        fileUrl: undefined,
        content: undefined
      },
      // 表单校验
      rules: {
        title: [{ required: true, message: "标题必填", trigger: "blur" }],
        category: [{ required: true, message: "请选择成果类型", trigger: "change" }],
        collegeId: [{ required: true, message: "请选择学院", trigger: "change" }],
        content: [{ required: true, message: "详述不能为空", trigger: "blur" }]
      }
    };
  },
  created() {
    this.getCollegeList();
  },
  methods: {
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
          myAddAchievement(this.form).then(response => {
            this.$modal.msgSuccess("申报成功");
            this.loading = false;
            this.reset();
          }).catch(() => {
            this.loading = false;
          });
        }
      });
    },
    /** 表单重置 */
    reset() {
      this.form = {
        title: undefined,
        category: '1',
        collegeId: undefined,
        fileUrl: undefined,
        content: undefined
      };
      this.resetForm("form");
    }
  }
};
</script>
