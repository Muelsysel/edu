package com.edu.achievement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.edu.common.security.annotation.EnableCustomConfig;
import com.edu.common.security.annotation.EnableRyFeignClients;

/**
 * 教学成果管理模块
 * 
 * @author edu
 */
@EnableCustomConfig
@EnableRyFeignClients
@SpringBootApplication
public class EduAchievementApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(EduAchievementApplication.class, args);
        System.out.println("(♥◠‿◠) ﾉﾞ  教学成果管理模块启动成功   ლ(´ڡ`ლ)ﾞ  \n" +
                " .-------.       ____     __        \n" +
                " |  _ _   \\      \\   \\   /  /    \n" +
                " | ( ' )  |       \\  _. /  '       \n" +
                " |(_ o _) /        _( )_ .'         \n" +
                " | (_,_).' __  ___(_ o _)'          \n" +
                " |  |\\ \\  |  ||   |(_,_)'         \n" +
                " |  | \\ `'   /|   `-'  /           \n" +
                " |  |  \\    /  \\      /           \n" +
                " ''-'   `'-'    `-..-'              ");
    }
}
