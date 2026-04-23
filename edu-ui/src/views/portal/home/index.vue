<template>
  <div class="portal-home">
    <section class="hero-light">
      <div class="hero-content">
        <el-tag size="small" effect="plain" class="hero-tag">高校教学成果管理平台</el-tag>
        <h1 class="hero-title">一站式完成成果申报、浏览与审核</h1>
        <p class="hero-desc">面向全校教师与专家的统一协作门户。化繁为简，让教学成果申报与流转更加高效、透明。</p>
        <div class="hero-actions">
          <el-button type="primary" size="medium" round class="action-btn" @click="goWithAuth(primaryAction.path)">
            {{ primaryAction.label }}
          </el-button>
          <el-button size="medium" round class="action-btn-plain" @click="$router.push('/portal/news')">
            浏览校园新闻
          </el-button>
        </div>
      </div>
      <div class="hero-illustration">
        <div class="glass-card">
          <div class="glass-item">
            <span class="glass-label">已发布新闻</span>
            <span class="glass-value">{{ newsList.length }}</span>
          </div>
          <div class="glass-divider"></div>
          <div class="glass-item">
            <span class="glass-label">您的工作台</span>
            <span class="glass-value text-blue">{{ primaryAction.label }}</span>
          </div>
        </div>
      </div>
    </section>

    <el-row :gutter="24" class="main-content">
      <el-col :lg="16" :md="24">
        <div class="elegant-panel">
          <div class="panel-header">
            <span class="panel-title">最新动态</span>
            <el-button type="text" @click="$router.push('/portal/news')">查看更多 <i class="el-icon-arrow-right"></i></el-button>
          </div>
          <div class="news-wrapper">
            <div v-if="newsList.length === 0" class="empty-state">暂无动态</div>
            <div v-for="item in newsList" :key="item.newsId" class="news-row" @click="goDetail(item.newsId)">
              <div class="news-text">
                <div class="news-title-link">{{ item.title }}</div>
                <div class="news-meta">
                  <i class="el-icon-time"></i> {{ parseTime(item.publishTime, '{y}-{m}-{d}') }}
                  <span class="divider">·</span>
                  <i class="el-icon-view"></i> {{ item.viewCount || 0 }} 次阅读
                </div>
              </div>
            </div>
          </div>
        </div>
      </el-col>

      <el-col :lg="8" :md="24">
        <div class="elegant-panel">
          <div class="panel-header">
            <span class="panel-title">快捷办理</span>
          </div>
          <div class="action-grid">
            <div class="action-card" @click="goWithAuth('/portal/declare')">
              <i class="el-icon-document-add action-icon text-blue"></i>
              <span>教师申报</span>
            </div>
            <div class="action-card" @click="goWithAuth('/portal/mine')">
              <i class="el-icon-folder-opened action-icon text-green"></i>
              <span>我的申报</span>
            </div>
            <div class="action-card" @click="goWithAuth('/portal/audit/college')">
              <i class="el-icon-s-check action-icon text-orange"></i>
              <span>院级审核</span>
            </div>
            <div class="action-card" @click="goWithAuth('/portal/audit/school')">
              <i class="el-icon-office-building action-icon text-red"></i>
              <span>校级审核</span>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import { portalNewsList } from '@/api/achievement/portal'
import { getToken } from '@/utils/auth'
import { getPortalLandingPath } from '@/utils/role-route'

export default {
  name: 'PortalHome',
  data() {
    return { newsList: [] }
  },
  computed: {
    ...mapGetters(['roles']),
    primaryAction() {
      const path = getPortalLandingPath(this.roles)
      const labelMap = {
        '/portal/audit/college': '进入院级审核',
        '/portal/audit/school': '进入校级审核',
        '/portal/audit/records': '查看审核记录',
        '/portal/declare': '开始成果申报',
        '/portal/mine': '查看我的申报'
      }
      return { path, label: labelMap[path] || '进入工作台' }
    }
  },
  created() {
    this.loadNews()
  },
  methods: {
    loadNews() {
      portalNewsList({ pageNum: 1, pageSize: 5 }).then(res => {
        this.newsList = res.rows || []
      }).catch(() => {})
    },
    goDetail(id) {
      this.$router.push('/portal/news/' + id)
    },
    goWithAuth(path) {
      // 优化：不仅仅检查本地存储，若跳转后触发401，拦截器会处理
      if (!getToken()) {
        this.$message.warning('请先登录系统')
        this.$router.push('/login?redirect=' + encodeURIComponent(path))
        return
      }
      this.$router.push(path)
    }
  }
}
</script>

