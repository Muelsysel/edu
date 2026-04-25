<template>
  <div class="dashboard-container">
    <!-- 欢迎卡片 -->
    <div class="welcome-card">
      <div class="welcome-accent"></div>
      <div class="welcome-body">
        <div class="welcome-left">
          <img :src="avatar" class="user-avatar" alt="avatar" />
          <div class="welcome-info">
            <h2 class="greeting">{{ greeting }}，{{ nickName }}</h2>
            <p class="welcome-sub">欢迎使用高校教学成果管理平台</p>
          </div>
        </div>
        <div class="welcome-right">
          <div class="stat-item">
            <div class="stat-title">当前角色</div>
            <div class="stat-val role-tag">{{ roleName }}</div>
          </div>
          <div class="stat-divider"></div>
          <div class="stat-item">
            <div class="stat-title">总成果数</div>
            <div class="stat-val">{{ stats.total || 0 }}</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 统计卡片 -->
    <panel-group :panel-list="currentPanelData" />

    <!-- ECharts 图表区 -->
    <el-row :gutter="20" class="chart-wrapper">
      <el-col :xs="24" :sm="24" :lg="12">
        <div class="chart-card">
          <div class="chart-card-accent"></div>
          <div class="chart-header">
            <span><i class="el-icon-s-data"></i> 成果类型分布</span>
          </div>
          <div ref="pieChart" class="chart-container"></div>
        </div>
      </el-col>
      <el-col :xs="24" :sm="24" :lg="12">
        <div class="chart-card">
          <div class="chart-card-accent accent-emerald"></div>
          <div class="chart-header">
            <span><i class="el-icon-data-analysis"></i> 审核状态分布</span>
          </div>
          <div ref="barChart" class="chart-container"></div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="chart-wrapper" style="margin-top: 20px;">
      <el-col :xs="24" :sm="24" :lg="12">
        <div class="chart-card">
          <div class="chart-card-accent accent-amber"></div>
          <div class="chart-header">
            <span><i class="el-icon-pie-chart"></i> 审核通过率</span>
          </div>
          <div ref="rateChart" class="chart-container"></div>
        </div>
      </el-col>
      <el-col :xs="24" :sm="24" :lg="12" v-if="!isTeacher">
        <div class="chart-card">
          <div class="chart-card-accent accent-indigo"></div>
          <div class="chart-header">
            <span><i class="el-icon-office-building"></i> 各学院成果数量</span>
          </div>
          <div ref="collegeChart" class="chart-container"></div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { mapGetters } from "vuex";
import PanelGroup from "./dashboard/PanelGroup";
import { getStatistics } from "@/api/achievement/audit";
import { listDept } from "@/api/system/dept";
import { getDicts } from "@/api/system/dict/data";
import * as echarts from "echarts";

