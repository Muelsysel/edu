<template>
  <div class="portal-profile-page">
    <section class="profile-head">
      <div>
        <span>User Center</span>
        <h1>个人中心</h1>
        <p>维护账号资料、头像与登录密码。</p>
      </div>
    </section>

    <section class="profile-layout">
      <aside class="identity-panel">
        <div class="avatar-section">
          <div class="avatar-wrapper" @click="editCropper">
            <img :src="options.img" class="avatar-img" />
            <div class="avatar-overlay">
              <i class="el-icon-camera" />
              <span>更换头像</span>
            </div>
          </div>
          <h2>{{ user.nickName || user.userName }}</h2>
          <p>{{ roleGroup || '暂无角色' }}</p>
        </div>

        <ul class="info-list">
          <li>
            <i class="el-icon-user" />
            <label>用户名</label>
            <span>{{ user.userName }}</span>
          </li>
          <li>
            <i class="el-icon-phone-outline" />
            <label>手机号</label>
            <span>{{ user.phonenumber || '未设置' }}</span>
          </li>
          <li>
            <i class="el-icon-message" />
            <label>邮箱</label>
            <span>{{ user.email || '未设置' }}</span>
          </li>
          <li>
            <i class="el-icon-office-building" />
            <label>部门</label>
            <span>{{ user.dept ? user.dept.deptName : '未分配' }}</span>
          </li>
          <li>
            <i class="el-icon-date" />
            <label>创建日期</label>
            <span>{{ user.createTime || '-' }}</span>
          </li>
        </ul>
      </aside>

      <main class="settings-panel">
        <el-tabs v-model="activeTab" class="profile-tabs">
          <el-tab-pane label="基本资料" name="userinfo">
            <el-form ref="infoForm" :model="infoForm" :rules="infoRules" label-width="96px" class="profile-form">
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
            <el-form ref="pwdForm" :model="pwdForm" :rules="pwdRules" label-width="96px" class="profile-form">
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
      </main>
    </section>

    <el-dialog title="修改头像" :visible.sync="cropperOpen" width="800px" append-to-body @opened="modalOpened" @close="closeDialog">
      <el-row>
        <el-col :xs="24" :md="12" :style="{ height: '350px' }">
          <vue-cropper
            v-if="cropperVisible"
            ref="cropper"
            :img="options.img"
            :info="true"
            :autoCrop="options.autoCrop"
            :autoCropWidth="options.autoCropWidth"
            :autoCropHeight="options.autoCropHeight"
            :fixedBox="options.fixedBox"
            :outputType="options.outputType"
            @realTime="realTime"
          />
        </el-col>
        <el-col :xs="24" :md="12" :style="{ height: '350px' }">
          <div class="avatar-upload-preview">
            <img :src="previews.url" :style="previews.img" />
          </div>
        </el-col>
      </el-row>
      <br />
      <el-row class="crop-actions">
        <el-col :lg="2" :sm="3" :xs="4">
          <el-upload action="#" :http-request="requestUpload" :show-file-list="false" :before-upload="beforeUpload">
            <el-button size="small">选择 <i class="el-icon-upload el-icon--right" /></el-button>
          </el-upload>
        </el-col>
        <el-col :lg="{ span: 1, offset: 2 }" :sm="2" :xs="3">
          <el-button icon="el-icon-plus" size="small" @click="changeScale(1)" />
        </el-col>
        <el-col :lg="{ span: 1, offset: 1 }" :sm="2" :xs="3">
          <el-button icon="el-icon-minus" size="small" @click="changeScale(-1)" />
        </el-col>
        <el-col :lg="{ span: 1, offset: 1 }" :sm="2" :xs="3">
          <el-button icon="el-icon-refresh-left" size="small" @click="rotateLeft" />
        </el-col>
        <el-col :lg="{ span: 1, offset: 1 }" :sm="2" :xs="3">
          <el-button icon="el-icon-refresh-right" size="small" @click="rotateRight" />
        </el-col>
        <el-col :lg="{ span: 2, offset: 6 }" :sm="3" :xs="5">
          <el-button type="primary" size="small" @click="uploadImg">提交</el-button>
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
          { pattern: /^[^<>"'|\\]+$/, message: '不能包含非法字符：< > " \' \\ |', trigger: 'blur' }
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
    submitInfo() {
      this.$refs.infoForm.validate(valid => {
        if (valid) {
          updateUserProfile(this.infoForm).then(() => {
            this.$message.success('修改成功')
            this.getUser()
          })
        }
      })
    },
    resetInfo() {
      this.infoForm = {
        nickName: this.user.nickName,
        phonenumber: this.user.phonenumber,
        email: this.user.email,
        sex: this.user.sex
      }
    },
    submitPwd() {
      this.$refs.pwdForm.validate(valid => {
        if (valid) {
          updateUserPwd(this.pwdForm.oldPassword, this.pwdForm.newPassword).then(() => {
            this.$message.success('密码修改成功')
            this.pwdForm = { oldPassword: undefined, newPassword: undefined, confirmPassword: undefined }
          })
        }
      })
    },
    resetPwd() {
      this.pwdForm = { oldPassword: undefined, newPassword: undefined, confirmPassword: undefined }
    },
    editCropper() {
      this.cropperOpen = true
    },
    modalOpened() {
      this.cropperVisible = true
      if (!this.resizeHandler) {
        this.resizeHandler = debounce(() => {
          this.$refs.cropper && this.$refs.cropper.refresh()
        }, 100)
      }
      window.addEventListener('resize', this.resizeHandler)
    },
    requestUpload() {},
    rotateLeft() {
      this.$refs.cropper.rotateLeft()
    },
    rotateRight() {
      this.$refs.cropper.rotateRight()
    },
    changeScale(num) {
      this.$refs.cropper.changeScale(num || 1)
    },
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
        const formData = new FormData()
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
    realTime(data) {
      this.previews = data
    },
    closeDialog() {
      this.options.img = this.user.avatar || store.getters.avatar
      this.cropperVisible = false
      window.removeEventListener('resize', this.resizeHandler)
    }
  }
}
</script>

