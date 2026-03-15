package com.edu.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * 网关启动程序
 *
 * @author zpz
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
public class EduGatewayApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(EduGatewayApplication.class, args);
        System.out.println("  ______  ______  _   _  _      _____                       \n" +
                " |___  / |___  /| | | || |    |_   _|                      \n" +
                "    / /     / / | | | || |      | |                        \n" +
                "   / /     / /  | | | || |      | |                        \n" +
                "  / /__   / /__ | |_| || |____  | |                        \n" +
                " /_____| /_____| \\___/ |______| |_|                        \n" +
                " :: 网关模块启动成功 ::                                      ");
    }
}
