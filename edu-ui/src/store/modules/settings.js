import defaultSettings from '@/settings'
import { useDynamicTitle } from '@/utils/dynamicTitle'

const { sideTheme, showSettings, navType, tagsView, tagsIcon, fixedHeader, sidebarLogo, dynamicTitle, footerVisible, footerContent } = defaultSettings

const storageSetting = JSON.parse(localStorage.getItem('layout-setting')) || ''
const state = {
  title: '',
  // 1. 采用科技蓝作为全局主色调
  theme: storageSetting.theme || '#1890ff',
  // 2. 强制默认侧边栏为浅色模式
  sideTheme: storageSetting.sideTheme || 'theme-light',
  // 3. 隐藏框架自带的右侧设置抽屉，防止 UI 被破坏
  showSettings: false,
  navType: storageSetting.navType || 1,
  tagsView: storageSetting.tagsView !== undefined ? storageSetting.tagsView : false,
  tagsIcon: storageSetting.tagsIcon !== undefined ? storageSetting.tagsIcon : false,
  fixedHeader: storageSetting.fixedHeader === undefined ? true : storageSetting.fixedHeader,
  sidebarLogo: storageSetting.sidebarLogo === undefined ? true : storageSetting.sidebarLogo,
  dynamicTitle: storageSetting.dynamicTitle === undefined ? true : storageSetting.dynamicTitle,
  footerVisible: storageSetting.footerVisible === undefined ? true : storageSetting.footerVisible,
  footerContent: '基于 Spring Cloud 的高校教学成果管理系统'
}

const mutations = {
  CHANGE_SETTING: (state, { key, value }) => {
    if (state.hasOwnProperty(key)) {
      state[key] = value
    }
  },
  SET_TITLE: (state, title) => {
    state.title = title
  }
}

const actions = {
  // 修改布局设置
  changeSetting({ commit }, data) {
    commit('CHANGE_SETTING', data)
  },
  // 设置网页标题
  setTitle({ commit }, title) {
    commit('SET_TITLE', title)
    useDynamicTitle()
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}
