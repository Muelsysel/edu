<template>
  <div class="navbar" :class="'nav' + navType">
    <hamburger id="hamburger-container" :is-active="sidebar.opened" class="hamburger-container" @toggleClick="toggleSideBar" />

    <breadcrumb v-if="navType == 1" id="breadcrumb-container" class="breadcrumb-container" />
    <top-nav v-if="navType == 2" id="topmenu-container" class="topmenu-container" />
    <template v-if="navType == 3">
      <logo v-show="showLogo" :collapse="false"></logo>
      <top-bar id="topbar-container" class="topbar-container" />
    </template>

    <div class="right-menu">
      <template v-if="device !== 'mobile'">
        <search id="header-search" class="right-menu-item" />

        <el-popover
          placement="bottom-end"
          width="360"
          trigger="click"
          @show="loadNotices"
          popper-class="notice-popover"
        >
          <div class="notice-list-container">
            <div class="notice-list-header">
              <span class="notice-header-title">消息通知</span>
              <el-button type="text" size="mini" v-if="noticeList.length > 0" @click="confirmAllNotices">全部已读</el-button>
            </div>
            <div class="notice-scroll-wrap" v-loading="noticeLoading">
              <div v-if="noticeList.length === 0" class="notice-empty">
                <i class="el-icon-bell notice-empty-icon"></i>
                <p>暂无新消息</p>
              </div>
              <div v-for="item in noticeList" :key="item.noticeId" class="notice-item" :class="{ 'notice-read': item.isRead }">
                <div class="notice-item-header">
                  <span class="notice-title">{{ item.noticeTitle }}</span>
                  <span class="notice-time">{{ parseTime(item.createTime, '{y}-{m}-{d} {h}:{i}') }}</span>
                </div>
                <div class="notice-content" v-html="stripHtml(item.noticeContent)"></div>
                <div class="notice-action">
                  <span v-if="item.isRead" class="notice-read-label">已读</span>
                  <el-button v-else size="mini" type="primary" plain round @click="confirmNotice(item)">我知道了</el-button>
                </div>
              </div>
            </div>
          </div>
          <el-badge slot="reference" :value="noticeCount" :hidden="noticeCount === 0" :max="99" class="right-menu-item hover-effect notice-badge">
            <i class="el-icon-bell" style="font-size: 18px; cursor: pointer;"></i>
          </el-badge>
        </el-popover>

        <screenfull id="screenfull" class="right-menu-item hover-effect" />
      </template>

      <el-dropdown class="avatar-container right-menu-item hover-effect" trigger="hover">
        <div class="avatar-wrapper">
          <img :src="avatar" class="user-avatar" alt="avatar">
          <span class="user-nickname">{{ nickName }}</span>
          <i class="el-icon-arrow-down el-icon--right"></i>
        </div>
        <el-dropdown-menu slot="dropdown" class="user-dropdown">
          <router-link to="/user/profile">
            <el-dropdown-item icon="el-icon-user">个人中心</el-dropdown-item>
          </router-link>
          <el-dropdown-item @click.native="setLayout" v-if="setting" icon="el-icon-setting">
            <span>布局设置</span>
          </el-dropdown-item>
          <el-dropdown-item divided @click.native="logout" icon="el-icon-switch-button" class="logout-btn">
            <span>退出系统</span>
          </el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
    </div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import Breadcrumb from '@/components/Breadcrumb'
import TopNav from '@/components/TopNav'
import TopBar from './TopBar'
import Logo from './Sidebar/Logo'
import Hamburger from '@/components/Hamburger'
import Screenfull from '@/components/Screenfull'
import Search from '@/components/HeaderSearch'
import request from '@/utils/request'
import { updateNotice } from "@/api/system/notice"

