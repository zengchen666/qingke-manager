import request from './request'

/** 条件分页查询用户列表 */
export function listUser(params) {
  return request.get('/users', { params })
}

/** 按角色标识查询用户（分配线索时选线索专员） */
export function listUserByRole(roleLabel) {
  return request.get(`/users/role/${roleLabel}`)
}

/** 新增用户（密码由后端按「用户名+123」规则 MD5 加密） */
export function addUser(data) {
  return request.post('/users', data)
}

/** 批量删除用户，ids 支持单个或多个 */
export function deleteUser(ids) {
  return request.delete(`/users/${ids.join(',')}`)
}
