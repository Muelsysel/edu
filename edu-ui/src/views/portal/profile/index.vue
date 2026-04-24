<template>
  <div class="portal-profile">
    <div class="profile-header">
      <h2 class="profile-title">
        <i class="el-icon-user"></i> 个人设置
      </h2>
      <p class="profile-subtitle">管理您的个人信息、修改密码和头像</p>
    </div>

    <el-row :gutter="24">
      <!-- 左侧：个人信息卡片 -->
      <el-col :span="8" :xs="24">
        <div class="profile-card info-card">
          <div class="card-header">
            <span>个人信息</span>
          </div>
          <div class="card-body">
            <div class="avatar-section">
              <div class="avatar-wrapper" @click="editCropper">
                <img :src="options.img" class="avatar-img" />
                <div class="avatar-overlay">
                  <i class="el-icon-camera"></i>
                  <span>更换头像</span>
                </div>
              </div>
              <h3 class="user-name">{{ user.nickName || user.userName }}</h3>
            </div>
            <ul class="info-list">
              <li>
                <i class="el-icon-user"></i>
                <span class="info-label">用户名称</span>
                <span class="info-value">{{ user.userName }}</span>
              </li>
              <li>
                <i class="el-icon-phone-outline"></i>
                <span class="info-label">手机号码</span>
                <span class="info-value">{{ user.phonenumber || '未设置' }}</span>
              </li>
              <li>
                <i class="el-icon-message"></i>
                <span class="info-label">用户邮箱</span>
                <span class="info-value">{{ user.email || '未设置' }}</span>
              </li>
              <li>
                <i class="el-icon-office-building"></i>
                <span class="info-label">所属部门</span>
                <span class="info-value">{{ user.dept ? user.dept.deptName : '未分配' }}</span>
              </li>
              <li>
                <i class="el-icon-s-custom"></i>
                <span class="info-label">所属角色</span>
                <span class="info-value">{{ roleGroup || '未分配' }}</span>
              </li>
              <li>
                <i class="el-icon-date"></i>
                <span class="info-label">创建日期</span>
                <span class="info-value">{{ user.createTime }}</span>
              </li>
            </ul>
          </div>
        </div>
      </el-col>

      <!-- 右侧：编辑区域 -->
      <el-col :span="16" :xs="24">
        <div class="profile-card edit-card">
          <el-tabs v-model="activeTab" class="profile-tabs">
            <el-tab-pane label="基本资料" name="userinfo">
              <el-form ref="infoForm" :model="infoForm" :rules="infoRules" label-width="100px" class="profile-form">
                <el-form-item label="用户昵称" prop="nickName">
                  <el-input v-model="infoForm.nickName" placeholder="请输入用户昵称" maxlength="30" />
                </el-form-item>
                <el-form-item label="手机号码" prop="phonenumber">
                  <el-input v-model="infoForm.phonenumber" placeholder="请输入手机号码" maxlength="11" />
                </el-form-item>
                <el-form-item label="邮箱" prop="email">
                  <el-input v-model="infoForm.email" placeholder="请输入邮箱" maxlength="50" />
                </el-form-item>
                <el-form-item label="性别">
                  <el-radio-group v-model="infoForm.sex">
                    <el-radio label="0">男</el-radio>
                    <el-radio label="1">女</el-radio>
                  </el-radio-group>
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="submitInfo">保存修改</el-button>
                  <el-button @click="resetInfo">重置</el-button>
                </el-form-item>
              </el-form>
            </el-tab-pane>

            <el-tab-pane label="修改密码" name="resetPwd">
              <el-form ref="pwdForm" :model="pwdForm" :rules="pwdRules" label-width="100px" class="profile-form">
                <el-form-item label="旧密码" prop="oldPassword">
                  <el-input v-model="pwdForm.oldPassword" placeholder="请输入旧密码" type="password" show-password />
                </el-form-item>
                <el-form-item label="新密码" prop="newPassword">
                  <el-input v-model="pwdForm.newPassword" placeholder="请输入新密码" type="password" show-password />
                </el-form-item>
                <el-form-item label="确认密码" prop="confirmPassword">
                  <el-input v-model="pwdForm.confirmPassword" placeholder="请确认新密码" type="password" show-password />
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="submitPwd">保存修改</el-button>
                  <el-button @click="resetPwd">重置</el-button>
                </el-form-item>
              </el-form>
            </el-tab-pane>
          </el-tabs>
        </div>
      </el-col>
    </el-row>

    <!-- 头像裁剪弹窗 -->
    <el-dialog title="修改头像" :visible.sync="cropperOpen" width="800px" append-to-body @opened="modalOpened" @close="closeDialog">
      <el-row>
        <el-col :xs="24" :md="12" :style="{height: '350px'}">
          <vue-cropper
            ref="cropper"
            :img="options.img"
            :info="true"
            :autoCrop="options.autoCrop"
            :autoCropWidth="options.autoCropWidth"
            :autoCropHeight="options.autoCropHeight"
            :fixedBox="options.fixedBox"
            :outputType="options.outputType"
            @realTime="realTime"
            v-if="cropperVisible"
          />
        </el-col>
        <el-col :xs="24" :md="12" :style="{height: '350px'}">
          <div class="avatar-upload-preview">
            <img :src="previews.url" :style="previews.img" />
          </div>
        </el-col>
      </el-row>
      <br />
      <el-row>
        <el-col :lg="2" :sm="3" :xs="3">
          <el-upload action="#" :http-request="requestUpload" :show-file-list="false" :before-upload="beforeUpload">
            <el-button size="small">选择 <i class="el-icon-upload el-icon--right"></i></el-button>
          </el-upload>
        </el-col>
        <el-col :lg="{span: 1, offset: 2}" :sm="2" :xs="2">
          <el-button icon="el-icon-plus" size="small" @click="changeScale(1)"></el-button>
        </el-col>
        <el-col :lg="{span: 1, offset: 1}" :sm="2" :xs="2">
          <el-button icon="el-icon-minus" size="small" @click="changeScale(-1)"></el-button>
        </el-col>
        <el-col :lg="{span: 1, offset: 1}" :sm="2" :xs="2">
          <el-button icon="el-icon-refresh-left" size="small" @click="rotateLeft"></el-button>
        </el-col>
        <el-col :lg="{span: 1, offset: 1}" :sm="2" :xs="2">
          <el-button icon="el-icon-refresh-right" size="small" @click="rotateRight"></el-button>
        </el-col>
        <el-col :lg="{span: 2, offset: 6}" :sm="2" :xs="2">
          <el-button type="primary" size="small" @click="uploadImg">提 交</el-button>
        </el-col>
      </el-row>
    </el-dialog>
  </div>
