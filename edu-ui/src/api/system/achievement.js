import request from '@/utils/request'

// 查询教学成果管理列表
export function listAchievement(query) {
  return request({
    url: '/system/achievement/list',
    method: 'get',
    params: query
  })
}

// 查询教学成果管理详细
export function getAchievement(achievementId) {
  return request({
    url: '/system/achievement/' + achievementId,
    method: 'get'
  })
}

// 新增教学成果管理
export function addAchievement(data) {
  return request({
    url: '/system/achievement',
    method: 'post',
    data: data
  })
}

// 修改教学成果管理
export function updateAchievement(data) {
  return request({
    url: '/system/achievement',
    method: 'put',
    data: data
  })
}

// 删除教学成果管理
export function delAchievement(achievementId) {
  return request({
    url: '/system/achievement/' + achievementId,
    method: 'delete'
  })
}
