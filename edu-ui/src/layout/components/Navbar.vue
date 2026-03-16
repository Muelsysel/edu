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
          width="320"
          trigger="click"
          @show="loadNotices"
          popper-class="notice-popover"
        >
          <div class="notice-list-container">
            <div class="notice-list-header">
              <span>消息通知</span>
              <el-button type="text" size="mini" v-if="noticeList.length > 0" @click="confirmAllNotices">全部已读</el-button>
            </div>
            <div class="notice-scroll-wrap" v-loading="noticeLoading">
              <div v-if="noticeList.length === 0" class="notice-empty" style="text-align: center; padding: 20px; color: #999;">
                <i class="el-icon-bell notice-empty-icon" style="font-size: 32px; margin-bottom: 8px;"></i>
                <p style="margin: 0; font-size: 13px;">暂无新消息</p>
              </div>
              <div v-for="item in noticeList" :key="item.noticeId" class="notice-item" style="padding: 12px; border-bottom: 1px solid #ebeef5; transition: all 0.3s;" :style="{ opacity: item.isRead ? 0.3 : 1 }">
                <div class="notice-item-header" style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 6px;">
                  <span class="notice-title" style="font-weight: 500; font-size: 14px; color: #303133;">{{ item.noticeTitle }}</span>
                  <span class="notice-time" style="font-size: 12px; color: #909399;">{{ parseTime(item.createTime, '{y}-{m}-{d} {h}:{i}') }}</span>
                </div>
                <div class="notice-content" v-html="stripHtml(item.noticeContent)" style="font-size: 13px; color: #606266; line-height: 1.5; margin-bottom: 8px;"></div>
                <div class="notice-action" style="text-align: right;">
                  <span v-if="item.isRead" style="font-size: 12px; color: #909399; margin-right: 8px;">已读</span>
                  <el-button v-else size="mini" type="primary" plain round @click="confirmNotice(item)">我知道了</el-button>
                </div>
              </div>
            </div>
          </div>
          <el-badge slot="reference" :value="noticeCount" :hidden="noticeCount === 0" :max="99" class="right-menu-item hover-effect notice-badge">
            <i class="el-icon-bell" style="font-size: 20px; cursor: pointer;"></i>
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
      'name' // User name/id usually mapped here
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
      // In RuoYi, id or name in getters might represent the userId or username.
      // Often, the backend `sendAuditNotice` uses `String.valueOf(teacherId)`.
      // We will obtain it from the store state.user.id. Since we need to access getters safely:
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
      // 修改：为避免 LIKE 模糊匹配（如userId=1匹配到10,11等），后端在写入时给 userId 穿上包裹符 [-ID-]
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
      // 视觉上先隐藏或变成已读
      this.$set(item, 'isRead', true);
      // 将通知设为关闭状态 (status = 1) 代表已读或已确认
      const data = { ...item, status: '1' };
      updateNotice(data).then(() => {
        // 延迟移除，给一点动画时间
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
      // 截取前100个字符
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
  height: 60px; // 稍微增高顶部导航，增加大气感
  overflow: hidden;
  position: relative;
  background: transparent; // 配合外层的毛玻璃
  display: flex;
  align-items: center;
  box-sizing: border-box;

  .hamburger-container {
    line-height: 60px;
    height: 100%;
    cursor: pointer;
    transition: background .3s;
    -webkit-tap-highlight-color: transparent;
    display: flex;
    align-items: center;
    padding: 0 15px;

    &:hover {
      background: rgba(0, 0, 0, .03);
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
    padding-right: 15px;

    &:focus {
      outline: none;
    }

    .right-menu-item {
      display: inline-block;
      padding: 0 12px;
      height: 100%;
      font-size: 18px;
      color: #5a5e66;
      display: flex;
      align-items: center;

      &.hover-effect {
        cursor: pointer;
        transition: background .3s;
        border-radius: 8px; // 给右侧按钮增加点击悬浮的圆角
        height: 44px;

        &:hover {
          background: rgba(0, 0, 0, .04);
        }
      }
    }

    .avatar-container {
      margin-left: 10px;

      .avatar-wrapper {
        display: flex;
        align-items: center;
        position: relative;

        .user-avatar {
          width: 32px;
          height: 32px;
          border-radius: 50%;
          border: 1px solid #f0f0f0;
        }

        .user-nickname {
          margin-left: 10px;
          font-size: 14px;
          font-weight: 500;
          color: #333;
        }

        .el-icon-arrow-down {
          margin-left: 5px;
          font-size: 12px;
          color: #999;
        }
      }
    }
  }
}

// 修改下拉菜单弹出框的圆角和颜色
::v-deep .el-dropdown-menu.user-dropdown {
  border-radius: 8px;
  .logout-btn {
    color: #ff4d4f;
  }
}
</style>
