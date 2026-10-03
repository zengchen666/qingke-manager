import request from './request'

/** 条件分页查询部门列表 */
export function listDept(params) {
  return request.get('/depts', { params })
}

/** 新增部门 */
export function addDept(data) {
  return request.post('/depts', data)
}

/** 根据 id 查询部门（编辑时回显） */
export function getDeptById(id) {
  return request.get(`/depts/${id}`)
}

/** 修改部门 */
export function updateDept(data) {
  return request.put('/depts', data)
}

/** 删除部门 */
export function deleteDept(id) {
  return request.delete(`/depts/${id}`)
}
