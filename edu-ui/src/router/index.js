import Vue from 'vue'
import Router from 'vue-router'

Vue.use(Router)

import Layout from '@/layout'
import PortalLayout from '@/layout/portal/PortalLayout'

export const constantRoutes = [
  {
    path: '/',
    component: PortalLayout,
    children: [
      {
        path: '',
        component: () => import('@/views/portal/home/index'),
        name: 'PortalHomeRoot',
        meta: { title: '首页', public: true, affix: true }
      }
    ]
  },
  {
    path: '/redirect',
    component: Layout,
    hidden: true,
    children: [
      {
        path: '/redirect/:path(.*)',
        component: () => import('@/views/redirect')
      }
    ]
  },
  {
    path: '/login',
    component: () => import('@/views/login'),
    hidden: true
  },
  {
    path: '/register',
    component: () => import('@/views/register'),
    hidden: true
  },
  {
    path: '/404',
    component: () => import('@/views/error/404'),
    hidden: true
  },
  {
    path: '/401',
    component: () => import('@/views/error/401'),
    hidden: true
  },
  {
    path: '/portal',
    component: PortalLayout,
    redirect: '/portal/home',
    children: [
      {
        path: 'home',
        component: () => import('@/views/portal/home/index'),
        name: 'PortalHome',
        meta: { title: '首页', public: true }
      },
      {
        path: 'news',
        component: () => import('@/views/portal/news/index'),
        name: 'PortalNews',
        meta: { title: '新闻中心', public: true }
      },
      {
        path: 'news/:id(\\d+)',
        component: () => import('@/views/portal/news/detail'),
        name: 'PortalNewsDetail',
        meta: { title: '新闻详情', public: true, activeMenu: '/portal/news' }
      },
      {
        path: 'declare',
        component: () => import('@/views/portal/declare/index'),
        name: 'PortalDeclare',
        meta: { title: '教师申报', requiresAuth: true, portalOnly: true }
      },
      {
        path: 'mine',
        component: () => import('@/views/portal/mine/index'),
        name: 'PortalMine',
        meta: { title: '我的申报', requiresAuth: true, portalOnly: true }
      },
      {
        path: 'audit/college',
        component: () => import('@/views/achievement/collegeAudit/index'),
        name: 'PortalCollegeAudit',
        meta: { title: '院级审核', requiresAuth: true, portalOnly: true }
      },
      {
        path: 'audit/school',
        component: () => import('@/views/achievement/schoolAudit/index'),
        name: 'PortalSchoolAudit',
        meta: { title: '校级审核', requiresAuth: true, portalOnly: true }
      },
      {
        path: 'audit/records',
        component: () => import('@/views/achievement/auditRecord/index'),
        name: 'PortalAuditRecords',
        meta: { title: '审核记录', requiresAuth: true, portalOnly: true }
      },
      {
        path: 'profile',
        component: () => import('@/views/portal/profile/index'),
        name: 'PortalProfile',
        meta: { title: '个人设置', requiresAuth: true }
      }
    ]
  },
  {
    path: '/admin',
    component: Layout,
    redirect: '/admin/index',
    children: [
      {
        path: 'index',
        component: () => import('@/views/index'),
        name: 'AdminIndex',
        meta: { title: '首页', icon: 'dashboard', affix: true, adminOnly: true }
      }
    ]
  },
  {
    path: '/user',
    component: Layout,
    hidden: true,
    redirect: 'noredirect',
    children: [
      {
        path: 'profile',
        component: () => import('@/views/system/user/profile/index'),
        name: 'Profile',
        meta: { title: '个人中心', icon: 'user' }
      }
    ]
  }
]

export const dynamicRoutes = [
  {
    path: '/system/user-auth',
    component: Layout,
    hidden: true,
    permissions: ['system:user:edit'],
    children: [
      {
        path: 'role/:userId(\\d+)',
        component: () => import('@/views/system/user/authRole'),
        name: 'AuthRole',
        meta: { title: '分配角色', activeMenu: '/system/user' }
      }
    ]
  },
  {
    path: '/system/role-auth',
    component: Layout,
    hidden: true,
    permissions: ['system:role:edit'],
    children: [
      {
        path: 'user/:roleId(\\d+)',
        component: () => import('@/views/system/role/authUser'),
        name: 'AuthUser',
        meta: { title: '分配用户', activeMenu: '/system/role' }
      }
    ]
  },
  {
    path: '/system/dict-data',
    component: Layout,
    hidden: true,
    permissions: ['system:dict:list'],
    children: [
      {
        path: 'index/:dictId(\\d+)',
        component: () => import('@/views/system/dict/data'),
        name: 'Data',
        meta: { title: '字典数据', activeMenu: '/system/dict' }
      }
    ]
  },
  {
    path: '/monitor/job-log',
    component: Layout,
    hidden: true,
    permissions: ['monitor:job:list'],
    children: [
      {
        path: 'index/:jobId(\\d+)',
        component: () => import('@/views/monitor/job/log'),
        name: 'JobLog',
        meta: { title: '调度日志', activeMenu: '/monitor/job' }
      }
    ]
  },
  {
    path: '/tool/gen-edit',
    component: Layout,
    hidden: true,
    permissions: ['tool:gen:edit'],
    children: [
      {
        path: 'index/:tableId(\\d+)',
        component: () => import('@/views/tool/gen/editTable'),
        name: 'GenEdit',
        meta: { title: '修改生成配置', activeMenu: '/tool/gen' }
      }
    ]
  }
]

const routerPush = Router.prototype.push
const routerReplace = Router.prototype.replace

Router.prototype.push = function push(location) {
  return routerPush.call(this, location).catch(err => err)
}

Router.prototype.replace = function replace(location) {
  return routerReplace.call(this, location).catch(err => err)
}

export default new Router({
  mode: 'history',
  scrollBehavior: () => ({ y: 0 }),
  routes: constantRoutes
})
