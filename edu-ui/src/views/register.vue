<template>
  <div class="register-wrapper">
    <div class="register-bg-pattern"></div>

    <div class="register-card">
      <el-form ref="registerForm" :model="registerForm" :rules="registerRules" class="register-form">
        <div class="form-brand">
          <span class="brand-icon">🎓</span>
          <h3 class="form-title">{{title}}</h3>
          <p class="form-subtitle">创建新账号以使用系统</p>
        </div>

        <el-form-item prop="username">
          <el-input v-model="registerForm.username" type="text" auto-complete="off" placeholder="账号" class="modern-input">
            <svg-icon slot="prefix" icon-class="user" class="el-input__icon input-icon" />
          </el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="registerForm.password"
            type="password"
            auto-complete="off"
            placeholder="密码"
            class="modern-input"
            @keyup.enter.native="handleRegister"
          >
            <svg-icon slot="prefix" icon-class="password" class="el-input__icon input-icon" />
          </el-input>
        </el-form-item>
        <el-form-item prop="confirmPassword">
          <el-input
            v-model="registerForm.confirmPassword"
            type="password"
            auto-complete="off"
            placeholder="确认密码"
            class="modern-input"
            @keyup.enter.native="handleRegister"
          >
            <svg-icon slot="prefix" icon-class="password" class="el-input__icon input-icon" />
          </el-input>
        </el-form-item>
        <el-form-item prop="code" v-if="captchaEnabled">
          <div class="captcha-row">
            <el-input
              v-model="registerForm.code"
              auto-complete="off"
              placeholder="验证码"
              class="modern-input captcha-input"
              @keyup.enter.native="handleRegister"
            >
              <svg-icon slot="prefix" icon-class="validCode" class="el-input__icon input-icon" />
            </el-input>
            <div class="register-code">
              <img :src="codeUrl" @click="getCode" class="register-code-img"/>
            </div>
          </div>
        </el-form-item>
        <el-form-item style="width:100%;">
          <el-button
            :loading="loading"
            size="medium"
            type="primary"
            class="register-btn"
            @click.native.prevent="handleRegister"
          >
            <span v-if="!loading">注 册</span>
            <span v-else>注 册 中...</span>
          </el-button>
          <div class="login-link-row">
            <router-link class="login-link" :to="'/login'">已有账号？去登录</router-link>
          </div>
        </el-form-item>
      </el-form>
    </div>

    <div class="el-register-footer">
      <span>{{ footerContent }}</span>
    </div>
  </div>
</template>

<script>
import { getCodeImg, register } from "@/api/login"
import defaultSettings from '@/settings'

