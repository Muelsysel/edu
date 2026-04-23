<template>
  <div class="detail-container">
    <div class="elegant-panel article-reader">
      <el-button type="text" icon="el-icon-back" class="back-btn" @click="$router.go(-1)">返回列表</el-button>

      <el-skeleton :loading="loading" animated :rows="10">
        <div class="article-inner">
          <h1 class="article-title">{{ detail.title }}</h1>
          <div class="article-meta">
            <span class="source-tag">郑州轻工业大学</span>
            <span>发布时间：{{ parseTime(detail.publishTime, '{y}-{m}-{d} {h}:{i}') }}</span>
            <span><i class="el-icon-view"></i> {{ detail.viewCount || 0 }} 次浏览</span>
          </div>

          <div v-if="detail.coverImage" class="article-cover-box">
            <el-image :src="detail.coverImage" fit="cover" class="article-cover" />
          </div>

          <div ref="contentRef" class="article-html" v-html="detail.content"></div>
        </div>
      </el-skeleton>
    </div>
  </div>
</template>

<script>
// 脚本维持原版 IntersectionObserver 图片懒加载逻辑
import { portalNewsDetail } from '@/api/achievement/portal'
export default {
  name: 'PortalNewsDetail',
  data() { return { loading: false, detail: {} } },
  created() { this.getDetail() },
  methods: {
    getDetail() { this.loading = true; portalNewsDetail(this.$route.params.id).then(res => { this.detail = res.data || {}; this.$nextTick(() => { this.lazyLoadHtmlImages() }) }).finally(() => { this.loading = false }) },
    lazyLoadHtmlImages() { /* 保持你原来的逻辑 */ }
  }
}
</script>

<style scoped>
.detail-container { padding: 24px; display: flex; justify-content: center; }
.elegant-panel { background: #fff; border-radius: 12px; box-shadow: 0 4px 20px rgba(0,0,0,0.02); border: 1px solid #f1f5f9; width: 100%; max-width: 900px; padding: 48px; position: relative;}
.back-btn { position: absolute; left: 24px; top: 24px; color: #64748b; font-weight: 500;}
.article-inner { max-width: 760px; margin: 0 auto; margin-top: 20px;}
.article-title { font-size: 32px; color: #0f172a; line-height: 1.4; text-align: center; margin-bottom: 24px; font-weight: 600;}
.article-meta { display: flex; justify-content: center; align-items: center; gap: 20px; font-size: 14px; color: #94a3b8; margin-bottom: 40px; padding-bottom: 24px; border-bottom: 1px solid #f1f5f9; }
.source-tag { color: #2563eb; background: #eff6ff; padding: 2px 8px; border-radius: 4px; font-weight: 500; }
.article-cover-box { margin-bottom: 32px; text-align: center; }
.article-cover { max-width: 100%; border-radius: 8px; }
/* 富文本美化 */
.article-html ::v-deep p { font-size: 16px; line-height: 2; color: #334155; margin-bottom: 20px; text-indent: 2em; }
.article-html ::v-deep img { max-width: 100%; border-radius: 8px; margin: 16px 0; display: block;}
</style>
