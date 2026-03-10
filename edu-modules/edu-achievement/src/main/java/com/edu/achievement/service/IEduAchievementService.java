package com.edu.achievement.service;

import java.util.List;
import com.edu.achievement.domain.EduAchievement;

/**
 * 教学成果 Service 接口
 * 
 * @author edu
 */
public interface IEduAchievementService 
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
     * 查询成果详情（含审核记录）
     * 
     * @param achievementId 成果 ID
     * @return 教学成果
     */
    public EduAchievement selectAchievementDetailById(Long achievementId);

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
     * 批量删除教学成果
     * 
     * @param achievementIds 需要删除的成果 ID
     * @return 结果
     */
    public int deleteAchievementByIds(Long[] achievementIds);

    /**
     * 删除教学成果信息
     * 
     * @param achievementId 成果 ID
     * @return 结果
     */
    public int deleteAchievementById(Long achievementId);

    /**
     * 提交成果（教师操作）
     * 
     * @param achievementId 成果 ID
     * @param username 操作用户
     * @return 结果
     */
    public int submitAchievement(Long achievementId, String username);

    /**
     * 院级审核
     * 
     * @param achievementId 成果 ID
     * @param auditResult 审核结果 (0:通过 1:驳回)
     * @param auditOpinion 审核意见
     * @param username 操作用户
     * @return 结果
     */
    public int collegeAudit(Long achievementId, String auditResult, String auditOpinion, String username);

    /**
     * 校级审核
     * 
     * @param achievementId 成果 ID
     * @param auditResult 审核结果 (0:通过 1:驳回)
     * @param auditOpinion 审核意见
     * @param username 操作用户
     * @return 结果
     */
    public int schoolAudit(Long achievementId, String auditResult, String auditOpinion, String username);
}
