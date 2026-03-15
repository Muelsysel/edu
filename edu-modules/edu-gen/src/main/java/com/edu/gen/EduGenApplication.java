package com.edu.gen;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.edu.common.security.annotation.EnableCustomConfig;
import com.edu.common.security.annotation.EnableRyFeignClients;

/**
 * 代码生成
 * 
 * @author zpz
 */
@EnableCustomConfig
@EnableRyFeignClients
@SpringBootApplication
public class EduGenApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(EduGenApplication.class, args);
        System.out.println("  ______  ______  _   _  _      _____                       \n" +
                " |___  / |___  /| | | || |    |_   _|                      \n" +
                "    / /     / / | | | || |      | |                        \n" +
                "   / /     / /  | | | || |      | |                        \n" +
                "  / /__   / /__ | |_| || |____  | |                        \n" +
                " /_____| /_____| \\___/ |______| |_|                        \n" +
                " :: 代码生成模块启动成功 ::                                  ");
    }
}
