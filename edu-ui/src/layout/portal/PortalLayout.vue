<template>
  <div class="portal-layout">
    <header class="portal-header">
      <div class="top-band">
        <div class="top-inner">
          <div class="brand" @click="goHome">
            <div class="brand-mark">教</div>
            <div class="brand-copy">
              <strong>高校教学成果管理系统</strong>
              <span>Teaching Achievement Management Portal</span>
            </div>
          </div>
          <div class="motto">崇德尚学 · 笃行致远</div>
        </div>
      </div>

      <div class="nav-band">
        <div class="nav-inner">
          <nav class="nav">
            <router-link to="/portal/home">网站首页</router-link>
            <router-link to="/portal/news">新闻动态</router-link>
            <router-link v-if="isTeacher" to="/portal/declare">成果申报</router-link>
            <router-link v-if="isTeacher" to="/portal/mine">我的成果</router-link>
            <router-link v-if="isSchoolAuditor" to="/portal/audit/school">审核工作台</router-link>
            <router-link v-if="isAuditor" to="/portal/audit/records">审核档案</router-link>
          </nav>

          <div class="right-tools">
            <el-input
              class="site-search"
              size="mini"
              placeholder="请输入关键字"
              prefix-icon="el-icon-search"
            />

            <template v-if="hasToken">
              <el-popover placement="bottom" width="300" trigger="click" @show="loadNotices" popper-class="elegant-notice-popover notice-popover-fix">
                <div class="notice-list-container">
                  <div class="notice-list-header">
                    <span class="notice-header-title">系统通知</span>
                    <el-button type="text" size="mini" v-if="noticeList.length > 0" @click="confirmAllNotices">全部标为已读</el-button>
                  </div>
                  <div class="notice-scroll-wrap" v-loading="noticeLoading">
                    <div v-if="noticeList.length === 0" class="notice-empty">
                      <i class="el-icon-bell notice-empty-icon"></i>
                      <p>暂无新消息</p>
                    </div>
                    <div v-for="item in noticeList" :key="item.noticeId" class="notice-item" :class="{ 'notice-read': item.isRead }">
                      <div class="notice-item-header">
                        <span class="notice-title">{{ item.noticeTitle }}</span>
                        <span class="notice-time">{{ parseTime(item.createTime, '{m}-{d} {h}:{i}') }}</span>
                      </div>
                      <div class="notice-content" v-html="stripHtml(item.noticeContent)"></div>
                      <div class="notice-action">
                        <span v-if="item.isRead" class="notice-read-label">已读</span>
                        <el-button v-else size="mini" type="primary" plain round @click="confirmNotice(item)">我知道了</el-button>
                      </div>
                    </div>
                  </div>
                </div>
                <el-badge slot="reference" :value="noticeCount" :hidden="noticeCount === 0" :max="99" class="notice-badge">
                  <i class="el-icon-message-solid notice-icon"></i>
                </el-badge>
              </el-popover>

              <el-dropdown class="user-dropdown" trigger="hover">
                <div class="avatar-wrapper">
                  <div class="avatar-circle">{{ (nickName || name || 'U').charAt(0).toUpperCase() }}</div>
                  <span class="nick">{{ nickName || name }}</span>
                  <i class="el-icon-arrow-down el-icon--right"></i>
                </div>
                <el-dropdown-menu slot="dropdown" class="elegant-dropdown">
                  <router-link to="/portal/profile">
                    <el-dropdown-item icon="el-icon-user">个人设置</el-dropdown-item>
                  </router-link>
                  <router-link v-if="isAdmin" to="/index">
                    <el-dropdown-item icon="el-icon-s-platform">进入系统后台</el-dropdown-item>
                  </router-link>
                  <el-dropdown-item divided @click.native="logoutHandle" class="text-danger" icon="el-icon-switch-button">
                    退出登录
                  </el-dropdown-item>
                </el-dropdown-menu>
              </el-dropdown>
            </template>

            <template v-else>
              <el-button type="primary" size="mini" class="login-btn" @click="goLogin">账号登录</el-button>
            </template>
          </div>
        </div>
      </div>
    </header>

    <main class="portal-main">
      <transition name="fade-transform" mode="out-in">
        <router-view />
      </transition>
    </main>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import { getToken } from '@/utils/auth'
import request from '@/utils/request'
import { getPortalLandingPath, isAdminUser, isAuditorUser, isSchoolAuditor } from '@/utils/role-route'

