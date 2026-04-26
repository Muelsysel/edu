<template>
  <div class="app-container">
    <div class="form-card">
      <div class="form-header">
        <div class="form-header-icon">
          <i :class="form.achievementId ? 'el-icon-edit-outline' : 'el-icon-document-add'"></i>
        </div>
        <div class="form-header-text">
          <h3>{{ form.achievementId ? '修改我的成果' : '教学成果申报' }}</h3>
          <p>{{ form.achievementId ? '修改已有成果信息，修改后将重新进入审核流程' : '填写教学成果信息并提交审核' }}</p>
        </div>
      </div>

      <el-form ref="form" :model="form" :rules="rules" label-width="120px" class="modern-form">
        <!-- 审核状态展示（仅编辑时） -->
        <div v-if="form.achievementId" class="status-display">
          <div class="status-steps">
           <div :class="['step', form.status >= '2' ? 'step-active' : '']">
             <div class="step-dot"></div>
             <span>提交申报</span>
           </div>
           <div class="step-line" :class="{ 'line-active': form.status >= '2' }"></div>
          <div :class="['step', form.status >= '2' ? 'step-active' : '', form.status === '4' ? 'step-error' : '']">
            <div class="step-dot"></div>
            <span>审核中</span>
          </div>
          <div class="step-line" :class="{ 'line-active': form.status >= '3' }"></div>
          <div :class="['step', form.status >= '3' ? 'step-active step-success' : '']">
            <div class="step-dot"></div>
            <span>审核通过</span>
          </div>
          </div>
          <el-tag v-if="form.status === '4'" type="danger" effect="dark" size="small"
            style="margin-top: 8px;">该成果已被驳回，请修改后重新提交</el-tag>
        </div>

        <el-divider v-if="form.achievementId" content-position="left">成果信息</el-divider>

        <el-form-item label="成果标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入成果标题" :disabled="form.status === '3'" />
        </el-form-item>

        <el-form-item label="成果类型" prop="category">
          <el-radio-group v-model="form.category" :disabled="form.status === '3'">
            <el-radio-button v-for="dict in dict.type.edu_achievement_category" :key="dict.value" :label="dict.value">{{
              dict.label }}</el-radio-button>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="归属学院" prop="collegeId">
          <el-select v-model="form.collegeId" placeholder="请选择学院" style="width: 100%" clearable
            :disabled="form.status === '3'">
            <el-option v-for="item in collegeOptions" :key="item.deptId" :label="item.deptName" :value="item.deptId" />
          </el-select>
        </el-form-item>

        <el-form-item label="证明材料" prop="fileUrl">
          <file-upload v-model="form.fileUrl" :limit="5" :fileSize="50" v-if="form.status !== '3'" />
          <div v-else class="file-preview">
            <el-link v-for="(url, i) in (form.fileUrl || '').split(',')" :key="i" :href="url" target="_blank"
              type="primary" v-if="url" :underline="false" class="file-link">
              <i class="el-icon-document"></i> 附件 {{ i + 1 }}
            </el-link>
          </div>
        </el-form-item>

        <el-form-item label="成果详述" prop="content">
          <editor v-model="form.content" :min-height="200" :readOnly="form.status === '3'" />
        </el-form-item>

        <el-form-item style="padding-top: 12px;">
          <el-button type="primary" :loading="loading" @click="submitForm" icon="el-icon-check">
            {{ form.achievementId ? '保存修改' : '提交申报' }}
          </el-button>
          <el-button @click="handleClose" icon="el-icon-back">返回列表</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script>
import { teacherAddAchievement, teacherListAchievement, teacherUpdateAchievement, teacherGetAchievement } from "@/api/achievement/achievement";
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
        category: '',
        collegeId: undefined,
        fileUrl: undefined,
        content: undefined,
        status: '2'
      },
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
    const achievementId = this.$route.query.id;
    if (achievementId) {
      this.handleGetDetail(achievementId);
    }
  },
  methods: {
    handleGetDetail(id) {
      this.loading = true;
      teacherGetAchievement(id).then(response => {
        this.form = response.data;
        this.loading = false;
      });
    },
    getCollegeList() {
      listDept().then(response => {
        this.collegeOptions = response.data;
      });
    },
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          this.loading = true;
          if (this.form.achievementId) {
            teacherUpdateAchievement(this.form).then(() => {
              this.$modal.msgSuccess("修改成功");
              this.loading = false;
              this.$router.back();
            }).catch(() => { this.loading = false; });
          } else {
            teacherAddAchievement(this.form).then(() => {
              this.$modal.msgSuccess("申报成功");
              this.loading = false;
              this.reset();
            }).catch(() => { this.loading = false; });
          }
        }
      });
    },
    handleClose() {
      this.$router.back();
    },
    reset() {
      this.form = {
        achievementId: undefined, title: undefined, category: '1',
        collegeId: undefined, fileUrl: undefined, content: undefined, status: '2'
      };
      this.resetForm("form");
    }
  }
};
</script>

<style scoped>
.form-card {
  background: #fff;
  border-radius: 14px;
  padding: 0;
  border: 1px solid #e2e8f0;
  box-shadow: 0 1px 3px rgba(15, 23, 42, 0.04);
  overflow: hidden;
}

.form-header {
  background: linear-gradient(135deg, #0f172a 0%, #1e40af 100%);
  padding: 28px 32px;
  display: flex;
  align-items: center;
  gap: 16px;
}

.form-header-icon {
  width: 48px;
  height: 48px;
  background: rgba(212, 168, 83, 0.15);
  border: 1px solid rgba(212, 168, 83, 0.3);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 22px;
  color: #d4a853;
}

.form-header-text h3 {
  color: #f1f5f9;
  margin: 0 0 4px;
  font-size: 18px;
  font-family: 'Noto Serif SC', serif;
  font-weight: 700;
}

.form-header-text p {
  color: rgba(203, 213, 225, 0.7);
  margin: 0;
  font-size: 13px;
}

.modern-form {
  padding: 28px 32px;
}

/* 审核进度 */
.status-display {
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  padding: 20px 24px;
  margin-bottom: 20px;
}

.status-steps {
  display: flex;
  align-items: center;
  justify-content: center;
}

.step {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
}

.step-dot {
  width: 14px;
  height: 14px;
  border-radius: 50%;
  background: #cbd5e1;
  transition: all .3s;
}

.step span {
  font-size: 12px;
  color: #94a3b8;
}

.step-active .step-dot {
  background: #1e40af;
  box-shadow: 0 0 0 4px rgba(30, 64, 175, 0.12);
}

.step-active span {
  color: #1e40af;
  font-weight: 600;
}

.step-success .step-dot {
  background: #10b981;
  box-shadow: 0 0 0 4px rgba(16, 185, 129, 0.12);
}

.step-success span {
  color: #10b981;
}

.step-error .step-dot {
  background: #ef4444;
  box-shadow: 0 0 0 4px rgba(239, 68, 68, 0.12);
}

.step-error span {
  color: #ef4444;
}

.step-line {
  width: 60px;
  height: 2px;
  background: #e2e8f0;
  margin: 0 8px;
  margin-bottom: 20px;
}

.line-active {
  background: #1e40af;
}

/* 附件 */
.file-preview {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.file-link {
  padding: 6px 14px;
  background: rgba(30, 64, 175, 0.06);
  border-radius: 8px;
  font-size: 13px;
}
</style>
