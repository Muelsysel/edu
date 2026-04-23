import request from '@/utils/request'

export function portalNewsList(query) {
  return request({
    url: '/achievement/portal/news',
    method: 'get',
    params: query,
    headers: { isToken: false }
  })
}


export function portalNewsDetail(newsId) {
  return request({
    url: '/achievement/portal/news/' + newsId,
    method: 'get',
    headers: { isToken: false }
  })
}

export function teacherWithdraw(achievementId) {
  return request({
    url: '/achievement/achievement/teacherWithdraw/' + achievementId,
    method: 'put'
  })
}
