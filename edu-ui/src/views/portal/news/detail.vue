<template>
  <div class="portal-detail-page" v-loading="loading">
    <section class="detail-shell">
      <div class="article-head">
        <el-button icon="el-icon-arrow-left" plain size="small" @click="$router.back()">返回列表</el-button>
        <span class="article-type">{{ detail.noticeType === '1' ? '通知公告' : '新闻动态' }}</span>
      </div>

      <article class="article">
        <h1>{{ detail.title }}</h1>
        <div class="article-meta">
          <span><i class="el-icon-time" /> {{ parseTime(detail.publishTime || detail.createTime, '{y}-{m}-{d} {h}:{i}') }}</span>
          <span><i class="el-icon-view" /> {{ detail.viewCount || 0 }}</span>
        </div>

        <div v-if="detail.coverImage" class="article-cover">
          <el-image :src="detail.coverImage" fit="cover" />
        </div>

        <div class="article-content" v-html="detail.content || detail.summary"></div>
      </article>
    </section>
  </div>
</template>

<script>
import { portalNewsDetail } from '@/api/achievement/portal'

export default {
  name: 'PortalNewsDetail',
  data() {
    return {
      loading: false,
      detail: {}
    }
  },
  created() {
    this.getDetail()
  },
  methods: {
    getDetail() {
      this.loading = true
      portalNewsDetail(this.$route.params.id).then(res => {
        this.detail = res.data || {}
        this.$nextTick(() => {
          this.lazyLoadHtmlImages()
        })
      }).finally(() => {
        this.loading = false
      })
    },
    lazyLoadHtmlImages() {}
  }
}
</script>

<style lang="scss" scoped>
.portal-detail-page {
  max-width: 1320px;
  margin: 0 auto;
  padding: 30px 20px 60px;
}

.detail-shell {
  background: #fff;
  border: 1px solid #dbe6f2;
  box-shadow: 0 14px 32px rgba(18, 70, 122, 0.08);
}

.article-head {
  height: 68px;
  padding: 0 34px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1px solid #e6edf5;
  background: #f7fbff;
}

.article-type {
  color: #0b4f93;
  font-weight: 600;
}

.article {
  padding: 42px 86px 58px;
}

.article h1 {
  margin: 0;
  color: #0d3564;
  font-size: 30px;
  line-height: 1.45;
  text-align: center;
}

.article-meta {
  margin: 18px auto 30px;
  padding-bottom: 22px;
  display: flex;
  justify-content: center;
  gap: 26px;
  color: #7f91a5;
  border-bottom: 1px solid #edf2f7;
  font-size: 14px;
}

.article-cover {
  margin-bottom: 34px;
  height: 360px;
  overflow: hidden;
  background: #eef5fb;
}

.article-cover .el-image {
  width: 100%;
  height: 100%;
}

.article-content {
  color: #24384d;
  font-size: 16px;
  line-height: 1.95;
}

.article-content ::v-deep img {
  max-width: 100%;
  height: auto;
}

.article-content ::v-deep p {
  margin: 0 0 16px;
}

@media (max-width: 768px) {
  .article-head {
    padding: 0 18px;
  }

  .article {
    padding: 28px 22px 42px;
  }

  .article h1 {
    font-size: 24px;
  }

  .article-cover {
    height: 220px;
  }
}
</style>
