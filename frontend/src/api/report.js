import request from './request'

/** 首页概览：线索 + 商机各状态统计 */
export function getOverview() {
  return request.get('/report/overview')
}
