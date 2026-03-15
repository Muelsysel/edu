package com.edu.achievement.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.edu.common.security.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
        eduAchievement.setTeacherId(SecurityUtils.getUserId());
        eduAchievement.setCreateBy(SecurityUtils.getUsername());
        return toAjax(eduAchievementService.insertEduAchievement(eduAchievement));
    }

    /**
     * 修改教学成果
     */
    @RequiresPermissions("achievement:achievement:edit")
    @Log(title = "教学成果管理", businessType = BusinessType.UPDATE)
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
            // 教师修改后，强制重置为"待审核(1)"
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
     * 删除教学成果
     */
    @RequiresPermissions("achievement:achievement:remove")
    @Log(title = "教学成果管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{achievementIds}")
    public AjaxResult remove(@PathVariable Long[] achievementIds){
        return toAjax(eduAchievementService.deleteEduAchievementByAchievementIds(achievementIds));
    }


    // ==================== 教师功能 ====================

    /**
     * 教师查询成果列表
     */
    @RequiresPermissions("achievement:achievement:teacherQuery")
    @GetMapping("/teacherListAchievement")
    public TableDataInfo teacherList(EduAchievement eduAchievement) {
        startPage();
        // 【核心】：强制将当前登录用户的ID注入查询条件，确保教师只能查出自己的数据
        eduAchievement.setTeacherId(SecurityUtils.getUserId());
        List<EduAchievement> list = eduAchievementService.selectEduAchievementList(eduAchievement);
        return getDataTable(list);
    }

    /**
     * 教师新增成果
     */
    @RequiresPermissions("achievement:achievement:teacherAdd")
    @Log(title = "我的成果-新增", businessType = BusinessType.INSERT)
    @PostMapping("/teacherAddAchievement")
    public AjaxResult teacherAdd(@RequestBody EduAchievement eduAchievement) {
        // 强制绑定当前登录用户的ID，作为该成果的所有者
        eduAchievement.setTeacherId(SecurityUtils.getUserId());
        eduAchievement.setCreateBy(SecurityUtils.getUsername());
        return toAjax(eduAchievementService.insertEduAchievement(eduAchievement));
    }

    /**
     * 教师修改成果
     */
    @RequiresPermissions("achievement:achievement:teacherEdit")
    @Log(title = "我的成果-修改", businessType = BusinessType.UPDATE)
    @PutMapping("/teacherUpdateAchievement")
    public AjaxResult teacherEdit(@RequestBody EduAchievement eduAchievement) {
        // 1. 先从数据库查出旧数据
        EduAchievement oldData = eduAchievementService.selectEduAchievementByAchievementId(eduAchievement.getAchievementId());
        if (oldData == null) {
            return AjaxResult.error("成果不存在");
        }
        // 2. 权限校验：仅允许修改自己的成果
        if (!oldData.getTeacherId().equals(SecurityUtils.getUserId())) {
            return AjaxResult.error("你无权修改他人的成果");
        }
        // 3. 状态检查：已通过(3)或审核中(1,2)不可修改
        if ("3".equals(oldData.getStatus())) {
            return AjaxResult.error("该成果已通过审核，不可修改");
        }

        // 4. 强制将当前登录用户的ID注入，确保教师只能修改自己的数据
        eduAchievement.setTeacherId(SecurityUtils.getUserId());
        eduAchievement.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(eduAchievementService.updateEduAchievement(eduAchievement));
    }

    /**
     * 教师删除成果
     */
    @RequiresPermissions("achievement:achievement:teacherRemove")
    @Log(title = "我的成果-删除", businessType = BusinessType.DELETE)
    @DeleteMapping("/teacherDelAchievement/{achievementIds}")
    public AjaxResult teacherRemove(@PathVariable Long[] achievementIds) {
        return toAjax(eduAchievementService.deleteEduAchievementByAchievementIds(achievementIds));
    }

    /**
     * 教师获取单个成果信息
     */
    @RequiresPermissions("achievement:achievement:teacherQuery")
    @GetMapping("/teacherGetAchievement/{achievementId}")
    public AjaxResult teacherGet(@PathVariable Long achievementId) {
        return success(eduAchievementService.selectEduAchievementByAchievementId(achievementId));
    }

    /**
     * 教师端：获取该教师的所有成果并统计各状态数量
     */
    @RequiresPermissions("achievement:achievement:teacherQuery")
    @GetMapping("/teacherListAllAchievement")
    public AjaxResult teacherAllList(EduAchievement eduAchievement)
    {
        // 1. 核心防越权：强制将查询条件设置为当前登录的账号ID
        eduAchievement.setTeacherId(SecurityUtils.getUserId());

        // 2. 不分页，查询该教师的全量数据
        List<EduAchievement> list = eduAchievementService.selectEduAchievementList(eduAchievement);

        // 3. 在内存中进行状态统计
        long total = list.size();
        long passCount = 0;
        long auditCount = 0;
        long rejectCount = 0;

        for (EduAchievement item : list) {
            String status = item.getStatus();
            if ("3".equals(status)) {
                passCount++;
            } else if ("1".equals(status) || "2".equals(status)) {
                auditCount++;
            } else if ("4".equals(status)) {
                rejectCount++;
            }
        }

        // 4. 使用 AjaxResult 封装自定义字段返回给前端
        AjaxResult ajax = AjaxResult.success();
        ajax.put("total", total);
        ajax.put("pass", passCount);
        ajax.put("audit", auditCount);
        ajax.put("reject", rejectCount);

        return ajax;
    }
}
