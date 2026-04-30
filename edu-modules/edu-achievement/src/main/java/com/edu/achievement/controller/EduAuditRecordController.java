package com.edu.achievement.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.edu.achievement.domain.EduAchievement;
import com.edu.achievement.domain.EduAuditRecord;
import com.edu.achievement.service.IEduAchievementService;
import com.edu.achievement.service.IEduAuditRecordService;
import com.edu.common.core.utils.DateUtils;
import com.edu.common.core.utils.poi.ExcelUtil;
import com.edu.common.core.web.controller.BaseController;
import com.edu.common.core.web.domain.AjaxResult;
import com.edu.common.core.web.page.TableDataInfo;
import com.edu.common.log.annotation.Log;
import com.edu.common.log.enums.BusinessType;
import com.edu.common.security.annotation.RequiresPermissions;
import com.edu.common.security.utils.SecurityUtils;

@RestController
@RequestMapping("/audit")
public class EduAuditRecordController extends BaseController
{
    @Autowired
    private IEduAchievementService eduAchievementService;

    @Autowired
    private IEduAuditRecordService eduAuditRecordService;

    @RequiresPermissions("achievement:audit:school")
    @GetMapping("/school/list")
    public TableDataInfo schoolList(EduAchievement eduAchievement)
    {
        startPage();
        eduAchievement.setStatus("2");
        List<EduAchievement> list = eduAchievementService.selectEduAchievementList(eduAchievement);
        return getDataTable(list);
    }

    @RequiresPermissions("achievement:audit:query")
    @GetMapping("/detail/{achievementId}")
    public AjaxResult getDetail(@PathVariable("achievementId") Long achievementId)
    {
        return success(eduAchievementService.selectEduAchievementByAchievementId(achievementId));
    }

    @RequiresPermissions("achievement:audit:school")
    @Log(title = "School Audit", businessType = BusinessType.UPDATE)
    @PostMapping("/school/handle")
    public AjaxResult schoolHandle(@RequestBody EduAuditRecord auditRecord)
    {
        if (auditRecord == null
            || (!"1".equals(auditRecord.getAuditResult()) && !"2".equals(auditRecord.getAuditResult())))
        {
            return AjaxResult.error("Invalid audit result");
        }

        EduAchievement achievement = eduAchievementService.selectEduAchievementByAchievementId(auditRecord.getAchievementId());
        if (achievement == null)
        {
            return AjaxResult.error("Achievement does not exist");
        }

        if (!"2".equals(achievement.getStatus()))
        {
            return AjaxResult.error("Current status does not allow school audit");
        }

        EduAchievement update = new EduAchievement();
        update.setAchievementId(auditRecord.getAchievementId());

        String resultText;
        if ("1".equals(auditRecord.getAuditResult()))
        {
            update.setStatus("3");
            resultText = "has passed the school audit";
        }
        else
        {
            update.setStatus("4");
            resultText = "did not pass the school audit";
        }

        update.setUpdateBy(SecurityUtils.getUsername());
        eduAchievementService.updateEduAchievement(update);

        auditRecord.setAuditLevel("2");
        auditRecord.setAuditorId(SecurityUtils.getUserId());
        auditRecord.setAuditorName(SecurityUtils.getUsername());
        auditRecord.setCreateTime(DateUtils.getNowDate());
        eduAuditRecordService.insertEduAuditRecord(auditRecord);

        // 消息通知功能已取消
        // sendAuditNotice(
        //     achievement.getTeacherId(),
        //     achievement.getTitle(),
        //     resultText,
        //     auditRecord.getAuditOpinion()
        // );

        return success();
    }

    @RequiresPermissions("achievement:audit:recordList")
    @GetMapping("/record/list")
    public TableDataInfo recordList(EduAuditRecord eduAuditRecord)
    {
        startPage();
        if (!SecurityUtils.isAdmin(SecurityUtils.getUserId()))
        {
            if (eduAuditRecord.getAchievementId() == null)
            {
                eduAuditRecord.setAuditorId(SecurityUtils.getUserId());
            }
        }
        List<EduAuditRecord> list = eduAuditRecordService.selectEduAuditRecordList(eduAuditRecord);
        return getDataTable(list);
    }

