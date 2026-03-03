import request from '@/utils/request'

// 查询教学成果管理列表
export function listAchievement(query) {
  return request({
    url: '/achievement/achievement/list',
    method: 'get',
    params: query
  })
}

// 查询教学成果管理详细
export function getAchievement(achievementId) {
  return request({
    url: '/achievement/achievement/' + achievementId,
    method: 'get'
  })
}

// 新增教学成果管理
export function addAchievement(data) {
  return request({
    url: '/achievement/achievement',
    method: 'post',
    data: data
  })
}

// 修改教学成果管理
export function updateAchievement(data) {
  return request({
    url: '/achievement/achievement',
    method: 'put',
    data: data
  })
}

// 删除教学成果管理
export function delAchievement(achievementId) {
  return request({
    url: '/achievement/achievement/' + achievementId,
    method: 'delete'
  })
}




// 教师端专用的新增接口
export function myAddAchievement(data) {
  return request({
    url: '/achievement/achievement/myAdd',
    method: 'post',
    data: data
  })
}

//myAchievementList
export function myAchievementList(query) {
  return request({
    url: '/achievement/achievement/myAchievementList',
    method: 'get',
    params: query
  })
}

//教师查询自己的成果列表
export function myListAchievement(query) {
  return request({
    url: '/achievement/achievement/myList',
    method: 'get',
    params: query
  })
}



