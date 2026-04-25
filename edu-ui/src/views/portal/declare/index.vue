<template>
  <div class="declare-container">
    <div class="elegant-doc-wrapper">
      <div class="doc-header">
        <div class="title-section">
          <div class="icon-box"><i class="el-icon-edit-outline"></i></div>
          <div>
            <h1 class="doc-title">填写教学成果</h1>
            <p class="doc-subtitle">请准确填写成果信息，系统将在后台为您每 15 秒自动保存一次草稿。</p>
          </div>
        </div>

        <div class="save-status">
          <transition name="fade" mode="out-in">
            <span v-if="saving" class="status-badge saving" key="saving">
              <i class="el-icon-loading"></i> 云端同步中...
            </span>
            <span v-else class="status-badge saved" key="saved">
              <i class="el-icon-circle-check"></i> 已保存至本地
            </span>
          </transition>
        </div>
      </div>

      <el-collapse-transition>
        <div v-if="draftRestored" class="draft-notice">
          <i class="el-icon-info"></i>
          <span>系统检测到您上次未完成的草稿，已为您自动恢复。</span>
          <i class="el-icon-close close-btn" @click="draftRestored = false"></i>
        </div>
      </el-collapse-transition>

      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-position="top"
        class="modern-form"
      >
        <div class="form-section">
          <h3 class="section-title">基本信息</h3>

          <el-form-item label="成果标题" prop="title">
            <el-input
              v-model="form.title"
              placeholder="请输入精准、简明的成果名称（如：基于微服务架构的软件工程专业教学改革与实践）"
              maxlength="120"
              show-word-limit
              class="huge-input"
            />
          </el-form-item>

          <el-row :gutter="24">
            <el-col :xs="24" :sm="8">
              <el-form-item label="成果归属类型" prop="category">
                <el-select v-model="form.category" placeholder="请选择类型" class="full-width-select" popper-class="elegant-select-dropdown">
                  <el-option v-for="dict in dict.type.edu_achievement_category" :key="dict.value" :label="dict.label" :value="dict.value" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :xs="24" :sm="8">
              <el-form-item label="申报等级" prop="level">
                <el-select v-model="form.level" placeholder="请选择申报等级" class="full-width-select" popper-class="elegant-select-dropdown">
                  <el-option v-for="dict in dict.type.edu_achievement_level" :key="dict.value" :label="dict.label" :value="dict.value" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :xs="24" :sm="8">
              <el-form-item label="申报人所属学院" prop="collegeId">
                <el-select v-model="form.collegeId" placeholder="请选择学院" class="full-width-select" popper-class="elegant-select-dropdown" filterable>
                  <el-option v-for="item in collegeOptions" :key="item.deptId" :label="item.deptName" :value="item.deptId" />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
        </div>

        <div class="form-section">
          <h3 class="section-title">佐证与详述</h3>

          <el-form-item label="佐证材料附件" prop="fileUrl">
            <div class="upload-wrapper">
              <file-upload v-model="form.fileUrl" :limit="5" :fileSize="50" drag />
              <div class="upload-tip">
                <i class="el-icon-warning-outline"></i> 支持格式：doc / docx / xls / xlsx / ppt / pptx / txt / pdf。单文件限制：50MB，最多支持 5 个文件。
              </div>
            </div>
          </el-form-item>

          <el-form-item label="成果详细报告" prop="content" class="editor-item">
            <div class="editor-wrapper">
              <editor v-model="form.content" :min-height="360" placeholder="请详细阐述成果的研究背景、主要解决的教学问题、解决方法及创新点、推广应用效果..." />
            </div>
          </el-form-item>
        </div>

        <div class="form-actions">
          <el-button @click="saveDraft" :loading="saving" round class="action-btn outline-btn">
            <i class="el-icon-folder"></i> 暂存草稿箱
          </el-button>
          <el-button type="primary" @click="submitForm" :loading="submitting" round class="action-btn submit-btn">
            正式提交申报 <i class="el-icon-s-promotion"></i>
          </el-button>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script>
import { listDept } from '@/api/system/dept'
import { teacherAddAchievement } from '@/api/achievement/achievement'

const DRAFT_KEY = 'edu-ui-portal-achievement-draft'

