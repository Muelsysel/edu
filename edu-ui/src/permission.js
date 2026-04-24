import router from './router'
import store from './store'
import { Message } from 'element-ui'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'
import { getToken } from '@/utils/auth'
import { isPathMatch } from '@/utils/validate'
import { isRelogin } from '@/utils/request'
import {
  canAccessPortalRoute,
  getLandingPathByRoles,
  isAdminUser,
  isPortalUser
} from '@/utils/role-route'

NProgress.configure({ showSpinner: false })

const whiteList = ['/','/login', '/register']

const isWhiteList = (path) => whiteList.some(pattern => isPathMatch(pattern, path))

const isPublicRoute = (to) => to.matched.some(record => record.meta && record.meta.public)

const requiresAuth = (to) => to.matched.some(record => record.meta && record.meta.requiresAuth)

const isAdminArea = (to) => {
  if (to.path === '/admin' || to.path.startsWith('/admin/')) {
    return true
  }
  return to.matched.some(record => record.meta && record.meta.adminOnly)
}

const needsAdminRoutes = () => {
  return !store.getters.sidebarRouters || store.getters.sidebarRouters.length === 0
}

const guardPortalAccess = (to, next, roles) => {
  if (isAdminArea(to)) {
    next(getLandingPathByRoles(roles))
    return true
  }
  if (to.path.startsWith('/portal') && !canAccessPortalRoute(to.path, roles)) {
    next(getLandingPathByRoles(roles))
    return true
  }
  if (!to.path.startsWith('/portal') && to.path !== '/' && !to.path.startsWith('/redirect')) {
    next(getLandingPathByRoles(roles))
    return true
  }
  return false
}

router.beforeEach((to, from, next) => {
  NProgress.start()

  if (!getToken()) {
    if (isWhiteList(to.path) || isPublicRoute(to) || to.path === '/') {
      next()
    } else {
      next('/login?redirect=' + encodeURIComponent(to.fullPath))
      NProgress.done()
    }
    return
  }

  to.meta.title && store.dispatch('settings/setTitle', to.meta.title)

  if (to.path === '/login') {
    if (store.getters.roles.length > 0) {
      next({ path: getLandingPathByRoles(store.getters.roles) })
    } else {
      next()
    }
    NProgress.done()
    return
  }

  const afterGetInfo = (roles) => {
    if (isPortalUser(roles)) {
      if (guardPortalAccess(to, next, roles)) {
        return
      }
      next()
      return
    }

    if (needsAdminRoutes()) {
      store.dispatch('GenerateRoutes').then(accessRoutes => {
        router.addRoutes(accessRoutes)
        if (to.path === '/' || to.path.startsWith('/portal')) {
          next({ path: getLandingPathByRoles(roles), replace: true })
          return
        }
        next({ ...to, replace: true })
      })
      return
    }

    if (to.path === '/' || to.path.startsWith('/portal')) {
      next({ path: getLandingPathByRoles(roles), replace: true })
      return
    }
    next()
  }

  if (store.getters.roles.length === 0) {
    isRelogin.show = true
    store.dispatch('GetInfo').then((res) => {
      isRelogin.show = false
      afterGetInfo(res.roles || store.getters.roles || [])
    }).catch(err => {
      store.dispatch('LogOut').then(() => {
        Message.error(err)
        next({ path: '/login' })
      })
    })
    return
  }

  afterGetInfo(store.getters.roles)
})

router.afterEach(() => {
  NProgress.done()
})
