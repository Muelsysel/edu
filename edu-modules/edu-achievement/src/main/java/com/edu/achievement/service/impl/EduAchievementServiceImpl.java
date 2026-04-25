package com.edu.achievement.service.impl;

import java.util.List;
import com.edu.common.core.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.edu.achievement.mapper.EduAchievementMapper;
import com.edu.achievement.domain.EduAchievement;
import com.edu.achievement.service.IEduAchievementService;

/**
 * 教学成果管理 Service 业务层处理
 *
 * <p>核心业务服务类，封装了教学成果的增删改查操作。
 * 作为 Controller 与 Mapper 之间的桥梁，负责业务规则处理和数据转换。</p>
 *
 * <p>该服务被以下 Controller 调用：
 * <ul>
 *   <li>{@link com.edu.achievement.controller.EduAchievementController} - 管理员和教师的成果 CRUD</li>
 *   <li>{@link com.edu.achievement.controller.EduAuditRecordController} - 审核时查询/更新成果状态</li>
 * </ul></p>
 *
 * @author zpz
 * @date 2026-03-03
 */
@Service
public class EduAchievementServiceImpl implements IEduAchievementService
{
    @Autowired
    private EduAchievementMapper eduAchievementMapper;

    /**
     * 根据主键查询单条教学成果
     *
     * <p>通过 MyBatis Mapper 执行 SQL 查询，自动通过 LEFT JOIN sys_user
     * 关联获取教师姓名（teacherName）字段。</p>
     *
     * @param achievementId 教学成果主键ID
     * @return 教学成果详情对象（包含 teacherName 等 JOIN 字段），
     *         如果不存在则返回 null
     */
    @Override
    public EduAchievement selectEduAchievementByAchievementId(Long achievementId)
    {
        return eduAchievementMapper.selectEduAchievementByAchievementId(achievementId);
    }

    /**
     * 按条件查询教学成果列表
     *
     * <p>支持的过滤条件：
     * <ul>
     *   <li>title - 成果标题（模糊匹配）</li>
     *   <li>teacherName - 教师姓名（模糊匹配，通过 JOIN sys_user 实现）</li>
     *   <li>collegeId - 所属学院ID</li>
     *   <li>status - 审核状态（0-4）</li>
     *   <li>category - 成果类型（1-4）</li>
     * </ul></p>
     *
     * <p>查询结果默认按 create_time DESC 排序。
     * 调用方可通过 PageHelper 实现分页。</p>
     *
     * @param eduAchievement 封装查询条件的成果对象（非 null 的字段作为过滤条件）
     * @return 符合条件的成果列表（含 teacherName），可能为空列表
     */
    @Override
    public List<EduAchievement> selectEduAchievementList(EduAchievement eduAchievement)
    {
        return eduAchievementMapper.selectEduAchievementList(eduAchievement);
    }

    /**
     * 新增教学成果
     *
     * <p>教师提交成果申报时调用。自动设置创建时间为当前系统时间。
     * 新增时 status 应为 "0"（草稿）或 "2"（直接提交审核），
     * 由 Controller 层根据业务需求决定。</p>
     *
     * @param eduAchievement 待新增的教学成果对象，必须包含 title、teacherId、collegeId
     * @return 影响的行数（正常应为1）
     */
    @Override
    public int insertEduAchievement(EduAchievement eduAchievement)
    {
        if (eduAchievement.getStatus() == null || !eduAchievement.getStatus().matches("[02]")) {
            eduAchievement.setStatus("0");
        }
        eduAchievement.setCreateTime(DateUtils.getNowDate());
        return eduAchievementMapper.insertEduAchievement(eduAchievement);
    }

    /**
     * 修改教学成果
     *
     * <p>两种使用场景：
     * <ol>
     *   <li>教师修改自己的成果内容（title、content、fileUrl、category 等）</li>
     *   <li>审核时更新成果状态（仅设置 achievementId 和 status）</li>
     * </ol>
     * 自动设置更新时间为当前系统时间。
     * MyBatis XML 中使用动态 SQL（if 判断），只更新非 null 的字段。</p>
     *
     * @param eduAchievement 包含要更新字段的成果对象，achievementId 不能为空
     * @return 影响的行数（正常应为1）
     */
    @Override
    public int updateEduAchievement(EduAchievement eduAchievement)
    {
        eduAchievement.setUpdateTime(DateUtils.getNowDate());
        return eduAchievementMapper.updateEduAchievement(eduAchievement);
    }

    /**
     * 批量删除教学成果（逻辑删除）
     *
     * <p>管理员批量删除成果时调用。实际执行的是 UPDATE 将 del_flag 设为 "2"，
     * 而非物理 DELETE（取决于 Mapper XML 的实现）。</p>
     *
     * @param achievementIds 需要删除的成果主键数组
     * @return 影响的行数
     */
    @Override
    public int deleteEduAchievementByAchievementIds(Long[] achievementIds)
    {
        return eduAchievementMapper.deleteEduAchievementByAchievementIds(achievementIds);
    }

    /**
     * 单条删除教学成果
     *
     * @param achievementId 需要删除的成果主键
     * @return 影响的行数
     */
    @Override
    public int deleteEduAchievementByAchievementId(Long achievementId)
    {
        return eduAchievementMapper.deleteEduAchievementByAchievementId(achievementId);
    }

    @Override
    public List<java.util.Map<String, Object>> countByStatus() {
        return eduAchievementMapper.countByStatus();
    }

    @Override
    public List<java.util.Map<String, Object>> countByCategory() {
        return eduAchievementMapper.countByCategory();
    }

    @Override
    public List<java.util.Map<String, Object>> countByCollege() {
        return eduAchievementMapper.countByCollege();
    }

    @Override
    public int countTotal() {
        return eduAchievementMapper.countTotal();
    }

    @Override
    public List<java.util.Map<String, Object>> countByStatusForTeacher(Long teacherId) {
        return eduAchievementMapper.countByStatusForTeacher(teacherId);
    }

    @Override
    public List<java.util.Map<String, Object>> countByCategoryForTeacher(Long teacherId) {
        return eduAchievementMapper.countByCategoryForTeacher(teacherId);
    }

    @Override
    public int countTotalForTeacher(Long teacherId) {
        return eduAchievementMapper.countTotalForTeacher(teacherId);
    }

    @Override
    public int countTotalPending() { return eduAchievementMapper.countTotalPending(); }

    @Override
    public int countTodayNew() { return eduAchievementMapper.countTodayNew(); }

    @Override
    public int countWeekNew() { return eduAchievementMapper.countWeekNew(); }

    @Override
    public int countMonthPassed() { return eduAchievementMapper.countMonthPassed(); }

    @Override
    public int countMonthRejected() { return eduAchievementMapper.countMonthRejected(); }

    @Override
    public int countTotalAuditRecords() { return eduAchievementMapper.countTotalAuditRecords(); }
}
