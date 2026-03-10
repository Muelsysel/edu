package com.edu.achievement.mapper;

import java.util.List;
import com.edu.achievement.domain.EduAchievement;

/**
 * 教学成果 Mapper 接口
 * 
 * @author edu
 */

public interface EduAchievementMapper 
{
    /**
     * 查询教学成果
     * 
     * @param achievementId 成果 ID
     * @return 教学成果
     */
    public EduAchievement selectAchievementById(Long achievementId);

    /**
     * 查询教学成果列表
     * 
     * @param eduAchievement 教学成果
     * @return 教学成果集合
     */
    public List<EduAchievement> selectAchievementList(EduAchievement eduAchievement);

    /**
     * 新增教学成果
     * 
     * @param eduAchievement 教学成果
     * @return 结果
     */
    public int insertAchievement(EduAchievement eduAchievement);

    /**
     * 修改教学成果
     * 
     * @param eduAchievement 教学成果
     * @return 结果
     */
    public int updateAchievement(EduAchievement eduAchievement);

    /**
     * 删除教学成果
     * 
     * @param achievementId 成果 ID
     * @return 结果
     */
    public int deleteAchievementById(Long achievementId);

    /**
     * 批量删除教学成果
     * 
     * @param achievementIds 需要删除的数据 ID
     * @return 结果
     */
    public int deleteAchievementByIds(Long[] achievementIds);

    /**
     * 查询成果详情（含审核记录）
     * 
     * @param achievementId 成果 ID
     * @return 教学成果
     */
    public EduAchievement selectAchievementDetailById(Long achievementId);
}