export default {
  name: 'PortalLayout',
  data() {
    return {
      noticeCount: 0,
      noticeTimer: null,
      noticeList: [],
      noticeLoading: false
    }
  },
  computed: {
    ...mapGetters(['name', 'nickName', 'roles']),
    userId() { return this.$store.state.user.id || this.name },
    hasToken() { return !!getToken() },
    isAdmin() { return isAdminUser(this.roles) },
    isAuditor() { return isAuditorUser(this.roles) },
    isSchoolAuditor() { return isSchoolAuditor(this.roles) },
    isTeacher() { return this.hasToken && !this.isAuditor && !this.isAdmin }
  },
  mounted() {
    if (this.hasToken) {
      this.fetchNoticeCount()
      this.noticeTimer = setInterval(() => { this.fetchNoticeCount() }, 60000)
    }
  },
  beforeDestroy() {
    if (this.noticeTimer) clearInterval(this.noticeTimer)
  },
  methods: {
    goHome() { this.$router.push(getPortalLandingPath(this.roles)) },
    goLogin() {
      const redirect = encodeURIComponent(this.$route.fullPath || '/portal/home')
      this.$router.push('/login?redirect=' + redirect)
    },
    logoutHandle() {
      this.$confirm('确定要退出门户系统吗？', '提示', { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' }).then(() => {
        this.$store.dispatch('LogOut').then(() => {
          window.location.href = '/portal/home?_t=' + new Date().getTime()
        }).catch(() => {
          window.location.href = '/portal/home?_t=' + new Date().getTime()
        })
      }).catch(() => {})
    },
    fetchNoticeCount() {
      if (!this.hasToken) return
      request({ url: '/system/notice/list', method: 'get', params: { pageNum: 1, pageSize: 1, status: '0', createBy: '[-' + this.userId + '-]' } })
        .then(res => { this.noticeCount = res.total || 0 }).catch(() => {})
    },
    loadNotices() {
      this.noticeLoading = true
      request({ url: '/system/notice/list', method: 'get', params: { pageNum: 1, pageSize: 10, status: '0', createBy: '[-' + this.userId + '-]' } })
        .then(res => {
          this.noticeList = res.rows || []
          this.noticeCount = res.total || 0
          this.noticeLoading = false
        }).catch(() => { this.noticeLoading = false })
    },
    confirmNotice(item) {
      this.$set(item, 'isRead', true)
      request({ url: '/system/notice/read/' + item.noticeId, method: 'put' }).then(() => {
        setTimeout(() => {
          this.noticeList = this.noticeList.filter(n => n.noticeId !== item.noticeId)
          this.noticeCount = Math.max(0, this.noticeCount - 1)
        }, 300)
      }).catch(error => {
        this.$set(item, 'isRead', false)
        this.$message.error('标记失败，请重试')
        console.error(error)
      })
    },
    confirmAllNotices() {
      if (this.noticeList.length === 0) return
      this.noticeLoading = true
      const promises = this.noticeList.map(item => request({ url: '/system/notice/read/' + item.noticeId, method: 'put' }))
      Promise.all(promises).then(() => {
        this.$message.success('全部已读')
        this.noticeList = []
        this.noticeCount = 0
      }).catch(error => {
        this.$message.error('部分标记失败，请重试')
        console.error(error)
        this.loadNotices()
        this.fetchNoticeCount()
      }).finally(() => { this.noticeLoading = false })
    },
    stripHtml(html) {
      if (!html) return ''
      const tmp = document.createElement('DIV')
      tmp.innerHTML = html
      const text = tmp.textContent || tmp.innerText || ''
      return text.length > 45 ? text.substring(0, 45) + '...' : text
    }
  }
}
</script>

<style scoped>
.portal-layout {
  min-height: 100vh;
  background: #ffffff;
}

.portal-header {
  position: sticky;
  top: 0;
  z-index: 999;
  color: #173b63;
  box-shadow: 0 2px 12px rgba(78, 121, 165, 0.12);
}

