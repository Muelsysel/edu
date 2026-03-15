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
 * <p>负责审核记录的查询、新增，以及审核结果站内通知的发送。</p>
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
     * 根据主键查询单条审核记录
     *
     * @param recordId 审核记录主键ID
     * @return 审核记录详情（包含关联的成果标题、类型等信息）
     */
    @Override
    public EduAuditRecord selectEduAuditRecordByRecordId(Long recordId)
    {
        return eduAuditRecordMapper.selectEduAuditRecordByRecordId(recordId);
    }

    /**
     * 按条件查询审核记录列表
     *
     * <p>支持的过滤条件包括：成果标题（模糊匹配）、审核级别、审核结果、审核人姓名。
     * 通过 MyBatis XML 中的 LEFT JOIN 关联 edu_achievement 表获取成果标题和类型。</p>
     *
     * @param eduAuditRecord 封装查询条件的审核记录对象
     * @return 符合条件的审核记录列表
     */
    @Override
    public List<EduAuditRecord> selectEduAuditRecordList(EduAuditRecord eduAuditRecord)
    {
        return eduAuditRecordMapper.selectEduAuditRecordList(eduAuditRecord);
    }

    /**
     * 新增审核记录
     *
     * <p>在院级或校级审核操作后调用，将审核结果、审核意见、审核人等信息持久化。
     * 自动设置创建时间为当前系统时间。</p>
     *
     * @param eduAuditRecord 待插入的审核记录对象
     * @return 影响的行数（正常应为1）
     */
    @Override
    public int insertEduAuditRecord(EduAuditRecord eduAuditRecord)
    {
        eduAuditRecord.setCreateTime(DateUtils.getNowDate());
        return eduAuditRecordMapper.insertEduAuditRecord(eduAuditRecord);
    }

    /**
     * 向 sys_notice 表插入站内审核结果通知
     *
     * <p>审核操作完成后，系统自动调用此方法向成果提交者发送通知。
     * 通知类型固定为 "1"（通知类型），状态为 "0"（正常），
     * 前端右上角的通知铃铛可实时显示未读消息。</p>
     *
     * @param noticeTitle 通知标题（如"您的教学成果审核结果通知"）
     * @param noticeContent 通知详细内容（包含成果名称、审核结果、审核意见）
     * @param targetUserId 通知目标用户ID（成果提交者）
     */
    @Override
    public void insertSysNotice(String noticeTitle, String noticeContent, String targetUserId)
    {
        eduAuditRecordMapper.insertSysNotice(noticeTitle, noticeContent, targetUserId);
    }
}
