package com.edu.achievement.service;

import java.util.List;
import com.edu.achievement.domain.EduAchievement;

/**
 * 教学成果管理 Service 接口
 *
 * <p>定义教学成果模块的核心业务方法契约。
 * 实现类 {@link com.edu.achievement.service.impl.EduAchievementServiceImpl}。</p>
 *
 * @author zpz
 * @date 2026-03-03
 */
public interface IEduAchievementService
{
    /**
     * 根据主键查询单条教学成果（含教师姓名）
     *
     * @param achievementId 教学成果主键ID
     * @return 成果详情对象（包含通过 LEFT JOIN 获取的 teacherName），不存在返回 null
     */
    public EduAchievement selectEduAchievementByAchievementId(Long achievementId);

    /**
     * 按条件查询教学成果列表
     *
     * <p>支持按 title、teacherName、collegeId、status、category 等字段过滤。
     * 配合 PageHelper 分页使用。</p>
     *
     * @param eduAchievement 封装查询条件的对象，非 null 字段作为过滤条件
     * @return 符合条件的成果列表
     */
    public List<EduAchievement> selectEduAchievementList(EduAchievement eduAchievement);

    /**
     * 新增教学成果
     *
     * @param eduAchievement 待新增的成果对象（需包含 title、teacherId、collegeId、status）
     * @return 影响行数
     */
    public int insertEduAchievement(EduAchievement eduAchievement);

    /**
     * 修改教学成果（支持部分更新）
     *
     * @param eduAchievement 包含更新字段的成果对象，achievementId 必须非空
     * @return 影响行数
     */
    public int updateEduAchievement(EduAchievement eduAchievement);

    /**
     * 批量删除教学成果
     *
     * @param achievementIds 主键数组
     * @return 影响行数
     */
    public int deleteEduAchievementByAchievementIds(Long[] achievementIds);

    /**
     * 单条删除教学成果
     *
     * @param achievementId 主键
     * @return 影响行数
     */
    public int deleteEduAchievementByAchievementId(Long achievementId);

    /** SQL 聚合：按状态统计 */
    public List<java.util.Map<String, Object>> countByStatus();

    /** SQL 聚合：按类型统计 */
    public List<java.util.Map<String, Object>> countByCategory();

    /** SQL 聚合：按学院统计 */
    public List<java.util.Map<String, Object>> countByCollege();

    /** 查询成果总数 */
    public int countTotal();
}
