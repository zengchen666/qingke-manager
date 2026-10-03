import request from './request'

/** 线索列表（条件 + 分页） */
export function listClue(params) {
  return request.get('/clues', { params })
}

/** 线索详情（含跟进记录列表） */
export function getClueById(id) {
  return request.get(`/clues/${id}`)
}

/** 新增线索 */
export function addClue(data) {
  return request.post('/clues', data)
}

/** 分配线索给某个用户：待分配 -> 待跟进 */
export function assignClue(clueId, userId) {
  return request.put(`/clues/assign/${clueId}/${userId}`)
}

/** 跟进线索：更新线索基本信息 + 追加一条跟进记录 */
export function trackClue(data) {
  return request.put('/clues', data)
}

/** 线索转商机 */
export function toBusiness(id) {
  return request.put(`/clues/toBusiness/${id}`)
}
