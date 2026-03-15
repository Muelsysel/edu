package com.edu.achievement.service;

import java.util.List;
import com.edu.achievement.domain.EduAuditRecord;

/**
 * 审核记录Service接口
 *
 * @author zpz
 * @date 2026-03-15
 */
public interface IEduAuditRecordService
{
    /**
     * 查询审核记录
     *
     * @param recordId 审核记录主键
     * @return 审核记录
     */
    public EduAuditRecord selectEduAuditRecordByRecordId(Long recordId);

    /**
     * 查询审核记录列表
     *
     * @param eduAuditRecord 审核记录
     * @return 审核记录集合
     */
    public List<EduAuditRecord> selectEduAuditRecordList(EduAuditRecord eduAuditRecord);

    /**
     * 新增审核记录
     *
     * @param eduAuditRecord 审核记录
     * @return 结果
     */
    public int insertEduAuditRecord(EduAuditRecord eduAuditRecord);

    /**
     * 向 sys_notice 表插入站内通知
     *
     * @param noticeTitle 通知标题
     * @param noticeContent 通知内容
     * @param targetUserId 目标用户ID
     */
    public void insertSysNotice(String noticeTitle, String noticeContent, String targetUserId);
}
