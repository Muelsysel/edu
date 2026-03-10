<template>
  <div class="dashboard-container">
    <div class="welcome-card">
      <div class="welcome-left">
        <img :src="avatar" class="user-avatar" alt="avatar" />
        <div class="welcome-info">
          <h2 class="greeting">{{ greeting }}，{{ nickName }}</h2>
          <p class="weather">欢迎使用 高校教学成果管理平台。今天也要在学术的道路上继续发光发热哦！</p>
        </div>
      </div>
      <div class="welcome-right">
        <div class="stat-item">
          <div class="stat-title">系统版本</div>
          <div class="stat-val">v3.6.7</div>
        </div>
        <div class="stat-item">
          <div class="stat-title">当前角色</div>
          <div class="stat-val role-tag">{{ roleName }}</div>
        </div>
      </div>
    </div>

    <panel-group :panel-list="currentPanelData" />

    <el-row :gutter="20" class="chart-wrapper">
      <el-col :xs="24" :sm="24" :lg="12">
        <el-card shadow="hover" class="chart-card">
          <div slot="header" class="chart-header">
            <span>成果申报趋势</span>
          </div>
          <div class="chart-placeholder">
            <svg-icon icon-class="chart" class="placeholder-icon"/>
            <p>近六个月数据统计</p>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="24" :lg="12">
        <el-card shadow="hover" class="chart-card">
          <div slot="header" class="chart-header">
            <span>成果类型分布</span>
          </div>
          <div class="chart-placeholder">
            <svg-icon icon-class="chart" class="placeholder-icon"/>
            <p>科研 / 教材 / 竞赛 / 教改</p>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { mapGetters } from "vuex";
import PanelGroup from "./dashboard/PanelGroup";

export default {
  name: "Index",
  components: {
    PanelGroup
  },
  data() {
    return {
      // 预设三种角色的面板数据
      teacherPanels: [
        { title: "我的申报", icon: "form", count: 12, color: "#1890ff" },
        { title: "审核中", icon: "time-range", count: 3, color: "#faad14" },
        { title: "已通过", icon: "validCode", count: 8, color: "#52c41a" },
        { title: "被驳回", icon: "message", count: 1, color: "#ff4d4f" }
      ],
      auditorPanels: [
        { title: "待我审核", icon: "peoples", count: 25, color: "#ff4d4f" },
        { title: "今日已审", icon: "validCode", count: 14, color: "#1890ff" },
        { title: "本院总申报", icon: "form", count: 156, color: "#722ed1" },
        { title: "驳回率(%)", icon: "chart", count: 12, color: "#faad14" }
      ],
      adminPanels: [
        { title: "全校总成果", icon: "education", count: 799, color: "#1890ff" },
        { title: "活跃教师", icon: "peoples", count: 12, color: "#52c41a" },
        { title: "系统访问量", icon: "online", count: 114514, color: "#722ed1" },
        { title: "今日新增成果", icon: "server", count: 4, color: "#faad14" }
      ]
    };
  },
  computed: {
    ...mapGetters(["avatar", "nickName", "roles"]),
    greeting() {
      const hour = new Date().getHours();
      if (hour < 9) return "早上好";
      if (hour < 12) return "上午好";
      if (hour < 14) return "中午好";
      if (hour < 18) return "下午好";
      return "晚上好";
    },
    // 判断当前用户的最高角色权重，用于展示名称
    roleName() {
      if (this.roles.includes("admin")) return "系统管理员";
      if (this.roles.includes("auditor")) return "成果审核专家";
      return "高校教师";
    },
    // 根据角色动态渲染不同的卡片数据
    currentPanelData() {
      if (this.roles.includes("admin")) {
        return this.adminPanels;
      } else if (this.roles.includes("auditor")) {
        return this.auditorPanels;
      } else {
        // 默认显示教师数据
        return this.teacherPanels;
      }
    }
  }
};
</script>

<style lang="scss" scoped>
.dashboard-container {
  padding: 24px;
  background-color: #f5f7fa;
  min-height: calc(100vh - 84px);

  /* 欢迎卡片样式 */
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
        width: 64px;
        height: 64px;
        border-radius: 50%;
        margin-right: 20px;
        border: 2px solid #e6f7ff;
      }

      .welcome-info {
        .greeting {
          margin: 0 0 8px 0;
          font-size: 22px;
          font-weight: 600;
          color: #1f2d3d;
        }
        .weather {
          margin: 0;
          font-size: 14px;
          color: #909399;
        }
      }
    }

    .welcome-right {
      display: flex;
      gap: 40px;

      .stat-item {
        text-align: right;
        .stat-title {
          font-size: 13px;
          color: #909399;
          margin-bottom: 8px;
        }
        .stat-val {
          font-size: 20px;
          font-weight: bold;
          color: #303133;
        }
        .role-tag {
          color: #1890ff;
          background: #e6f7ff;
          padding: 2px 10px;
          border-radius: 4px;
          font-size: 14px;
        }
      }
    }
  }

  /* 图表占位区样式 */
  .chart-wrapper {
    margin-top: 24px;
    .chart-card {
      border-radius: 12px;
      border: none;
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.03);

      .chart-header {
        font-weight: 600;
        color: #333;
      }

      .chart-placeholder {
        height: 300px;
        display: flex;
        flex-direction: column;
        justify-content: center;
        align-items: center;
        color: #c0c4cc;
        background: #fafafa;
        border-radius: 8px;

        .placeholder-icon {
          font-size: 48px;
          margin-bottom: 15px;
        }
      }
    }
  }
}

@media (max-width: 768px) {
  .welcome-card {
    flex-direction: column;
    align-items: flex-start !important;
    .welcome-right {
      margin-top: 20px;
      justify-content: flex-start;
    }
  }
}
</style>
