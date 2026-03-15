package com.edu.achievement.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.edu.common.core.annotation.Excel;
import com.edu.common.core.web.domain.BaseEntity;

/**
 * 审核记录对象 edu_audit_record
 *
 * @author zpz
 * @date 2026-03-15
 */
public class EduAuditRecord extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 审核记录 ID */
    @Excel(name = "审核记录 ID")
    private Long recordId;

    /** 关联成果 ID */
    @Excel(name = "关联成果 ID")
    private Long achievementId;

    /** 审核级别 (1:院级审核 2:校级审核) */
    @Excel(name = "审核级别", readConverterExp = "1=院级审核,2=校级审核")
    private String auditLevel;

    /** 审核结果 (1:通过 2:驳回) */
    @Excel(name = "审核结果", readConverterExp = "1=通过,2=驳回")
    private String auditResult;

    /** 审核意见 */
    @Excel(name = "审核意见")
    private String auditOpinion;

    /** 审核人 ID */
    @Excel(name = "审核人 ID")
    private Long auditorId;

    /** 审核人姓名 */
    @Excel(name = "审核人姓名")
    private String auditorName;

    /**
     * 非数据库字段，仅用于前端展示
     */
    private String achievementTitle;

    /** 非数据库字段：成果类型 */
    private String category;

    /** 非数据库字段：学院ID */
    private Long collegeId;

    public Long getRecordId() {
        return recordId;
    }

    public void setRecordId(Long recordId) {
        this.recordId = recordId;
    }

    public Long getAchievementId() {
        return achievementId;
    }

    public void setAchievementId(Long achievementId) {
        this.achievementId = achievementId;
    }

    public String getAuditLevel() {
        return auditLevel;
    }

    public void setAuditLevel(String auditLevel) {
        this.auditLevel = auditLevel;
    }

    public String getAuditResult() {
        return auditResult;
    }

    public void setAuditResult(String auditResult) {
        this.auditResult = auditResult;
    }

    public String getAuditOpinion() {
        return auditOpinion;
    }

    public void setAuditOpinion(String auditOpinion) {
        this.auditOpinion = auditOpinion;
    }

    public Long getAuditorId() {
        return auditorId;
    }

    public void setAuditorId(Long auditorId) {
        this.auditorId = auditorId;
    }

    public String getAuditorName() {
        return auditorName;
    }

    public void setAuditorName(String auditorName) {
        this.auditorName = auditorName;
    }

    public String getAchievementTitle() {
        return achievementTitle;
    }

    public void setAchievementTitle(String achievementTitle) {
        this.achievementTitle = achievementTitle;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Long getCollegeId() {
        return collegeId;
    }

    public void setCollegeId(Long collegeId) {
        this.collegeId = collegeId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("recordId", getRecordId())
            .append("achievementId", getAchievementId())
            .append("auditLevel", getAuditLevel())
            .append("auditResult", getAuditResult())
            .append("auditOpinion", getAuditOpinion())
            .append("auditorId", getAuditorId())
            .append("auditorName", getAuditorName())
            .append("createTime", getCreateTime())
            .toString();
    }
}
