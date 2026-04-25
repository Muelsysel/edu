<template>
  <div class="portal-layout">
    <header class="portal-header">
      <div class="header-inner">
        <div class="brand" @click="goHome">
          <img src="@/assets/logo/logo.png" alt="logo" class="brand-logo" v-if="false" /> <i class="el-icon-reading brand-icon"></i>
          <div class="brand-copy">
            <strong>教学成果管理门户</strong>
            <span>Teaching Achievement Portal</span>
          </div>
        </div>

        <nav class="nav">
          <router-link to="/portal/home">首页门户</router-link>
          <router-link to="/portal/news">新闻动态</router-link>
          <router-link v-if="isTeacher" to="/portal/declare">教师申报</router-link>
          <router-link v-if="isTeacher" to="/portal/mine">我的申报</router-link>
          <router-link v-if="isCollegeAuditor" to="/portal/audit/college">院级初审</router-link>
          <router-link v-if="isSchoolAuditor" to="/portal/audit/school">校级复审</router-link>
          <router-link v-if="isAuditor" to="/portal/audit/records">审核档案</router-link>
        </nav>

        <div class="user-actions">
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
              <el-badge slot="reference" :value="noticeCount" :hidden="noticeCount === 0" :max="99" class="notice-badge hover-effect">
                <i class="el-icon-message-solid notice-icon"></i>
              </el-badge>
            </el-popover>

            <el-dropdown class="user-dropdown hover-effect" trigger="hover">
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
            <el-button type="primary" round class="login-btn" @click="goLogin">账号登录</el-button>
          </template>
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
// import { updateNotice } from "@/api/system/notice"
import { getPortalLandingPath, isAdminUser, isAuditorUser, isCollegeAuditor, isSchoolAuditor } from '@/utils/role-route'

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
    userId() { return this.$store.state.user.id || this.name; },
    hasToken() { return !!getToken() },
    isAdmin() { return isAdminUser(this.roles) },
    isAuditor() { return isAuditorUser(this.roles) },
    isCollegeAuditor() { return isCollegeAuditor(this.roles) },
    isSchoolAuditor() { return isSchoolAuditor(this.roles) },
    isTeacher() { return this.hasToken && !this.isAuditor && !this.isAdmin }
  },
  mounted() {
    // 只有在登录状态下才去轮询消息，防止 Gateway AuthFilter 报 401 拦截
    if (this.hasToken) {
      this.fetchNoticeCount();
      this.noticeTimer = setInterval(() => { this.fetchNoticeCount(); }, 60000);
    }
  },
  beforeDestroy() {
    if (this.noticeTimer) clearInterval(this.noticeTimer);
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
          // 物理级刷新，清除一切前端Vuex和路由状态，回到首页
          window.location.href = '/portal/home?_t=' + new Date().getTime();
        }).catch(() => {
          window.location.href = '/portal/home?_t=' + new Date().getTime();
        })
      }).catch(() => {})
    },
    // --- 消息通知相关逻辑 ---
    fetchNoticeCount() {
      if(!this.hasToken) return;
      request({ url: '/system/notice/list', method: 'get', params: { pageNum: 1, pageSize: 1, status: '0', createBy: '[-' + this.userId + '-]' } })
        .then(res => { this.noticeCount = res.total || 0; }).catch(() => {});
    },
    loadNotices() {
      this.noticeLoading = true;
      request({ url: '/system/notice/list', method: 'get', params: { pageNum: 1, pageSize: 10, status: '0', createBy: '[-' + this.userId + '-]' } })
        .then(res => {
          this.noticeList = res.rows || [];
          this.noticeCount = res.total || 0;
          this.noticeLoading = false;
        }).catch(() => { this.noticeLoading = false; });
    },
    confirmNotice(item) {
      this.$set(item, 'isRead', true);
      request({ url: '/system/notice/read/' + item.noticeId, method: 'put' }).then(() => {
        setTimeout(() => {
          this.noticeList = this.noticeList.filter(n => n.noticeId !== item.noticeId);
          this.noticeCount = Math.max(0, this.noticeCount - 1);
        }, 300);
      }).catch(error => {
        // revert optimistic update
        this.$set(item, 'isRead', false);
        this.$message.error('标记失败，请重试');
        console.error(error);
      });
    },
    confirmAllNotices() {
      if (this.noticeList.length === 0) return;
      this.noticeLoading = true;
      const promises = this.noticeList.map(item => request({ url: '/system/notice/read/' + item.noticeId, method: 'put' }));
      Promise.all(promises).then(() => {
        this.$message.success('全部已读');
        this.noticeList = [];
        this.noticeCount = 0;
      }).catch(error => {
        this.$message.error('部分标记失败，请重试');
        console.error(error);
        this.loadNotices();
        this.fetchNoticeCount();
      }).finally(() => { this.noticeLoading = false; })
    },
    stripHtml(html) {
      if (!html) return '';
      let tmp = document.createElement("DIV"); tmp.innerHTML = html;
      let text = tmp.textContent || tmp.innerText || "";
      return text.length > 45 ? text.substring(0, 45) + '...' : text;
    }
  }
}
</script>

