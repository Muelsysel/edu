package com.edu.achievement.service;

import java.util.List;
import com.edu.achievement.domain.EduNews;

/**
 * 门户新闻Service接口
 *
 * @author zpz
 * @date 2026-04-23
 */
public interface IEduNewsService
{
    public EduNews selectEduNewsByNewsId(Long newsId);

    public List<EduNews> selectEduNewsList(EduNews eduNews);

    public int insertEduNews(EduNews eduNews);

    public int updateEduNews(EduNews eduNews);

    public int deleteEduNewsByNewsIds(Long[] newsIds);

    public int deleteEduNewsByNewsId(Long newsId);

    public List<EduNews> selectPortalNewsList(EduNews eduNews);

    public EduNews selectPortalNewsByNewsId(Long newsId);

    public int increaseViewCount(Long newsId);
}
