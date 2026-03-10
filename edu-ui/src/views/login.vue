<template>
  <div class="login-wrapper">
    <div class="login-overlay"></div>

    <div class="login-card">
      <div class="login-left">
        <div class="left-header">
          <img src="../assets/logo/logo1.png" alt="logo" class="logo" v-if="false" /> <span class="brand-name">H.E.A.M.S</span>
        </div>
        <div class="left-body">
          <h1 class="sys-title">{{ title }}</h1>
          <p class="sys-desc">高校信息化转型时代下的成果申报、多级审核与数字化管理平台。</p>
        </div>
        <div class="left-footer">
          <span>郑州轻工业大学 · 软件工程</span>
        </div>
        <div class="circle-light top-light"></div>
        <div class="circle-light bottom-light"></div>
      </div>

      <div class="login-right">
        <el-form ref="loginForm" :model="loginForm" :rules="loginRules" class="login-form">
          <h2 class="form-title">欢迎登录</h2>
          <p class="form-subtitle">请输入您的账号和密码</p>

          <el-form-item prop="username">
            <el-input
              v-model="loginForm.username"
              type="text"
              auto-complete="off"
              placeholder="请输入账号"
              class="modern-input"
            >
              <svg-icon slot="prefix" icon-class="user" class="el-input__icon input-icon" />
            </el-input>
          </el-form-item>

          <el-form-item prop="password">
            <el-input
              v-model="loginForm.password"
              type="password"
              auto-complete="off"
              placeholder="请输入密码"
              class="modern-input"
              @keyup.enter.native="handleLogin"
            >
              <svg-icon slot="prefix" icon-class="password" class="el-input__icon input-icon" />
            </el-input>
          </el-form-item>

          <el-form-item prop="code" v-if="captchaEnabled">
            <div class="captcha-row">
              <el-input
                v-model="loginForm.code"
                auto-complete="off"
                placeholder="验证码"
                class="modern-input captcha-input"
                @keyup.enter.native="handleLogin"
              >
                <svg-icon slot="prefix" icon-class="validCode" class="el-input__icon input-icon" />
              </el-input>
              <div class="login-code">
                <img :src="codeUrl" @click="getCode" class="login-code-img" title="点击切换验证码" />
              </div>
            </div>
          </el-form-item>

          <div class="form-actions">
            <el-checkbox v-model="loginForm.rememberMe" class="remember-me">记住密码</el-checkbox>
            <router-link v-if="register" class="register-link" :to="'/register'">无账号？去注册</router-link>
          </div>

          <el-form-item style="width:100%; margin-top: 10px;">
            <el-button
              :loading="loading"
              size="medium"
              type="primary"
              class="login-btn"
              @click.native.prevent="handleLogin"
            >
              <span v-if="!loading">立 即 登 录</span>
              <span v-else>登 录 中...</span>
            </el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>

    <div class="el-login-footer">
      <span>{{ footerContent }}</span>
    </div>
  </div>
</template>

<script>
// ---------- 这里的 Script 逻辑一字未改，保证功能 100% 正常运行 ----------
import { getCodeImg } from "@/api/login"
import Cookies from "js-cookie"
import { encrypt, decrypt } from '@/utils/jsencrypt'
import defaultSettings from '@/settings'

