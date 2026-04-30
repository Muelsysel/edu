package com.edu.achievement.domain;

import java.util.Date;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.edu.common.core.annotation.Excel;
import com.edu.common.core.web.domain.BaseEntity;

/**
 * 教学成果管理对象 edu_achievement
 *
 * <p>对应数据库表 {@code edu_achievement}，存储教师申报的教学成果信息，
 * 包含成果标题、内容、佐证材料路径、所属教师/学院、审核状态等字段。</p>
 *
 * <p>审核状态流转：草稿(0) → 审核中(2) → 已通过(3)；</p>
 *
 * @author zpz
 * @date 2026-03-03
 */
public class EduAchievement extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 成果 ID（主键，自增） */
    @Excel(name = "成果编号")
    private Long achievementId;

    /** 成果标题 */
    @Excel(name = "成果标题")
    private String title;

    /** 成果详述内容（富文本 HTML） */
    @Excel(name = "详细描述")
    private String content;

    /** 佐证材料文件路径，多文件以逗号分隔（不导出） */
    private String fileUrl;

    /** 申报教师 ID（关联 sys_user.user_id） */
    private Long teacherId;

    /** 所属学院 ID（关联 sys_dept.dept_id） */
    private Long collegeId;

    /** 审核时使用的部门范围起点，包含当前部门及其所有下级部门 */
    private Long auditDeptId;

    /** 审核状态 (0:草稿 2:审核中 3:已通过 4:已驳回) */
    @Excel(name = "审核状态", readConverterExp = "0=草稿,2=审核中,3=已通过,4=已驳回")
    private String status;

    /** 删除标志 (0:存在 2:已删除) */
    private String delFlag;

    /** 成果类型 (1:论文 2:教材 3:竞赛 4:教改) */
    @Excel(name = "成果类型", readConverterExp = "1=科研指导,2=教材建设,3=竞赛指导,4=教学改革,5=教学评估,6=优秀课程")
    private String category;

    /** 申报等级 (如特等奖、一等奖等，字典 edu_achievement_level) */
    @Excel(name = "申报等级")
    private String level;

    /**
     * 非数据库字段：教师姓名（通过 LEFT JOIN sys_user 查询获得）
     * 用于前端显示和 Excel 导出
     */
    @Excel(name = "教师姓名")
    private String teacherName;

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    /** 非数据库字段：学院名称（JOIN sys_dept） */
    @Excel(name = "所属学院")
    private String collegeName;

    /** 非数据库字段：审核结果（JOIN edu_audit_record） */
    @Excel(name = "审核结果")
    private String auditResultLabel;

    /** 非数据库字段：审核意见（JOIN edu_audit_record） */
    @Excel(name = "审核意见")
    private String auditOpinionExport;

    /** 非数据库字段：审核人姓名（JOIN edu_audit_record） */
    @Excel(name = "审核人")
    private String auditorNameExport;

    /** 非数据库字段：审核时间（JOIN edu_audit_record） */
    @Excel(name = "审核时间")
    private String auditTimeExport;

    /** 非数据库字段：审核人所属学院（JOIN sys_dept via auditor） */
    @Excel(name = "审核人所属学院")
    private String auditorCollege;

    /** 非数据库字段：成果得分（根据level计算） */
    @Excel(name = "成果得分")
    private Integer score;

    /** 非数据库字段：提交时间（重写 BaseEntity 字段以添加 Excel 注解） */
    @Excel(name = "提交时间", width = 20, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    public String getCollegeName() {
        return collegeName;
    }

    public void setCollegeName(String collegeName) {
        this.collegeName = collegeName;
    }

    public String getAuditResultLabel() {
        if (auditResultLabel != null) return auditResultLabel;
        if (status == null) return "未知";
        switch (status) {
            case "0": return "草稿";
            case "2": return "待审核";
            case "3": return "通过";
            case "4": return "驳回";
            default: return String.valueOf(status);
        }
    }

    public void setAuditResultLabel(String auditResultLabel) {
        this.auditResultLabel = auditResultLabel;
    }

    public String getAuditOpinionExport() {
        return auditOpinionExport;
    }

    public void setAuditOpinionExport(String auditOpinionExport) {
        this.auditOpinionExport = auditOpinionExport;
    }

    public String getAuditorNameExport() {
        return auditorNameExport;
    }

    public void setAuditorNameExport(String auditorNameExport) {
        this.auditorNameExport = auditorNameExport;
    }

    public String getAuditTimeExport() {
        return auditTimeExport;
    }

    public void setAuditTimeExport(String auditTimeExport) {
        this.auditTimeExport = auditTimeExport;
    }

    public String getAuditorCollege() {
        return auditorCollege;
    }

    public void setAuditorCollege(String auditorCollege) {
        this.auditorCollege = auditorCollege;
    }

    public Integer getScore() {
        if (level == null) return 0;
        int s;
        switch (level) {
            case "0": s = 10; break;
            case "1": s = 8; break;
            case "2": s = 6; break;
            case "3": s = 4; break;
            case "4": s = 2; break;
            default: s = 0;
        }
        this.score = s;
        return s;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    @Override
    public Date getCreateTime() {
        return createTime;
    }

    @Override
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public void setCategory(String category) {
        this.category = category;
    }
    public String getCategory() {
        return category;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
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

    public void setAuditDeptId(Long auditDeptId)
    {
        this.auditDeptId = auditDeptId;
    }

    public Long getAuditDeptId()
    {
        return auditDeptId;
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
            .append("teacherName", getTeacherName())
            .append("collegeId", getCollegeId())
            .append("collegeName", getCollegeName())
            .append("auditDeptId", getAuditDeptId())
            .append("status", getStatus())
            .append("auditResultLabel", getAuditResultLabel())
            .append("auditOpinionExport", getAuditOpinionExport())
            .append("auditorNameExport", getAuditorNameExport())
            .append("auditTimeExport", getAuditTimeExport())
            .append("auditorCollege", getAuditorCollege())
            .append("score", getScore())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("delFlag", getDelFlag())
            .append("category", getCategory())
            .append("level", getLevel())
            .toString();
    }
}
