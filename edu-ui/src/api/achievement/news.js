import request from '@/utils/request'

// 查询新闻列表
export function listNews(query) {
  return request({
    url: '/achievement/news/list',
    method: 'get',
    params: query
  })
}

// 查询新闻详情
export function getNews(newsId) {
  return request({
    url: '/achievement/news/' + newsId,
    method: 'get'
  })
}

// 新增新闻
export function addNews(data) {
  return request({
    url: '/achievement/news',
    method: 'post',
    data: data
  })
}

// 修改新闻
export function updateNews(data) {
  return request({
    url: '/achievement/news',
    method: 'put',
    data: data
  })
}

// 删除新闻
export function delNews(newsId) {
  return request({
    url: '/achievement/news/' + newsId,
    method: 'delete'
  })
}
