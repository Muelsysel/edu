package com.edu.achievement.service.impl;

import java.util.List;
import com.edu.common.core.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.edu.achievement.mapper.EduAchievementMapper;
import com.edu.achievement.domain.EduAchievement;
import com.edu.achievement.service.IEduAchievementService;

/**
 * 教学成果管理Service业务层处理
 * 
 * @author zpz
 * @date 2026-03-03
 */
@Service
public class EduAchievementServiceImpl implements IEduAchievementService 
{
    @Autowired
    private EduAchievementMapper eduAchievementMapper;

    /**
     * 查询教学成果管理
     * 
     * @param achievementId 教学成果管理主键
     * @return 教学成果管理
     */
    @Override
    public EduAchievement selectEduAchievementByAchievementId(Long achievementId)
    {
        return eduAchievementMapper.selectEduAchievementByAchievementId(achievementId);
    }

    /**
     * 查询教学成果管理列表
     * 
     * @param eduAchievement 教学成果管理
     * @return 教学成果管理
     */
    @Override
    public List<EduAchievement> selectEduAchievementList(EduAchievement eduAchievement)
    {
        return eduAchievementMapper.selectEduAchievementList(eduAchievement);
    }

    /**
     * 新增教学成果管理
     * 
     * @param eduAchievement 教学成果管理
     * @return 结果
     */
    @Override
    public int insertEduAchievement(EduAchievement eduAchievement)
    {
        eduAchievement.setCreateTime(DateUtils.getNowDate());
        return eduAchievementMapper.insertEduAchievement(eduAchievement);
    }

    /**
     * 修改教学成果管理
     * 
     * @param eduAchievement 教学成果管理
     * @return 结果
     */
    @Override
    public int updateEduAchievement(EduAchievement eduAchievement)
    {
        eduAchievement.setUpdateTime(DateUtils.getNowDate());
        return eduAchievementMapper.updateEduAchievement(eduAchievement);
    }

    /**
     * 批量删除教学成果管理
     * 
     * @param achievementIds 需要删除的教学成果管理主键
     * @return 结果
     */
    @Override
    public int deleteEduAchievementByAchievementIds(Long[] achievementIds)
    {
        return eduAchievementMapper.deleteEduAchievementByAchievementIds(achievementIds);
    }

    /**
     * 删除教学成果管理信息
     * 
     * @param achievementId 教学成果管理主键
     * @return 结果
     */
    @Override
    public int deleteEduAchievementByAchievementId(Long achievementId)
    {
        return eduAchievementMapper.deleteEduAchievementByAchievementId(achievementId);
    }
}
