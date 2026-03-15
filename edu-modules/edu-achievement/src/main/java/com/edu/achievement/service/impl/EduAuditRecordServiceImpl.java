package com.edu.achievement.service.impl;

import java.util.List;
import com.edu.common.core.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.edu.achievement.mapper.EduAuditRecordMapper;
import com.edu.achievement.domain.EduAuditRecord;
import com.edu.achievement.service.IEduAuditRecordService;

/**
 * 审核记录Service业务层处理
 *
 * @author zpz
 * @date 2026-03-15
 */
@Service
public class EduAuditRecordServiceImpl implements IEduAuditRecordService
{
    @Autowired
    private EduAuditRecordMapper eduAuditRecordMapper;

    /**
     * 查询审核记录
     *
     * @param recordId 审核记录主键
     * @return 审核记录
     */
    @Override
    public EduAuditRecord selectEduAuditRecordByRecordId(Long recordId)
    {
        return eduAuditRecordMapper.selectEduAuditRecordByRecordId(recordId);
    }

    /**
     * 查询审核记录列表
     *
     * @param eduAuditRecord 审核记录
     * @return 审核记录
     */
    @Override
    public List<EduAuditRecord> selectEduAuditRecordList(EduAuditRecord eduAuditRecord)
    {
        return eduAuditRecordMapper.selectEduAuditRecordList(eduAuditRecord);
    }

    /**
     * 新增审核记录
     *
     * @param eduAuditRecord 审核记录
     * @return 结果
     */
    @Override
    public int insertEduAuditRecord(EduAuditRecord eduAuditRecord)
    {
        eduAuditRecord.setCreateTime(DateUtils.getNowDate());
        return eduAuditRecordMapper.insertEduAuditRecord(eduAuditRecord);
    }
}
