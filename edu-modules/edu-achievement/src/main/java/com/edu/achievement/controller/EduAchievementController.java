package com.edu.achievement.controller;

import java.util.List;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

import com.edu.common.security.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
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
 * 教学成果管理Controller
 * 
 * @author zpz
 * @date 2026-03-03
 */
@RestController
@RequestMapping("/achievement")
public class EduAchievementController extends BaseController
{
    @Autowired
    private IEduAchievementService eduAchievementService;

    /**
     * 查询教学成果管理列表
     */
    @RequiresPermissions("achievement:achievement:list")
    @GetMapping("/list")
    public TableDataInfo list(EduAchievement eduAchievement)
    {
        startPage();
        List<EduAchievement> list = eduAchievementService.selectEduAchievementList(eduAchievement);
        return getDataTable(list);
    }

    /**
     * 导出教学成果管理列表
     */
    @RequiresPermissions("achievement:achievement:export")
    @Log(title = "教学成果管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, EduAchievement eduAchievement)
    {
        List<EduAchievement> list = eduAchievementService.selectEduAchievementList(eduAchievement);
        ExcelUtil<EduAchievement> util = new ExcelUtil<EduAchievement>(EduAchievement.class);
        util.exportExcel(response, list, "教学成果管理数据");
    }

    /**
     * 获取教学成果管理详细信息
     */
    @RequiresPermissions("achievement:achievement:query")
    @GetMapping(value = "/{achievementId}")
    public AjaxResult getInfo(@PathVariable("achievementId") Long achievementId)
    {
        return success(eduAchievementService.selectEduAchievementByAchievementId(achievementId));
    }

    /**
     * 新增教学成果管理
     */
    @RequiresPermissions("achievement:achievement:add")
    @Log(title = "教学成果管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody EduAchievement eduAchievement)
    {
        return toAjax(eduAchievementService.insertEduAchievement(eduAchievement));
    }

    /**
     * 修改教学成果
     */
    @PreAuthorize("@ss.hasPermi('achievement:achievement:edit')")
    @PutMapping
    public AjaxResult edit(@RequestBody EduAchievement eduAchievement)
    {
        // 1. 先从数据库查出旧数据
        EduAchievement oldData = eduAchievementService.selectEduAchievementByAchievementId(eduAchievement.getAchievementId());

        // 安全校验：非管理员需要进行权限和状态检查
        if (!SecurityUtils.isAdmin(SecurityUtils.getUserId())) {
            // 权限检查：是否是本人
            if (!oldData.getTeacherId().equals(SecurityUtils.getUserId())) {
                return AjaxResult.error("你无权修改他人的成果");
            }
            // 状态检查：已通过(3)禁止修改
            if ("3".equals(oldData.getStatus())) {
                return AjaxResult.error("该成果已通过审核，不可修改");
            }
            // 教师修改后，强制重置为“待审核(1)”
            eduAchievement.setStatus("1");
        } else {
            // 如果是管理员修改，保留原状态，除非前端传了新状态
            if (eduAchievement.getStatus() == null) {
                eduAchievement.setStatus(oldData.getStatus());
            }
        }

        eduAchievement.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(eduAchievementService.updateEduAchievement(eduAchievement));
    }

    /**
     * 删除教学成果管理
     */
    @RequiresPermissions("achievement:achievement:remove")
    @Log(title = "教学成果管理", businessType = BusinessType.DELETE)
	@DeleteMapping("/{achievementIds}")
    public AjaxResult remove(@PathVariable Long[] achievementIds)
    {
        return toAjax(eduAchievementService.deleteEduAchievementByAchievementIds(achievementIds));
    }
    /**
     * 教师端：提交教学成果申报
     */
    @PreAuthorize("@ss.hasPermi('achievement:achievement:add')")
    @PostMapping("/myAdd")
    public AjaxResult myAdd(@RequestBody EduAchievement eduAchievement)
    {
        // 1. 自动获取当前登录用户的 ID (核心安全保障)
        Long userId = SecurityUtils.getUserId();
        eduAchievement.setTeacherId(userId); // 对应你数据库的 teacher_id

        // 1 为“待审核”，0 为“草稿”。
        eduAchievement.setStatus("1");

        // 4. 设置基础审计字段
        eduAchievement.setCreateBy(SecurityUtils.getUsername());

        return toAjax(eduAchievementService.insertEduAchievement(eduAchievement));
    }
    /**
     * 查询个人成果列表（教师端）
     */
    @PreAuthorize("@ss.hasPermi('achievement:achievement:myList')")
    @GetMapping("/myList")
    public TableDataInfo myList(EduAchievement eduAchievement)
    {
        startPage();
        // 关键：强制设置教师ID为当前登录用户ID，实现数据隔离
        eduAchievement.setTeacherId(SecurityUtils.getUserId());

        // 调用 service 查询
        List<EduAchievement> list = eduAchievementService.selectEduAchievementList(eduAchievement);
        return getDataTable(list);
    }


}
