import request from '@/utils/request'

// ==================== 院级审核 ====================

// 院级审核 - 获取待审核列表
export function collegeAuditList(query) {
  return request({
    url: '/achievement/audit/college/list',
    method: 'get',
    params: query
  })
}

// 院级审核 - 审核操作
export function collegeAuditHandle(data) {
  return request({
    url: '/achievement/audit/college/handle',
    method: 'post',
    data: data
  })
}

// ==================== 校级审核 ====================

// 校级审核 - 获取待审核列表
export function schoolAuditList(query) {
  return request({
    url: '/achievement/audit/school/list',
    method: 'get',
    params: query
  })
}

// 校级审核 - 审核操作
export function schoolAuditHandle(data) {
  return request({
    url: '/achievement/audit/school/handle',
    method: 'post',
    data: data
  })
}

// ==================== 通用 ====================

// 获取成果详情（审核时查看）
export function getAuditDetail(achievementId) {
  return request({
    url: '/achievement/audit/detail/' + achievementId,
    method: 'get'
  })
}

// 查询审核记录列表
export function listAuditRecord(query) {
  return request({
    url: '/achievement/audit/record/list',
    method: 'get',
    params: query
  })
}

// 首页统计数据
export function getStatistics() {
  return request({
    url: '/achievement/audit/statistics',
    method: 'get'
  })
}
