-- ============================================================
-- rollback_realistic_data.sql
-- 回退 realistic_data.sql 的所有数据插入
-- ============================================================

DELETE FROM edu_achievement WHERE achievement_id >= 100 AND achievement_id < 200;
DELETE FROM edu_news WHERE news_id >= 30 AND news_id < 50;
DELETE FROM sys_user_role WHERE user_id >= 200 AND user_id < 300;
DELETE FROM sys_user WHERE user_id >= 200 AND user_id < 300;
