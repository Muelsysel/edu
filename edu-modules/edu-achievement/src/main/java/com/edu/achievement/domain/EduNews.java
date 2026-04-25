package com.edu.achievement.domain;

import java.util.Date;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.edu.common.core.annotation.Excel;
import com.edu.common.core.web.domain.BaseEntity;

/**
 * 门户新闻对象 edu_news
 *
 * @author zpz
 * @date 2026-04-23
 */
public class EduNews extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 新闻ID */
    private Long newsId;

    /** 标题 */
    @Excel(name = "标题")
    private String title;

    /** 摘要 */
    @Excel(name = "摘要")
    private String summary;

    /** 封面图 */
    private String coverImage;

    /** 富文本正文 */
    private String content;

    /** 发布时间 */
    @Excel(name = "发布时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date publishTime;

    /** 浏览量 */
    @Excel(name = "浏览量")
    private Long viewCount;

    /** 发布到门户(0否 1是) */
    @Excel(name = "发布到门户", readConverterExp = "0=否,1=是")
    private String publishPortal;

    /** 排序权重 */
    @Excel(name = "排序权重")
    private Integer sortWeight;

    /** 状态(0正常 1停用) */
    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;

    /** 删除标记(0存在 2删除) */
    private String delFlag;

    /** 类型(1=通知公告 2=新闻动态) */
    private String noticeType;

    /** 关键词搜索（非表字段） */
    private String keyword;

    public Long getNewsId() {
        return newsId;
    }

    public void setNewsId(Long newsId) {
        this.newsId = newsId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(Date publishTime) {
        this.publishTime = publishTime;
    }

    public Long getViewCount() {
        return viewCount;
    }

    public void setViewCount(Long viewCount) {
        this.viewCount = viewCount;
    }

    public String getPublishPortal() {
        return publishPortal;
    }

    public void setPublishPortal(String publishPortal) {
        this.publishPortal = publishPortal;
    }

    public Integer getSortWeight() {
        return sortWeight;
    }

    public void setSortWeight(Integer sortWeight) {
        this.sortWeight = sortWeight;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getNoticeType() {
        return noticeType;
    }

    public void setNoticeType(String noticeType) {
        this.noticeType = noticeType;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("newsId", getNewsId())
                .append("title", getTitle())
                .append("summary", getSummary())
                .append("coverImage", getCoverImage())
                .append("content", getContent())
                .append("publishTime", getPublishTime())
                .append("viewCount", getViewCount())
                .append("publishPortal", getPublishPortal())
                .append("sortWeight", getSortWeight())
                .append("status", getStatus())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .append("delFlag", getDelFlag())
                .toString();
    }
}