    @RequiresPermissions("achievement:audit:export")
    @Log(title = "Audit Record", businessType = BusinessType.EXPORT)
    @PostMapping("/record/export")
    public void export(HttpServletResponse response, EduAuditRecord eduAuditRecord)
    {
        List<EduAuditRecord> list = eduAuditRecordService.selectEduAuditRecordList(eduAuditRecord);
        ExcelUtil<EduAuditRecord> util = new ExcelUtil<>(EduAuditRecord.class);
        util.exportExcel(response, list, "audit_record");
    }

    @GetMapping("/statistics")
    public AjaxResult statistics(@RequestParam(value = "teacherId", required = false) Long teacherId)
    {
        List<Map<String, Object>> statusRows;
        List<Map<String, Object>> categoryRows;
        List<Map<String, Object>> collegeRows;
        int total;

        if (teacherId != null)
        {
            statusRows = eduAchievementService.countByStatusForTeacher(teacherId);
            categoryRows = eduAchievementService.countByCategoryForTeacher(teacherId);
            collegeRows = Collections.emptyList();
            total = eduAchievementService.countTotalForTeacher(teacherId);
        }
        else
        {
            statusRows = eduAchievementService.countByStatus();
            categoryRows = eduAchievementService.countByCategory();
            collegeRows = eduAchievementService.countByCollege();
            total = eduAchievementService.countTotal();
        }

        Map<String, Long> statusMap = new HashMap<>();
        statusMap.put("draft", 0L);
        statusMap.put("schoolAudit", 0L);
        statusMap.put("passed", 0L);
        statusMap.put("rejected", 0L);
        for (Map<String, Object> row : statusRows)
        {
            String key = String.valueOf(row.get("k"));
            long value = ((Number) row.get("v")).longValue();
            switch (key)
            {
                case "0":
                    statusMap.put("draft", value);
                    break;
                case "2":
                    statusMap.put("schoolAudit", value);
                    break;
                case "3":
                    statusMap.put("passed", value);
                    break;
                case "4":
                    statusMap.put("rejected", value);
                    break;
                default:
                    break;
            }
        }

        Map<String, Long> categoryMap = new HashMap<>();
        for (Map<String, Object> row : categoryRows)
        {
            String key = String.valueOf(row.get("k"));
            long value = ((Number) row.get("v")).longValue();
            categoryMap.put(key, value);
        }

        Map<String, Long> collegeMap = new HashMap<>();
        for (Map<String, Object> row : collegeRows)
        {
            String key = String.valueOf(row.get("k"));
            long value = ((Number) row.get("v")).longValue();
            collegeMap.put(key, value);
        }

        AjaxResult ajax = AjaxResult.success();
        ajax.put("total", total);
        ajax.put("totalCount", eduAchievementService.countTotalAuditRecords());
        ajax.put("totalPending", eduAchievementService.countTotalPending());
        ajax.put("todayNew", eduAchievementService.countTodayNew());
        ajax.put("weekNew", eduAchievementService.countWeekNew());
        ajax.put("monthPassed", eduAchievementService.countMonthPassed());
        ajax.put("monthRejected", eduAchievementService.countMonthRejected());
        ajax.put("statusData", statusMap);
        ajax.put("categoryData", categoryMap);
        ajax.put("collegeData", collegeMap);
        return ajax;
    }

    private void sendAuditNotice(Long teacherId, String achievementTitle, String resultText, String opinion)
    {
        try
        {
            String noticeTitle = "Achievement audit result";
            String noticeContent = "Your achievement [" + achievementTitle + "] " + resultText
                + (opinion != null && !opinion.isEmpty() ? "\nOpinion: " + opinion : "");

            String targetUser = "[-" + teacherId + "-]";
            eduAuditRecordService.insertSysNotice(noticeTitle, noticeContent, targetUser);
        }
        catch (Exception e)
        {
            logger.error("Failed to send audit notice", e);
        }
    }
}
