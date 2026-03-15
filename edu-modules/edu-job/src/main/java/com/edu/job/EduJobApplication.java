package com.edu.job;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.edu.common.security.annotation.EnableCustomConfig;
import com.edu.common.security.annotation.EnableRyFeignClients;

/**
 * 定时任务
 * 
 * @author zpz
 */
@EnableCustomConfig
@EnableRyFeignClients   
@SpringBootApplication
public class EduJobApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(EduJobApplication.class, args);
        System.out.println("  ______  ______  _   _  _      _____                       \n" +
                " |___  / |___  /| | | || |    |_   _|                      \n" +
                "    / /     / / | | | || |      | |                        \n" +
                "   / /     / /  | | | || |      | |                        \n" +
                "  / /__   / /__ | |_| || |____  | |                        \n" +
                " /_____| /_____| \\___/ |______| |_|                        \n" +
                " :: 定时任务模块启动成功 ::                                  ");
    }
}