export default {
  name: 'PortalDeclare',
  // 注意这里新增了 edu_achievement_level 字典
  dicts: ['edu_achievement_category', 'edu_achievement_level'],
  data() {
    return {
      saving: false,
      submitting: false,
      draftRestored: false,
      autoSaveTimer: null,
      collegeOptions: [],
      form: {
        title: '',
        category: '',
        level: '', // 新增等级字段
        collegeId: '',
        fileUrl: '',
        content: '',
        status: '0'
      },
      rules: {
        title: [{ required: true, message: '请输入成果标题', trigger: 'blur' }],
        category: [{ required: true, message: '请选择成果类型', trigger: 'change' }],
        level: [{ required: true, message: '请选择申报等级', trigger: 'change' }], // 新增必填校验
        collegeId: [{ required: true, message: '请选择所属学院', trigger: 'change' }],
        content: [{ required: true, message: '请输入成果描述', trigger: 'blur' }]
      }
    }
  },
  created() {
    this.getCollegeList();
    this.restoreDraft();
    this.startAutoSave();
  },
  beforeDestroy() {
    if (this.autoSaveTimer) {
      clearInterval(this.autoSaveTimer)
    }
  },
  methods: {
    getCollegeList() {
      listDept().then(res => {
        this.collegeOptions = res.data || []
      })
    },
    restoreDraft() {
      const raw = localStorage.getItem(DRAFT_KEY)
      if (raw) {
        try {
          this.form = { ...this.form, ...JSON.parse(raw) };
          this.draftRestored = true;
        } catch (e) {
          localStorage.removeItem(DRAFT_KEY)
        }
      }
    },
    persistDraft() {
      localStorage.setItem(DRAFT_KEY, JSON.stringify(this.form))
    },
    startAutoSave() {
      this.autoSaveTimer = setInterval(() => {
        if(!this.form.title && !this.form.content) return;
        this.saving = true;
        this.persistDraft();
        setTimeout(() => this.saving = false, 800)
      }, 15000)
    },
    saveDraft() {
      this.saving = true;
      this.form.status = '0';
      this.persistDraft();
      this.$message.success('草稿已成功保存至本地！');
      this.saving = false;
    },
    submitForm() {
      this.$refs.formRef.validate(valid => {
        if (!valid) {
          this.$message.warning('请完善必填信息后再提交');
          return;
        }
        this.$confirm('确认提交该成果吗？提交后将进入审核流程，在此期间您无法修改内容。', '提交确认', {
          confirmButtonText: '确认提交',
          cancelButtonText: '再检查一下',
          type: 'warning',
          customClass: 'elegant-confirm-box'
        }).then(() => {
          this.submitting = true
          teacherAddAchievement({ ...this.form, status: '2' }).then(() => {
            this.$message.success('申报提交成功！请耐心等待审核结果。')
            localStorage.removeItem(DRAFT_KEY)
            this.$router.push('/portal/mine')
          }).finally(() => { this.submitting = false })
        }).catch(() => {})
      })
    }
  }
}
</script>

<style lang="scss" scoped>
/* 变量定义 */
$primary-blue: #2563eb;
$bg-color: #f8fafc;
$text-dark: #0f172a;
$text-normal: #334155;
$text-light: #64748b;
$border-color: #e2e8f0;

.declare-container {
  padding: 32px 24px 60px;
  background-color: transparent;
  display: flex;
  justify-content: center;
}

.elegant-doc-wrapper {
  background: #ffffff;
  border-radius: 16px;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.03);
  border: 1px solid rgba(226, 232, 240, 0.6);
  width: 100%;
  max-width: 960px;
  padding: 48px 56px;
  position: relative;
}

/* 头部样式 */
.doc-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 32px;
  padding-bottom: 24px;
  border-bottom: 1px dashed $border-color;
}

.title-section {
  display: flex;
  align-items: center;
  gap: 16px;
}

.icon-box {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  background: #eff6ff;
  color: $primary-blue;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
}

.doc-title {
  font-size: 28px;
  font-weight: 600;
  color: $text-dark;
  margin: 0 0 6px 0;
  letter-spacing: 0.5px;
}

.doc-subtitle {
  font-size: 14px;
  color: $text-light;
  margin: 0;
}

