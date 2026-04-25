<template>
  <div class="news-container">
    <div class="elegant-panel search-header">
      <h2 class="page-title">{{ pageTitle }}</h2>
      <el-input v-model="queryParams.keyword" placeholder="搜索标题或摘要..." clearable prefix-icon="el-icon-search" class="search-input" @keyup.enter.native="handleQuery">
        <el-button slot="append" icon="el-icon-search" @click="handleQuery"></el-button>
      </el-input>
    </div>

    <div class="news-grid">
      <el-empty v-if="!list.length" :description="emptyText" class="elegant-panel empty-box" />
      <div v-for="item in list" :key="item.newsId" class="elegant-panel news-card" @click="goDetail(item.newsId)">
        <div class="news-cover">
          <el-image :src="item.coverImage" fit="cover" lazy>
            <div slot="error" class="image-placeholder"><i class="el-icon-picture-outline"></i></div>
          </el-image>
        </div>
        <div class="news-body">
          <h3 class="news-title" :title="item.title">{{ item.title }}</h3>
          <p class="news-summary">{{ item.summary || '点击查看详情...' }}</p>
          <div class="news-footer">
            <span class="news-date"><i class="el-icon-date"></i> {{ parseTime(item.publishTime, '{y}-{m}-{d}') }}</span>
            <span class="news-views"><i class="el-icon-view"></i> {{ item.viewCount || 0 }}</span>
          </div>
        </div>
      </div>
    </div>

    <div class="pagination-wrapper">
      <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />
    </div>
  </div>
</template>

<script>
import { portalNewsList } from '@/api/achievement/portal'
export default {
  name: 'PortalNewsList',
  data() {
    const type = this.$route.query.type || '2'
    return {
      total: 0,
      list: [],
      currentType: type,
      queryParams: { pageNum: 1, pageSize: 12, keyword: undefined, noticeType: type }
    }
  },
  computed: {
    pageTitle() { return this.currentType === '1' ? '通知公告' : '新闻动态' },
    emptyText() { return this.currentType === '1' ? '暂无通知公告' : '暂无新闻动态' }
  },
  created() { this.getList() },
  watch: {
    '$route.query.type'(val) {
      this.currentType = val || '2'
      this.queryParams.noticeType = this.currentType
      this.queryParams.pageNum = 1
      this.getList()
    }
  },
  methods: {
    getList() { portalNewsList(this.queryParams).then(res => { this.list = res.rows || []; this.total = res.total || 0 }) },
    handleQuery() { this.queryParams.pageNum = 1; this.getList() },
    goDetail(id) { this.$router.push('/portal/news/' + id) }
  }
}
</script>

<style scoped>
.news-container { padding: 24px; max-width: 1200px; margin: 0 auto; }
.page-title { font-size: 24px; color: #1e293b; margin: 0 0 20px 0; font-weight: 600; }
.elegant-panel { background: #fff; border-radius: 12px; box-shadow: 0 4px 20px rgba(0,0,0,0.03); border: 1px solid #f1f5f9; }
.search-header { padding: 32px 24px 24px; margin-bottom: 24px; text-align: center; }
.search-input { max-width: 600px; margin: 0 auto; }
.search-input ::v-deep .el-input-group__append { background-color: #2563eb; color: white; border-color: #2563eb; border-radius: 0 6px 6px 0;}
.news-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(500px, 1fr)); gap: 24px; }
.empty-box { grid-column: 1 / -1; }
.news-card { display: flex; padding: 16px; gap: 20px; cursor: pointer; transition: transform 0.3s, box-shadow 0.3s; }
.news-card:hover { transform: translateY(-4px); box-shadow: 0 12px 30px rgba(0,0,0,0.06); }
.news-cover { width: 180px; height: 120px; flex-shrink: 0; border-radius: 8px; overflow: hidden; }
.news-cover .el-image { width: 100%; height: 100%; }
.image-placeholder { width: 100%; height: 100%; background: #f1f5f9; display: flex; justify-content: center; align-items: center; color: #cbd5e1; font-size: 24px; }
.news-body { display: flex; flex-direction: column; flex: 1; min-width: 0; }
.news-title { margin: 0 0 8px; font-size: 18px; color: #1e293b; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.news-summary { font-size: 14px; color: #64748b; line-height: 1.6; margin: 0; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; flex: 1;}
.news-footer { display: flex; justify-content: space-between; font-size: 13px; color: #94a3b8; margin-top: 12px; }
.pagination-wrapper { margin-top: 32px; display: flex; justify-content: center; }
@media (max-width: 768px) { .news-grid { grid-template-columns: 1fr; } .news-card { flex-direction: column; } .news-cover { width: 100%; height: 200px; } }
</style>