export default {
  name: "Index",
  components: { PanelGroup },
  data() {
    return {
      stats: {},
      deptMap: {},
      dictMap: {},
      charts: []
    };
  },
  computed: {
    ...mapGetters(["avatar", "nickName", "roles", "id"]),
    greeting() {
      const hour = new Date().getHours();
      if (hour < 9) return "早上好";
      if (hour < 12) return "上午好";
      if (hour < 14) return "中午好";
      if (hour < 18) return "下午好";
      return "晚上好";
    },
    isTeacher() {
      return this.roles.includes("teacher") || (!this.isAdmin && !this.isAuditor);
    },
    isAdmin() {
      return this.roles.includes("admin") || this.roles.includes("admin1");
    },
    isAuditor() {
      return this.roles.includes("SchoolAudit") || this.roles.includes("auditor");
    },
    roleName() {
      if (this.isAdmin) return "系统管理员";
      if (this.isAuditor) return "成果审核专家";
      return "高校教师";
    },
    currentPanelData() {
      const s = this.stats.statusData || {};
      if (this.isAdmin) {
        return [
          { title: "全校总成果", icon: "education", count: this.stats.total || 0, color: "#1e40af" },
          { title: "已通过", icon: "validCode", count: s.passed || 0, color: "#10b981" },
          { title: "审核中", icon: "time-range", count: (s.schoolAudit || 0), color: "#f59e0b" },
          { title: "已驳回", icon: "message", count: s.rejected || 0, color: "#ef4444" }
        ];
      } else if (this.isAuditor) {
        return [
          { title: "待我审核", icon: "peoples", count: (s.schoolAudit || 0), color: "#ef4444" },
          { title: "本级已通过", icon: "validCode", count: s.passed || 0, color: "#10b981" },
          { title: "成果总数", icon: "form", count: this.stats.total || 0, color: "#6366f1" },
          { title: "已驳回", icon: "message", count: s.rejected || 0, color: "#f59e0b" }
        ];
      } else {
        return [
          { title: "我的申报", icon: "form", count: this.stats.total || 0, color: "#1e40af" },
          { title: "审核中", icon: "time-range", count: (s.schoolAudit || 0), color: "#f59e0b" },
          { title: "已通过", icon: "validCode", count: s.passed || 0, color: "#10b981" },
          { title: "已被驳回", icon: "message", count: s.rejected || 0, color: "#ef4444" }
        ];
      }
    }
  },
  created() {
    this.loadData();
  },
  mounted() {
    window.addEventListener("resize", this.handleResize);
  },
  beforeDestroy() {
    window.removeEventListener("resize", this.handleResize);
    this.charts.forEach(c => c.dispose());
  },
  methods: {
    handleResize() {
      this.charts.forEach(c => c.resize());
    },
    async loadData() {
      try {
        const dictRes = await getDicts("edu_achievement_category");
        (dictRes.data || []).forEach(d => { this.dictMap[d.dictValue] = d.dictLabel; });
      } catch (e) { /* ignore */ }

      try {
        const deptRes = await listDept();
        (deptRes.data || []).forEach(d => { this.deptMap[d.deptId] = d.deptName; });
      } catch (e) { /* ignore */ }

      try {
        const params = {};
        if (this.isTeacher) {
          params.teacherId = this.id;
        }
        const res = await getStatistics(params);
        this.stats = res;
        this.$nextTick(() => { this.initCharts(); });
      } catch (e) { /* ignore */ }
    },
    initCharts() {
      this.initPieChart();
      this.initBarChart();
      this.initRateChart();
      if (!this.isTeacher) {
        this.initCollegeChart();
      }
    },
    initPieChart() {
      const chart = echarts.init(this.$refs.pieChart);
      this.charts.push(chart);
      const cat = this.stats.categoryData || {};
      // 学术配色
      const pieColors = ['#1e40af', '#10b981', '#d4a853', '#ef4444', '#6366f1', '#f59e0b'];
      const data = Object.keys(cat).map(k => ({
        value: cat[k],
        name: this.dictMap[k] || ('类型' + k)
      }));
      chart.setOption({
        tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
        legend: { bottom: '5%', left: 'center', textStyle: { color: '#64748b', fontSize: 12 } },
        color: pieColors,
        series: [{
          type: 'pie', radius: ['42%', '72%'], avoidLabelOverlap: false,
          itemStyle: { borderRadius: 8, borderColor: '#fff', borderWidth: 2 },
          label: { show: false, position: 'center' },
          emphasis: { label: { show: true, fontSize: 18, fontWeight: 'bold', color: '#0f172a' } },
          data: data
        }]
      });
    },
    initBarChart() {
      const chart = echarts.init(this.$refs.barChart);
      this.charts.push(chart);
      const s = this.stats.statusData || {};

      let chartCategories = ['草稿', '审核中', '已通过', '已驳回'];
      let chartData = [s.draft || 0, (s.schoolAudit || 0), s.passed || 0, s.rejected || 0];
      let chartColors = ['#94a3b8', '#6366f1', '#10b981', '#ef4444'];

      if (!this.isTeacher) {
        chartCategories = chartCategories.slice(1);
        chartData = chartData.slice(1);
        chartColors = chartColors.slice(1);
      }

      chart.setOption({
        tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
        grid: { left: '3%', right: '6%', bottom: '3%', containLabel: true },
        xAxis: { type: 'category', data: chartCategories,
          axisLine: { lineStyle: { color: '#e2e8f0' } },
          axisLabel: { color: '#64748b', fontSize: 12 } },
        yAxis: { type: 'value', axisLine: { show: false },
          splitLine: { lineStyle: { color: '#f1f5f9' } } },
        series: [{
          type: 'bar', barWidth: '42%',
          itemStyle: {
            borderRadius: [6, 6, 0, 0],
            color: function(params) {
              return chartColors[params.dataIndex];
            }
          },
          data: chartData
        }]
      });
    },
    initRateChart() {
      const chart = echarts.init(this.$refs.rateChart);
      this.charts.push(chart);
      const s = this.stats.statusData || {};
      const total = (this.stats.total || 0) - (s.draft || 0);
      const passed = s.passed || 0;
      const rate = total > 0 ? Math.round((passed / total) * 100) : 0;
      chart.setOption({
        series: [{
          type: 'gauge', startAngle: 200, endAngle: -20,
          min: 0, max: 100,
          pointer: { show: true, length: '60%', width: 6, itemStyle: { color: '#1e40af' } },
          progress: { show: true, width: 18, roundCap: true,
            itemStyle: { color: { type: 'linear', x: 0, y: 0, x2: 1, y2: 0,
              colorStops: [{ offset: 0, color: '#1e40af' }, { offset: 1, color: '#10b981' }] } } },
          axisLine: { lineStyle: { width: 18, color: [[1, '#f1f5f9']] } },
          axisTick: { show: false }, splitLine: { show: false },
          axisLabel: { distance: 25, color: '#94a3b8', fontSize: 12 },
          detail: { valueAnimation: true, fontSize: 28, fontWeight: 'bold', color: '#0f172a',
            fontFamily: 'Noto Serif SC',
            formatter: '{value}%', offsetCenter: [0, '70%'] },
          title: { offsetCenter: [0, '90%'], fontSize: 14, color: '#94a3b8' },
          data: [{ value: rate, name: '通过率' }]
        }]
      });
    },
    initCollegeChart() {
      const el = this.$refs.collegeChart;
      if (!el) return;
      const chart = echarts.init(el);
      this.charts.push(chart);
      const col = this.stats.collegeData || {};
      const names = []; const values = [];
      Object.keys(col).forEach(k => {
        names.push(this.deptMap[k] || '学院' + k);
        values.push(col[k]);
      });
      chart.setOption({
        tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
        grid: { left: '3%', right: '6%', bottom: '3%', containLabel: true },
        xAxis: { type: 'value', splitLine: { lineStyle: { color: '#f1f5f9' } } },
        yAxis: { type: 'category', data: names, axisLine: { lineStyle: { color: '#e2e8f0' } },
          axisLabel: { color: '#64748b' } },
        series: [{
          type: 'bar', barWidth: '60%',
          itemStyle: { borderRadius: [0, 6, 6, 0],
            color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
              { offset: 0, color: '#1e40af' }, { offset: 1, color: '#6366f1' }
            ])
          },
          data: values
        }]
      });
    }
  }
};
</script>

