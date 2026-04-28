package com.edu.achievement.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.edu.achievement.domain.EduNews;
import com.edu.achievement.service.IEduNewsService;
import com.edu.common.core.utils.poi.ExcelUtil;
import com.edu.common.core.web.controller.BaseController;
import com.edu.common.core.web.domain.AjaxResult;
import com.edu.common.core.web.page.TableDataInfo;
import com.edu.common.log.annotation.Log;
import com.edu.common.log.enums.BusinessType;
import com.edu.common.security.annotation.RequiresPermissions;
import com.edu.common.security.utils.SecurityUtils;

/**
 * 新闻管理Controller
 *
 * @author zpz
 * @date 2026-04-23
 */
@RestController
@RequestMapping("/news")
public class EduNewsController extends BaseController
{
    @Autowired
    private IEduNewsService eduNewsService;

    //@RequiresPermissions("achievement:news:list")
    @GetMapping("/list")
    public TableDataInfo list(EduNews eduNews)
    {
        startPage();
        List<EduNews> list = eduNewsService.selectEduNewsList(eduNews);
        return getDataTable(list);
    }

    @RequiresPermissions("achievement:news:export")
    @Log(title = "新闻管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, EduNews eduNews)
    {
        List<EduNews> list = eduNewsService.selectEduNewsList(eduNews);
        ExcelUtil<EduNews> util = new ExcelUtil<EduNews>(EduNews.class);
        util.exportExcel(response, list, "新闻数据");
    }

    //@RequiresPermissions("achievement:news:query")
    @GetMapping(value = "/{newsId}")
    public AjaxResult getInfo(@PathVariable("newsId") Long newsId)
    {
        return success(eduNewsService.selectEduNewsByNewsId(newsId));
    }

    @RequiresPermissions("achievement:news:add")
    @Log(title = "新闻管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody EduNews eduNews)
    {
        if (!isValidNoticeType(eduNews.getNoticeType()))
        {
            return AjaxResult.error("请选择正确的新闻/通知分类");
        }
        eduNews.setCreateBy(SecurityUtils.getUsername());
        return toAjax(eduNewsService.insertEduNews(eduNews));
    }

    @RequiresPermissions("achievement:news:edit")
    @Log(title = "新闻管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody EduNews eduNews)
    {
        if (!isValidNoticeType(eduNews.getNoticeType()))
        {
            return AjaxResult.error("请选择正确的新闻/通知分类");
        }
        eduNews.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(eduNewsService.updateEduNews(eduNews));
    }

    @RequiresPermissions("achievement:news:remove")
    @Log(title = "新闻管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{newsIds}")
    public AjaxResult remove(@PathVariable Long[] newsIds)
    {
        return toAjax(eduNewsService.deleteEduNewsByNewsIds(newsIds));
    }

    private boolean isValidNoticeType(String noticeType)
    {
        return noticeType == null || "".equals(noticeType) || "1".equals(noticeType) || "2".equals(noticeType);
    }
}
