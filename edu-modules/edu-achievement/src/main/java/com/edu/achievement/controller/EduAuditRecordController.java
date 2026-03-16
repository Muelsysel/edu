package com.edu.achievement.controller;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.edu.achievement.domain.EduAuditRecord;
import com.edu.achievement.service.IEduAuditRecordService;
import com.edu.common.core.utils.DateUtils;
import com.edu.common.security.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.edu.common.log.annotation.Log;
import com.edu.common.log.enums.BusinessType;
import com.edu.common.security.annotation.RequiresPermissions;
import com.edu.achievement.domain.EduAchievement;
import com.edu.achievement.service.IEduAchievementService;
import com.edu.common.core.web.controller.BaseController;
import com.edu.common.core.web.domain.AjaxResult;
import com.edu.common.core.utils.poi.ExcelUtil;
import com.edu.common.core.web.page.TableDataInfo;

/**
 * 审核管理Controller
 *
 * <p>提供院级审核、校级审核的待审列表查询、审核操作（通过/驳回）、
 * 审核记录查询和导出、以及全局统计数据接口。</p>
 *
 * <p>审核操作流程：
 * <ul>
 *   <li>院级审核：成果 status 1→2（通过）或 1→4（驳回）</li>
 *   <li>校级审核：成果 status 2→3（通过）或 2→4（驳回）</li>
 * </ul>
 * 每次审核操作都会在 edu_audit_record 表中写入一条记录。</p>
 *
 * @author zpz
 * @date 2026-03-15
 */
@RestController
@RequestMapping("/audit")
public class EduAuditRecordController extends BaseController
{
    @Autowired
    private IEduAchievementService eduAchievementService;

    @Autowired
    private IEduAuditRecordService eduAuditRecordService;

    /**
     * 院级审核 - 获取待审核列表
     *
     * <p>自动按当前审核人所在学院（dept_id）过滤，只返回 status=1（院级审核中）的成果。
     * 确保院级审核员只能看到本院的成果数据。</p>
     *
     * @param eduAchievement 查询条件（可按 title、category 等过滤）
     * @return 分页后的待审核成果列表
     */
    @RequiresPermissions("achievement:audit:college")
    @GetMapping("/college/list")
    public TableDataInfo collegeList(EduAchievement eduAchievement)
    {
        startPage();
        eduAchievement.setStatus("1");
        Long deptId = SecurityUtils.getLoginUser().getSysUser().getDeptId();
        eduAchievement.setCollegeId(deptId);
        List<EduAchievement> list = eduAchievementService.selectEduAchievementList(eduAchievement);
        return getDataTable(list);
    }

    /**
     * 校级审核 - 获取待审核列表
     *
     * <p>返回全校范围内 status=2（校级审核中）的所有成果，不做学院过滤。</p>
     *
     * @param eduAchievement 查询条件
     * @return 分页后的待审核成果列表
     */
    @RequiresPermissions("achievement:audit:school")
    @GetMapping("/school/list")
    public TableDataInfo schoolList(EduAchievement eduAchievement)
    {
        startPage();
        eduAchievement.setStatus("2");
        List<EduAchievement> list = eduAchievementService.selectEduAchievementList(eduAchievement);
        return getDataTable(list);
    }

    /**
     * 获取成果详细信息（审核时查看）
     *
     * @param achievementId 成果主键ID
     * @return 成果完整信息（含教师姓名、学院等 JOIN 字段）
     */
    @RequiresPermissions("achievement:audit:query")
    @GetMapping("/detail/{achievementId}")
    public AjaxResult getDetail(@PathVariable("achievementId") Long achievementId)
    {
        return success(eduAchievementService.selectEduAchievementByAchievementId(achievementId));
    }

