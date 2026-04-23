<template>
  <div class="form-container">
    <div class="elegant-panel doc-form">
      <div class="doc-header">
        <div class="doc-title-wrapper">
          <h2 class="doc-title">教师教学成果申报表</h2>
          <span class="doc-subtitle">请如实填写成果信息，系统将每15秒自动保存草稿</span>
        </div>
        <el-tag size="small" type="success" v-if="saving"><i class="el-icon-loading"></i> 保存中</el-tag>
        <el-tag size="small" type="info" v-else><i class="el-icon-document-checked"></i> 已自动保存</el-tag>
      </div>

      <el-alert v-if="draftRestored" title="检测到未提交的草稿，已为您自动恢复。" type="warning" show-icon class="draft-alert" @close="draftRestored = false" />

      <el-form ref="formRef" :model="form" :rules="rules" label-width="120px" class="elegant-form" label-position="left">
        <el-row :gutter="24">
          <el-col :span="24">
            <el-form-item label="成果标题" prop="title">
              <el-input v-model="form.title" placeholder="请输入精准、简明的成果名称" maxlength="120" show-word-limit />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="成果类型" prop="category">
              <el-select v-model="form.category" placeholder="请选择成果归属类别" style="width: 100%">
                <el-option v-for="dict in dict.type.edu_achievement_category" :key="dict.value" :label="dict.label" :value="dict.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="所属学院" prop="collegeId">
              <el-select v-model="form.collegeId" placeholder="请选择您所在的学院" style="width: 100%">
                <el-option v-for="item in collegeOptions" :key="item.deptId" :label="item.deptName" :value="item.deptId" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="佐证材料" prop="fileUrl">
          <file-upload v-model="form.fileUrl" :limit="5" :fileSize="50" drag />
          <div class="form-tip">支持上传 PDF、Word、图片等格式，单文件不超过 50MB。</div>
        </el-form-item>

        <el-form-item label="成果详细描述" prop="content">
          <editor v-model="form.content" :min-height="300" placeholder="请详细阐述成果的背景、实施过程、创新点及应用效果..." />
        </el-form-item>

        <div class="form-actions">
          <el-button @click="saveDraft" :loading="saving" round>手动保存草稿</el-button>
          <el-button type="primary" @click="submitForm" :loading="submitting" round class="submit-btn">正式提交申报</el-button>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script>
// 脚本部分与原版逻辑一致，无需大改，仅保留必要逻辑
import { listDept } from '@/api/system/dept'
import { teacherAddAchievement } from '@/api/achievement/achievement'

const DRAFT_KEY = 'edu-ui-portal-achievement-draft'

export default {
  name: 'PortalDeclare',
  dicts: ['edu_achievement_category'],
  data() {
    return {
      saving: false, submitting: false, draftRestored: false, autoSaveTimer: null, collegeOptions: [],
      form: { title: '', category: '', collegeId: '', fileUrl: '', content: '', status: '0' },
      rules: {
        title: [{ required: true, message: '请输入成果标题', trigger: 'blur' }],
        category: [{ required: true, message: '请选择成果类型', trigger: 'change' }],
        collegeId: [{ required: true, message: '请选择所属学院', trigger: 'change' }],
        content: [{ required: true, message: '请输入成果描述', trigger: 'blur' }]
      }
    }
  },
  created() {
    this.getCollegeList(); this.restoreDraft(); this.startAutoSave();
  },
  beforeDestroy() { clearInterval(this.autoSaveTimer) },
  methods: {
    getCollegeList() { listDept().then(res => { this.collegeOptions = res.data || [] }) },
    restoreDraft() {
      const raw = localStorage.getItem(DRAFT_KEY)
      if (raw) {
        try { this.form = { ...this.form, ...JSON.parse(raw) }; this.draftRestored = true }
        catch (e) { localStorage.removeItem(DRAFT_KEY) }
      }
    },
    persistDraft() { localStorage.setItem(DRAFT_KEY, JSON.stringify(this.form)) },
    startAutoSave() { this.autoSaveTimer = setInterval(() => { this.saving = true; this.persistDraft(); setTimeout(() => this.saving = false, 800) }, 15000) },
    saveDraft() { this.saving = true; this.form.status = '0'; this.persistDraft(); this.$message.success('草稿保存成功'); this.saving = false; },
    submitForm() {
      this.$refs.formRef.validate(valid => {
        if (!valid) return;
        this.$confirm('确认提交该成果吗？提交后将流转至学院进行初审。', '提交确认', { confirmButtonText: '确认提交', cancelButtonText: '再检查一下', type: 'warning' }).then(() => {
          this.submitting = true
          teacherAddAchievement({ ...this.form, status: '1' }).then(() => {
            this.$message.success('申报提交成功！')
            localStorage.removeItem(DRAFT_KEY) // 提交成功清理草稿
            this.$router.push('/portal/mine')
          }).finally(() => { this.submitting = false })
        })
      })
    }
  }
}
</script>

<style scoped>
.form-container { padding: 24px; max-width: 1000px; margin: 0 auto; }
.elegant-panel { background: #fff; border-radius: 12px; box-shadow: 0 4px 20px rgba(0,0,0,0.03); padding: 40px; border: 1px solid #f1f5f9; }
.doc-header { display: flex; justify-content: space-between; align-items: flex-end; margin-bottom: 32px; padding-bottom: 20px; border-bottom: 1px solid #f1f5f9; }
.doc-title { font-size: 26px; color: #1e293b; margin: 0 0 8px 0; font-weight: 600; }
.doc-subtitle { font-size: 14px; color: #94a3b8; }
.draft-alert { margin-bottom: 24px; border-radius: 8px; }
/* 表单美化 */
.elegant-form ::v-deep .el-form-item__label { font-weight: 500; color: #334155; }
.elegant-form ::v-deep .el-input__inner, .elegant-form ::v-deep .el-textarea__inner { border-radius: 6px; border-color: #cbd5e1; background: #f8fafc; }
.elegant-form ::v-deep .el-input__inner:focus { background: #fff; border-color: #2563eb; }
.form-tip { font-size: 12px; color: #94a3b8; margin-top: 6px; line-height: 1.4; }
.form-actions { margin-top: 40px; padding-top: 20px; border-top: 1px solid #f1f5f9; display: flex; justify-content: center; gap: 20px; }
.submit-btn { padding: 12px 40px; font-size: 15px; }
</style>