export default {
  emits: ['setLayout'],
  components: {
    Breadcrumb,
    Logo,
    TopNav,
    TopBar,
    Hamburger,
    Screenfull,
    Search
  },
  data() {
    return {
      noticeCount: 0,
      noticeTimer: null,
      noticeList: [],
      noticeLoading: false
    }
  },
  computed: {
    ...mapGetters([
      'sidebar',
      'avatar',
      'device',
      'nickName',
      'name'
    ]),
    setting: {
      get() {
        return this.$store.state.settings.showSettings
      }
    },
    navType: {
      get() {
        return this.$store.state.settings.navType
      }
    },
    showLogo: {
      get() {
        return this.$store.state.settings.sidebarLogo
      }
    },
    userId() {
      return this.$store.state.user.id || this.name;
    }
  },
  methods: {
    toggleSideBar() {
      this.$store.dispatch('app/toggleSideBar')
    },
    setLayout(event) {
      this.$emit('setLayout')
    },
    logout() {
      this.$confirm('确定注销并退出系统吗？', '系统提示', {
        confirmButtonText: '确定退出',
        cancelButtonText: '取消',
        type: 'warning',
        customClass: 'modern-confirm'
      }).then(() => {
        this.$store.dispatch('LogOut').then(() => {
          location.href = '/index'
        })
      }).catch(() => {})
    },
    fetchNoticeCount() {
      request({ url: '/system/notice/list', method: 'get', params: { pageNum: 1, pageSize: 1, status: '0', createBy: '[-' + this.userId + '-]' } })
        .then(res => { this.noticeCount = res.total || 0; })
        .catch(() => {});
    },
    loadNotices() {
      this.noticeLoading = true;
      request({ url: '/system/notice/list', method: 'get', params: { pageNum: 1, pageSize: 10, status: '0', createBy: '[-' + this.userId + '-]' } })
        .then(res => {
          this.noticeList = res.rows || [];
          this.noticeCount = res.total || 0;
          this.noticeLoading = false;
        })
        .catch(() => {
          this.noticeLoading = false;
        });
    },
    confirmNotice(item) {
      this.$set(item, 'isRead', true);
      const data = { ...item, status: '1' };
      updateNotice(data).then(() => {
        setTimeout(() => {
          this.noticeList = this.noticeList.filter(n => n.noticeId !== item.noticeId);
          this.noticeCount = Math.max(0, this.noticeCount - 1);
        }, 300);
      });
    },
    confirmAllNotices() {
      if (this.noticeList.length === 0) return;
      this.noticeLoading = true;
      const promises = this.noticeList.map(item => {
        return updateNotice({ ...item, status: '1' });
      });
      Promise.all(promises).then(() => {
        this.$message.success('全部已读');
        this.loadNotices();
        this.fetchNoticeCount();
      }).finally(() => {
        this.noticeLoading = false;
      })
    },
    stripHtml(html) {
      if (!html) return '';
      let tmp = document.createElement("DIV");
      tmp.innerHTML = html;
      let text = tmp.textContent || tmp.innerText || "";
      return text.length > 50 ? text.substring(0, 50) + '...' : text;
    }
  },
  mounted() {
    this.fetchNoticeCount();
    this.noticeTimer = setInterval(() => { this.fetchNoticeCount(); }, 60000);
  },
  beforeDestroy() {
    if (this.noticeTimer) clearInterval(this.noticeTimer);
  }
}
</script>

<style lang="scss" scoped>
.navbar.nav3 {
  .hamburger-container {
    display: none !important;
  }
}

.navbar {
  height: 56px;
  overflow: hidden;
  position: relative;
  background: transparent;
  display: flex;
  align-items: center;
  box-sizing: border-box;

  .hamburger-container {
    line-height: 56px;
    height: 100%;
    cursor: pointer;
    transition: background .3s;
    -webkit-tap-highlight-color: transparent;
    display: flex;
    align-items: center;
    padding: 0 14px;

    &:hover {
      background: rgba(15, 23, 42, 0.04);
      border-radius: 8px;
    }
  }

  .breadcrumb-container {
    flex-shrink: 0;
  }

  .topmenu-container {
    position: absolute;
    left: 50px;
  }

  .topbar-container {
    flex: 1;
    min-width: 0;
    display: flex;
    align-items: center;
    overflow: hidden;
    margin-left: 8px;
  }

  .right-menu {
    height: 100%;
    display: flex;
    align-items: center;
    margin-left: auto;
    padding-right: 16px;

    &:focus {
      outline: none;
    }

    .right-menu-item {
      display: inline-block;
      padding: 0 10px;
      height: 100%;
      font-size: 18px;
      color: #475569;
      display: flex;
      align-items: center;

      &.hover-effect {
        cursor: pointer;
        transition: all .25s ease;
        border-radius: 8px;
        height: 40px;

        &:hover {
          background: rgba(15, 23, 42, 0.05);
        }
      }
    }

    .avatar-container {
      margin-left: 8px;

      .avatar-wrapper {
        display: flex;
        align-items: center;
        position: relative;

        .user-avatar {
          width: 30px;
          height: 30px;
          border-radius: 50%;
          border: 2px solid #e2e8f0;
        }

        .user-nickname {
          margin-left: 8px;
          font-size: 13px;
          font-weight: 500;
          color: #334155;
        }

        .el-icon-arrow-down {
          margin-left: 4px;
          font-size: 11px;
          color: #94a3b8;
        }
      }
    }
  }
}

// 通知弹窗样式
.notice-list-container {
  max-height: 400px;
  overflow: hidden;
}

.notice-list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  border-bottom: 1px solid #e2e8f0;

  .notice-header-title {
    font-weight: 600;
    font-size: 14px;
    color: #0f172a;
  }
}

.notice-scroll-wrap {
  max-height: 340px;
  overflow-y: auto;
}

.notice-empty {
  text-align: center;
  padding: 32px 20px;
  color: #94a3b8;

  .notice-empty-icon {
    font-size: 36px;
    margin-bottom: 8px;
    display: block;
  }

  p {
    margin: 0;
    font-size: 13px;
  }
}

.notice-item {
  padding: 14px 16px;
  border-bottom: 1px solid #f1f5f9;
  transition: all 0.3s;

  &.notice-read {
    opacity: 0.35;
  }

  .notice-item-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 6px;
  }

  .notice-title {
    font-weight: 500;
    font-size: 14px;
    color: #0f172a;
  }

  .notice-time {
    font-size: 11px;
    color: #94a3b8;
  }

  .notice-content {
    font-size: 13px;
    color: #64748b;
    line-height: 1.5;
    margin-bottom: 8px;
  }

  .notice-action {
    text-align: right;

    .notice-read-label {
      font-size: 12px;
      color: #94a3b8;
    }
  }
}

// 下拉菜单
::v-deep .el-dropdown-menu.user-dropdown {
  border-radius: 10px;
  .logout-btn {
    color: #ef4444;
  }
}
</style>
