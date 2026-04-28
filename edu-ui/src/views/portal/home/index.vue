<template>
  <div class="portal-home-container">
    <section class="banner-section">
      <el-carousel height="460px" arrow="hover" indicator-position="outside" class="elegant-carousel">
        <el-carousel-item v-for="(banner, index) in bannerList" :key="index">
          <div class="banner-item" :style="{ backgroundImage: `linear-gradient(rgba(15, 23, 42, 0.4), rgba(15, 23, 42, 0.7)), url(${banner.image})` }">
            <div class="banner-content">
              <el-tag size="medium" effect="dark" class="banner-tag" v-if="banner.tag">{{ banner.tag }}</el-tag>
              <h2 class="banner-title">{{ banner.title }}</h2>
              <p class="banner-subtitle">{{ banner.subtitle }}</p>
            </div>
          </div>
        </el-carousel-item>
      </el-carousel>
    </section>

    <div class="main-wrapper">
      <el-row :gutter="32" class="content-row">
        <el-col :xs="24" :sm="24" :md="14" :lg="15">
          <div class="elegant-panel">
            <div class="panel-header">
              <div class="title-with-deco">
                <span class="deco-line"></span>
                <h3 class="panel-title">新闻动态</h3>
              </div>
              <el-button type="text" class="more-btn" @click="$router.push('/portal/news?type=2')">
                查看更多 <i class="el-icon-right"></i>
              </el-button>
            </div>

            <div class="news-list">
              <el-empty v-if="newsList.length === 0" description="暂无新闻动态" :image-size="80"></el-empty>
              <div v-for="item in newsList" :key="item.newsId" class="news-card" @click="goDetail(item.newsId)">
                <div class="news-cover">
                  <el-image :src="item.coverImage || require('@/assets/images/profile.jpg')" lazy fit="cover">
                    <div slot="error" class="image-slot"><i class="el-icon-picture-outline"></i></div>
                  </el-image>
                </div>
                <div class="news-info">
                  <h4 class="news-title" :title="item.title">{{ item.title }}</h4>
                  <p class="news-summary" :title="item.summary">{{ item.summary || '点击查看校园最新教学成果动态与发展历程...' }}</p>
                  <div class="news-meta">
                    <span><i class="el-icon-time"></i> {{ parseTime(item.publishTime, '{y}-{m}-{d}') }}</span>
                    <span><i class="el-icon-view"></i> 阅读 {{ item.viewCount || 0 }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </el-col>

        <el-col :xs="24" :sm="24" :md="10" :lg="9">
          <div class="elegant-panel">
            <div class="panel-header">
              <div class="title-with-deco">
                <span class="deco-line"></span>
                <h3 class="panel-title">通知公告</h3>
              </div>
              <el-button type="text" class="more-btn" @click="$router.push('/portal/news?type=1')">
                更多 <i class="el-icon-right"></i>
              </el-button>
            </div>

            <div class="notice-list">
              <el-empty v-if="noticeList.length === 0" description="暂无通知公告" :image-size="80"></el-empty>
              <div v-for="(item, index) in noticeList" :key="item.noticeId || index" class="notice-item" @click="goDetail(item.noticeId || item.newsId)">
                <div class="notice-icon-wrap">
                  <i class="el-icon-bell"></i>
                </div>
                <div class="notice-content">
                  <h4 class="notice-title" :title="item.title">{{ item.title }}</h4>
                  <span class="notice-date">{{ parseTime(item.publishTime || item.createTime, '{y}-{m}-{d}') }}</span>
                </div>
              </div>
            </div>
          </div>
        </el-col>
      </el-row>

      <section class="quick-links-section">
        <div class="panel-header" style="margin-bottom: 24px;">
          <div class="title-with-deco">
            <span class="deco-line"></span>
            <h3 class="panel-title">业务直通车</h3>
          </div>
        </div>

        <el-row :gutter="24" class="quick-grid">
          <el-col :xs="12" :sm="8" :md="6" v-if="isTeacherRole">
            <div class="quick-card blue-card" @click="goWithAuth('/portal/declare')">
              <div class="card-icon"><i class="el-icon-document-add"></i></div>
              <div class="card-text">
                <h4>教师申报</h4>
                <p>在线填报年度教学成果</p>
              </div>
            </div>
          </el-col>
          <el-col :xs="12" :sm="8" :md="6" v-if="isTeacherRole">
            <div class="quick-card green-card" @click="goWithAuth('/portal/mine')">
              <div class="card-icon"><i class="el-icon-folder-opened"></i></div>
              <div class="card-text">
                <h4>我的申报</h4>
                <p>查看历史申报与审核进度</p>
              </div>
            </div>
          </el-col>

          <el-col :xs="12" :sm="8" :md="6" v-if="isSchoolRole">
            <div class="quick-card red-card" @click="goWithAuth('/portal/audit/school')">
              <div class="card-icon"><i class="el-icon-office-building"></i></div>
              <div class="card-text">
                <h4>校级审核</h4>
                <p>教务处校级终审与归档</p>
              </div>
            </div>
          </el-col>
          <el-col :xs="12" :sm="8" :md="6" v-if="isSchoolRole">
            <div class="quick-card purple-card" @click="goWithAuth('/portal/audit/records')">
              <div class="card-icon"><i class="el-icon-collection"></i></div>
              <div class="card-text">
                <h4>审核档案</h4>
                <p>查询全校已审批的成果</p>
              </div>
            </div>
          </el-col>
        </el-row>
      </section>
    </div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import { portalNewsList } from '@/api/achievement/portal'
import { getToken } from '@/utils/auth'

export default {
  name: 'PortalHome',
  data() {
    return {
      newsList: [],
      noticeList: [], // 新增：单独存储通知公告
      // 模拟的轮播图数据（后期可改为从后端接口获取）
      bannerList: [
        {
          image: require('@/assets/images/banner/banner1.jpg'),
          tag: '平台指引',
          title: '全面提升高校教学质量管理效能',
          subtitle: '一站式完成成果申报、流程审核与数据分析，打造透明、高效的教务生态。'
        },
        {
          image: require('@/assets/images/banner/banner2.jpg'),
          tag: '年度申报',
          title: '2026年度教学成果奖申报已启动',
          subtitle: '请各位教师及时登录系统完善申报材料，截止日期为本月底。'
        },
        {
          image: require('@/assets/images/banner/banner3.jpg'),
          tag: '系统更新',
          title: '全新工作台上线，审批更快捷',
          subtitle: '新增移动端预览、多格式附件切片上传及一键批量审批功能。'
        }
      ]
    }
  },
  computed: {
    ...mapGetters(['roles']),
    // 安全地推断当前用户的角色权限，用于动态渲染快捷入口
    isTeacherRole() {
      return !this.roles.includes('admin') && !this.roles.includes('SchoolAudit') && !this.roles.includes('auditor');
    },
    isSchoolRole() {
      return this.roles.includes('SchoolAudit') || this.roles.includes('auditor');
    }
  },
  created() {
    this.loadNews()
  },
  methods: {
    loadNews() {
      // 1. 获取新闻动态 (假设后端靠 noticeType = 2 区分)
      portalNewsList({ pageNum: 1, pageSize: 4, noticeType: '2' }).then(res => {
        this.newsList = res.rows || []
      }).catch(() => {})

      // 2. 获取通知公告 (假设后端靠 noticeType = 1 区分)
      portalNewsList({ pageNum: 1, pageSize: 5, noticeType: '1' }).then(res => {
        this.noticeList = res.rows || []
      }).catch(() => {})
    },
    goDetail(id) {
      this.$router.push('/portal/news/' + id)
    },
    goWithAuth(path) {
      if (!getToken()) {
        this.$message.warning('请先登录系统以访问业务模块')
        this.$router.push('/login?redirect=' + encodeURIComponent(path))
        return
      }
      this.$router.push(path)
    }
  }
}
</script>

<style lang="scss" scoped>
/* 定义全局主题色变量 */
$primary-blue: #2563eb;
$navy-dark: #0f172a;
$slate-gray: #64748b;
$bg-color: #f8fafc;
$card-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
$hover-shadow: 0 12px 24px rgba(37, 99, 235, 0.08);

.portal-home-container {
  background-color: $bg-color;
  min-height: calc(100vh - 64px);
  padding-bottom: 40px;
}

/* 1. Hero Banner */
.banner-section {
  width: 100%;
  background: #fff;
  border-bottom: 1px solid #e2e8f0;
}
.elegant-carousel ::v-deep .el-carousel__indicators--outside {
  background-color: $bg-color;
  padding-top: 16px;
}
.elegant-carousel ::v-deep .el-carousel__indicator.is-active button {
  background-color: $primary-blue;
  width: 24px;
  border-radius: 4px;
}
.banner-item {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  /* 预留真实图片背景的位置 */
  background-size: cover !important;
  background-position: center !important;
}
.banner-content {
  max-width: 1200px;
  width: 100%;
  padding: 0 40px;
  color: #ffffff;
  text-align: center;
}
.banner-content ::v-deep .el-button,
.banner-content .el-button {
  display: none !important;
}
.banner-tag {
  background: rgba(255,255,255,0.2);
  border: none;
  color: #fff;
  margin-bottom: 20px;
  font-size: 14px;
  padding: 0 16px;
}
.banner-title {
  font-size: 42px;
  font-weight: 600;
  margin: 0 0 16px 0;
  letter-spacing: 2px;
  text-shadow: 0 4px 12px rgba(0,0,0,0.1);
}
.banner-subtitle {
  font-size: 16px;
  color: rgba(255,255,255,0.9);
  max-width: 700px;
  margin: 0 auto 32px auto;
  line-height: 1.8;
}
/* 主体内容包裹区 */
.main-wrapper {
  max-width: 1240px;
  margin: 0 auto;
  padding: 32px 24px 0;
}

/* 通用面板样式 */
.elegant-panel {
  background: #ffffff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: $card-shadow;
  border: 1px solid #f1f5f9;
  height: 100%;
  transition: box-shadow 0.3s ease;
  &:hover {
    box-shadow: 0 4px 20px rgba(0,0,0,0.06);
  }
}
.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  border-bottom: 1px solid #f1f5f9;
  padding-bottom: 12px;
}
.title-with-deco {
  display: flex;
  align-items: center;
}
.deco-line {
  width: 4px;
  height: 18px;
  background-color: $primary-blue;
  border-radius: 2px;
  margin-right: 12px;
}
.panel-title {
  font-size: 20px;
  font-weight: 600;
  color: $navy-dark;
  margin: 0;
}
.more-btn {
  color: $slate-gray;
  font-size: 14px;
  &:hover { color: $primary-blue; }
}

