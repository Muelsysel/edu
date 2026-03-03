package com.edu.achievement.mapper;

import java.util.List;
import com.edu.achievement.domain.EduAchievementAuditLog;

/**
 * 教学成果审核记录Mapper接口
 * 
 * @author zpz
 * @date 2026-03-03
 */
public interface EduAchievementAuditLogMapper 
{
    /**
     * 查询教学成果审核记录
     * 
     * @param id 教学成果审核记录主键
     * @return 教学成果审核记录
     */
    public EduAchievementAuditLog selectEduAchievementAuditLogById(Long id);

    /**
     * 查询教学成果审核记录列表
     * 
     * @param eduAchievementAuditLog 教学成果审核记录
     * @return 教学成果审核记录集合
     */
    public List<EduAchievementAuditLog> selectEduAchievementAuditLogList(EduAchievementAuditLog eduAchievementAuditLog);

    /**
     * 新增教学成果审核记录
     * 
     * @param eduAchievementAuditLog 教学成果审核记录
     * @return 结果
     */
    public int insertEduAchievementAuditLog(EduAchievementAuditLog eduAchievementAuditLog);

    /**
     * 修改教学成果审核记录
     * 
     * @param eduAchievementAuditLog 教学成果审核记录
     * @return 结果
     */
    public int updateEduAchievementAuditLog(EduAchievementAuditLog eduAchievementAuditLog);

    /**
     * 删除教学成果审核记录
     * 
     * @param id 教学成果审核记录主键
     * @return 结果
     */
    public int deleteEduAchievementAuditLogById(Long id);

    /**
     * 批量删除教学成果审核记录
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteEduAchievementAuditLogByIds(Long[] ids);
}
