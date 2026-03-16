<template>
  <div class="dashboard-container">
    <!-- 欢迎卡片 -->
    <div class="welcome-card">
      <div class="welcome-left">
        <img :src="avatar" class="user-avatar" alt="avatar" />
        <div class="welcome-info">
          <h2 class="greeting">{{ greeting }}，{{ nickName }}</h2>
          <p class="weather">欢迎使用高校教学成果管理平台</p>
        </div>
      </div>
      <div class="welcome-right">
        <div class="stat-item">
          <div class="stat-title">当前角色</div>
          <div class="stat-val role-tag">{{ roleName }}</div>
        </div>
        <div class="stat-item">
          <div class="stat-title">总成果数</div>
          <div class="stat-val">{{ stats.total || 0 }}</div>
        </div>
      </div>
    </div>

    <!-- 统计卡片 -->
    <panel-group :panel-list="currentPanelData" />

    <!-- ECharts 图表区 -->
    <el-row :gutter="20" class="chart-wrapper">
      <el-col :xs="24" :sm="24" :lg="12">
        <el-card shadow="hover" class="chart-card">
          <div slot="header" class="chart-header">
            <span><i class="el-icon-s-data"></i> 成果类型分布</span>
          </div>
          <div ref="pieChart" class="chart-container"></div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="24" :lg="12">
        <el-card shadow="hover" class="chart-card">
          <div slot="header" class="chart-header">
            <span><i class="el-icon-data-analysis"></i> 审核状态分布</span>
          </div>
          <div ref="barChart" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="chart-wrapper" style="margin-top: 20px;">
      <el-col :xs="24" :sm="24" :lg="12">
        <el-card shadow="hover" class="chart-card">
          <div slot="header" class="chart-header">
            <span><i class="el-icon-pie-chart"></i> 审核通过率</span>
          </div>
          <div ref="rateChart" class="chart-container"></div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="24" :lg="12" v-if="!isTeacher">
        <el-card shadow="hover" class="chart-card">
          <div slot="header" class="chart-header">
            <span><i class="el-icon-office-building"></i> 各学院成果数量</span>
          </div>
          <div ref="collegeChart" class="chart-container"></div>
        </el-card>
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
      dictMap: {}, // category code → label 映射
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
      // 没有任何审核和管理员角色的，即认为是教师
      return this.roles.includes("teacher") || (!this.isAdmin && !this.isAuditor);
    },
    isAdmin() {
      // 匹配 admin 或 admin1 等管理角色
      return this.roles.includes("admin") || this.roles.includes("admin1");
    },
    isAuditor() {
      // 匹配院级或校级审核角色
      return this.roles.includes("CollegeAudit") || this.roles.includes("SchoolAudit") || this.roles.includes("auditor");
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
          { title: "全校总成果", icon: "education", count: this.stats.total || 0, color: "#1890ff" },
          { title: "已通过", icon: "validCode", count: s.passed || 0, color: "#52c41a" },
          { title: "审核中", icon: "time-range", count: (s.collegeAudit || 0) + (s.schoolAudit || 0), color: "#faad14" },
          { title: "已驳回", icon: "message", count: s.rejected || 0, color: "#ff4d4f" }
        ];
      } else if (this.isAuditor) {
        return [
          { title: "待我审核", icon: "peoples", count: (s.collegeAudit || 0) + (s.schoolAudit || 0), color: "#ff4d4f" },
          { title: "本级已通过", icon: "validCode", count: s.passed || 0, color: "#52c41a" },
          { title: "成果总数", icon: "form", count: this.stats.total || 0, color: "#722ed1" },
          { title: "已驳回", icon: "message", count: s.rejected || 0, color: "#faad14" }
        ];
      } else {
        return [
          { title: "我的申报", icon: "form", count: this.stats.total || 0, color: "#1890ff" },
          { title: "审核中", icon: "time-range", count: (s.collegeAudit || 0) + (s.schoolAudit || 0), color: "#faad14" },
          { title: "已通过", icon: "validCode", count: s.passed || 0, color: "#52c41a" },
          { title: "已被驳回", icon: "message", count: s.rejected || 0, color: "#ff4d4f" }
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
      // 1. 加载字典（成果类型）
      try {
        const dictRes = await getDicts("edu_achievement_category");
        (dictRes.data || []).forEach(d => { this.dictMap[d.dictValue] = d.dictLabel; });
      } catch (e) { /* ignore */ }

      // 2. 加载学院映射
      try {
        const deptRes = await listDept();
        (deptRes.data || []).forEach(d => { this.deptMap[d.deptId] = d.deptName; });
      } catch (e) { /* ignore */ }

      // 3. 加载统计数据：教师角色传 teacherId
      try {
        const params = {};
        if (this.isTeacher) {
          // Fix: use this.id mapped from vuex instead of undefined this.userId
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
      const pieColors = ['#5470C6', '#91CC75', '#FAC858', '#EE6666', '#73C0DE', '#FC8452'];
      // 使用字典映射 category code → 中文标签
      const data = Object.keys(cat).map(k => ({
        value: cat[k],
        name: this.dictMap[k] || ('类型' + k)
      }));
      chart.setOption({
        tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
        legend: { bottom: '5%', left: 'center' },
        color: pieColors,
        series: [{
          type: 'pie', radius: ['40%', '70%'], avoidLabelOverlap: false,
          itemStyle: { borderRadius: 10, borderColor: '#fff', borderWidth: 2 },
          label: { show: false, position: 'center' },
          emphasis: { label: { show: true, fontSize: 18, fontWeight: 'bold' } },
          data: data
        }]
      });
    },
    initBarChart() {
      const chart = echarts.init(this.$refs.barChart);
      this.charts.push(chart);
      const s = this.stats.statusData || {};
      
      let chartCategories = ['草稿', '院审中', '校审中', '已通过', '已驳回'];
      let chartData = [s.draft || 0, s.collegeAudit || 0, s.schoolAudit || 0, s.passed || 0, s.rejected || 0];
      let chartColors = ['#909399', '#E6A23C', '#F56C6C', '#67C23A', '#F56C6C'];
      
      // 如果不是教师（即审核员或管理员），无需展示草稿状态
      if (!this.isTeacher) {
        chartCategories = chartCategories.slice(1);
        chartData = chartData.slice(1);
        chartColors = chartColors.slice(1);
      }

      chart.setOption({
        tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
        grid: { left: '3%', right: '6%', bottom: '3%', containLabel: true },
        xAxis: { type: 'category', data: chartCategories,
          axisLine: { lineStyle: { color: '#ddd' } }, axisLabel: { color: '#666' } },
        yAxis: { type: 'value', axisLine: { show: false }, splitLine: { lineStyle: { color: '#f0f0f0' } } },
        series: [{
          type: 'bar', barWidth: '45%',
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
          pointer: { show: true, length: '60%', width: 6 },
          progress: { show: true, width: 18, roundCap: true,
            itemStyle: { color: { type: 'linear', x: 0, y: 0, x2: 1, y2: 0,
              colorStops: [{ offset: 0, color: '#58D9F9' }, { offset: 1, color: '#67C23A' }] } } },
          axisLine: { lineStyle: { width: 18, color: [[1, '#f0f0f0']] } },
          axisTick: { show: false }, splitLine: { show: false },
          axisLabel: { distance: 25, color: '#999', fontSize: 12 },
          detail: { valueAnimation: true, fontSize: 28, fontWeight: 'bold', color: '#333',
            formatter: '{value}%', offsetCenter: [0, '70%'] },
          title: { offsetCenter: [0, '90%'], fontSize: 14, color: '#999' },
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
        xAxis: { type: 'value', splitLine: { lineStyle: { color: '#f0f0f0' } } },
        yAxis: { type: 'category', data: names, axisLine: { lineStyle: { color: '#ddd' } },
          axisLabel: { color: '#666' } },
        series: [{
          type: 'bar', barWidth: '60%',
          itemStyle: { borderRadius: [0, 6, 6, 0],
            color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
              { offset: 0, color: '#1890ff' }, { offset: 1, color: '#69c0ff' }
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
  background-color: #f5f7fa;
  min-height: calc(100vh - 84px);

  .welcome-card {
    background: #ffffff;
    border-radius: 12px;
    padding: 24px 32px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.03);
    margin-bottom: 24px;

    .welcome-left {
      display: flex;
      align-items: center;
      .user-avatar {
        width: 64px; height: 64px; border-radius: 50%;
        margin-right: 20px; border: 2px solid #e6f7ff;
      }
      .greeting { margin: 0 0 8px; font-size: 22px; font-weight: 600; color: #1f2d3d; }
      .weather { margin: 0; font-size: 14px; color: #909399; }
    }

    .welcome-right {
      display: flex; gap: 40px;
      .stat-item { text-align: right;
        .stat-title { font-size: 13px; color: #909399; margin-bottom: 8px; }
        .stat-val { font-size: 20px; font-weight: bold; color: #303133; }
        .role-tag { color: #1890ff; background: #e6f7ff; padding: 2px 10px; border-radius: 4px; font-size: 14px; }
      }
    }
  }

  .chart-wrapper {
    margin-top: 24px;
    .chart-card {
      border-radius: 12px; border: none;
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.03);
      .chart-header { font-weight: 600; color: #333; display: flex; align-items: center; gap: 6px;
        i { color: #1890ff; }
      }
      .chart-container { height: 320px; }
    }
  }
}

@media (max-width: 768px) {
  .welcome-card {
    flex-direction: column !important;
    align-items: flex-start !important;
    .welcome-right { margin-top: 20px; justify-content: flex-start; }
  }
}
</style>