.top-band {
  background: linear-gradient(180deg, #f7fbff 0%, #edf6ff 100%);
  border-bottom: 1px solid #d7e6f4;
}

.top-inner,
.nav-inner {
  width: min(1400px, calc(100% - 56px));
  margin: 0 auto;
}

.top-inner {
  height: 88px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.brand {
  display: flex;
  align-items: center;
  gap: 14px;
  cursor: pointer;
}

.brand-mark {
  width: 54px;
  height: 54px;
  border-radius: 50%;
  border: 3px solid #8fb7de;
  display: flex;
  align-items: center;
  justify-content: center;
  font-family: "STKaiti", "KaiTi", serif;
  font-size: 32px;
  font-weight: 700;
  line-height: 1;
}

.brand-copy {
  display: flex;
  flex-direction: column;
}

.brand-copy strong {
  font-size: 28px;
  line-height: 1.1;
  font-family: "STKaiti", "KaiTi", "Microsoft YaHei", serif;
  letter-spacing: 2px;
}

.brand-copy span {
  margin-top: 6px;
  font-size: 12px;
  letter-spacing: 0.7px;
  color: #6c8299;
  text-transform: uppercase;
}

.motto {
  min-width: 360px;
  align-self: stretch;
  padding: 0 46px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(217, 236, 250, 0.6);
  color: #8aa4bd;
  font-family: "STKaiti", "KaiTi", serif;
  font-size: 30px;
  letter-spacing: 8px;
}

.nav-band {
  background: #ffffff;
  border-top: 1px solid #e5eef7;
  border-bottom: 1px solid #d7e6f4;
}

.nav-inner {
  height: 52px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 28px;
}

.nav {
  display: flex;
  align-items: center;
  gap: 0;
  height: 100%;
}

.nav a {
  height: 100%;
  padding: 0 22px;
  display: inline-flex;
  align-items: center;
  color: #245173;
  text-decoration: none;
  font-size: 16px;
  font-weight: 600;
  white-space: nowrap;
}

.nav a:hover,
.nav a.router-link-active {
  background: #edf6ff;
  color: #0b5c95;
}

.right-tools {
  display: flex;
  align-items: center;
  gap: 14px;
  flex-shrink: 0;
}

.site-search {
  width: 190px;
}

.site-search ::v-deep .el-input__inner {
  border-radius: 999px;
  border: 1px solid #cfddeb;
  height: 30px;
  background: #ffffff;
}

.notice-badge {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.notice-icon {
  font-size: 20px;
  color: #3d78aa;
}

.avatar-wrapper {
  height: 34px;
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  color: #245173;
}

.avatar-circle {
  width: 30px;
  height: 30px;
  border-radius: 50%;
  background: #edf6ff;
  border: 1px solid #b8d3ec;
  color: #245173;
  font-weight: 700;
  display: flex;
  align-items: center;
  justify-content: center;
}

.nick {
  max-width: 86px;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
  font-size: 14px;
}

.login-btn {
  border-color: #8fb7de;
  background: #edf6ff;
  color: #245173;
}

.portal-main {
  min-height: calc(100vh - 140px);
}

.text-danger {
  color: #ef4444 !important;
}

.notice-list-container { max-height: 400px; overflow: hidden; }
.notice-list-header { display: flex; justify-content: space-between; align-items: center; padding: 12px 16px; border-bottom: 1px solid #f1f5f9; }
.notice-header-title { font-weight: 600; font-size: 15px; color: #1e293b; }
.notice-scroll-wrap { max-height: 340px; overflow-y: auto; }
.notice-empty { text-align: center; padding: 40px 20px; color: #94a3b8; }
.notice-empty-icon { font-size: 42px; margin-bottom: 12px; display: block; opacity: 0.5; }
.notice-item { padding: 14px 16px; border-bottom: 1px solid #f8fafc; transition: background 0.3s; }
.notice-item:hover { background: #f8fafc; }
.notice-read { opacity: 0.5; }
.notice-item-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px; }
.notice-title { font-weight: 600; font-size: 14px; color: #0f172a; }
.notice-time { font-size: 12px; color: #94a3b8; }
.notice-content { font-size: 13px; color: #64748b; line-height: 1.6; margin-bottom: 10px; }
.notice-action { text-align: right; }
.notice-read-label { font-size: 12px; color: #94a3b8; }

.fade-transform-leave-active,
.fade-transform-enter-active {
  transition: all .3s;
}
.fade-transform-enter { opacity: 0; transform: translateX(-20px); }
.fade-transform-leave-to { opacity: 0; transform: translateX(20px); }

@media (max-width: 1100px) {
  .motto,
  .site-search {
    display: none;
  }
  .nav a {
    padding: 0 12px;
    font-size: 14px;
  }
}

@media (max-width: 720px) {
  .top-inner {
    height: 72px;
  }
  .brand-copy strong {
    font-size: 21px;
  }
  .brand-copy span {
    display: none;
  }
  .nav-inner {
    height: auto;
    padding: 8px 0;
    flex-direction: column;
    align-items: flex-start;
  }
  .nav {
    width: 100%;
    overflow-x: auto;
  }
  .right-tools {
    width: 100%;
    justify-content: flex-end;
  }
}
</style>

<style>
.notice-popover-fix {
  z-index: 2000 !important;
  margin-top: 4px;
}
</style>