<style lang="scss" scoped>
.portal-profile-page {
  max-width: 1380px;
  margin: 0 auto;
  padding: 28px 20px 60px;
}

.profile-head {
  min-height: 156px;
  padding: 30px 38px;
  display: flex;
  align-items: center;
  color: #173b63;
  background:
    linear-gradient(90deg, rgba(249, 253, 255, 0.95), rgba(231, 245, 255, 0.88)),
    url('~@/assets/images/portal-hero-ink.png') center 62% / cover;
  border: 1px solid #d7e6f4;
  border-bottom: 4px solid #d6a23a;
}

.profile-head span {
  display: block;
  margin-bottom: 10px;
  color: #0b5c95;
  font-size: 13px;
  text-transform: uppercase;
  letter-spacing: 1px;
}

.profile-head h1 {
  margin: 0;
  font-size: 32px;
}

.profile-head p {
  margin: 12px 0 0;
  color: #4f6f8b;
}

.profile-layout {
  margin-top: 22px;
  display: grid;
  grid-template-columns: 360px minmax(0, 1fr);
  gap: 22px;
}

.identity-panel,
.settings-panel {
  background: #fff;
  border: 1px solid #dbe6f2;
  box-shadow: 0 12px 28px rgba(18, 70, 122, 0.08);
}

.identity-panel {
  padding: 30px 28px;
}

.avatar-section {
  text-align: center;
  padding-bottom: 24px;
  border-bottom: 1px solid #e7eef7;
}

.avatar-wrapper {
  position: relative;
  width: 112px;
  height: 112px;
  margin: 0 auto 16px;
  cursor: pointer;
}

.avatar-img {
  width: 112px;
  height: 112px;
  border-radius: 50%;
  object-fit: cover;
  border: 4px solid #edf5fd;
}

.avatar-overlay {
  position: absolute;
  inset: 4px;
  border-radius: 50%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 6px;
  color: #fff;
  background: rgba(6, 45, 86, 0.66);
  opacity: 0;
  transition: opacity 0.2s ease;
  font-size: 12px;
}

.avatar-wrapper:hover .avatar-overlay {
  opacity: 1;
}

.avatar-section h2 {
  margin: 0;
  color: #0d3564;
  font-size: 22px;
}

.avatar-section p {
  margin: 8px 0 0;
  color: #6c7f92;
}

.info-list {
  list-style: none;
  margin: 22px 0 0;
  padding: 0;
}

.info-list li {
  display: grid;
  grid-template-columns: 22px 72px minmax(0, 1fr);
  gap: 8px;
  align-items: center;
  padding: 12px 0;
  color: #51677e;
  border-bottom: 1px solid #f0f4f8;
}

.info-list label {
  color: #7b8da1;
}

.info-list span {
  min-width: 0;
  color: #173b63;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.settings-panel {
  padding: 28px 34px 36px;
}

.profile-tabs ::v-deep .el-tabs__item {
  color: #51677e;
  font-weight: 600;
}

.profile-tabs ::v-deep .el-tabs__item.is-active {
  color: #0b4f93;
}

.profile-tabs ::v-deep .el-tabs__active-bar {
  background: #d6a23a;
}

.profile-form {
  max-width: 760px;
  padding-top: 18px;
}

.profile-form ::v-deep .el-input__inner {
  border-radius: 0;
  border-color: #cfddeb;
}

.profile-form .el-button {
  border-radius: 0;
}

.profile-form .el-button--primary {
  background: #0b4f93;
  border-color: #0b4f93;
}

.avatar-upload-preview {
  position: absolute;
  top: 50%;
  transform: translate(50%, -50%);
  width: 200px;
  height: 200px;
  border-radius: 50%;
  overflow: hidden;
  box-shadow: 0 0 4px #ccc;
}

@media (max-width: 900px) {
  .profile-layout {
    grid-template-columns: 1fr;
  }
}
</style>