/* 2. 新闻动态列表 */
.news-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}
.news-card {
  display: flex;
  gap: 16px;
  padding: 12px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border: 1px solid transparent;

  &:hover {
    background: #ffffff;
    border-color: #e2e8f0;
    box-shadow: $hover-shadow;
    transform: translateY(-2px);

    .news-title { color: $primary-blue; }
  }
}
.news-cover {
  width: 120px;
  height: 80px;
  flex-shrink: 0;
  border-radius: 6px;
  overflow: hidden;
  .el-image { width: 100%; height: 100%; }
}
.image-slot {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
  background: #f1f5f9;
  color: #cbd5e1;
  font-size: 24px;
}
.news-info {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  justify-content: center;
}
.news-title {
  margin: 0 0 8px;
  font-size: 16px;
  color: $navy-dark;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  transition: color 0.3s;
}
.news-summary {
  margin: 0 0 8px;
  font-size: 13px;
  color: $slate-gray;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.news-meta {
  font-size: 12px;
  color: #94a3b8;
  display: flex;
  gap: 16px;
}

/* 3. 通知公告列表 */
.notice-list {
  padding-left: 8px;
}
.notice-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px 0;
  border-bottom: 1px dashed #e2e8f0;
  cursor: pointer;
  transition: all 0.2s;

  &:last-child { border-bottom: none; }
  &:hover {
    .notice-icon-wrap { background: $primary-blue; color: #fff; transform: scale(1.1); }
    .notice-title { color: $primary-blue; }
  }
}
.notice-icon-wrap {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: #f1f5f9;
  color: #94a3b8;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s;
}
.notice-content {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 6px;
}
.notice-title {
  margin: 0;
  font-size: 15px;
  color: #334155;
  font-weight: 500;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  transition: color 0.2s;
}
.notice-date {
  font-size: 12px;
  color: #94a3b8;
}

/* 4. 快捷入口区 */
.quick-links-section {
  margin-top: 32px;
}
.quick-grid {
  display: flex;
  flex-wrap: wrap;
}
.quick-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 24px;
  background: #ffffff;
  border-radius: 12px;
  box-shadow: $card-shadow;
  border: 1px solid #f1f5f9;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  margin-bottom: 24px;

  &:hover {
    transform: translateY(-4px);
    box-shadow: $hover-shadow;
    border-color: #e2e8f0;
  }

  /* 不同角色入口的图标主题色 */
  &.blue-card .card-icon { color: $primary-blue; background: #eff6ff; }
  &.green-card .card-icon { color: #10b981; background: #ecfdf5; }
  &.orange-card .card-icon { color: #f59e0b; background: #fffbeb; }
  &.red-card .card-icon { color: #ef4444; background: #fef2f2; }
  &.purple-card .card-icon { color: #8b5cf6; background: #f5f3ff; }
}
.card-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  flex-shrink: 0;
  transition: transform 0.3s;
}
.quick-card:hover .card-icon {
  transform: scale(1.05);
}
.card-text {
  flex: 1;
  min-width: 0;
  h4 {
    margin: 0 0 6px 0;
    font-size: 17px;
    color: $navy-dark;
  }
  p {
    margin: 0;
    font-size: 13px;
    color: $slate-gray;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }
}

/* 响应式调整 */
@media (max-width: 992px) {
  .content-row { display: flex; flex-direction: column; gap: 24px; }
  .banner-title { font-size: 32px; }
  .banner-section { height: 360px; }
  .elegant-carousel { height: 360px; }
}
</style>