<style scoped>
/* 全局底色：极浅的高级灰/蓝底色 */
.portal-layout {
  min-height: 100vh;
  background: #f4f7fb;
  background-image: radial-gradient(circle at 15% 50%, rgba(37, 99, 235, 0.04), transparent 25%),
  radial-gradient(circle at 85% 30%, rgba(37, 99, 235, 0.04), transparent 25%);
}

/* 浅色亚克力通透导航栏 */
.portal-header {
  position: sticky;
  top: 0;
  z-index: 999;
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: saturate(180%) blur(20px);
  border-bottom: 1px solid rgba(226, 232, 240, 0.8);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.02);
}

.header-inner {
  max-width: 1320px;
  margin: 0 auto;
  height: 64px;
  padding: 0 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

/* 品牌区域 */
.brand {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  transition: opacity 0.3s;
}
.brand:hover { opacity: 0.8; }
.brand-icon { font-size: 32px; color: #2563eb; } /* 学术蓝 */
.brand-copy { display: flex; flex-direction: column; }
.brand-copy strong { font-size: 18px; color: #0f172a; font-weight: 600; letter-spacing: 0.5px;}
.brand-copy span { font-size: 11px; color: #64748b; font-family: Arial, sans-serif;}

/* 导航链接 */
.nav {
  display: flex;
  align-items: center;
  gap: 32px;
  height: 100%;
}
.nav a {
  color: #475569;
  text-decoration: none;
  font-size: 15px;
  font-weight: 500;
  height: 100%;
  display: flex;
  align-items: center;
  position: relative;
  transition: color 0.3s;
}
.nav a:hover { color: #2563eb; }
.nav a.router-link-active { color: #2563eb; font-weight: 600; }
.nav a.router-link-active::after {
  content: '';
  position: absolute;
  bottom: 0; left: 10%; right: 10%;
  height: 3px;
  background: #2563eb;
  border-radius: 3px 3px 0 0;
}

/* 右侧用户区 */
.user-actions { display: flex; align-items: center; gap: 16px; }

/* 悬浮交互效果 */
.hover-effect {
  padding: 6px 10px;
  border-radius: 8px;
  cursor: pointer;
  transition: background 0.3s;
}
.hover-effect:hover { background: rgba(15, 23, 42, 0.04); }

/* 消息通知图标 */
.notice-badge { display: flex; align-items: center; margin-right: 8px;}
.notice-icon { font-size: 20px; color: #64748b; transition: color 0.3s;}
.notice-badge:hover .notice-icon { color: #2563eb; }

/* 用户下拉区 */
.user-dropdown { display: flex; align-items: center; }
.avatar-wrapper { display: flex; align-items: center; gap: 8px; }
.avatar-circle {
  width: 32px; height: 32px; border-radius: 50%;
  background: linear-gradient(135deg, #2563eb, #60a5fa);
  color: white; font-weight: bold; font-size: 14px;
  display: flex; align-items: center; justify-content: center;
  box-shadow: 0 2px 8px rgba(37, 99, 235, 0.2);
}
.nick { font-size: 14px; color: #334155; font-weight: 500;}

.login-btn { padding: 8px 24px; font-weight: 500; box-shadow: 0 4px 12px rgba(37, 99, 235, 0.2); }

/* 页面主体 */
.portal-main {
  max-width: 1320px;
  margin: 0 auto;
  padding: 24px;
  min-height: calc(100vh - 64px);
}

.text-danger { color: #ef4444 !important; }

/* 通知下拉框内部样式 */
.notice-list-container { max-height: 400px; overflow: hidden; }
.notice-list-header { display: flex; justify-content: space-between; align-items: center; padding: 12px 16px; border-bottom: 1px solid #f1f5f9; }
.notice-header-title { font-weight: 600; font-size: 15px; color: #1e293b; }
.notice-scroll-wrap { max-height: 340px; overflow-y: auto; }
.notice-empty { text-align: center; padding: 40px 20px; color: #94a3b8; }
.notice-empty-icon { font-size: 42px; margin-bottom: 12px; display: block; opacity: 0.5;}
.notice-item { padding: 14px 16px; border-bottom: 1px solid #f8fafc; transition: background 0.3s; }
.notice-item:hover { background: #f8fafc; }
.notice-read { opacity: 0.5; }
.notice-item-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px; }
.notice-title { font-weight: 600; font-size: 14px; color: #0f172a; }
.notice-time { font-size: 12px; color: #94a3b8; }
.notice-content { font-size: 13px; color: #64748b; line-height: 1.6; margin-bottom: 10px; }
.notice-action { text-align: right; }
.notice-read-label { font-size: 12px; color: #94a3b8; }

/* 过渡动画 */
.fade-transform-leave-active, .fade-transform-enter-active { transition: all .3s; }
.fade-transform-enter { opacity: 0; transform: translateX(-20px); }
.fade-transform-leave-to { opacity: 0; transform: translateX(20px); }
</style>

<style>
/* 修改 3: 全局修复 notice-popover 位置，强制提高层级 */
.notice-popover-fix {
  z-index: 2000 !important;
  margin-top: 4px;
}
</style>
