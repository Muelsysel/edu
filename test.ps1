# ==================== 高校教学成果管理系统 - 自动化测试 ====================
# 用法: powershell -ExecutionPolicy Bypass -File test.ps1
# 前置: Gateway 已启动 (port 8080), Nacos 中 captcha.enabled=false

$ErrorActionPreference = "Continue"
$gateway = "http://localhost:8080"

# ====== 身份凭据 ======
$adminUser = "admin";   $adminPass = "admin123"
$teacherUser = "teacher"; $teacherPass = "123456"
$auditorUser = "schoolAudit"; $auditorPass = "123456"

# ====== 教师信息 ======
$teacherId = 100
$collegeId = 124

$script:PASS = 0
$script:FAIL = 0

function Pass($msg) { Write-Host "  [PASS] $msg" -ForegroundColor Green; $script:PASS++ }
function Fail($msg, $expected, $got) { Write-Host "  [FAIL] $msg (expected: $expected, got: $got)" -ForegroundColor Red; $script:FAIL++ }

# ====== 1. 登录 ======
Write-Host "`n=== 1. Login ===" -ForegroundColor Cyan

function Login($user, $pass) {
    $body = @{username=$user; password=$pass} | ConvertTo-Json
    $resp = Invoke-RestMethod -Uri "$gateway/auth/login" -Method Post -ContentType "application/json" -Body $body
    if (-not $resp.data.access_token) { Write-Host "  [DEBUG] Login response for $user : $($resp | ConvertTo-Json)" -ForegroundColor Yellow }
    return $resp.data.access_token
}

$tokenTeacher = Login $teacherUser $teacherPass
$tokenAuditor = Login $auditorUser $auditorPass

if ($tokenTeacher) { Pass "Teacher login" } else { Fail "Teacher login" "token" "empty" }
if ($tokenAuditor) { Pass "Auditor login" } else { Fail "Auditor login" "token" "empty" }

$headersT = @{ Authorization = "Bearer $tokenTeacher"; "Content-Type" = "application/json" }
$headersA = @{ Authorization = "Bearer $tokenAuditor"; "Content-Type" = "application/json" }

# ====== 2. 教师提交成果 ======
Write-Host "`n=== 2. Teacher submits achievement ===" -ForegroundColor Cyan

$title = "AutoTest_" + (Get-Date -Format "yyyyMMdd_HHmmss")
$body = @{
    title = $title
    category = "1"
    collegeId = $collegeId
    content = "<p>Automated test content</p>"
    status = "2"
} | ConvertTo-Json

$submitResp = Invoke-RestMethod -Uri "$gateway/achievement/achievement/teacherAddAchievement" -Method Post -Headers $headersT -Body $body
if ($submitResp.code -eq 200) { Pass "Submit achievement" } else { Fail "Submit achievement" "200" $submitResp.code }
Start-Sleep -Milliseconds 500

# ====== 3. 审计员查询待审列表 ======
Write-Host "`n=== 3. Auditor queries pending list ===" -ForegroundColor Cyan

$listResp = Invoke-RestMethod -Uri "$gateway/achievement/audit/school/list?pageNum=1&pageSize=5" -Headers $headersA
if ($listResp.total -gt 0) { Pass "Pending list count=$($listResp.total)" } else { Fail "Pending list" ">0" $listResp.total }
$achId = if ($listResp.rows.Count -gt 0) { $listResp.rows[0].achievementId } else { 0 }
if ($achId -ne 0) { Pass "Got achievement ID=$achId" } else { Fail "Get achievement ID" ">0" $achId }

# ====== 4. 审计员审核通过 ======
Write-Host "`n=== 4. Auditor approves ===" -ForegroundColor Cyan

$auditBody = @{
    achievementId = $achId
    auditResult = "1"
    auditOpinion = "Auto-test approved"
} | ConvertTo-Json

$approveResp = Invoke-RestMethod -Uri "$gateway/achievement/audit/school/handle" -Method Post -Headers $headersA -Body $auditBody
if ($approveResp.code -eq 200) { Pass "Approve achievement" } else { Fail "Approve" "200" $approveResp.code }
Start-Sleep -Milliseconds 500

# ====== 5. 教师查看我的申报 ======
Write-Host "`n=== 5. Teacher checks own list ===" -ForegroundColor Cyan

$mineResp = Invoke-RestMethod -Uri "$gateway/achievement/achievement/teacherListAchievement?pageNum=1&pageSize=99" -Headers $headersT
$match = ($mineResp.rows | Where-Object { $_.title -eq $title } | Select-Object -First 1)
if ($match.status -eq 3) { Pass "Achievement status=3 (passed)" } else { Fail "Achievement status" "3" $match.status }

# ====== 6. 审计员查询审核档案 ======
Write-Host "`n=== 6. Auditor queries records ===" -ForegroundColor Cyan

$recordResp = Invoke-RestMethod -Uri "$gateway/achievement/audit/record/list?pageNum=1&pageSize=99" -Headers $headersA
if ($recordResp.total -gt 0) { Pass "Audit records count=$($recordResp.total)" } else { Fail "Audit records" ">0" $recordResp.total }

# ====== 结果 ======
Write-Host "`n==============================" -ForegroundColor Cyan
Write-Host "  PASS: $PASS   FAIL: $FAIL"
Write-Host "==============================" -ForegroundColor Cyan
if ($FAIL -eq 0) { Write-Host "[OK] ALL TESTS PASSED" -ForegroundColor Green } else { Write-Host "[ERR] SOME TESTS FAILED" -ForegroundColor Red }