<style lang="scss" scoped>
.dashboard-container {
  padding: 24px;
  background-color: #f8fafc;
  min-height: calc(100vh - 84px);

  .welcome-card {
    background: #ffffff;
    border-radius: 14px;
    border: 1px solid #e2e8f0;
    box-shadow: 0 1px 3px rgba(15, 23, 42, 0.04);
    margin-bottom: 20px;
    overflow: hidden;

    .welcome-accent {
      height: 3px;
      background: linear-gradient(90deg, #0f172a 0%, #1e40af 40%, #d4a853 100%);
    }

    .welcome-body {
      padding: 24px 32px;
      display: flex;
      justify-content: space-between;
      align-items: center;
    }

    .welcome-left {
      display: flex;
      align-items: center;
      .user-avatar {
        width: 56px; height: 56px; border-radius: 50%;
        margin-right: 18px; border: 2px solid #e2e8f0;
        box-shadow: 0 2px 8px rgba(15, 23, 42, 0.08);
      }
      .greeting {
        margin: 0 0 4px;
        font-size: 20px;
        font-weight: 700;
        color: #0f172a;
        font-family: 'Noto Serif SC', serif;
      }
      .welcome-sub { margin: 0; font-size: 13px; color: #94a3b8; }
    }

    .welcome-right {
      display: flex;
      align-items: center;
      gap: 24px;

      .stat-divider {
        width: 1px;
        height: 36px;
        background: #e2e8f0;
      }

      .stat-item { text-align: right;
        .stat-title { font-size: 12px; color: #94a3b8; margin-bottom: 6px; text-transform: uppercase; letter-spacing: 0.5px; }
        .stat-val { font-size: 20px; font-weight: 700; color: #0f172a; font-family: 'Noto Serif SC', serif; }
        .role-tag {
          color: #1e40af;
          background: rgba(30, 64, 175, 0.08);
          padding: 3px 12px;
          border-radius: 6px;
          font-size: 13px;
          font-weight: 600;
        }
      }
    }
  }

  .chart-wrapper {
    margin-top: 20px;

    .chart-card {
      background: #ffffff;
      border-radius: 14px;
      border: 1px solid #e2e8f0;
      box-shadow: 0 1px 3px rgba(15, 23, 42, 0.04);
      overflow: hidden;
      transition: box-shadow 0.25s ease;

      &:hover {
        box-shadow: 0 4px 12px rgba(15, 23, 42, 0.06);
      }

      .chart-card-accent {
        height: 3px;
        background: linear-gradient(90deg, #1e40af, #6366f1);
      }
      .chart-card-accent.accent-emerald {
        background: linear-gradient(90deg, #10b981, #34d399);
      }
      .chart-card-accent.accent-amber {
        background: linear-gradient(90deg, #d4a853, #f59e0b);
      }
      .chart-card-accent.accent-indigo {
        background: linear-gradient(90deg, #6366f1, #818cf8);
      }

      .chart-header {
        padding: 16px 20px 0;
        font-weight: 600;
        font-size: 15px;
        color: #0f172a;
        display: flex;
        align-items: center;
        gap: 6px;
        font-family: 'Noto Serif SC', serif;

        i {
          color: #1e40af;
          font-size: 16px;
        }
      }
      .chart-container { height: 320px; padding: 8px 12px; }
    }
  }
}

@media (max-width: 768px) {
  .welcome-card .welcome-body {
    flex-direction: column !important;
    align-items: flex-start !important;
  }
  .welcome-right { margin-top: 16px; justify-content: flex-start; }
}
</style>
