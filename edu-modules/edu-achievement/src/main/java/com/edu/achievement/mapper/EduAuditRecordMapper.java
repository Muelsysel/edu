package com.edu.achievement.mapper;

import java.util.List;
import com.edu.achievement.domain.EduAuditRecord;

/**
 * 审核记录Mapper接口
 *
 * @author zpz
 * @date 2026-03-15
 */
public interface EduAuditRecordMapper
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
     * 插入站内通知
     */
    public void insertSysNotice(@org.apache.ibatis.annotations.Param("noticeTitle") String noticeTitle,
                                @org.apache.ibatis.annotations.Param("noticeContent") String noticeContent,
                                @org.apache.ibatis.annotations.Param("createBy") String createBy);
}
