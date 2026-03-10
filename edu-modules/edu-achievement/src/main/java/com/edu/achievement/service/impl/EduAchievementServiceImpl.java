package com.edu.achievement.service.impl;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.edu.achievement.domain.EduAchievement;
import com.edu.achievement.domain.EduAchievementAuditLog;
import com.edu.achievement.mapper.EduAchievementMapper;
import com.edu.achievement.mapper.EduAchievementAuditLogMapper;
import com.edu.achievement.service.IEduAchievementService;
import com.edu.common.core.exception.ServiceException;
import com.edu.common.core.utils.DateUtils;
import com.edu.common.core.utils.StringUtils;
import com.edu.common.security.utils.SecurityUtils;
import java.util.Arrays;

/**
 * 教学成果 Service 业务层处理
 * 
 * @author edu
 */
@Service
public class EduAchievementServiceImpl implements IEduAchievementService {
    @Autowired
    private EduAchievementMapper eduAchievementMapper;

    @Autowired
    private EduAchievementAuditLogMapper eduAchievementAuditLogMapper;

    /**
     * 查询教学成果
     * 
     * @param achievementId 成果 ID
     * @return 教学成果
     */
    @Override
    public EduAchievement selectAchievementById(Long achievementId) {
        return eduAchievementMapper.selectAchievementById(achievementId);
    }

    /**
     * 查询教学成果列表（带数据权限控制）
     * 
     * @param eduAchievement 教学成果
     * @return 教学成果
     */
    @Override
    public List<EduAchievement> selectAchievementList(EduAchievement eduAchievement) {
        Long userId = SecurityUtils.getUserId();

        // 教师角色：只能查看本人的数据
        if (eduAchievement.getTeacherId() == null) {
            eduAchievement.setTeacherId(userId);
        }

        return eduAchievementMapper.selectAchievementList(eduAchievement);
    }

    /**
     * 查询成果详情（含审核记录）
     * 
     * @param achievementId 成果 ID
     * @return 教学成果
     */
    @Override
    public EduAchievement selectAchievementDetailById(Long achievementId) {
        return eduAchievementMapper.selectAchievementDetailById(achievementId);
    }

    /**
     * 新增教学成果
     * 
     * @param eduAchievement 教学成果
     * @return 结果
     */
    @Override
    @Transactional
    public int insertAchievement(EduAchievement eduAchievement) {
        eduAchievement.setCreateTime(DateUtils.getNowDate());
        return eduAchievementMapper.insertAchievement(eduAchievement);
    }

    /**
     * 修改教学成果
     * 
     * @param eduAchievement 教学成果
     * @return 结果
     */
    @Override
    @Transactional
    public int updateAchievement(EduAchievement eduAchievement) {
        eduAchievement.setUpdateTime(DateUtils.getNowDate());
        return eduAchievementMapper.updateAchievement(eduAchievement);
    }

    /**
     * 批量删除教学成果
     * 
     * @param achievementIds 需要删除的成果 ID
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteAchievementByIds(Long[] achievementIds) {
        return eduAchievementMapper.deleteAchievementByIds(achievementIds);
    }

    /**
     * 删除教学成果信息
     * 
     * @param achievementId 成果 ID
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteAchievementById(Long achievementId) {
        return eduAchievementMapper.deleteAchievementById(achievementId);
    }

    /**
     * 提交成果（教师操作）
     * 
     * @param achievementId 成果 ID
     * @param username      操作用户
     * @return 结果
     */
    @Override
    @Transactional
    public int submitAchievement(Long achievementId, String username) {
        EduAchievement achievement = eduAchievementMapper.selectAchievementById(achievementId);
        if (achievement == null) {
            throw new ServiceException("成果不存在");
        }

        // 验证权限：只能提交自己的成果
        Long currentUserId = SecurityUtils.getUserId();
        if (!achievement.getTeacherId().equals(currentUserId)) {
            throw new ServiceException("无权操作该成果");
        }

        // 验证状态：只有草稿或已驳回状态才能提交
        if (!StringUtils.equalsAny(achievement.getStatus(), "0", "4")) {
            throw new ServiceException("当前状态不能提交，只有草稿或已驳回状态才能提交");
        }

        // 更新状态为院级审核中
        String previousStatus = achievement.getStatus();
        achievement.setStatus("1");
        achievement.setUpdateTime(DateUtils.getNowDate());
        achievement.setUpdateBy(username);
        int result = eduAchievementMapper.updateAchievement(achievement);

        // 记录审核日志
        if (result > 0) {
            recordAuditLog(achievementId, currentUserId, "1", previousStatus, "1", "提交审核", username);
        }

        return result;
    }