export default {
  name: "Login",
  data() {
    return {
      title: process.env.VUE_APP_TITLE || '高校教学成果管理系统',
      footerContent: defaultSettings.footerContent,
      codeUrl: "",
      loginForm: {
        username: "admin",
        password: "admin123",
        rememberMe: false,
        code: "",
        uuid: ""
      },
      loginRules: {
        username: [{ required: true, trigger: "blur", message: "请输入您的账号" }],
        password: [{ required: true, trigger: "blur", message: "请输入您的密码" }],
        code: [{ required: true, trigger: "change", message: "请输入验证码" }]
      },
      loading: false,
      captchaEnabled: true,
      register: true,
      redirect: undefined
    }
  },
  watch: {
    $route: {
      handler: function(route) {
        this.redirect = route.query && route.query.redirect
      },
      immediate: true
    }
  },
  created() {
    this.getCode()
    this.getCookie()
  },
  methods: {
    getCode() {
      getCodeImg().then(res => {
        this.captchaEnabled = res.captchaEnabled === undefined ? true : res.captchaEnabled
        if (this.captchaEnabled) {
          this.codeUrl = "data:image/gif;base64," + res.img
          this.loginForm.uuid = res.uuid
        }
      })
    },
    getCookie() {
      const username = Cookies.get("username")
      const password = Cookies.get("password")
      const rememberMe = Cookies.get('rememberMe')
      this.loginForm = {
        username: username === undefined ? this.loginForm.username : username,
        password: password === undefined ? this.loginForm.password : decrypt(password),
        rememberMe: rememberMe === undefined ? false : Boolean(rememberMe)
      }
    },
    handleLogin() {
      this.$refs.loginForm.validate(valid => {
        if (valid) {
          this.loading = true
          if (this.loginForm.rememberMe) {
            Cookies.set("username", this.loginForm.username, { expires: 30 })
            Cookies.set("password", encrypt(this.loginForm.password), { expires: 30 })
            Cookies.set('rememberMe', this.loginForm.rememberMe, { expires: 30 })
          } else {
            Cookies.remove("username")
            Cookies.remove("password")
            Cookies.remove('rememberMe')
          }
          this.$store.dispatch("Login", this.loginForm).then(() => {
            this.$router.push({ path: this.redirect || "/" }).catch(()=>{})
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
/* 全屏容器 */
.login-wrapper {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-image: url("../assets/images/login-background.jpg");
  background-size: cover;
  background-position: center;
  position: relative;
}

/* 深色半透明遮罩，提升高级感 */
.login-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.45);
  backdrop-filter: blur(3px); /* 轻微毛玻璃背景 */
}

/* 核心登录卡片 */
.login-card {
  position: relative;
  z-index: 10;
  display: flex;
  width: 900px;
  height: 500px;
  background: #ffffff;
  border-radius: 20px;
  box-shadow: 0 20px 50px rgba(0, 0, 0, 0.3);
  overflow: hidden;
}

/* --- 左侧品牌区 --- */
.login-left {
  flex: 5;
  background: linear-gradient(135deg, #1f426e 0%, #1890ff 100%);
  padding: 40px;
  color: #ffffff;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  position: relative;
  overflow: hidden;

  .left-header {
    font-size: 18px;
    font-weight: bold;
    letter-spacing: 2px;
    z-index: 2;
  }

  .left-body {
    z-index: 2;
    .sys-title {
      font-size: 36px;
      font-weight: 600;
      margin-bottom: 15px;
      line-height: 1.2;
    }
    .sys-desc {
      font-size: 15px;
      line-height: 1.8;
      color: rgba(255, 255, 255, 0.85);
    }
  }

  .left-footer {
    z-index: 2;
    font-size: 13px;
    color: rgba(255, 255, 255, 0.6);
  }

  /* 极简风的几何光晕装饰 */
  .circle-light {
    position: absolute;
    border-radius: 50%;
    background: rgba(255, 255, 255, 0.05);
    z-index: 1;
  }
  .top-light {
    width: 300px;
    height: 300px;
    top: -100px;
    right: -100px;
  }
  .bottom-light {
    width: 400px;
    height: 400px;
    bottom: -150px;
    left: -150px;
  }
}

/* --- 右侧表单区 --- */
.login-right {
  flex: 4;
  padding: 50px 50px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  background: #ffffff;

  .form-title {
    font-size: 28px;
    color: #333;
    margin: 0;
    font-weight: 600;
  }
  .form-subtitle {
    font-size: 14px;
    color: #888;
    margin: 10px 0 35px 0;
  }
}

.login-form {
  width: 100%;
}

/* 现代化输入框样式覆盖 */
::v-deep .modern-input {
  .el-input__inner {
    height: 48px;
    line-height: 48px;
    border-radius: 8px;
    background-color: #f7f9fc;
    border: 1px solid transparent;
    padding-left: 38px;
    transition: all 0.3s;

    &:focus {
      background-color: #fff;
      border-color: #1890ff;
      box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.1);
    }
  }
  .el-input__prefix {
    left: 12px;
  }
}

.input-icon {
  height: 48px;
  width: 16px;
  color: #a0a0a0;
}

/* 验证码 Flex 布局 */
.captcha-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 15px;

  .captcha-input {
    flex: 1;
  }
  .login-code {
    width: 110px;
    height: 48px;
    border-radius: 8px;
    overflow: hidden;
    box-shadow: 0 2px 8px rgba(0,0,0,0.05);
    .login-code-img {
      width: 100%;
      height: 100%;
      object-fit: cover;
      cursor: pointer;
      transition: transform 0.3s;
      &:hover {
        transform: scale(1.05);
      }
    }
  }
}

/* 记住我与注册区 */
.form-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 25px;

  .remember-me {
    margin: 0;
  }
  .register-link {
    font-size: 14px;
    color: #1890ff;
    text-decoration: none;
    transition: color 0.3s;
    &:hover {
      color: #40a9ff;
    }
  }
}

/* 登录大按钮 */
.login-btn {
  height: 48px;
  font-size: 16px;
  border-radius: 8px;
  font-weight: 500;
  letter-spacing: 2px;
  background: linear-gradient(to right, #1890ff, #3e98f7);
  border: none;
  transition: all 0.3s;

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 8px 20px rgba(24, 144, 255, 0.3);
  }
}

/* 最底部通用脚部 */
.el-login-footer {
  height: 40px;
  line-height: 40px;
  position: absolute;
  bottom: 0;
  width: 100%;
  text-align: center;
  color: rgba(255,255,255,0.7);
  font-size: 13px;
  letter-spacing: 1px;
}
</style>
