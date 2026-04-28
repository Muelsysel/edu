<template>
  <div class="login-page">
    <header class="login-header">
      <div class="brand">
        <span class="brand-logo">教</span>
        <div class="brand-text">
          <strong>{{ title }}</strong>
          <em>Teaching Achievement Management System</em>
        </div>
      </div>
      <router-link class="home-link" to="/">
        <i class="el-icon-s-home" />
        网站首页
      </router-link>
    </header>

    <main class="login-main">
      <section class="intro-panel">
        <div class="intro-kicker">Quality Management Portal</div>
        <h1>教学成果全过程管理平台</h1>
        <p>面向教师申报、学校审核、结果归档与数据统计，形成清晰、规范、可追溯的教学质量管理闭环。</p>

        <div class="intro-cards">
          <div class="intro-card">
            <i class="el-icon-edit-outline" />
            <strong>在线申报</strong>
            <span>成果材料集中提交</span>
          </div>
          <div class="intro-card">
            <i class="el-icon-s-check" />
            <strong>流程审核</strong>
            <span>审核意见全程留痕</span>
          </div>
          <div class="intro-card">
            <i class="el-icon-data-analysis" />
            <strong>统计归档</strong>
            <span>数据汇总辅助决策</span>
          </div>
        </div>
      </section>

      <section class="login-card">
        <div class="card-head">
          <span>Account Login</span>
          <h2>用户登录</h2>
          <p>请输入账号、密码和验证码进入系统。</p>
        </div>

        <el-form ref="loginForm" :model="loginForm" :rules="loginRules" class="login-form">
          <el-form-item prop="username">
            <el-input
              v-model="loginForm.username"
              type="text"
              auto-complete="off"
              placeholder="请输入用户名"
              class="login-input"
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
              class="login-input"
              show-password
              @keyup.enter.native="handleLogin"
            >
              <svg-icon slot="prefix" icon-class="password" class="el-input__icon input-icon" />
            </el-input>
          </el-form-item>

          <el-form-item v-if="captchaEnabled" prop="code">
            <div class="captcha-row">
              <el-input
                v-model="loginForm.code"
                auto-complete="off"
                placeholder="请输入验证码"
                class="login-input captcha-input"
                @keyup.enter.native="handleLogin"
              >
                <svg-icon slot="prefix" icon-class="validCode" class="el-input__icon input-icon" />
              </el-input>
              <button type="button" class="captcha-box" title="点击刷新验证码" @click="getCode">
                <img :src="codeUrl" class="login-code-img" />
              </button>
            </div>
          </el-form-item>

          <div class="form-options">
            <el-checkbox v-model="loginForm.rememberMe">记住密码</el-checkbox>
            <router-link v-if="register" class="register-link" :to="'/register'">注册账号</router-link>
          </div>

          <el-button
            :loading="loading"
            type="primary"
            class="login-btn"
            @click.native.prevent="handleLogin"
          >
            <span v-if="!loading">登录系统</span>
            <span v-else>正在登录...</span>
          </el-button>
        </el-form>
      </section>
    </main>

    <footer class="login-footer">{{ footerContent }}</footer>
  </div>
</template>

<script>
import { getCodeImg } from '@/api/login'
import Cookies from 'js-cookie'
import { encrypt, decrypt } from '@/utils/jsencrypt'
import defaultSettings from '@/settings'
import { getLandingPathByRoles } from '@/utils/role-route'

