package com.edu.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import com.edu.common.security.annotation.EnableRyFeignClients;

/**
 * 认证授权中心
 *
 * @author zpz
 */
@EnableRyFeignClients
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
public class EduAuthApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(EduAuthApplication.class, args);
        System.out.println("  ______  ______  _   _  _      _____                       \n" +
                " |___  / |___  /| | | || |    |_   _|                      \n" +
                "    / /     / / | | | || |      | |                        \n" +
                "   / /     / /  | | | || |      | |                        \n" +
                "  / /__   / /__ | |_| || |____  | |                        \n" +
                " /_____| /_____| \\___/ |______| |_|                        \n" +
                " :: 认证授权中心启动成功 ::                                  ");
    }
}