export default {
  name: "Register",
  data() {
    const equalToPassword = (rule, value, callback) => {
      if (this.registerForm.password !== value) {
        callback(new Error("两次输入的密码不一致"))
      } else {
        callback()
      }
    }
    return {
      title: process.env.VUE_APP_TITLE,
      footerContent: defaultSettings.footerContent,
      codeUrl: "",
      registerForm: {
        username: "",
        password: "",
        confirmPassword: "",
        code: "",
        uuid: ""
      },
      registerRules: {
        username: [
          { required: true, trigger: "blur", message: "请输入您的账号" },
          { min: 2, max: 20, message: '用户账号长度必须介于 2 和 20 之间', trigger: 'blur' }
        ],
        password: [
          { required: true, trigger: "blur", message: "请输入您的密码" },
          { min: 5, max: 20, message: "用户密码长度必须介于 5 和 20 之间", trigger: "blur" },
          { pattern: /^[^<>"'|\\]+$/, message: "不能包含非法字符：< > \" ' \\ |", trigger: "blur" }
        ],
        confirmPassword: [
          { required: true, trigger: "blur", message: "请再次输入您的密码" },
          { required: true, validator: equalToPassword, trigger: "blur" }
        ],
        code: [{ required: true, trigger: "change", message: "请输入验证码" }]
      },
      loading: false,
      captchaEnabled: true
    }
  },
  created() {
    this.getCode()
  },
  methods: {
    getCode() {
      getCodeImg().then(res => {
        this.captchaEnabled = res.captchaEnabled === undefined ? true : res.captchaEnabled
        if (this.captchaEnabled) {
          this.codeUrl = "data:image/gif;base64," + res.img
          this.registerForm.uuid = res.uuid
        }
      })
    },
    handleRegister() {
      this.$refs.registerForm.validate(valid => {
        if (valid) {
          this.loading = true
          register(this.registerForm).then(res => {
            const username = this.registerForm.username
            this.$alert("<font color='red'>恭喜你，您的账号 " + username + " 注册成功！</font>", '系统提示', {
              dangerouslyUseHTMLString: true,
              type: 'success'
            }).then(() => {
              this.$router.push("/login")
            }).catch(() => {})
          }).catch(() => {
            this.loading = false
            if (this.captchaEnabled) {
              this.getCode()
            }
          })
        }
      })
    }
  }
}
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
.register-wrapper {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background: #0f172a;
  position: relative;
  overflow: hidden;
}

.register-bg-pattern {
  position: absolute;
  top: 0; left: 0; right: 0; bottom: 0;
  background-image:
    radial-gradient(circle at 30% 40%, rgba(30, 64, 175, 0.12) 0%, transparent 50%),
    radial-gradient(circle at 70% 70%, rgba(212, 168, 83, 0.06) 0%, transparent 40%);
}

.register-card {
  position: relative;
  z-index: 10;
  background: #ffffff;
  border-radius: 20px;
  box-shadow: 0 25px 60px rgba(0, 0, 0, 0.4);
  overflow: hidden;
  width: 440px;
}

.register-form {
  padding: 40px 40px 24px;

  .form-brand {
    text-align: center;
    margin-bottom: 28px;

    .brand-icon {
      font-size: 36px;
      display: block;
      margin-bottom: 12px;
    }

    .form-title {
      font-family: 'Noto Serif SC', serif;
      font-size: 20px;
      font-weight: 700;
      color: #0f172a;
      margin: 0 0 6px;
    }

    .form-subtitle {
      font-size: 13px;
      color: #94a3b8;
      margin: 0;
    }
  }
}

::v-deep .modern-input {
  .el-input__inner {
    height: 44px;
    line-height: 44px;
    border-radius: 10px;
    background-color: #f8fafc;
    border: 1px solid #e2e8f0;
    padding-left: 38px;
    font-size: 14px;
    transition: all 0.25s ease;

    &:focus {
      background-color: #fff;
      border-color: #1e40af;
      box-shadow: 0 0 0 3px rgba(30, 64, 175, 0.08);
    }
  }
  .el-input__prefix {
    left: 12px;
  }
}

.input-icon {
  height: 44px;
  width: 14px;
  color: #94a3b8;
}

.captcha-row {
  display: flex;
  gap: 10px;
  align-items: center;

  .captcha-input { flex: 1; }
  .register-code {
    width: 110px;
    height: 44px;
    border-radius: 10px;
    overflow: hidden;
    border: 1px solid #e2e8f0;
    img {
      width: 100%;
      height: 100%;
      cursor: pointer;
      vertical-align: middle;
      object-fit: cover;
    }
  }
}

.register-btn {
  width: 100%;
  height: 44px;
  border-radius: 10px;
  font-weight: 600;
  letter-spacing: 3px;
  background: linear-gradient(135deg, #1e40af 0%, #1e3a5f 100%) !important;
  border: none !important;
  font-size: 15px;
  transition: all 0.3s ease;

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 8px 25px rgba(30, 64, 175, 0.35);
  }
}

.login-link-row {
  text-align: right;
  margin-top: 12px;
}

.login-link {
  font-size: 13px;
  color: #1e40af;
  font-weight: 500;
  &:hover { color: #d4a853; }
}

.el-register-footer {
  height: 40px;
  line-height: 40px;
  position: absolute;
  bottom: 0;
  width: 100%;
  text-align: center;
  color: rgba(148, 163, 184, 0.4);
  font-size: 12px;
  letter-spacing: 1px;
}
</style>