    /**
     * 院级审核
     * 
     * @param achievementId 成果 ID
     * @param auditResult   审核结果 (0:通过 1:驳回)
     * @param auditOpinion  审核意见
     * @param username      操作用户
     * @return 结果
     */
    @Override
    @Transactional
    public int collegeAudit(Long achievementId, String auditResult, String auditOpinion, String username) {
        EduAchievement achievement = eduAchievementMapper.selectAchievementById(achievementId);
        if (achievement == null) {
            throw new ServiceException("成果不存在");
        }

        // 验证状态：只有院级审核中状态才能审核
        if (!"1".equals(achievement.getStatus())) {
            throw new ServiceException("当前状态不是院级审核中，无法审核");
        }

        String previousStatus = achievement.getStatus();
        String newStatus;

        if ("0".equals(auditResult)) {
            // 审核通过，状态变更为校级审核中
            newStatus = "2";
        } else if ("1".equals(auditResult)) {
            // 审核驳回，状态变更为已驳回
            newStatus = "4";
        } else {
            throw new ServiceException("审核结果参数错误");
        }

        achievement.setStatus(newStatus);
        achievement.setUpdateTime(DateUtils.getNowDate());
        achievement.setUpdateBy(username);
        int result = eduAchievementMapper.updateAchievement(achievement);

        // 记录审核日志
        if (result > 0) {
            Long auditorId = SecurityUtils.getUserId();
            recordAuditLog(achievementId, auditorId, auditResult, previousStatus, newStatus, auditOpinion, username);
        }

        return result;
    }

    /**
     * 校级审核
     * 
     * @param achievementId 成果 ID
     * @param auditResult   审核结果 (0:通过 1:驳回)
     * @param auditOpinion  审核意见
     * @param username      操作用户
     * @return 结果
     */
    @Override
    @Transactional
    public int schoolAudit(Long achievementId, String auditResult, String auditOpinion, String username) {
        EduAchievement achievement = eduAchievementMapper.selectAchievementById(achievementId);
        if (achievement == null) {
            throw new ServiceException("成果不存在");
        }

        // 验证状态：只有校级审核中状态才能审核
        if (!"2".equals(achievement.getStatus())) {
            throw new ServiceException("当前状态不是校级审核中，无法审核");
        }

        String previousStatus = achievement.getStatus();
        String newStatus;

        if ("0".equals(auditResult)) {
            // 审核通过，状态变更为已通过
            newStatus = "3";
        } else if ("1".equals(auditResult)) {
            // 审核驳回，状态变更为已驳回
            newStatus = "4";
        } else {
            throw new ServiceException("审核结果参数错误");
        }

        achievement.setStatus(newStatus);
        achievement.setUpdateTime(DateUtils.getNowDate());
        achievement.setUpdateBy(username);
        int result = eduAchievementMapper.updateAchievement(achievement);

        // 记录审核日志
        if (result > 0) {
            Long auditorId = SecurityUtils.getUserId();
            recordAuditLog(achievementId, auditorId, auditResult, previousStatus, newStatus, auditOpinion, username);
        }

        return result;
    }

    /**
     * 记录审核日志
     */
    private void recordAuditLog(Long achievementId, Long auditorId, String auditResult,
            String previousStatus, String currentStatus, String auditOpinion, String username) {
        EduAchievementAuditLog auditLog = new EduAchievementAuditLog();
        auditLog.setAchievementId(achievementId);
        auditLog.setAuditorId(auditorId);
        auditLog.setAuditTime(new Date());
        auditLog.setAuditResult(auditResult);
        auditLog.setPreviousStatus(previousStatus);
        auditLog.setCurrentStatus(currentStatus);
        auditLog.setAuditOpinion(auditOpinion);
        auditLog.setCreateBy(username);
        auditLog.setCreateTime(DateUtils.getNowDate());
        eduAchievementAuditLogMapper.insertAuditLog(auditLog);
    }
}
