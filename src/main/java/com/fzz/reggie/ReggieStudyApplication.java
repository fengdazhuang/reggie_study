package com.fzz.reggie;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@Slf4j
@SpringBootApplication
@MapperScan("com.fzz.reggie.mapper")
@ServletComponentScan
public class ReggieStudyApplication {
    public static void main(String[] args) {
        log.info("开始启动......");
        SpringApplication.run(ReggieStudyApplication.class,args);
    }
}
