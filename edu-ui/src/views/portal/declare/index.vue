<template>
  <div class="portal-declare-page">
    <section class="page-head">
      <div>
        <span>Achievement Application</span>
        <h1>教学成果申报</h1>
        <p>填写成果基础信息、佐证材料与成果报告，提交后进入学校审核流程。</p>
      </div>
      <div class="save-state">
        <i :class="saving ? 'el-icon-loading' : 'el-icon-circle-check'" />
        {{ saving ? '正在保存' : '草稿已就绪' }}
      </div>
    </section>

    <el-collapse-transition>
      <div v-if="draftRestored" class="draft-notice">
        <i class="el-icon-info" />
        <span>已载入可编辑内容。</span>
        <i class="el-icon-close close-btn" @click="draftRestored = false" />
      </div>
    </el-collapse-transition>

    <div class="category-nav">
      <span class="nav-label">成果类型</span>
      <div class="category-grid">
        <div
          v-for="item in dict.type.edu_achievement_category"
          :key="item.value"
          class="category-cell"
          :class="{ selected: form.category === item.value }"
          @click="form.category = item.value"
        >
          <span class="cell-label">{{ item.label }}</span>
          <span class="cell-desc">{{ categoryDesc(item.value) }}</span>
        </div>
      </div>
    </div>

    <el-form
      ref="formRef"
      :model="form"
      :rules="rules"
      label-position="top"
      class="declare-form"
    >
      <section class="form-panel">
        <div class="section-title">
          <strong>01</strong>
          <h2>基本信息</h2>
        </div>

        <el-form-item label="成果标题" prop="title">
          <el-input
            v-model="form.title"
            placeholder="请输入成果标题"
            maxlength="120"
            show-word-limit
          />
        </el-form-item>

        <el-row :gutter="22">

          <el-col :xs="24" :sm="8">
            <el-form-item label="申报等级" prop="level">
              <el-select v-model="form.level" placeholder="请选择" filterable>
                <el-option v-for="dict in dict.type.edu_achievement_level" :key="dict.value" :label="dict.label" :value="dict.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="8">
            <el-form-item label="所属学院" prop="collegeId">
              <el-select v-model="form.collegeId" placeholder="请选择" filterable>
                <el-option v-for="item in collegeOptions" :key="item.deptId" :label="item.deptName" :value="item.deptId" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item prop="category" style="display:none">
          <el-input v-model="form.category" />
        </el-form-item>
      </section>

      <section class="form-panel">
        <div class="section-title">
          <strong>02</strong>
          <h2>材料与报告</h2>
        </div>

        <el-form-item label="佐证材料" prop="fileUrl">
          <div class="upload-area">
            <file-upload v-model="form.fileUrl" :limit="5" :fileSize="50" drag />
          </div>
          <div class="field-tip">支持 doc、docx、xls、xlsx、ppt、pptx、txt、pdf，单文件不超过 50MB。</div>
        </el-form-item>

        <el-form-item label="成果详细报告" prop="content">
          <div class="editor-box">
            <editor
              v-model="form.content"
              :min-height="380"
              placeholder="请详细阐述成果背景、教学问题、解决方法、创新点和应用效果。"
            />
          </div>
        </el-form-item>
      </section>

      <div class="form-actions">
        <el-button icon="el-icon-folder" :loading="saving" @click="saveDraft">暂存草稿</el-button>
        <el-button type="primary" icon="el-icon-s-promotion" :loading="submitting" @click="submitForm">正式提交申报</el-button>
      </div>
    </el-form>
  </div>
</template>

<script>
import { listDept } from '@/api/system/dept'
import { teacherAddAchievement, teacherGetAchievement, teacherUpdateAchievement, teacherResubmit } from '@/api/achievement/achievement'

const DRAFT_KEY = 'edu-ui-portal-achievement-draft'

