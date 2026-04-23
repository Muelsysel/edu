package com.edu.achievement.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.edu.achievement.domain.EduNews;
import com.edu.achievement.mapper.EduNewsMapper;
import com.edu.achievement.service.IEduNewsService;
import com.edu.common.core.utils.DateUtils;

/**
 * 门户新闻Service业务层处理
 *
 * @author zpz
 * @date 2026-04-23
 */
@Service
public class EduNewsServiceImpl implements IEduNewsService
{
    @Autowired
    private EduNewsMapper eduNewsMapper;

    @Override
    public EduNews selectEduNewsByNewsId(Long newsId)
    {
        return eduNewsMapper.selectEduNewsByNewsId(newsId);
    }

    @Override
    public List<EduNews> selectEduNewsList(EduNews eduNews)
    {
        return eduNewsMapper.selectEduNewsList(eduNews);
    }

    @Override
    public int insertEduNews(EduNews eduNews)
    {
        eduNews.setCreateTime(DateUtils.getNowDate());
        if (eduNews.getViewCount() == null)
        {
            eduNews.setViewCount(0L);
        }
        if (eduNews.getPublishPortal() == null)
        {
            eduNews.setPublishPortal("0");
        }
        if (eduNews.getStatus() == null)
        {
            eduNews.setStatus("0");
        }
        if (eduNews.getSortWeight() == null)
        {
            eduNews.setSortWeight(0);
        }
        return eduNewsMapper.insertEduNews(eduNews);
    }

    @Override
    public int updateEduNews(EduNews eduNews)
    {
        eduNews.setUpdateTime(DateUtils.getNowDate());
        return eduNewsMapper.updateEduNews(eduNews);
    }

    @Override
    public int deleteEduNewsByNewsIds(Long[] newsIds)
    {
        return eduNewsMapper.deleteEduNewsByNewsIds(newsIds);
    }

    @Override
    public int deleteEduNewsByNewsId(Long newsId)
    {
        return eduNewsMapper.deleteEduNewsByNewsId(newsId);
    }

    @Override
    public List<EduNews> selectPortalNewsList(EduNews eduNews)
    {
        return eduNewsMapper.selectPortalNewsList(eduNews);
    }

    @Override
    public EduNews selectPortalNewsByNewsId(Long newsId)
    {
        return eduNewsMapper.selectPortalNewsByNewsId(newsId);
    }

    @Override
    public int increaseViewCount(Long newsId)
    {
        return eduNewsMapper.increaseViewCount(newsId);
    }
}
