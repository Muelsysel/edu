<template>
  <div class="login-wrapper">
    <div class="login-bg-pattern"></div>

    <div class="login-card">
      <!-- 左侧学术品牌区 -->
      <div class="login-left">
        <div class="left-content">
          <div class="brand-badge">
            <span class="brand-icon">🎓</span>
          </div>
          <h1 class="sys-title">{{ title }}</h1>
          <p class="sys-desc">高校信息化转型时代下的成果申报、多级审核与数字化管理平台</p>
          <div class="feature-list">
            <div class="feature-item">
              <i class="el-icon-document-checked"></i>
              <span>成果申报与管理</span>
            </div>
            <div class="feature-item">
              <i class="el-icon-s-check"></i>
              <span>多级审核流程</span>
            </div>
            <div class="feature-item">
              <i class="el-icon-data-analysis"></i>
              <span>数据可视化分析</span>
            </div>
          </div>
        </div>
        <div class="left-footer">
          <span>郑州轻工业大学</span>
        </div>
        <!-- 几何装饰 -->
        <div class="geo-line geo-1"></div>
        <div class="geo-line geo-2"></div>
        <div class="geo-dot geo-dot-1"></div>
        <div class="geo-dot geo-dot-2"></div>
      </div>

      <!-- 右侧表单区 -->
      <div class="login-right">
        <el-form ref="loginForm" :model="loginForm" :rules="loginRules" class="login-form">
          <h2 class="form-title">欢迎登录</h2>
          <p class="form-subtitle">请输入您的账号和密码以访问系统</p>

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

          <el-form-item style="width:100%; margin-top: 8px;">
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
import { getCodeImg } from "@/api/login"
import Cookies from "js-cookie"
import { encrypt, decrypt } from '@/utils/jsencrypt'
import defaultSettings from '@/settings'
import { getLandingPathByRoles } from '@/utils/role-route'

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
            this.$store.dispatch("GetInfo").then((res) => {
              const roles = res.roles || []
              const fallback = getLandingPathByRoles(roles)
              const redirect = this.redirect || fallback
              this.$router.push({ path: redirect }).catch(() => {})
            })
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
/* 全屏容器 — 深蓝学术底 */
.login-wrapper {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background: #0f172a;
  position: relative;
  overflow: hidden;
}

/* 几何纹路背景 */
.login-bg-pattern {
  position: absolute;
  top: 0; left: 0; right: 0; bottom: 0;
  background-image:
    radial-gradient(circle at 20% 50%, rgba(30, 64, 175, 0.15) 0%, transparent 50%),
    radial-gradient(circle at 80% 20%, rgba(212, 168, 83, 0.08) 0%, transparent 40%),
    radial-gradient(circle at 60% 80%, rgba(99, 102, 241, 0.06) 0%, transparent 40%);
  animation: bgFloat 20s ease-in-out infinite;
}

@keyframes bgFloat {
  0%, 100% { transform: translate(0, 0); }
  50% { transform: translate(-10px, 10px); }
}

/* 核心登录卡片 */
.login-card {
  position: relative;
  z-index: 10;
  display: flex;
  width: 960px;
  min-height: 540px;
  background: #ffffff;
  border-radius: 20px;
  box-shadow:
    0 25px 60px rgba(0, 0, 0, 0.4),
    0 0 0 1px rgba(255, 255, 255, 0.05);
  overflow: hidden;
}

/* --- 左侧品牌区 --- */
.login-left {
  flex: 5;
  background: linear-gradient(160deg, #0f172a 0%, #1e3a5f 50%, #1e40af 100%);
  padding: 44px 40px;
  color: #ffffff;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  position: relative;
  overflow: hidden;

  .left-content {
    z-index: 2;
    position: relative;
  }

  .brand-badge {
   width: 52px; height: 52px;
    background: rgba(212, 168, 83, 0.15);
    border: 1px solid rgba(212, 168, 83, 0.3);
    border-radius: 14px;
    display: flex; align-items: center; justify-content: center;
    margin-bottom: 28px;

    .brand-icon {
      font-size: 26px;
    }
  }

  .sys-title {
    font-family: 'Noto Serif SC', serif;
    font-size: 30px;
    font-weight: 700;
    margin-bottom: 16px;
    line-height: 1.3;
    letter-spacing: 1px;
    color: #f1f5f9;
  }

  .sys-desc {
    font-size: 14px;
    line-height: 1.8;
    color: rgba(203, 213, 225, 0.8);
    margin-bottom: 32px;
  }

  .feature-list {
    display: flex;
    flex-direction: column;
    gap: 14px;

    .feature-item {
      display: flex;
      align-items: center;
      gap: 10px;
      font-size: 13px;
      color: rgba(203, 213, 225, 0.7);

      i {
        color: #d4a853;
        font-size: 16px;
        width: 28px; height: 28px;
        background: rgba(212, 168, 83, 0.1);
        border-radius: 6px;
        display: flex; align-items: center; justify-content: center;
      }
    }
  }

  .left-footer {
    z-index: 2;
    position: relative;
    font-size: 12px;
    color: rgba(148, 163, 184, 0.5);
    letter-spacing: 1px;
  }

  /* 几何线条装饰 */
  .geo-line {
    position: absolute;
    background: rgba(212, 168, 83, 0.06);
    z-index: 1;
  }
  .geo-1 {
    width: 200px; height: 1px;
    top: 40%; right: -40px;
    transform: rotate(-30deg);
  }
  .geo-2 {
    width: 300px; height: 1px;
    bottom: 30%; left: -60px;
    transform: rotate(20deg);
  }
  .geo-dot {
    position: absolute;
    width: 6px; height: 6px;
    border-radius: 50%;
    background: rgba(212, 168, 83, 0.2);
    z-index: 1;
  }
  .geo-dot-1 { top: 20%; right: 20%; }
  .geo-dot-2 { bottom: 25%; left: 15%; }
}

/* --- 右侧表单区 --- */
.login-right {
  flex: 4;
  padding: 52px 48px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  background: #ffffff;

  .form-title {
    font-family: 'Noto Serif SC', serif;
    font-size: 26px;
    color: #0f172a;
    margin: 0;
    font-weight: 700;
  }
  .form-subtitle {
    font-size: 13px;
    color: #94a3b8;
    margin: 10px 0 32px 0;
  }
}

.login-form {
  width: 100%;
}

/* 输入框 */
::v-deep .modern-input {
  .el-input__inner {
    height: 46px;
    line-height: 46px;
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
  height: 46px;
  width: 16px;
  color: #94a3b8;
}

/* 验证码 */
.captcha-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;

  .captcha-input {
    flex: 1;
  }
  .login-code {
    width: 110px;
    height: 46px;
    border-radius: 10px;
    overflow: hidden;
    border: 1px solid #e2e8f0;
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

/* 记住我与注册 */
.form-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;

  .remember-me {
    margin: 0;
  }
  .register-link {
    font-size: 13px;
    color: #1e40af;
    text-decoration: none;
    font-weight: 500;
    transition: color 0.25s;
    &:hover {
      color: #d4a853;
    }
  }
}

/* 登录按钮 */
.login-btn {
  height: 46px;
  font-size: 15px;
  border-radius: 10px;
  font-weight: 600;
  letter-spacing: 3px;
  background: linear-gradient(135deg, #1e40af 0%, #1e3a5f 100%) !important;
  border: none !important;
  transition: all 0.3s ease;
  width: 100%;

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 8px 25px rgba(30, 64, 175, 0.35);
  }
}

/* 底部 */
.el-login-footer {
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
