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





// 教师专用

//教师新增成果
export function teacherAddAchievement(data) {
  return request({
    url: '/achievement/achievement/teacherAddAchievement',
    method: 'post',
    data: data
  })
}


//教师删除成果
export function teacherDelAchievement(achievementId) {
  return request({
    url: '/achievement/achievement/teacherDelAchievement/' + achievementId,
    method: 'delete'
  })
}

//教师修改成果
export function teacherUpdateAchievement(data) {
  return request({
    url: '/achievement/achievement/teacherUpdateAchievement',
    method: 'put',
    data: data
  })
}



//教师查询成果列表
export function teacherListAchievement(query) {
  return request({
    url: '/achievement/achievement/teacherListAchievement',
    method: 'get',
    params: query
  })
}

 //教师查询成果详细
export function teacherGetAchievement(achievementId) {
  return request({
    url: '/achievement/achievement/teacherGetAchievement/' + achievementId,
    method: 'get'
  })
}
export function teacherListAllAchievement(query) {
  return request({
    url: '/achievement/achievement/teacherListAllAchievement',
  })
}





