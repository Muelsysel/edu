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
     * 修改教学成果管理
     */
    @RequiresPermissions("achievement:achievement:edit")
    @Log(title = "教学成果管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody EduAchievement eduAchievement)
    {
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
     * 教师端：新增教学成果
     */
    @PreAuthorize("@ss.hasPermi('achievement:achievement:myAdd')")
    @Log(title = "上传成果", businessType = BusinessType.INSERT)
    @PostMapping("/myAdd") // 匹配前端的 /myAdd
    public AjaxResult myAdd(@RequestBody EduAchievement eduAchievement)
    {
        eduAchievement.setTeacherId(SecurityUtils.getUserId());
        eduAchievement.setStatus("0");
        eduAchievement.setCreateBy(SecurityUtils.getUsername());
        return toAjax(eduAchievementService.insertEduAchievement(eduAchievement));
    }


    /**
     * 查询当前教师的成果列表
     */
    @PreAuthorize("@ss.hasPermi('achievement:achievement:add')")
    @GetMapping("/myList")
    public TableDataInfo myList(EduAchievement eduAchievement) {
        startPage();
        // 强制过滤：只看当前登录人的数据
        eduAchievement.setTeacherId(SecurityUtils.getUserId());
        List<EduAchievement> list = eduAchievementService.selectEduAchievementList(eduAchievement);
        return getDataTable(list);
    }



}