export default {
  name: 'Login',
  data() {
    return {
      title: process.env.VUE_APP_TITLE || '高校教学成果管理系统',
      footerContent: defaultSettings.footerContent,
      codeUrl: '',
      loginForm: {
        username: 'admin',
        password: 'admin123',
        rememberMe: false,
        code: '',
        uuid: ''
      },
      loginRules: {
        username: [{ required: true, trigger: 'blur', message: '请输入用户名' }],
        password: [{ required: true, trigger: 'blur', message: '请输入密码' }],
        code: [{ required: true, trigger: 'change', message: '请输入验证码' }]
      },
      loading: false,
      captchaEnabled: true,
      register: true,
      redirect: undefined
    }
  },
  watch: {
    $route: {
      handler(route) {
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
          this.codeUrl = 'data:image/gif;base64,' + res.img
          this.loginForm.uuid = res.uuid
        }
      })
    },
    getCookie() {
      const username = Cookies.get('username')
      const password = Cookies.get('password')
      const rememberMe = Cookies.get('rememberMe')
      this.loginForm = {
        ...this.loginForm,
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
            Cookies.set('username', this.loginForm.username, { expires: 30 })
            Cookies.set('password', encrypt(this.loginForm.password), { expires: 30 })
            Cookies.set('rememberMe', this.loginForm.rememberMe, { expires: 30 })
          } else {
            Cookies.remove('username')
            Cookies.remove('password')
            Cookies.remove('rememberMe')
          }
          this.$store.dispatch('Login', this.loginForm).then(() => {
            this.$store.dispatch('GetInfo').then(res => {
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
.login-page {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background:
    linear-gradient(90deg, rgba(250, 253, 255, 0.96), rgba(232, 246, 255, 0.88)),
    url('~@/assets/images/portal-hero-ink.png') center bottom / cover;
}

.login-header {
  height: 84px;
  padding: 0 7vw;
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: rgba(255, 255, 255, 0.9);
  border-bottom: 1px solid #d7e6f4;
  color: #173b63;
}

.brand {
  display: flex;
  align-items: center;
  gap: 14px;
}

.brand-logo {
  width: 48px;
  height: 48px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border: 2px solid #8fb7de;
  color: #176898;
  font-size: 25px;
  font-weight: 700;
}

.brand-text strong {
  display: block;
  font-size: 22px;
  letter-spacing: 1px;
}

.brand-text em {
  display: block;
  margin-top: 4px;
  color: #6c8299;
  font-style: normal;
  font-size: 12px;
}

.home-link {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  color: #245173;
  text-decoration: none;
  font-size: 14px;
}

.home-link:hover {
  color: #0b5c95;
}

.login-main {
  flex: 1;
  width: min(1320px, calc(100vw - 48px));
  margin: 0 auto;
  padding: 48px 0 58px;
  display: grid;
  grid-template-columns: minmax(0, 1fr) 430px;
  gap: 48px;
  align-items: center;
}

.intro-panel {
  min-height: 480px;
  padding: 46px 52px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  background: rgba(255, 255, 255, 0.72);
  border: 1px solid #d7e6f4;
  border-left: 4px solid #d6a23a;
  box-shadow: 0 18px 46px rgba(99, 139, 177, 0.14);
}

.intro-kicker {
  color: #0b5c95;
  font-size: 14px;
  letter-spacing: 1px;
  text-transform: uppercase;
}

.intro-panel h1 {
  margin: 16px 0 18px;
  color: #173b63;
  font-size: 46px;
  line-height: 1.24;
  letter-spacing: 1px;
}

.intro-panel p {
  margin: 0;
  max-width: 680px;
  color: #4f6f8b;
  font-size: 17px;
  line-height: 1.9;
}

.intro-cards {
  margin-top: 44px;
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  border: 1px solid #d7e6f4;
  background: rgba(255, 255, 255, 0.76);
}

.intro-card {
  min-height: 112px;
  padding: 20px 18px;
  border-right: 1px solid #d7e6f4;
}

.intro-card:last-child {
  border-right: none;
}

.intro-card i {
  color: #d6a23a;
  font-size: 26px;
}

.intro-card strong {
  display: block;
  margin-top: 12px;
  color: #173b63;
  font-size: 17px;
}

.intro-card span {
  display: block;
  margin-top: 6px;
  color: #6c8299;
  font-size: 13px;
}

.login-card {
  padding: 42px 38px 38px;
  background: rgba(255, 255, 255, 0.96);
  border: 1px solid #d7e6f4;
  border-top: 4px solid #d6a23a;
  box-shadow: 0 20px 46px rgba(99, 139, 177, 0.18);
}

.card-head {
  margin-bottom: 28px;
}

.card-head span {
  color: #0b5c95;
  font-size: 13px;
  letter-spacing: 1px;
  text-transform: uppercase;
}

.card-head h2 {
  margin: 10px 0 8px;
  color: #0d3564;
  font-size: 28px;
}

.card-head p {
  margin: 0;
  color: #6b7d91;
}

.login-form {
  width: 100%;
}

.login-form ::v-deep .el-form-item {
  margin-bottom: 20px;
}

.login-input ::v-deep .el-input__inner {
  height: 46px;
  line-height: 46px;
  border-radius: 0;
  border-color: #cfddeb;
  background: #f8fbff;
  padding-left: 40px;
}

.login-input ::v-deep .el-input__inner:focus {
  border-color: #0b5c95;
  background: #fff;
}

.input-icon {
  height: 46px;
  width: 16px;
  color: #7c91a6;
}

.captcha-row {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 116px;
  gap: 12px;
}

.captcha-box {
  height: 46px;
  padding: 0;
  border: 1px solid #cfddeb;
  background: #fff;
  cursor: pointer;
}

.login-code-img {
  width: 100%;
  height: 44px;
  display: block;
  object-fit: cover;
}

.form-options {
  margin: 2px 0 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.register-link {
  color: #0b5c95;
  font-size: 14px;
  text-decoration: none;
}

.register-link:hover {
  color: #d6a23a;
}

.login-btn {
  width: 100%;
  height: 46px;
  border-radius: 0;
  background: #0b5c95;
  border-color: #0b5c95;
  font-size: 16px;
  letter-spacing: 1px;
}

.login-btn:hover,
.login-btn:focus {
  background: #084a78;
  border-color: #084a78;
}

.login-footer {
  padding: 0 20px 22px;
  color: #6c8299;
  text-align: center;
  font-size: 13px;
}

@media (max-width: 980px) {
  .login-main {
    grid-template-columns: 1fr;
    gap: 28px;
    padding-top: 30px;
  }

  .intro-panel {
    min-height: auto;
  }

  .login-card {
    max-width: 430px;
    width: 100%;
    margin: 0 auto;
  }
}

@media (max-width: 640px) {
  .login-header {
    height: auto;
    padding: 18px 20px;
  }

  .brand-text strong {
    font-size: 18px;
  }

  .brand-text em,
  .home-link {
    display: none;
  }

  .login-main {
    width: calc(100vw - 28px);
    padding: 24px 0 42px;
  }

  .intro-panel,
  .login-card {
    padding: 28px 22px;
  }

  .intro-panel h1 {
    font-size: 30px;
  }

  .intro-cards {
    grid-template-columns: 1fr;
  }

  .intro-card {
    min-height: auto;
    border-right: none;
    border-bottom: 1px solid #d7e6f4;
  }

  .intro-card:last-child {
    border-bottom: none;
  }
}
</style>
