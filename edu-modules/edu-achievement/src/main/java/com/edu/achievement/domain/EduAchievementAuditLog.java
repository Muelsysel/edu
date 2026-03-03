package com.edu.achievement.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.edu.common.core.annotation.Excel;
import com.edu.common.core.web.domain.BaseEntity;

/**
 * 教学成果审核记录对象 edu_achievement_audit_log
 * 
 * @author zpz
 * @date 2026-03-03
 */
public class EduAchievementAuditLog extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 审核记录 ID */
    private Long id;

    /** 成果 ID(关联 edu_achievement 表) */
    @Excel(name = "成果 ID(关联 edu_achievement 表)")
    private Long achievementId;

    /** 审核人 ID(关联 sys_user 表) */
    @Excel(name = "审核人 ID(关联 sys_user 表)")
    private Long auditorId;

    /** 审核时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "审核时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date auditTime;

    /** 审核意见 */
    @Excel(name = "审核意见")
    private String auditOpinion;

    /** 审核结果 (0:通过 1:驳回) */
    @Excel(name = "审核结果 (0:通过 1:驳回)")
    private String auditResult;

    /** 审核前状态 */
    @Excel(name = "审核前状态")
    private String previousStatus;

    /** 审核后状态 */
    @Excel(name = "审核后状态")
    private String currentStatus;

    /** 删除标志 (0 代表存在 2 代表删除) */
    private String delFlag;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setAchievementId(Long achievementId) 
    {
        this.achievementId = achievementId;
    }

    public Long getAchievementId() 
    {
        return achievementId;
    }

    public void setAuditorId(Long auditorId) 
    {
        this.auditorId = auditorId;
    }

    public Long getAuditorId() 
    {
        return auditorId;
    }

    public void setAuditTime(Date auditTime) 
    {
        this.auditTime = auditTime;
    }

    public Date getAuditTime() 
    {
        return auditTime;
    }

    public void setAuditOpinion(String auditOpinion) 
    {
        this.auditOpinion = auditOpinion;
    }

    public String getAuditOpinion() 
    {
        return auditOpinion;
    }

    public void setAuditResult(String auditResult) 
    {
        this.auditResult = auditResult;
    }

    public String getAuditResult() 
    {
        return auditResult;
    }

    public void setPreviousStatus(String previousStatus) 
    {
        this.previousStatus = previousStatus;
    }

    public String getPreviousStatus() 
    {
        return previousStatus;
    }

    public void setCurrentStatus(String currentStatus) 
    {
        this.currentStatus = currentStatus;
    }

    public String getCurrentStatus() 
    {
        return currentStatus;
    }

    public void setDelFlag(String delFlag) 
    {
        this.delFlag = delFlag;
    }

    public String getDelFlag() 
    {
        return delFlag;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("achievementId", getAchievementId())
            .append("auditorId", getAuditorId())
            .append("auditTime", getAuditTime())
            .append("auditOpinion", getAuditOpinion())
            .append("auditResult", getAuditResult())
            .append("previousStatus", getPreviousStatus())
            .append("currentStatus", getCurrentStatus())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("delFlag", getDelFlag())
            .toString();
    }
}
