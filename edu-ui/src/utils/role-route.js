export function isAdminUser(roles = []) {
  return roles.includes('admin') || roles.includes('admin1')
}

export function isTeacherUser(roles = []) {
  const hasTeacher = roles.includes('teacher') || roles.includes('common') || roles.includes('ROLE_TEACHER')
  return hasTeacher && !isAdminUser(roles) && !isAuditorUser(roles)
}

export function isSchoolAuditor(roles = []) {
  return roles.includes('SchoolAudit')
}

export function isAuditorUser(roles = []) {
  return isSchoolAuditor(roles) || roles.includes('auditor')
}

export function isPortalUser(roles = []) {
  return !isAdminUser(roles)
}

export function canAccessPortalRoute(path, roles = []) {
  if (path === '/portal/declare' || path === '/portal/mine') {
    return isTeacherUser(roles)
  }
  if (path.startsWith('/portal/audit/school')) {
    return isSchoolAuditor(roles)
  }
  if (path.startsWith('/portal/audit/records')) {
    return isAuditorUser(roles)
  }
  return true
}

export function getPortalLandingPath(roles = []) {
  if (!roles || roles.length === 0) {
    return '/'
  }
  if (isSchoolAuditor(roles)) {
    return '/portal/audit/school'
  }
  if (isAuditorUser(roles)) {
    return '/portal/audit/records'
  }
  return '/portal/home'
}

export function getLandingPathByRoles(roles = []) {
  return isAdminUser(roles) ? '/admin' : getPortalLandingPath(roles)
}
