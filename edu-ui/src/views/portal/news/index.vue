<template>
  <div class="portal-news-page">
    <section class="portal-subhero">
      <div>
        <span class="eyebrow">{{ currentType === '1' ? 'Notice' : 'News' }}</span>
        <h1>{{ pageTitle }}</h1>
        <p>{{ currentType === '1' ? '校内通知与教学管理安排' : '教学成果建设、教改动态与校园要闻' }}</p>
      </div>
      <el-input
        v-model="queryParams.keyword"
        placeholder="请输入关键词"
        clearable
        prefix-icon="el-icon-search"
        class="hero-search"
        @keyup.enter.native="handleQuery"
        @clear="handleQuery"
      >
        <el-button slot="append" icon="el-icon-search" @click="handleQuery" />
      </el-input>
    </section>

    <section class="news-board" v-loading="loading">
      <div class="board-title">
        <h2>{{ pageTitle }}</h2>
        <span>共 {{ total || 0 }} 条</span>
      </div>

      <el-empty v-if="!list.length" :description="emptyText" class="empty-box" />

      <article
        v-for="item in list"
        :key="item.newsId"
        class="news-row"
        @click="goDetail(item.newsId)"
      >
        <div class="date-card">
          <strong>{{ parseTime(item.publishTime, '{d}') || '--' }}</strong>
          <span>{{ parseTime(item.publishTime, '{y}-{m}') || '---- --' }}</span>
        </div>

        <div class="cover-wrap">
          <el-image :src="item.coverImage" fit="cover" lazy>
            <div slot="error" class="image-placeholder">
              <i class="el-icon-picture-outline" />
            </div>
          </el-image>
        </div>

        <div class="news-main">
          <h3 :title="item.title">{{ item.title }}</h3>
          <p>{{ item.summary || '点击查看详情。' }}</p>
          <div class="row-meta">
            <span><i class="el-icon-time" /> {{ parseTime(item.publishTime, '{y}-{m}-{d}') }}</span>
            <span><i class="el-icon-view" /> {{ item.viewCount || 0 }}</span>
          </div>
        </div>

        <i class="el-icon-arrow-right row-arrow" />
      </article>

      <pagination
        v-show="total > 0"
        :total="total"
        :page.sync="queryParams.pageNum"
        :limit.sync="queryParams.pageSize"
        @pagination="getList"
      />
    </section>
  </div>
</template>

<script>
import { portalNewsList } from '@/api/achievement/portal'

export default {
  name: 'PortalNewsList',
  data() {
    const type = this.$route.query.type || '2'
    return {
      loading: false,
      total: 0,
      list: [],
      currentType: type,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        keyword: undefined,
        noticeType: type
      }
    }
  },
  computed: {
    pageTitle() {
      return this.currentType === '1' ? '通知公告' : '新闻动态'
    },
    emptyText() {
      return this.currentType === '1' ? '暂无通知公告' : '暂无新闻动态'
    }
  },
  created() {
    this.getList()
  },
  watch: {
    '$route.query.type'(val) {
      this.currentType = val || '2'
      this.queryParams.noticeType = this.currentType
      this.queryParams.pageNum = 1
      this.getList()
    }
  },
  methods: {
    getList() {
      this.loading = true
      portalNewsList(this.queryParams).then(res => {
        this.list = res.rows || []
        this.total = res.total || 0
      }).finally(() => {
        this.loading = false
      })
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    goDetail(id) {
      this.$router.push('/portal/news/' + id)
    }
  }
}
</script>

<style lang="scss" scoped>
.portal-news-page {
  max-width: 1440px;
  margin: 0 auto;
  padding: 28px 20px 56px;
}

.portal-subhero {
  min-height: 190px;
  padding: 34px 42px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 32px;
  background:
    linear-gradient(90deg, rgba(249, 253, 255, 0.95), rgba(231, 245, 255, 0.88)),
    url('~@/assets/images/portal-hero-ink.png') center 58% / cover;
  color: #173b63;
  border: 1px solid #d7e6f4;
  border-bottom: 4px solid #d6a23a;
}

.eyebrow {
  display: inline-block;
  margin-bottom: 12px;
  font-size: 13px;
  text-transform: uppercase;
  letter-spacing: 1px;
  color: #0b5c95;
}

.portal-subhero h1 {
  margin: 0;
  font-size: 32px;
  font-weight: 700;
}

.portal-subhero p {
  margin: 12px 0 0;
  color: #4f6f8b;
  font-size: 15px;
}

.hero-search {
  width: 320px;
  flex: 0 0 320px;
}

.hero-search ::v-deep .el-input__inner,
.hero-search ::v-deep .el-input-group__append {
  border: none;
}

.hero-search ::v-deep .el-input-group__append {
  background: #d6a23a;
  color: #fff;
}

.news-board {
  margin-top: 22px;
  padding: 28px 34px 34px;
  background: #fff;
  border: 1px solid #dbe6f2;
  box-shadow: 0 12px 28px rgba(18, 70, 122, 0.08);
}

.board-title {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 16px;
  border-bottom: 2px solid #e7eef7;
}

.board-title h2 {
  margin: 0;
  font-size: 22px;
  color: #063b75;
  position: relative;
}

.board-title h2::after {
  content: '';
  position: absolute;
  left: 0;
  bottom: -18px;
  width: 74px;
  height: 2px;
  background: #d6a23a;
}

.board-title span {
  color: #6b7d91;
  font-size: 14px;
}

.empty-box {
  padding: 60px 0;
}

.news-row {
  display: grid;
  grid-template-columns: 78px 220px minmax(0, 1fr) 24px;
  gap: 20px;
  align-items: center;
  min-height: 138px;
  padding: 20px 0;
  border-bottom: 1px solid #edf2f7;
  cursor: pointer;
  transition: background-color 0.2s ease, transform 0.2s ease;
}

.news-row:hover {
  background: #f7fbff;
  transform: translateX(4px);
}

.date-card {
  width: 68px;
  height: 74px;
  border: 1px solid #dbe6f2;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #0b4f93;
  background: #f8fbff;
}

.date-card strong {
  font-size: 28px;
  line-height: 1;
}

.date-card span {
  margin-top: 8px;
  font-size: 12px;
  color: #6b7d91;
}

.cover-wrap {
  width: 220px;
  height: 124px;
  overflow: hidden;
  background: #eef5fb;
}

.cover-wrap .el-image,
.image-placeholder {
  width: 100%;
  height: 100%;
}

.image-placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  color: #8aa7c4;
  font-size: 28px;
}

.news-main {
  min-width: 0;
}

.news-main h3 {
  margin: 0 0 10px;
  color: #173b63;
  font-size: 19px;
  font-weight: 600;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.news-main p {
  margin: 0;
  color: #5e7187;
  line-height: 1.7;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.row-meta {
  margin-top: 12px;
  display: flex;
  gap: 22px;
  color: #8a9bad;
  font-size: 13px;
}

.row-arrow {
  color: #a2b2c2;
  font-size: 18px;
}

::v-deep .pagination-container {
  margin-top: 22px;
}

@media (max-width: 900px) {
  .portal-subhero {
    display: block;
    padding: 28px 24px;
  }

  .hero-search {
    width: 100%;
    margin-top: 22px;
    flex: none;
  }

  .news-board {
    padding: 22px 18px;
  }

  .news-row {
    grid-template-columns: 64px minmax(0, 1fr);
  }

  .cover-wrap,
  .row-arrow {
    display: none;
  }
}
</style>
