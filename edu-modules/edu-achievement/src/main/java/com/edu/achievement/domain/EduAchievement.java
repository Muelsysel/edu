package com.edu.achievement.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.edu.common.core.annotation.Excel;
import com.edu.common.core.web.domain.BaseEntity;

/**
 * 教学成果管理对象 edu_achievement
 * 
 * @author zpz
 * @date 2026-03-03
 */
public class EduAchievement extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 成果 ID */
    @Excel(name = "成果 ID")
    private Long achievementId;

    /** 成果标题 */
    @Excel(name = "成果标题")
    private String title;

    /** 成果内容 */
    private String content;

    /** 文件存储路径 (MinIO 路径) */
    private String fileUrl;

    /** 教师 ID(关联 sys_user 表) */
    @Excel(name = "教师 ID(关联 sys_user 表)")
    private Long teacherId;

    /** 学院 ID(关联 sys_dept 表) */
    @Excel(name = "学院 ID(关联 sys_dept 表)")
    private Long collegeId;

    /** 状态 (0:草稿 1:院级审核中 2:校级审核中 3:已通过 4:已驳回) */
    @Excel(name = "状态 (0:草稿 1:院级审核中 2:校级审核中 3:已通过 4:已驳回)")
    private String status;

    /** 删除标志 (0 代表存在 2 代表删除) */
    private String delFlag;
    @Excel(name = "成果类型", readConverterExp = "1=论文,2=教材,3=竞赛,4=教改")
    private String category;

    public void setCategory(String category) {
        this.category = category;
    }
    public String getCategory() {
        return category;
    }

    public void setAchievementId(Long achievementId) 
    {
        this.achievementId = achievementId;
    }

    public Long getAchievementId() 
    {
        return achievementId;
    }

    public void setTitle(String title) 
    {
        this.title = title;
    }

    public String getTitle() 
    {
        return title;
    }

    public void setContent(String content) 
    {
        this.content = content;
    }

    public String getContent() 
    {
        return content;
    }

    public void setFileUrl(String fileUrl) 
    {
        this.fileUrl = fileUrl;
    }

    public String getFileUrl() 
    {
        return fileUrl;
    }

    public void setTeacherId(Long teacherId) 
    {
        this.teacherId = teacherId;
    }

    public Long getTeacherId() 
    {
        return teacherId;
    }

    public void setCollegeId(Long collegeId) 
    {
        this.collegeId = collegeId;
    }

    public Long getCollegeId() 
    {
        return collegeId;
    }

    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
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
            .append("achievementId", getAchievementId())
            .append("title", getTitle())
            .append("content", getContent())
            .append("fileUrl", getFileUrl())
            .append("teacherId", getTeacherId())
            .append("collegeId", getCollegeId())
            .append("status", getStatus())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("delFlag", getDelFlag())
            .append("category", getCategory())
            .toString();
    }
}
