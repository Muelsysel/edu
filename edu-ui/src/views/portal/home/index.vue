<template>
  <div class="portal-home">
    <section class="hero-banner" :style="{ backgroundImage: `url(${heroImage})` }">
      <div class="hero-overlay">
        <div class="hero-copy">
          <span class="hero-kicker">教学成果管理门户</span>
          <h1>建设书香校园 培育时代新人</h1>
          <p>汇聚成果申报、审核流转、新闻发布与数据服务，打造规范、高效、透明的教学成果管理平台。</p>
        </div>
      </div>
    </section>

    <section class="content-shell">
      <div class="news-board">
        <div class="feature-news" @click="featuredNews && goDetail(featuredNews.newsId)">
          <div class="section-head">
            <h2>综合要闻</h2>
            <button type="button" @click.stop="$router.push('/portal/news?type=2')">更多</button>
          </div>
          <div class="feature-body">
            <div class="feature-cover">
              <el-image :src="featuredCover" fit="cover">
                <div slot="error" class="image-slot"><i class="el-icon-picture-outline"></i></div>
              </el-image>
            </div>
            <div class="feature-list">
              <div v-if="newsList.length === 0" class="empty-line">暂无新闻动态</div>
              <div v-for="item in newsList" :key="item.newsId" class="news-line" @click.stop="goDetail(item.newsId)">
                <span class="line-title">{{ item.title }}</span>
                <time>{{ parseTime(item.publishTime, '{m}-{d}') }}</time>
              </div>
            </div>
          </div>
        </div>

        <aside class="notice-board">
          <div class="section-head">
            <h2>通知公告</h2>
            <button type="button" @click="$router.push('/portal/news?type=1')">更多</button>
          </div>
          <div class="notice-list">
            <div v-if="noticeList.length === 0" class="empty-line">暂无通知公告</div>
            <div v-for="(item, index) in noticeList" :key="item.noticeId || item.newsId || index" class="notice-row" @click="goDetail(item.newsId || item.noticeId)">
              <div class="date-card">
                <strong>{{ parseTime(item.publishTime || item.createTime, '{d}') }}</strong>
                <span>{{ parseTime(item.publishTime || item.createTime, '{y}-{m}') }}</span>
              </div>
              <p>{{ item.title }}</p>
            </div>
          </div>
        </aside>
      </div>

      <div class="service-strip">
        <button v-if="isTeacherRole" type="button" class="service-item" @click="goWithAuth('/portal/declare')">
          <i class="el-icon-edit-outline"></i>
          <span>成果申报</span>
        </button>
        <button v-if="isTeacherRole" type="button" class="service-item" @click="goWithAuth('/portal/mine')">
          <i class="el-icon-folder-opened"></i>
          <span>我的申报</span>
        </button>
        <button v-if="isSchoolRole" type="button" class="service-item" @click="goWithAuth('/portal/audit/school')">
          <i class="el-icon-s-check"></i>
          <span>审核工作台</span>
        </button>
        <button v-if="isSchoolRole" type="button" class="service-item" @click="goWithAuth('/portal/audit/records')">
          <i class="el-icon-collection"></i>
          <span>审核档案</span>
        </button>
      </div>
    </section>
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
      heroImage: require('@/assets/images/portal-hero-ink.png'),
      fallbackCover: require('@/assets/images/banner/banner2.jpg'),
      newsList: [],
      noticeList: []
    }
  },
  computed: {
    ...mapGetters(['roles']),
    featuredNews() {
      return this.newsList[0] || null
    },
    featuredCover() {
      return (this.featuredNews && this.featuredNews.coverImage) || this.fallbackCover
    },
    isTeacherRole() {
      return !this.roles.includes('admin') && !this.roles.includes('SchoolAudit') && !this.roles.includes('auditor')
    },
    isSchoolRole() {
      return this.roles.includes('SchoolAudit') || this.roles.includes('auditor')
    }
  },
  created() {
    this.loadNews()
  },
  methods: {
    loadNews() {
      portalNewsList({ pageNum: 1, pageSize: 6, noticeType: '2' }).then(res => {
        this.newsList = res.rows || []
      }).catch(() => {})

      portalNewsList({ pageNum: 1, pageSize: 5, noticeType: '1' }).then(res => {
        this.noticeList = res.rows || []
      }).catch(() => {})
    },
    goDetail(id) {
      if (id) this.$router.push('/portal/news/' + id)
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
$brand-blue: #174b84;
$brand-deep: #0f3e73;
$ink-blue: #0f5f92;
$line: #d9e4ef;

.portal-home {
  background: #ffffff;
  min-height: calc(100vh - 144px);
}

.hero-banner {
  min-height: 580px;
  background-size: cover;
  background-position: center;
  position: relative;
  overflow: hidden;
}

.hero-overlay {
  min-height: 580px;
  display: flex;
  align-items: flex-start;
  justify-content: center;
  padding-top: 92px;
  background: linear-gradient(180deg, rgba(242, 250, 255, 0.1), rgba(242, 250, 255, 0.18) 58%, rgba(255,255,255,0.25));
}

.hero-copy {
  width: min(1280px, calc(100% - 64px));
  text-align: center;
  color: $ink-blue;
}

.hero-kicker {
  display: inline-flex;
  height: 34px;
  padding: 0 22px;
  align-items: center;
  border-radius: 999px;
  color: #0b5c95;
  background: rgba(255, 255, 255, 0.72);
  border: 1px solid #c7dcef;
  font-size: 15px;
  letter-spacing: 1px;
}

.hero-copy h1 {
  margin: 36px 0 18px;
  font-family: "STKaiti", "KaiTi", "FangSong", serif;
  font-size: 84px;
  line-height: 1.08;
  font-weight: 700;
  letter-spacing: 10px;
  color: #176898;
  text-shadow: 0 8px 28px rgba(255,255,255,0.75);
}

.hero-copy p {
  margin: 0 auto;
  max-width: 780px;
  font-size: 20px;
  line-height: 1.8;
  color: #285d82;
  text-shadow: 0 2px 12px rgba(255,255,255,0.7);
}

.content-shell {
  width: min(1400px, calc(100% - 64px));
  margin: -10px auto 0;
  padding-bottom: 48px;
}

.news-board {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 430px;
  gap: 34px;
  align-items: start;
  background: #ffffff;
  padding: 26px 0 12px;
}

.section-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1px solid $line;
  margin-bottom: 18px;
}

.section-head h2 {
  margin: 0;
  padding-bottom: 12px;
  border-bottom: 3px solid $brand-blue;
  color: #063d78;
  font-size: 24px;
  font-weight: 700;
  letter-spacing: 1px;
}

.section-head button {
  border: 0;
  background: transparent;
  color: #255f9d;
  cursor: pointer;
  font-size: 14px;
}

.feature-body {
  display: grid;
  grid-template-columns: 44% minmax(0, 1fr);
  gap: 26px;
}

.feature-cover {
  height: 260px;
  overflow: hidden;
  border-radius: 2px;
}

.feature-cover .el-image {
  width: 100%;
  height: 100%;
}

.image-slot {
  display: flex;
  width: 100%;
  height: 100%;
  align-items: center;
  justify-content: center;
  background: #edf4fa;
  color: #8aa8c5;
  font-size: 32px;
}

.feature-list {
  min-width: 0;
}

.news-line {
  display: grid;
  grid-template-columns: minmax(0, 1fr) auto auto;
  gap: 10px;
  align-items: center;
  height: 43px;
  border-bottom: 1px dotted #c8d6e4;
  cursor: pointer;
  color: #1c2f45;
}

.news-line:hover .line-title,
.notice-row:hover p {
  color: $brand-blue;
}

.line-title {
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
  font-size: 16px;
}

.news-line time {
  color: #8a98a9;
  font-size: 15px;
}

.notice-board {
  min-width: 0;
}

.notice-row {
  display: grid;
  grid-template-columns: 62px minmax(0, 1fr);
  gap: 18px;
  align-items: center;
  min-height: 76px;
  border-bottom: 1px solid #e4ebf2;
  cursor: pointer;
}

.date-card {
  width: 58px;
  height: 58px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: #edf5ff;
  color: #1a5da2;
}

.date-card strong {
  font-size: 26px;
  line-height: 1;
  font-weight: 500;
}

.date-card span {
  margin-top: 5px;
  color: #335574;
  font-size: 12px;
}

.notice-row p {
  margin: 0;
  color: #26384c;
  font-size: 16px;
  line-height: 1.45;
}

.empty-line {
  padding: 42px 0;
  color: #7f90a3;
  text-align: center;
}

.service-strip {
  display: flex;
  gap: 14px;
  flex-wrap: wrap;
  padding: 18px 0 0;
  border-top: 1px solid #e8eef5;
}

.service-item {
  height: 46px;
  padding: 0 20px;
  border: 1px solid #c9d9e8;
  background: linear-gradient(180deg, #ffffff, #f7fbff);
  color: $brand-deep;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  font-size: 15px;
}

.service-item:hover {
  border-color: #d6a23a;
  color: #16456d;
  background: #fff8e6;
}

@media (max-width: 1100px) {
  .hero-banner,
  .hero-overlay {
    min-height: 470px;
  }
  .hero-copy h1 {
    font-size: 56px;
    letter-spacing: 5px;
  }
  .news-board,
  .feature-body {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 640px) {
  .hero-banner,
  .hero-overlay {
    min-height: 390px;
  }
  .hero-overlay {
    padding-top: 54px;
  }
  .hero-copy {
    width: calc(100% - 32px);
  }
  .hero-copy h1 {
    font-size: 38px;
    letter-spacing: 2px;
  }
  .hero-copy p {
    font-size: 15px;
  }
  .content-shell {
    width: calc(100% - 28px);
  }
}
</style>
