package com.edu.achievement.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.edu.achievement.domain.EduNews;
import com.edu.achievement.service.IEduNewsService;
import com.edu.common.core.web.controller.BaseController;
import com.edu.common.core.web.domain.AjaxResult;
import com.edu.common.core.web.page.TableDataInfo;

/**
 * Portal public news controller.
 */
@RestController
@RequestMapping("/portal/news")
public class PortalNewsController extends BaseController
{
    @Autowired
    private IEduNewsService eduNewsService;


    @GetMapping
    public TableDataInfo list(EduNews eduNews)
    {
        startPage();
        List<EduNews> list = eduNewsService.selectPortalNewsList(eduNews);
        return getDataTable(list);
    }

    @GetMapping("/{newsId}")
    public AjaxResult getInfo(@PathVariable Long newsId)
    {
        EduNews news = eduNewsService.selectPortalNewsByNewsId(newsId);
        if (news == null)
        {
            return AjaxResult.error("News does not exist or is not published");
        }
        eduNewsService.increaseViewCount(newsId);
        news.setViewCount((news.getViewCount() == null ? 0L : news.getViewCount()) + 1);
        return success(news);
    }
}
