/**
 * 登录态存取
 *
 * <p>token 与用户信息放在 localStorage：
 * 放 sessionStorage 关掉标签页就丢，放 localStorage 可以保持 12 小时登录态
 * （后端 JWT 有效期是 12 小时，两边对齐）。
 */

const TOKEN_KEY = 'qk_token'
const USER_KEY = 'qk_user'

export function getToken() {
  return localStorage.getItem(TOKEN_KEY)
}

export function setToken(token) {
  localStorage.setItem(TOKEN_KEY, token)
}

export function getUser() {
  const raw = localStorage.getItem(USER_KEY)
  if (!raw) return null
  try {
    return JSON.parse(raw)
  } catch (e) {
    // 数据被手动改坏时不要让整个应用崩掉，直接当作未登录
    localStorage.removeItem(USER_KEY)
    return null
  }
}

export function setUser(user) {
  localStorage.setItem(USER_KEY, JSON.stringify(user))
}

export function clearLogin() {
  localStorage.removeItem(TOKEN_KEY)
  localStorage.removeItem(USER_KEY)
}