    /**
     * 院级审核操作
     *
     * <p>业务逻辑：
     * <ol>
     *   <li>校验成果当前状态必须为 1（院级审核中），否则拒绝操作</li>
     *   <li>根据审核结果更新成果状态：通过→2（进入校级审核），驳回→4（已驳回）</li>
     *   <li>将审核操作写入 edu_audit_record 表，记录审核级别、结果、意见、审核人</li>
     *   <li>向成果提交者发送站内通知（插入 sys_notice 表）</li>
     * </ol></p>
     *
     * @param auditRecord 包含 achievementId、auditResult（1:通过 2:驳回）、auditOpinion
     * @return 操作结果
     */
    @RequiresPermissions("achievement:audit:college")
    @Log(title = "院级审核", businessType = BusinessType.UPDATE)
    @PostMapping("/college/handle")
    public AjaxResult collegeHandle(@RequestBody EduAuditRecord auditRecord)
    {
        EduAchievement achievement = eduAchievementService.selectEduAchievementByAchievementId(auditRecord.getAchievementId());
        if (achievement == null) {
            return AjaxResult.error("成果不存在");
        }
        if (!"1".equals(achievement.getStatus())) {
            return AjaxResult.error("该成果当前状态不允许院级审核操作");
        }

        // 更新成果状态
        EduAchievement update = new EduAchievement();
        update.setAchievementId(auditRecord.getAchievementId());
        String resultText;
        if ("1".equals(auditRecord.getAuditResult())) {
            update.setStatus("2");
            resultText = "通过院级审核，已进入校级审核";
        } else {
            update.setStatus("4");
            resultText = "院级审核未通过";
        }
        update.setUpdateBy(SecurityUtils.getUsername());
        eduAchievementService.updateEduAchievement(update);

        // 写入审核记录
        auditRecord.setAuditLevel("1");
        auditRecord.setAuditorId(SecurityUtils.getUserId());
        auditRecord.setAuditorName(SecurityUtils.getUsername());
        auditRecord.setCreateTime(DateUtils.getNowDate());
        eduAuditRecordService.insertEduAuditRecord(auditRecord);

        // 发送站内通知给成果提交者
        sendAuditNotice(achievement.getTeacherId(), achievement.getTitle(), resultText, auditRecord.getAuditOpinion());

        return success();
    }

    /**
     * 校级审核操作
     *
     * <p>业务逻辑：
     * <ol>
     *   <li>校验成果当前状态必须为 2（校级审核中），否则拒绝操作</li>
     *   <li>根据审核结果更新成果状态：通过→3（最终通过），驳回→4（已驳回）</li>
     *   <li>将审核操作写入 edu_audit_record 表</li>
     *   <li>向成果提交者发送站内通知</li>
     * </ol></p>
     *
     * @param auditRecord 包含 achievementId、auditResult（1:通过 2:驳回）、auditOpinion
     * @return 操作结果
     */
    @RequiresPermissions("achievement:audit:school")
    @Log(title = "校级审核", businessType = BusinessType.UPDATE)
    @PostMapping("/school/handle")
    public AjaxResult schoolHandle(@RequestBody EduAuditRecord auditRecord)
    {
        EduAchievement achievement = eduAchievementService.selectEduAchievementByAchievementId(auditRecord.getAchievementId());
        if (achievement == null) {
            return AjaxResult.error("成果不存在");
        }
        if (!"2".equals(achievement.getStatus())) {
            return AjaxResult.error("该成果当前状态不允许校级审核操作");
        }

        // 更新成果状态
        EduAchievement update = new EduAchievement();
        update.setAchievementId(auditRecord.getAchievementId());
        String resultText;
        if ("1".equals(auditRecord.getAuditResult())) {
            update.setStatus("3");
            resultText = "恭喜！您的成果已通过校级审核";
        } else {
            update.setStatus("4");
            resultText = "校级审核未通过";
        }
        update.setUpdateBy(SecurityUtils.getUsername());
        eduAchievementService.updateEduAchievement(update);

        // 写入审核记录
        auditRecord.setAuditLevel("2");
        auditRecord.setAuditorId(SecurityUtils.getUserId());
        auditRecord.setAuditorName(SecurityUtils.getUsername());
        auditRecord.setCreateTime(DateUtils.getNowDate());
        eduAuditRecordService.insertEduAuditRecord(auditRecord);

        // 发送站内通知给成果提交者
        sendAuditNotice(achievement.getTeacherId(), achievement.getTitle(), resultText, auditRecord.getAuditOpinion());

        return success();
    }

    /**
     * 查询审核记录列表
     *
     * @param eduAuditRecord 查询条件（可按成果标题、审核级别、审核结果、审核人过滤）
     * @return 分页后的审核记录列表
     */
    @RequiresPermissions("achievement:audit:recordList")
    @GetMapping("/record/list")
    public TableDataInfo recordList(EduAuditRecord eduAuditRecord)
    {
        startPage();
        // 非管理员只能看到自己审核的记录
        if (!SecurityUtils.isAdmin(SecurityUtils.getUserId())) {
            eduAuditRecord.setAuditorId(SecurityUtils.getUserId());
        }
        List<EduAuditRecord> list = eduAuditRecordService.selectEduAuditRecordList(eduAuditRecord);
        return getDataTable(list);
    }

