<template>
  <el-row :gutter="20" class="panel-group">
    <el-col
      :xs="12" :sm="12" :lg="6"
      class="card-panel-col"
      v-for="(item, index) in panelList"
      :key="index"
    >
      <div class="card-panel" :style="{'--accent': item.color}">
        <div class="card-panel-accent"></div>
        <div class="card-panel-body">
          <div class="card-panel-icon-wrapper" :style="{ color: item.color, backgroundColor: item.color + '12' }">
            <svg-icon :icon-class="item.icon" class-name="card-panel-icon" />
          </div>
          <div class="card-panel-description">
            <div class="card-panel-text">
              {{ item.title }}
            </div>
            <count-to :start-val="0" :end-val="item.count" :duration="2000" class="card-panel-num" />
          </div>
        </div>
      </div>
    </el-col>
  </el-row>
</template>

<script>
import CountTo from 'vue-count-to'

export default {
  name: 'PanelGroup',
  components: {
    CountTo
  },
  props: {
    panelList: {
      type: Array,
      default: () => []
    }
  }
}
</script>

<style lang="scss" scoped>
.panel-group {
  margin-top: 0;

  .card-panel-col {
    margin-bottom: 20px;
  }

  .card-panel {
    height: 110px;
    cursor: pointer;
    font-size: 12px;
    position: relative;
    overflow: hidden;
    background: #fff;
    border-radius: 12px;
    border: 1px solid #e2e8f0;
    box-shadow: 0 1px 3px rgba(15, 23, 42, 0.04);
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);

    /* 顶部彩色装饰条 */
    .card-panel-accent {
      height: 3px;
      background: var(--accent);
      border-radius: 3px 3px 0 0;
    }

    .card-panel-body {
      display: flex;
      align-items: center;
      padding: 0 24px;
      height: calc(100% - 3px);
    }

    &:hover {
      transform: translateY(-3px);
      box-shadow: 0 8px 25px rgba(15, 23, 42, 0.08);
      border-color: transparent;
    }

    .card-panel-icon-wrapper {
      padding: 14px;
      transition: all 0.3s ease-out;
      border-radius: 10px;
      display: flex;
      align-items: center;
      justify-content: center;
    }

    .card-panel-icon {
      font-size: 32px;
    }

    .card-panel-description {
      margin-left: 18px;
      flex: 1;

      .card-panel-text {
        line-height: 18px;
        color: #64748b;
        font-size: 13px;
        margin-bottom: 8px;
        font-weight: 500;
      }

      .card-panel-num {
        font-size: 28px;
        font-weight: 700;
        color: #0f172a;
        font-family: 'Noto Serif SC', serif;
      }
    }
  }
}

@media (max-width:550px) {
  .card-panel {
    .card-panel-body {
      padding: 0 14px !important;
    }
    .card-panel-icon-wrapper {
      padding: 10px !important;
    }
    .card-panel-icon {
      font-size: 24px !important;
    }
  }
}
</style>
