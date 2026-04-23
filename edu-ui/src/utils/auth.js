import Cookies from 'js-cookie'

const TokenKey = 'Admin-Token'
const ExpiresInKey = 'Admin-Expires-In'

const legacyTokenKeys = ['Edu-SSO-Token', 'Portal-Token']
const legacyExpiresKeys = ['Edu-SSO-Expires-In', 'Portal-Expires-In']

export function getToken() {
  return Cookies.get(TokenKey) || legacyTokenKeys.map(key => Cookies.get(key)).find(Boolean)
}

export function setToken(token) {
  Cookies.set(TokenKey, token)
  legacyTokenKeys.forEach(key => Cookies.remove(key))
  return token
}

export function removeToken() {
  Cookies.remove(TokenKey)
  legacyTokenKeys.forEach(key => Cookies.remove(key))
}

export function getExpiresIn() {
  return Cookies.get(ExpiresInKey) || legacyExpiresKeys.map(key => Cookies.get(key)).find(Boolean) || -1
}

export function setExpiresIn(time) {
  Cookies.set(ExpiresInKey, time)
  legacyExpiresKeys.forEach(key => Cookies.remove(key))
  return time
}

export function removeExpiresIn() {
  Cookies.remove(ExpiresInKey)
  legacyExpiresKeys.forEach(key => Cookies.remove(key))
}
