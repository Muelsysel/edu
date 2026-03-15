package com.edu.achievement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.edu.common.security.annotation.EnableCustomConfig;
import com.edu.common.security.annotation.EnableRyFeignClients;

/**
 * 教学成果管理模块
 *
 * @author zpz
 */
@EnableCustomConfig
@EnableRyFeignClients
@SpringBootApplication
public class EduAchievementApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(EduAchievementApplication.class, args);
        System.out.println("  ______  ______  _   _  _      _____                       \n" +
                " |___  / |___  /| | | || |    |_   _|                      \n" +
                "    / /     / / | | | || |      | |                        \n" +
                "   / /     / /  | | | || |      | |                        \n" +
                "  / /__   / /__ | |_| || |____  | |                        \n" +
                " /_____| /_____| \\___/ |______| |_|                        \n" +
                " :: 教学成果管理模块启动成功 ::                              ");
    }
}