    /**
     * 导出审核记录列表
     *
     * @param response HTTP 响应对象
     * @param eduAuditRecord 查询条件
     */
    @RequiresPermissions("achievement:audit:export")
    @Log(title = "审核记录", businessType = BusinessType.EXPORT)
    @PostMapping("/record/export")
    public void export(HttpServletResponse response, EduAuditRecord eduAuditRecord)
    {
        List<EduAuditRecord> list = eduAuditRecordService.selectEduAuditRecordList(eduAuditRecord);
        ExcelUtil<EduAuditRecord> util = new ExcelUtil<EduAuditRecord>(EduAuditRecord.class);
        util.exportExcel(response, list, "审核记录数据");
    }

    /**
     * 首页统计接口 - 获取成果的全局统计数据
     *
     * <p>返回数据包括：
     * <ul>
     *   <li>各状态数量（草稿、院审中、校审中、已通过、已驳回）</li>
     *   <li>各学院成果数量分布</li>
     *   <li>各类型成果数量分布</li>
     * </ul></p>
     *
     * @return 统计数据 Map
     */
    @GetMapping("/statistics")
    public AjaxResult statistics(@RequestParam(value = "teacherId", required = false) Long teacherId)
    {
        // 如果传了 teacherId，则只统计该教师的成果
        List<Map<String, Object>> statusRows;
        List<Map<String, Object>> categoryRows;
        List<Map<String, Object>> collegeRows;
        int total;
        if (teacherId != null) {
            statusRows = eduAchievementService.countByStatusForTeacher(teacherId);
            categoryRows = eduAchievementService.countByCategoryForTeacher(teacherId);
            collegeRows = java.util.Collections.emptyList();
            total = eduAchievementService.countTotalForTeacher(teacherId);
        } else {
            statusRows = eduAchievementService.countByStatus();
            categoryRows = eduAchievementService.countByCategory();
            collegeRows = eduAchievementService.countByCollege();
            total = eduAchievementService.countTotal();
        }

        // 状态统计
        Map<String, Long> statusMap = new HashMap<>();
        statusMap.put("draft", 0L);
        statusMap.put("collegeAudit", 0L);
        statusMap.put("schoolAudit", 0L);
        statusMap.put("passed", 0L);
        statusMap.put("rejected", 0L);
        for (Map<String, Object> row : statusRows) {
            String k = String.valueOf(row.get("k"));
            long v = ((Number) row.get("v")).longValue();
            switch (k) {
                case "0": statusMap.put("draft", v); break;
                case "1": statusMap.put("collegeAudit", v); break;
                case "2": statusMap.put("schoolAudit", v); break;
                case "3": statusMap.put("passed", v); break;
                case "4": statusMap.put("rejected", v); break;
            }
        }

        // 类型统计：直接用 category code 作为 key，前端用 dict 转中文
        Map<String, Long> categoryMap = new HashMap<>();
        for (Map<String, Object> row : categoryRows) {
            String k = String.valueOf(row.get("k"));
            long v = ((Number) row.get("v")).longValue();
            categoryMap.put(k, v);
        }

        // 学院统计
        Map<String, Long> collegeMap = new HashMap<>();
        for (Map<String, Object> row : collegeRows) {
            String k = String.valueOf(row.get("k"));
            long v = ((Number) row.get("v")).longValue();
            collegeMap.put(k, v);
        }

        AjaxResult ajax = AjaxResult.success();
        ajax.put("total", total);
        ajax.put("statusData", statusMap);
        ajax.put("categoryData", categoryMap);
        ajax.put("collegeData", collegeMap);

        return ajax;
    }

    /**
     * 向成果提交者发送站内审核结果通知
     *
     * <p>通过直接插入 sys_notice 表实现站内通知。
     * 通知标题为"教学成果审核结果通知"，内容包含成果标题、审核结果和审核意见。</p>
     *
     * @param teacherId 成果提交者的用户ID
     * @param achievementTitle 成果标题
     * @param resultText 审核结果描述
     * @param opinion 审核意见
     */
    private void sendAuditNotice(Long teacherId, String achievementTitle, String resultText, String opinion)
    {
        try {
            String noticeTitle = "您的教学成果审核结果通知";
            String noticeContent = "您申报的成果「" + achievementTitle + "」" + resultText + "。"
                    + (opinion != null && !opinion.isEmpty() ? "\n审核意见：" + opinion : "");

            // 通过 MyBatis 直接插入 sys_notice 表
            // 为避免后续查询（LIKE '%createBy%'）时，userId=1 误匹配 10,11,12 等角色，这里加上前后缀标识符 '[-ID-]'
            String targetUser = "[-" + teacherId + "-]";
            eduAuditRecordService.insertSysNotice(noticeTitle, noticeContent, targetUser);
        } catch (Exception e) {
            // 通知发送失败不影响主流程
            logger.error("发送审核通知失败", e);
        }
    }
}