export default {
  name: 'PortalDeclare',
  dicts: ['edu_achievement_category', 'edu_achievement_level'],
  data() {
    return {
      saving: false,
      submitting: false,
      draftRestored: false,
      autoSaveTimer: null,
      collegeOptions: [],
      form: {
        achievementId: undefined,
        title: '',
        category: '',
        level: '',
        collegeId: '',
        fileUrl: '',
        content: '',
        status: '0'
      },
      rules: {
        title: [{ required: true, message: '请输入成果标题', trigger: 'blur' }],
        category: [{ required: true, message: '请选择成果类型', trigger: 'change' }],
        level: [{ required: true, message: '请选择申报等级', trigger: 'change' }],
        collegeId: [{ required: true, message: '请选择所属学院', trigger: 'change' }],
        content: [{ required: true, message: '请输入成果描述', trigger: 'blur' }]
      }
    }
  },
  created() {
    this.getCollegeList()
    const id = this.$route.query.id
    if (id) {
      this.loadAchievement(id)
    } else {
      this.restoreDraft()
    }
    this.startAutoSave()
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
          this.form = { ...this.form, ...JSON.parse(raw) }
          this.draftRestored = true
        } catch (e) {
          localStorage.removeItem(DRAFT_KEY)
        }
      }
    },
    loadAchievement(id) {
      teacherGetAchievement(id).then(res => {
        const data = res.data || {}
        this.form = {
          achievementId: data.achievementId,
          title: data.title || '',
          category: data.category || '',
          level: data.level || '',
          collegeId: data.collegeId || '',
          fileUrl: data.fileUrl || '',
          content: data.content || '',
          status: data.status || '2'
        }
        this.draftRestored = true
      }).catch(() => {
        this.$message.error('加载成果失败')
      })
    },
    persistDraft() {
      localStorage.setItem(DRAFT_KEY, JSON.stringify(this.form))
    },
    startAutoSave() {
      this.autoSaveTimer = setInterval(() => {
        if (!this.form.title && !this.form.content) return
        this.saving = true
        this.persistDraft()
        setTimeout(() => {
          this.saving = false
        }, 800)
      }, 15000)
    },
    categoryDesc(val) {
      const map = {
        '1': '期刊论文、专著等',
        '2': '规划教材、校本教材',
        '3': '指导学生获奖',
        '4': '教学改革研究',
        '5': '教学评估工作',
        '6': '课程建设'
      };
      return map[val] || '';
    },
    saveDraft() {
      this.saving = true
      this.form.status = '0'
      this.persistDraft()
      this.$message.success('草稿已保存')
      this.saving = false
    },
    submitForm() {
      this.$refs.formRef.validate(valid => {
        if (!valid) {
          this.$message.warning('请完善必填信息后再提交')
          return
        }
        this.$confirm('确认提交该成果吗？提交后将进入审核流程。', '提交确认', {
          confirmButtonText: '确认提交',
          cancelButtonText: '再检查一下',
          type: 'warning',
          customClass: 'elegant-confirm-box'
        }).then(() => {
          this.submitting = true
          const submitData = { ...this.form, status: '2' }
          let apiCall
          if (this.form.achievementId && this.form.status === '4') {
            apiCall = teacherResubmit(submitData)
          } else if (this.form.achievementId) {
            apiCall = teacherUpdateAchievement(submitData)
          } else {
            apiCall = teacherAddAchievement(submitData)
          }
          apiCall.then(() => {
            this.$message.success(this.form.achievementId ? '修改成功' : '申报提交成功')
            localStorage.removeItem(DRAFT_KEY)
            this.$router.push('/portal/mine')
          }).finally(() => {
            this.submitting = false
          })
        }).catch(() => {})
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.portal-declare-page {
  max-width: 1380px;
  margin: 0 auto;
  padding: 28px 20px 64px;
}

.page-head {
  min-height: 176px;
  padding: 32px 38px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 24px;
  color: #173b63;
  background:
    linear-gradient(90deg, rgba(249, 253, 255, 0.95), rgba(231, 245, 255, 0.88)),
    url('~@/assets/images/portal-hero-ink.png') center 62% / cover;
  border: 1px solid #d7e6f4;
  border-bottom: 4px solid #d6a23a;
}

.page-head span {
  display: inline-block;
  margin-bottom: 10px;
  color: #0b5c95;
  font-size: 13px;
  letter-spacing: 1px;
  text-transform: uppercase;
}

.page-head h1 {
  margin: 0;
  font-size: 32px;
}

.page-head p {
  margin: 12px 0 0;
  color: #4f6f8b;
}

.save-state {
  flex: 0 0 auto;
  padding: 9px 16px;
  background: rgba(255, 255, 255, 0.76);
  border: 1px solid #cfddeb;
}

.draft-notice {
  margin: 18px 0;
  padding: 12px 16px;
  display: flex;
  align-items: center;
  gap: 10px;
  color: #8a5b08;
  background: #fff8e6;
  border: 1px solid #f3d48a;
}

.close-btn {
  margin-left: auto;
  cursor: pointer;
}

.declare-form {
  margin-top: 22px;
}

.form-panel {
  padding: 32px 42px 36px;
  margin-bottom: 22px;
  background: #fff;
  border: 1px solid #dbe6f2;
  box-shadow: 0 12px 28px rgba(18, 70, 122, 0.08);
}

.section-title {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 24px;
  border-bottom: 1px solid #e7eef7;
  padding-bottom: 14px;
}

.section-title strong {
  width: 42px;
  height: 30px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  background: #0b4f93;
  color: #fff;
  font-size: 14px;
}

.section-title h2 {
  margin: 0;
  color: #0d3564;
  font-size: 20px;
}

.declare-form ::v-deep .el-form-item__label {
  color: #173b63;
  font-weight: 600;
}

.declare-form ::v-deep .el-input__inner,
.declare-form ::v-deep .el-textarea__inner {
  border-radius: 0;
  border-color: #cfddeb;
}

.declare-form ::v-deep .el-select {
  width: 100%;
}

.upload-area {
  border: 1px dashed #b8cbe0;
  background: #f8fbff;
  padding: 14px;
}

.field-tip {
  margin-top: 10px;
  color: #7b8da1;
  font-size: 13px;
}

.editor-box {
  border: 1px solid #cfddeb;
}

.form-actions {
  padding: 22px 0 0;
  display: flex;
  justify-content: center;
  gap: 16px;
}

.form-actions .el-button {
  min-width: 140px;
  border-radius: 0;
}

.form-actions .el-button--primary {
  background: #0b4f93;
  border-color: #0b4f93;
}

@media (max-width: 768px) {
  .page-head {
    display: block;
    padding: 28px 24px;
  }

  .save-state {
    display: inline-block;
    margin-top: 20px;
  }

  .form-panel {
    padding: 24px 18px;
  }
}

.category-nav {
  margin-bottom: 32px;
  .nav-label {
    font-size: 14px;
    font-weight: 600;
    color: #0f172a;
    margin-bottom: 12px;
    display: block;
  }
}
.category-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
}
.category-cell {
  padding: 16px 20px;
  background: #f8fafc;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.25s;
  border: 2px solid transparent;
  text-align: center;
  &:hover {
    background: #eff6ff;
    border-color: #bfdbfe;
  }
  &.selected {
    background: #eff6ff;
    border-color: #2563eb;
    .cell-label { color: #2563eb; font-weight: 600; }
  }
  .cell-label { display: block; font-size: 15px; color: #334155; font-weight: 500; margin-bottom: 4px; transition: color 0.2s; }
  .cell-desc { display: block; font-size: 12px; color: #94a3b8; }
}
@media (max-width: 768px) {
  .category-grid { grid-template-columns: repeat(2, 1fr); }
}
</style>
