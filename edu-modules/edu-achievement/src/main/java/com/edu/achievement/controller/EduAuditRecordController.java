package com.edu.achievement.controller;

import java.util.List;
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
     * 自动按当前审核人所在学院过滤，只返回 status=1（院级审核中）的成果
     */
    @RequiresPermissions("achievement:audit:collegeList")
    @GetMapping("/college/list")
    public TableDataInfo collegeList(EduAchievement eduAchievement)
    {
        startPage();
        // 强制过滤状态为"院级审核中"
        eduAchievement.setStatus("1");
        // 按当前用户所在学院过滤（院级审核员只能审核本院的成果）
        Long deptId = SecurityUtils.getLoginUser().getSysUser().getDeptId();
        eduAchievement.setCollegeId(deptId);
        List<EduAchievement> list = eduAchievementService.selectEduAchievementList(eduAchievement);
        return getDataTable(list);
    }

    /**
     * 校级审核 - 获取待审核列表
     * 返回 status=2（校级审核中）的所有成果
     */
    @RequiresPermissions("achievement:audit:schoolList")
    @GetMapping("/school/list")
    public TableDataInfo schoolList(EduAchievement eduAchievement)
    {
        startPage();
        // 强制过滤状态为"校级审核中"
        eduAchievement.setStatus("2");
        List<EduAchievement> list = eduAchievementService.selectEduAchievementList(eduAchievement);
        return getDataTable(list);
    }

    /**
     * 获取成果详细信息（审核时查看）
     */
    @RequiresPermissions("achievement:audit:query")
    @GetMapping("/detail/{achievementId}")
    public AjaxResult getDetail(@PathVariable("achievementId") Long achievementId)
    {
        return success(eduAchievementService.selectEduAchievementByAchievementId(achievementId));
    }

    /**
     * 院级审核操作
     * 通过 -> 成果 status 改为 2（进入校级审核）
     * 驳回 -> 成果 status 改为 4（已驳回）
     */
    @RequiresPermissions("achievement:audit:collegeHandle")
    @Log(title = "院级审核", businessType = BusinessType.UPDATE)
    @PostMapping("/college/handle")
    public AjaxResult collegeHandle(@RequestBody EduAuditRecord auditRecord)
    {
        // 1. 校验成果当前状态是否为"院级审核中(1)"
        EduAchievement achievement = eduAchievementService.selectEduAchievementByAchievementId(auditRecord.getAchievementId());
        if (achievement == null) {
            return AjaxResult.error("成果不存在");
        }
        if (!"1".equals(achievement.getStatus())) {
            return AjaxResult.error("该成果当前状态不允许院级审核操作");
        }

        // 2. 更新成果状态
        EduAchievement update = new EduAchievement();
        update.setAchievementId(auditRecord.getAchievementId());
        if ("1".equals(auditRecord.getAuditResult())) {
            // 通过 -> 进入校级审核
            update.setStatus("2");
        } else {
            // 驳回
            update.setStatus("4");
        }
        update.setUpdateBy(SecurityUtils.getUsername());
        eduAchievementService.updateEduAchievement(update);

        // 3. 写入审核记录
        auditRecord.setAuditLevel("1"); // 院级审核
        auditRecord.setAuditorId(SecurityUtils.getUserId());
        auditRecord.setAuditorName(SecurityUtils.getUsername());
        auditRecord.setCreateTime(DateUtils.getNowDate());
        eduAuditRecordService.insertEduAuditRecord(auditRecord);

        return success();
    }

    /**
     * 校级审核操作
     * 通过 -> 成果 status 改为 3（已通过）
     * 驳回 -> 成果 status 改为 4（已驳回）
     */
    @RequiresPermissions("achievement:audit:schoolHandle")
    @Log(title = "校级审核", businessType = BusinessType.UPDATE)
    @PostMapping("/school/handle")
    public AjaxResult schoolHandle(@RequestBody EduAuditRecord auditRecord)
    {
        // 1. 校验成果当前状态是否为"校级审核中(2)"
        EduAchievement achievement = eduAchievementService.selectEduAchievementByAchievementId(auditRecord.getAchievementId());
        if (achievement == null) {
            return AjaxResult.error("成果不存在");
        }
        if (!"2".equals(achievement.getStatus())) {
            return AjaxResult.error("该成果当前状态不允许校级审核操作");
        }

        // 2. 更新成果状态
        EduAchievement update = new EduAchievement();
        update.setAchievementId(auditRecord.getAchievementId());
        if ("1".equals(auditRecord.getAuditResult())) {
            // 通过
            update.setStatus("3");
        } else {
            // 驳回
            update.setStatus("4");
        }
        update.setUpdateBy(SecurityUtils.getUsername());
        eduAchievementService.updateEduAchievement(update);

        // 3. 写入审核记录
        auditRecord.setAuditLevel("2"); // 校级审核
        auditRecord.setAuditorId(SecurityUtils.getUserId());
        auditRecord.setAuditorName(SecurityUtils.getUsername());
        auditRecord.setCreateTime(DateUtils.getNowDate());
        eduAuditRecordService.insertEduAuditRecord(auditRecord);

        return success();
    }

    /**
     * 查询审核记录列表
     */
    @RequiresPermissions("achievement:audit:recordList")
    @GetMapping("/record/list")
    public TableDataInfo recordList(EduAuditRecord eduAuditRecord)
    {
        startPage();
        List<EduAuditRecord> list = eduAuditRecordService.selectEduAuditRecordList(eduAuditRecord);
        return getDataTable(list);
    }

    /**
     * 导出审核记录列表
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
}
