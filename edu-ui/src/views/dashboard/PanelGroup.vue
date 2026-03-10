<template>
  <el-row :gutter="24" class="panel-group">
    <el-col
      :xs="12" :sm="12" :lg="6"
      class="card-panel-col"
      v-for="(item, index) in panelList"
      :key="index"
    >
      <div class="card-panel" :style="{'--hover-color': item.color}">
        <div class="card-panel-icon-wrapper" :style="{ color: item.color, backgroundColor: item.color + '15' }">
          <svg-icon :icon-class="item.icon" class-name="card-panel-icon" />
        </div>
        <div class="card-panel-description">
          <div class="card-panel-text">
            {{ item.title }}
          </div>
          <count-to :start-val="0" :end-val="item.count" :duration="2000" class="card-panel-num" />
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
    // 接收外部传来的面板数据
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
    margin-bottom: 24px;
  }

  .card-panel {
    height: 120px;
    cursor: pointer;
    font-size: 12px;
    position: relative;
    overflow: hidden;
    background: #fff;
    border-radius: 12px; // 胶囊圆角体系
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.03);
    transition: all 0.3s cubic-bezier(0.645, 0.045, 0.355, 1);
    display: flex;
    align-items: center;
    padding: 0 24px;

    /* 悬浮时的阴影上浮动效 */
    &:hover {
      transform: translateY(-4px);
      box-shadow: 0 12px 24px rgba(0, 0, 0, 0.06);

      .card-panel-icon-wrapper {
        background-color: var(--hover-color) !important;
        color: #fff !important;
      }
    }

    .card-panel-icon-wrapper {
      padding: 16px;
      transition: all 0.3s ease-out;
      border-radius: 12px;
      display: flex;
      align-items: center;
      justify-content: center;
    }

    .card-panel-icon {
      font-size: 36px;
    }

    .card-panel-description {
      margin-left: 20px;
      flex: 1;

      .card-panel-text {
        line-height: 18px;
        color: #8c8c8c;
        font-size: 15px;
        margin-bottom: 10px;
        font-weight: 500;
      }

      .card-panel-num {
        font-size: 26px;
        font-weight: bold;
        color: #1f2d3d;
      }
    }
  }
}

@media (max-width:550px) {
  .card-panel {
    padding: 0 15px !important;
    .card-panel-icon-wrapper {
      padding: 10px !important;
    }
    .card-panel-icon {
      font-size: 28px !important;
    }
  }
}
</style>