</template>

<script>
import store from '@/store'
import { VueCropper } from 'vue-cropper'
import { getUserProfile, updateUserProfile, updateUserPwd, uploadAvatar } from '@/api/system/user'
import { debounce } from '@/utils'

export default {
  name: 'PortalProfile',
  components: { VueCropper },
  data() {
    const equalToPassword = (rule, value, callback) => {
      if (this.pwdForm.newPassword !== value) {
        callback(new Error('两次输入的密码不一致'))
      } else {
        callback()
      }
    }
    return {
      activeTab: 'userinfo',
      user: {},
      roleGroup: '',
      postGroup: '',
      infoForm: {},
      pwdForm: {
        oldPassword: undefined,
        newPassword: undefined,
        confirmPassword: undefined
      },
      // 头像裁剪
      cropperOpen: false,
      cropperVisible: false,
      options: {
        img: store.getters.avatar,
        autoCrop: true,
        autoCropWidth: 200,
        autoCropHeight: 200,
        fixedBox: true,
        outputType: 'png',
        filename: 'avatar'
      },
      previews: {},
      resizeHandler: null,
      // 表单校验
      infoRules: {
        nickName: [
          { required: true, message: '用户昵称不能为空', trigger: 'blur' }
        ],
        email: [
          { required: true, message: '邮箱地址不能为空', trigger: 'blur' },
          { type: 'email', message: '请输入正确的邮箱地址', trigger: ['blur', 'change'] }
        ],
        phonenumber: [
          { required: true, message: '手机号码不能为空', trigger: 'blur' },
          { pattern: /^1[3|4|5|6|7|8|9][0-9]\d{8}$/, message: '请输入正确的手机号码', trigger: 'blur' }
        ]
      },
      pwdRules: {
        oldPassword: [
          { required: true, message: '旧密码不能为空', trigger: 'blur' }
        ],
        newPassword: [
          { required: true, message: '新密码不能为空', trigger: 'blur' },
          { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' },
          { pattern: /^[^<>"'|\\]+$/, message: "不能包含非法字符：< > \" ' \\ |", trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, message: '确认密码不能为空', trigger: 'blur' },
          { required: true, validator: equalToPassword, trigger: 'blur' }
        ]
      }
    }
  },
  created() {
    this.getUser()
  },
  methods: {
    /** 获取用户信息 */
    getUser() {
      getUserProfile().then(response => {
        this.user = response.data
        this.roleGroup = response.roleGroup
        this.postGroup = response.postGroup
        this.options.img = this.user.avatar || store.getters.avatar
        this.infoForm = {
          nickName: this.user.nickName,
          phonenumber: this.user.phonenumber,
          email: this.user.email,
          sex: this.user.sex
        }
      })
    },
    /** 提交基本资料 */
    submitInfo() {
      this.$refs['infoForm'].validate(valid => {
        if (valid) {
          updateUserProfile(this.infoForm).then(() => {
            this.$message.success('修改成功')
            this.getUser()
          })
        }
      })
    },
    /** 重置基本资料 */
    resetInfo() {
      this.infoForm = {
        nickName: this.user.nickName,
        phonenumber: this.user.phonenumber,
        email: this.user.email,
        sex: this.user.sex
      }
    },
    /** 提交密码修改 */
    submitPwd() {
      this.$refs['pwdForm'].validate(valid => {
        if (valid) {
          updateUserPwd(this.pwdForm.oldPassword, this.pwdForm.newPassword).then(() => {
            this.$message.success('密码修改成功')
            this.pwdForm = { oldPassword: undefined, newPassword: undefined, confirmPassword: undefined }
          })
        }
      })
    },
    /** 重置密码表单 */
    resetPwd() {
      this.pwdForm = { oldPassword: undefined, newPassword: undefined, confirmPassword: undefined }
    },
    // ========== 头像裁剪相关 ==========
    editCropper() {
      this.cropperOpen = true
    },
    modalOpened() {
      this.cropperVisible = true
      if (!this.resizeHandler) {
        this.resizeHandler = debounce(() => { this.$refs.cropper && this.$refs.cropper.refresh() }, 100)
      }
      window.addEventListener('resize', this.resizeHandler)
    },
    requestUpload() {},
    rotateLeft() { this.$refs.cropper.rotateLeft() },
    rotateRight() { this.$refs.cropper.rotateRight() },
    changeScale(num) { this.$refs.cropper.changeScale(num || 1) },
    beforeUpload(file) {
      if (file.type.indexOf('image/') === -1) {
        this.$message.error('文件格式错误，请上传 JPG、PNG 等图片文件')
      } else {
        const reader = new FileReader()
        reader.readAsDataURL(file)
        reader.onload = () => {
          this.options.img = reader.result
          this.options.filename = file.name
        }
      }
    },
    uploadImg() {
      this.$refs.cropper.getCropBlob(data => {
        let formData = new FormData()
        formData.append('avatarfile', data, this.options.filename)
        uploadAvatar(formData).then(response => {
          this.cropperOpen = false
          this.options.img = response.imgUrl
          store.commit('SET_AVATAR', this.options.img)
          this.$message.success('头像修改成功')
          this.cropperVisible = false
        })
      })
    },
    realTime(data) { this.previews = data },
    closeDialog() {
      this.options.img = this.user.avatar || store.getters.avatar
      this.cropperVisible = false
      window.removeEventListener('resize', this.resizeHandler)
    }
  }
}
</script>

<style scoped>
.portal-profile {
  max-width: 1100px;
  margin: 0 auto;
}

.profile-header {
  margin-bottom: 28px;
}

.profile-title {
  font-size: 22px;
  font-weight: 600;
  color: #0f172a;
  margin: 0 0 8px 0;
  display: flex;
  align-items: center;
  gap: 8px;
}

.profile-title i {
  color: #2563eb;
  font-size: 24px;
}

.profile-subtitle {
  font-size: 14px;
  color: #64748b;
  margin: 0;
}

/* 卡片通用样式 */
.profile-card {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04), 0 4px 16px rgba(0, 0, 0, 0.02);
  border: 1px solid rgba(226, 232, 240, 0.8);
  overflow: hidden;
  transition: box-shadow 0.3s;
}

.profile-card:hover {
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
}

.card-header {
  padding: 16px 24px;
  border-bottom: 1px solid #f1f5f9;
  font-size: 16px;
  font-weight: 600;
  color: #1e293b;
}

.card-body {
  padding: 24px;
}

/* 头像区域 */
.avatar-section {
  text-align: center;
  margin-bottom: 24px;
  padding-bottom: 24px;
  border-bottom: 1px solid #f1f5f9;
}

.avatar-wrapper {
  position: relative;
  display: inline-block;
  width: 100px;
  height: 100px;
  border-radius: 50%;
  overflow: hidden;
  cursor: pointer;
  box-shadow: 0 4px 12px rgba(37, 99, 235, 0.15);
  transition: transform 0.3s;
}

.avatar-wrapper:hover {
  transform: scale(1.05);
}

.avatar-wrapper:hover .avatar-overlay {
  opacity: 1;
}

.avatar-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #fff;
  opacity: 0;
  transition: opacity 0.3s;
}