/* 状态指示器 */
.save-status {
  padding-top: 8px;
}
.status-badge {
  font-size: 13px;
  padding: 6px 12px;
  border-radius: 20px;
  display: inline-flex;
  align-items: center;
  gap: 6px;
}
.status-badge.saved { color: #10b981; background: #ecfdf5; }
.status-badge.saving { color: $primary-blue; background: #eff6ff; }

/* 草稿恢复提示 */
.draft-notice {
  display: flex;
  align-items: center;
  gap: 10px;
  background: #fffbeb;
  border: 1px solid #fde68a;
  color: #b45309;
  padding: 12px 16px;
  border-radius: 8px;
  margin-bottom: 32px;
  font-size: 14px;

  .close-btn {
    margin-left: auto;
    cursor: pointer;
    opacity: 0.6;
    &:hover { opacity: 1; }
  }
}

/* 表单整体美化 (Top Label 风格) */
.modern-form ::v-deep .el-form-item__label {
  font-weight: 600;
  color: $text-dark;
  font-size: 15px;
  padding-bottom: 6px;
  line-height: 1.2;
}

/* 模块隔离区 */
.form-section {
  margin-bottom: 40px;

  .section-title {
    font-size: 18px;
    font-weight: 600;
    color: $primary-blue;
    margin: 0 0 20px 0;
    display: flex;
    align-items: center;
    &::before {
      content: '';
      display: inline-block;
      width: 4px;
      height: 16px;
      background: $primary-blue;
      border-radius: 2px;
      margin-right: 8px;
    }
  }
}

/* 输入框定制 */
.modern-form ::v-deep .el-input__inner {
  border-radius: 8px;
  border: 1px solid $border-color;
  background-color: $bg-color;
  padding: 0 16px;
  height: 44px;
  line-height: 44px;
  font-size: 15px;
  color: $text-normal;
  transition: all 0.2s;

  &:hover { border-color: #cbd5e1; }
  &:focus {
    background-color: #ffffff;
    border-color: $primary-blue;
    box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.1);
  }
}

.huge-input ::v-deep .el-input__inner {
  height: 52px;
  font-size: 16px;
}

.full-width-select { width: 100%; }

/* 上传区优化 */
.upload-wrapper {
  background: #fafbfc;
  border: 1px dashed $border-color;
  border-radius: 12px;
  padding: 24px;
  transition: all 0.3s;
  &:hover { border-color: $primary-blue; background: #f8fafc; }
}

.upload-wrapper ::v-deep .el-upload-dragger {
  background-color: transparent;
  border: none;
  width: 100%;
  height: auto;
}

.upload-tip {
  margin-top: 12px;
  font-size: 13px;
  color: $text-light;
  line-height: 1.6;
  i { color: #f59e0b; margin-right: 4px; }
}

/* 富文本编辑器包装 */
.editor-wrapper {
  border-radius: 8px;
  overflow: hidden;
  border: 1px solid $border-color;
  transition: border-color 0.3s;
  &:hover { border-color: #cbd5e1; }
}

/* 底部操作区 */
.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 16px;
  margin-top: 48px;
  padding-top: 24px;
  border-top: 1px solid #f1f5f9;
}

.action-btn {
  padding: 12px 32px;
  font-size: 15px;
  font-weight: 500;
  transition: all 0.3s;
}

.outline-btn {
  color: $text-normal;
  border-color: #cbd5e1;
  background: transparent;
  &:hover {
    color: $primary-blue;
    border-color: $primary-blue;
    background: #eff6ff;
  }
}

.submit-btn {
  background: $primary-blue;
  border-color: $primary-blue;
  box-shadow: 0 4px 12px rgba(37, 99, 235, 0.2);
  &:hover {
    background: #1d4ed8;
    transform: translateY(-1px);
    box-shadow: 0 6px 16px rgba(37, 99, 235, 0.3);
  }
}

/* 响应式适配 */
@media (max-width: 768px) {
  .declare-container { padding: 16px; }
  .elegant-doc-wrapper { padding: 32px 24px; }
  .doc-header { flex-direction: column; gap: 16px; align-items: flex-start;}
  .form-actions { flex-direction: column-reverse; }
  .action-btn { width: 100%; margin-left: 0 !important;}
}

/* Vue 过渡动画 */
.fade-enter-active, .fade-leave-active { transition: opacity 0.3s; }
.fade-enter, .fade-leave-to { opacity: 0; }
</style>

<style>
/* 全局样式覆盖 */
.elegant-select-dropdown {
  border-radius: 8px !important;
  box-shadow: 0 10px 24px rgba(0, 0, 0, 0.08) !important;
}
.elegant-confirm-box {
  border-radius: 12px !important;
  padding-bottom: 20px;
}
.elegant-confirm-box .el-message-box__title {
  font-weight: 600;
}
.elegant-confirm-box .el-button {
  border-radius: 6px;
}
</style>
