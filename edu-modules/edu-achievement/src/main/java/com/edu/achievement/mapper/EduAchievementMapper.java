package com.edu.achievement.mapper;

import java.util.List;
import com.edu.achievement.domain.EduAchievement;

/**
 * 教学成果管理Mapper接口
 * 
 * @author zpz
 * @date 2026-03-03
 */
public interface EduAchievementMapper 
{
    /**
     * 查询教学成果管理
     * 
     * @param achievementId 教学成果管理主键
     * @return 教学成果管理
     */
    public EduAchievement selectEduAchievementByAchievementId(Long achievementId);

    /**
     * 查询教学成果管理列表
     * 
     * @param eduAchievement 教学成果管理
     * @return 教学成果管理集合
     */
    public List<EduAchievement> selectEduAchievementList(EduAchievement eduAchievement);

    /**
     * 新增教学成果管理
     * 
     * @param eduAchievement 教学成果管理
     * @return 结果
     */
    public int insertEduAchievement(EduAchievement eduAchievement);

    /**
     * 修改教学成果管理
     * 
     * @param eduAchievement 教学成果管理
     * @return 结果
     */
    public int updateEduAchievement(EduAchievement eduAchievement);

    /**
     * 删除教学成果管理
     * 
     * @param achievementId 教学成果管理主键
     * @return 结果
     */
    public int deleteEduAchievementByAchievementId(Long achievementId);

    /**
     * 批量删除教学成果管理
     * 
     * @param achievementIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteEduAchievementByAchievementIds(Long[] achievementIds);
}