.avatar-overlay i {
  font-size: 22px;
  margin-bottom: 4px;
}

.avatar-overlay span {
  font-size: 12px;
}

.user-name {
  margin: 14px 0 0 0;
  font-size: 18px;
  color: #1e293b;
  font-weight: 600;
}

/* 信息列表 */
.info-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.info-list li {
  display: flex;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #f8fafc;
  font-size: 14px;
  transition: background 0.2s;
}

.info-list li:last-child {
  border-bottom: none;
}

.info-list li:hover {
  background: #f8fafc;
  margin: 0 -24px;
  padding: 12px 24px;
}

.info-list li i {
  color: #2563eb;
  font-size: 16px;
  width: 20px;
  margin-right: 10px;
  flex-shrink: 0;
}

.info-label {
  color: #64748b;
  width: 70px;
  flex-shrink: 0;
}

.info-value {
  color: #1e293b;
  margin-left: auto;
  text-align: right;
  word-break: break-all;
}

/* 编辑卡片 */
.edit-card {
  padding: 8px 24px 24px;
}

.profile-tabs >>> .el-tabs__header {
  margin-bottom: 24px;
}

.profile-tabs >>> .el-tabs__item {
  font-size: 15px;
  font-weight: 500;
}

.profile-tabs >>> .el-tabs__active-bar {
  background-color: #2563eb;
}

.profile-tabs >>> .el-tabs__item.is-active {
  color: #2563eb;
}

.profile-form {
  max-width: 500px;
}

.profile-form >>> .el-form-item__label {
  font-weight: 500;
  color: #475569;
}

/* 头像裁剪预览 */
.avatar-upload-preview {
  position: relative;
  top: 50%;
  transform: translateY(-50%);
  width: 200px;
  height: 200px;
  border-radius: 50%;
  box-shadow: 0 0 4px #ccc;
  overflow: hidden;
  margin: 0 auto;
}

/* 响应式 */
@media (max-width: 768px) {
  .profile-card {
    margin-bottom: 16px;
  }

  .edit-card {
    padding: 8px 16px 16px;
  }

  .info-list li:hover {
    margin: 0;
    padding: 12px 0;
  }
}
</style>