<style scoped>
.portal-home { padding: 24px; max-width: 1400px; margin: 0 auto; }
/* 浅色横幅 */
.hero-light {
  display: flex; justify-content: space-between; align-items: center;
  background: linear-gradient(120deg, #f0f4fa 0%, #ffffff 100%);
  border-radius: 16px; padding: 48px 56px; margin-bottom: 24px;
  box-shadow: 0 4px 24px rgba(37, 99, 235, 0.04);
  border: 1px solid #e5eaf2;
}
.hero-content { max-width: 600px; }
.hero-tag { margin-bottom: 16px; border-radius: 4px; color: #2563eb; border-color: #bfdbfe; background: #eff6ff;}
.hero-title { font-size: 36px; font-weight: 600; color: #1e293b; margin: 0 0 16px 0; letter-spacing: 0.5px; }
.hero-desc { font-size: 15px; color: #64748b; line-height: 1.8; margin-bottom: 32px; }
.hero-actions { display: flex; gap: 16px; }
.action-btn { padding: 12px 28px; font-weight: 500; background: #2563eb; border-color: #2563eb; }
.action-btn:hover { background: #1d4ed8; box-shadow: 0 4px 12px rgba(37,99,235,0.2); transform: translateY(-1px); transition: all 0.3s;}
.action-btn-plain { padding: 12px 28px; font-weight: 500; color: #475569; border-color: #cbd5e1; }
/* 毛玻璃数据卡片 */
.glass-card {
  display: flex; align-items: center; gap: 32px;
  background: rgba(255, 255, 255, 0.7); backdrop-filter: blur(10px);
  padding: 24px 40px; border-radius: 16px; border: 1px solid #ffffff;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.03);
}
.glass-item { display: flex; flex-direction: column; gap: 8px; }
.glass-label { font-size: 13px; color: #64748b; }
.glass-value { font-size: 28px; font-weight: 600; color: #0f172a; }
.glass-divider { width: 1px; height: 40px; background: #e2e8f0; }
.text-blue { color: #2563eb !important; }
/* 通用面板 */
.elegant-panel {
  background: #ffffff; border-radius: 12px; padding: 24px; height: 100%;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.02); border: 1px solid #f1f5f9;
}
.panel-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; border-bottom: 1px solid #f8fafc; padding-bottom: 12px;}
.panel-title { font-size: 18px; font-weight: 600; color: #1e293b; position: relative; padding-left: 12px;}
.panel-title::before { content: ''; position: absolute; left: 0; top: 10%; height: 80%; width: 4px; background: #2563eb; border-radius: 2px;}
/* 新闻列表 */
.news-row { padding: 16px 8px; border-bottom: 1px solid #f1f5f9; transition: background 0.2s; border-radius: 6px; cursor: pointer;}
.news-row:hover { background: #f8fafc; }
.news-row:last-child { border-bottom: none; }
.news-title-link { font-size: 15px; color: #334155; margin-bottom: 8px; font-weight: 500; transition: color 0.2s;}
.news-row:hover .news-title-link { color: #2563eb; }
.news-meta { font-size: 12px; color: #94a3b8; display: flex; align-items: center; }
.divider { margin: 0 8px; color: #cbd5e1; }
/* 快捷入口网格 */
.action-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 16px; }
.action-card {
  display: flex; flex-direction: column; align-items: center; justify-content: center;
  padding: 24px 12px; background: #f8fafc; border-radius: 10px; cursor: pointer;
  transition: all 0.3s ease; border: 1px solid transparent;
}
.action-card:hover { background: #ffffff; border-color: #e2e8f0; box-shadow: 0 8px 20px rgba(0,0,0,0.04); transform: translateY(-2px);}
.action-icon { font-size: 28px; margin-bottom: 12px; }
.text-green { color: #10b981; } .text-orange { color: #f59e0b; } .text-red { color: #ef4444; }
</style>
